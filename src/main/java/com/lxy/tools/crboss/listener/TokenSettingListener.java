package com.lxy.tools.crboss.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.lxy.tools.crboss.extension.persistent.CrBossPersistent;
import com.lxy.tools.crboss.utils.StringUtil;
import org.jetbrains.annotations.NotNull;

public class TokenSettingListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);

        String token = CrBossPersistent.getInstance().getGitlabToken();
        if (StringUtil.isNotBlank(token) && !"请输入Gitlab API Token".equals(token)) {
            ProjectManagerListener.super.projectOpened(project);
        }

        Notification notification = new Notification("Print", "CrBoss", "请配置Gitlab API Token", NotificationType.INFORMATION);

        // 在提示消息中，增加一个 Action，可以通过 Action 一步打开配置界面
        notification.addAction(new OpenTranslatorSettingAction());
        Notifications.Bus.notify(notification, project);
    }


    static class OpenTranslatorSettingAction extends NotificationAction {

        OpenTranslatorSettingAction() {
            super("打开CrBoss配置界面");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            // IntelliJ SDK 提供的一个工具类，可以通过配置项名字，直接显示对应的配置界面
            ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), "CsBoss");
            notification.expire();
        }
    }
}
