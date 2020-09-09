package com.seczone.sca.idea.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;


public class ComponentSecurityAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        System.out.println("start");
        // 获取当前在操作的工程上下文
        final Project data = e.getData(PlatformDataKeys.PROJECT);
        // 获取当前操作的类文件
        final PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        // 获取文件路径
        final String path = psiFile.getVirtualFile().getPath();

//        Application application = ApplicationManager.getApplication();
//        ShowComponent showComponent = application.getComponent(ShowComponent.class);
//        ShowComponent showComponent = new ShowComponent();
//        showComponent.show(path);
        CustomExecutor executor = new CustomExecutor(e.getProject()); // 参考窗口开发
        executor.run();
        System.out.println("end");

    }
}
