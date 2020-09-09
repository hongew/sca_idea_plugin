package com.seczone.sca.idea.plugin;

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
        return "com.seczone.sca.idea.plugin.ShowComponent";
    }

    public void show(String text){
//        String tip = "hello world";
        Messages.showMessageDialog(text,"tip",Messages.getInformationIcon());
    }


}
