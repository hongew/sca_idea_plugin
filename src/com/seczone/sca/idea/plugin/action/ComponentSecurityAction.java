package com.seczone.sca.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.seczone.sca.idea.plugin.component.ShowComponent;
import com.seczone.sca.idea.plugin.model.CveInfo;
import com.seczone.sca.idea.plugin.model.JarInfo;
import com.seczone.sca.idea.plugin.ui.CustomExecutor;
import com.seczone.sca.idea.plugin.util.JDBCUtils;
import com.seczone.sca.idea.plugin.util.Utils;
import fr.dutra.tools.maven.deptree.core.InputType;
import fr.dutra.tools.maven.deptree.core.Node;
import fr.dutra.tools.maven.deptree.core.Parser;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.*;
import org.fest.util.Lists;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ComponentSecurityAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            System.out.println("actionPerformed start===");
            // 获取当前操作的pom文件
            final PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
            String pomPath = psiFile.getVirtualFile().getPath();
            if (!pomPath.endsWith("pom.xml")){
                Messages.showInfoMessage("当前操作文件不是pom文件","tip");
                return;
            }

            // 获取依赖组件
            Dependency rootNode = new Dependency(); // pom自身
            List<Dependency> dependencies = getPomDependency(pomPath,rootNode);
            if (Utils.isEmpty(dependencies)){
                Messages.showInfoMessage("该pom无依赖组件","tip");
                return;
            }

            // 获取依赖组件安全等级及漏洞信息
            List<JarInfo> jarInfoList = getDependencySecurityData(dependencies);
            // 显示组件数据
            showJarInfoSecurity(jarInfoList,e.getProject(),rootNode);

            System.out.println("actionPerformed end====");
        } catch (Throwable ex) {
            System.out.println("actionPerformed err====");
            ex.printStackTrace();
            Messages.showErrorDialog(ex.getMessage(),"error");
        }

    }

    // 获取依赖组件安全等级及漏洞信息
    private List<JarInfo> getDependencySecurityData(List<Dependency> dependencies) throws Exception {
        List<JarInfo> jarInfoList = Lists.newArrayList();
        // 1.获取组件安全等级
        String jarSql = buildJarSql(dependencies);
        System.out.println("jarSql="+jarSql);
        List<JarInfo> dbJarInfos = JDBCUtils.findJars(jarSql);
        System.out.println("dbJarinfos.size="+dbJarInfos.size());
        for (Dependency dependency : dependencies) {
            String grade = getJarInfoGrade(dependency,dbJarInfos);
            JarInfo jarInfo = new JarInfo(dependency.getGroupId(),dependency.getArtifactId(),dependency.getVersion(),grade);
            jarInfoList.add(jarInfo);
        }
        // 2.获取组件漏洞
        String jarCveSql = buildJarCveSql(dependencies);
        System.out.println("jarCveSql="+jarCveSql);
        List<JarInfo> dbJarCves = JDBCUtils.findJarCves(jarCveSql);
        System.out.println("dbJarCves.size="+dbJarCves.size());

        // 3.获取漏洞安全等级
        Set<String> cveNos = dbJarCves.stream().map(dbJarCve -> dbJarCve.getCveNo()).collect(Collectors.toSet());
        List<CveInfo> dbCves = Lists.newArrayList();
        if (cveNos.size()>0) {
            String cveSql = buildCveSql(cveNos);
            System.out.println("cveSql="+cveSql);
            dbCves = JDBCUtils.findCves(cveSql);
            System.out.println("dbCves.size="+dbCves.size());
        }

        for (JarInfo jarInfo : jarInfoList) {
            // 获取该组件漏洞
            List<CveInfo> jarCveList = Lists.newArrayList();
            Set<String> jarCveNos = dbJarCves.stream().filter(dbJarCve -> dbJarCve.getG().equals(jarInfo.getG()) && dbJarCve.getA().equals(jarInfo.getA()) && dbJarCve.getV().equals(jarInfo.getV()) && !"0".equals(dbJarCve.getCveNo())).map(jarCve -> jarCve.getCveNo()).collect(Collectors.toSet());
            for (String jarCveNo : jarCveNos) {
                CveInfo cveInfo = new CveInfo(jarCveNo,null,getCveSeverity(jarCveNo,dbCves));
                jarCveList.add(cveInfo);
            }
            jarInfo.setCveInfoList(jarCveList);
        }

        return jarInfoList;
    }

    private String getCveSeverity(String jarCveNo, List<CveInfo> dbCves) {
        String severity ="UNKNOWN";
        for (CveInfo dbCve : dbCves) {
            if(jarCveNo.equals(dbCve.getName())){
                return dbCve.getSeverity();
            }
        }
        return severity;
    }

    private String getJarInfoGrade(Dependency dependency, List<JarInfo> dbJarInfos) {
        String grade = "";
        for (JarInfo dbJarInfo : dbJarInfos) {
            if (dependency.getGroupId().equals(dbJarInfo.getG()) && dependency.getArtifactId().equals(dbJarInfo.getA()) && dependency.getVersion().equals(dbJarInfo.getV())){
                return dbJarInfo.getGrade();
            }
        }
        return grade;
    }

    private List<Dependency> getPomDependency(String pomPath,Dependency rootNode) throws Exception {
        /*final Model model = getModel(new File(pomPath));
        return model.getDependencies();*/

        File pomFile = new File(pomPath);
        String dependencyTreeFilePath = pomFile.getParent() +File.separator + "pom.txt";
        Reader r = null;
        try {
            // 将pom文件解析成依赖树，将结果存入txt文件
            getDependencyTreeFile(pomFile);

            // 解析依赖树文件，得到所有的依赖
            InputType type = InputType.TEXT;
            r = new BufferedReader(new InputStreamReader(new FileInputStream(dependencyTreeFilePath), "UTF-8"));
            Parser parser = type.newParser();
            Node root = parser.parse(r);

            // rootNode赋值
            rootNode.setGroupId(root.getGroupId());
            rootNode.setArtifactId(root.getArtifactId());
            rootNode.setVersion(root.getVersion());
            rootNode.setType(root.getPackaging());

            // 获取依赖组件(直接引用)
            final List<Dependency> dependencies = root.getChildNodes().stream().map(childNode -> {
                Dependency dependency = new Dependency();
                dependency.setGroupId(childNode.getGroupId());
                dependency.setArtifactId(childNode.getArtifactId());
                dependency.setVersion(childNode.getVersion());
                dependency.setType(childNode.getPackaging());
                return dependency;
            }).collect(Collectors.toList());

            return dependencies;
        } finally {
            if(null != r){
                r.close();
            }
            File dependencyTreeFile = new File(dependencyTreeFilePath);
            if (dependencyTreeFile.exists()){
                dependencyTreeFile.delete();
            }
        }
    }

    // 将pom文件解析成依赖树，将结果存入txt文件
    private void getDependencyTreeFile(File pomFile) throws Exception {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList("dependency:tree -D outputFile=pom.txt"));

        Invoker invoker = new DefaultInvoker();
        String mavenHome = getMavenHome();
        System.out.println("maven_home="+mavenHome);
        invoker.setMavenHome(new File(mavenHome));
        InvocationResult invocationResult = invoker.execute(request);
        int exitCode = invocationResult.getExitCode();
        if (0!=exitCode){
            throw new Exception("解析pom文件失败，请检查格式是否正确");
        }
    }

    private String getMavenHome() throws Exception {
        String mavenHome = System.getenv("MAVEN_HOME");
        if (Utils.isEmpty(mavenHome)){
            mavenHome = System.getenv("M2_HOME");
        }
        if (Utils.isEmpty(mavenHome)){
            throw new Exception("没有配置mavne环境变量:MAVEN_HOME");
        }else {
            return mavenHome;
        }
    }

    private void showJarInfoSecurity(List<JarInfo> jarInfoList,Project project,Dependency rootNode) {
        CustomExecutor executor = new CustomExecutor(project);
        executor.showInfo(jarInfoList,rootNode);
    }

    // select * from jar_info where 1=2 union select * from jar_info where g= and a= and v=
    private String buildJarSql(List<Dependency> dependencies) {
        StringBuilder sqlBuilder = new StringBuilder("select group_name,artifact_name,version,grade from jar_info where 1=2");
        for (Dependency dependency : dependencies) {
            sqlBuilder.append(String.format(" union select group_name,artifact_name,version,grade from jar_info where artifact_name='%s' and group_name='%s' and version='%s' ",dependency.getArtifactId(),dependency.getGroupId(),dependency.getVersion()));
        }
        return sqlBuilder.toString();
    }

    // select * from t_vulnerable where 1=2 union select * from t_vulnerable where g= and a= and v=
    private String buildJarCveSql(List<Dependency> dependencies) {
        StringBuilder sqlBuilder = new StringBuilder("select g,a,v,custom_cve_no from t_vulnerable where 1=2");
        for (Dependency dependency : dependencies) {
            sqlBuilder.append(String.format(" union select g,a,v,custom_cve_no from t_vulnerable where g='%s' and a='%s' and v='%s' ",dependency.getGroupId(),dependency.getArtifactId(),dependency.getVersion()));
        }
        return sqlBuilder.toString();
    }

    // select * from t_cve where name in()
    private String buildCveSql(Set<String> cveNos) {
        StringBuilder sqlBuilder = new StringBuilder();
        for (String cveNo : cveNos) {
            sqlBuilder.append(String.format("'%s',",cveNo));
        }
        String sql = sqlBuilder.toString();
        sql = sql.substring(0,sql.length()-1);
        return String.format("select name,severity from t_cve where name in(%s)",sql);
    }


    private Model getModel(File file) throws Exception{
        FileInputStream fis = new FileInputStream(file);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(fis);
        return model;
    }
}
