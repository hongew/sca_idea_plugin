package com.seczone.sca.idea.plugin.component;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @author: hew
 * @create: 2020-09-08 13:57
 **/


public class ShowComponent implements ApplicationComponent {

    public ShowComponent(){}

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "showComponent";
    }

    public void showInfo(String msg){
        Messages.showMessageDialog(msg,"tip",Messages.getInformationIcon());
    }

    public void showErr(String msg){
        Messages.showMessageDialog(msg,"error",Messages.getErrorIcon());
    }


}
