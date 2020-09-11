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
import com.seczone.sca.idea.plugin.model.JarInfo;
import com.seczone.sca.idea.plugin.ui.CustomExecutor;
import com.seczone.sca.idea.plugin.util.JDBCUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


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
            final Model model = getModel(new File(pomPath));
            final List<Dependency> dependencies = model.getDependencies();
            Messages.showInfoMessage("dependencies.size="+dependencies.size(),"tip");
            // 拼接sql
            String sql = buildSql(dependencies);
            System.out.println("sql="+sql);
            Messages.showInfoMessage(sql,"tip");
            if (null == sql){
                Messages.showInfoMessage("该pom无依赖组件","tip");
            }else {
                // 获取组件数据
                List<JarInfo> jarInfoList = getJarInfoList(sql);
                System.out.println("print jarinfos=====");
                Messages.showInfoMessage("jarInfoList.size="+jarInfoList.size(),"tip");
                for (JarInfo jarInfo : jarInfoList) {
                    System.out.println(jarInfo.getShowInfo());
                }
                // 显示组件数据
                showJarInfoSecurity(jarInfoList,e.getProject());
            }
            System.out.println("actionPerformed end====");
        } catch (Exception ex) {
            System.out.println("actionPerformed err====");
            ex.printStackTrace();
            Messages.showErrorDialog(ex.getMessage(),"error");
        }

    }

    private void showJarInfoSecurity(List<JarInfo> jarInfoList,Project project) {
        CustomExecutor executor = new CustomExecutor(project);
        executor.showInfo(jarInfoList);
    }

    private List<JarInfo> getJarInfoList(String sql) throws Exception {
        List<JarInfo> jarInfoList = JDBCUtils.findAll(sql);
        return jarInfoList;
    }

    // select * from jar_info where 1=2 or (g= and a= and v=) or (g= and a= and v=)
    private String buildSql(List<Dependency> dependencies) {
        StringBuilder sqlBuilder = new StringBuilder();
        for (Dependency dependency : dependencies) {
            sqlBuilder.append(String.format(" or (group_name='%s' and artifact_name='%s' and version='%s') ",dependency.getGroupId(),dependency.getArtifactId(),dependency.getVersion()));
        }
        String sql = sqlBuilder.toString();
        if (null == sql || sql.length() < 1){
            return null;
        }
        return "select group_name,artifact_name,version,grade from jar_info where 1=2 "+sql;
    }


    private Model getModel(File file) throws Exception{
        FileInputStream fis = new FileInputStream(file);
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(fis);
        return model;
    }
}
