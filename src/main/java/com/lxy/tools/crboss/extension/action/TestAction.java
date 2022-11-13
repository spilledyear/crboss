package com.lxy.tools.crboss.extension.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Notifications.Bus.notify(new Notification("Print", "First plugin", "Hello, World" + System.currentTimeMillis(), NotificationType.INFORMATION), e.getProject());
    }
}
