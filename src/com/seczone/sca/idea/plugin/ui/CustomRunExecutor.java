package com.seczone.sca.idea.plugin.ui;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.seczone.sca.idea.plugin.util.IconUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 定义tool window的相关静态信息
 */
public class CustomRunExecutor extends Executor {

    public static final String TOOL_WINDOW_ID = "Component Security";

    @Override
    public String getToolWindowId() {
        return TOOL_WINDOW_ID;
    }

    @Override
    public Icon getToolWindowIcon() {
        return IconUtil.ICON;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconUtil.ICON;
    }

    @Override
    public Icon getDisabledIcon() {
        return IconUtil.ICON;
    }

    @Override
    public String getDescription() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public String getActionName() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public String getId() {
        return "executor";
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return TOOL_WINDOW_ID;
    }

    @Override
    public String getContextActionId() {
        return "custom context action id";
    }

    @Override
    public String getHelpId() {
        return TOOL_WINDOW_ID;
    }

    public static Executor getRunExecutorInstance() {
        return ExecutorRegistry.getInstance().getExecutorById("executor");
    }
}

