package com.seczone.sca.idea.plugin.ui;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.content.Content;
import com.seczone.sca.idea.plugin.model.JarInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 显示数据
 */
public class CustomExecutor implements Disposable {


    private ConsoleView consoleView = null;

    private Project project = null;

    public CustomExecutor(@NotNull Project project) {
        this.project = project;
        this.consoleView = createConsoleView(project);
    }

    private ConsoleView createConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ConsoleView console = consoleBuilder.getConsole();
        return console;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    // 构建一个tool window并展示
    public void showInfo(List<JarInfo> jarInfoList) {
        if (project.isDisposed()) {
            return;
        }

        Executor executor = CustomRunExecutor.getRunExecutorInstance();
        if (executor == null) {
            return;
        }

        final RunnerLayoutUi.Factory factory = RunnerLayoutUi.Factory.getInstance(project);
        RunnerLayoutUi layoutUi = factory.create("runnerId", "runnerTitle", "sessionName", project);
        final JComponent consolePanel = createConsolePanel(consoleView,jarInfoList);

        RunContentDescriptor descriptor = new RunContentDescriptor(new RunProfile() {
            @Nullable
            @Override
            public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
                return null;
            }

            @NotNull
            @Override
            public String getName() {
                return "info";
            }

            @Nullable
            @Override
            public Icon getIcon() {
                return null;
            }
        }, new DefaultExecutionResult(), layoutUi);
        descriptor.setExecutionId(System.nanoTime());

        final Content content = layoutUi.createContent("contentId", consolePanel, "displayName", AllIcons.Debugger.Console, consolePanel);
        content.setCloseable(false);
        layoutUi.addContent(content);

        Disposer.register(descriptor,this);

        Disposer.register(content, consoleView);

        ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
    }

    private JComponent createConsolePanel(ConsoleView consoleView,List<JarInfo> jarInfoList) {
//        panel.add(consoleView.getComponent(), BorderLayout.CENTER);
        List<String> collect = jarInfoList.stream().map(jarInfo -> jarInfo.getShowInfo()).collect(Collectors.toList());

//        JPanel panel = new JPanel();
        // 填充节点数据
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("pom dependency");
        for (String s : collect) {
            DefaultMutableTreeNode childNode=new DefaultMutableTreeNode(s);
            childNode.add(new DefaultMutableTreeNode("cve-2020-0001"));
            root.add(childNode);
        }

        // 设置显示样式
        // 创建渲染器
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        // 设置节点字体，以及 选中 和 未选中 时的颜色
        render.setFont(new Font("Monospaced", Font.PLAIN, 18));
        render.setTextSelectionColor(Color.yellow);
//        render.setTextNonSelectionColor(Color.black);
        // 设置节点 选中 和 未选中 时的背景颜色
//        render.setBackgroundSelectionColor(Color.black);
        render.setBackgroundNonSelectionColor(Color.darkGray);

        JTree tree=new JTree(root);
        tree.setCellRenderer(render);
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportBorder(BorderFactory.createEtchedBorder());

        return scrollPane;
    }
}