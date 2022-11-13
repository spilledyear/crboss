package com.lxy.tools.crboss.extension.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CrBossToolsWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // ContentFactory 在 IntelliJ 平台 SDK 中负责 UI 界面的管理
        ContentFactory contentFactory = ContentFactory.getInstance();

        MrMainWindow mainPanel = new MrMainWindow(toolWindow, contentFactory);

        Content content = contentFactory.createContent(mainPanel.getListMainPanel(), "MrList", true);
        content.setCloseable(false);

        // 将被界面工厂代理后创建的content，添加到工具栏窗口管理器中
        toolWindow.getContentManager().addContent(content);
//        toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, () -> {});
    }
}
