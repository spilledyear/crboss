package com.lxy.tools.crboss.extension.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.lxy.tools.crboss.extension.window.CrBossToolsWindow;

public class MouseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        // getSelectionModel() 可获取到鼠标选中文本对象，通过 getSelectedText() 方法获取到选中的文本字符串
        String text = editor.getSelectionModel().getSelectedText();

        Notifications.Bus.notify(new Notification("Print", "小天才翻译机", text, NotificationType.INFORMATION), e.getProject());
    }
}
