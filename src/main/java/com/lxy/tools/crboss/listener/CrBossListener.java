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
import com.lxy.tools.crboss.service.APIManager;
import com.lxy.tools.crboss.utils.StringUtil;
import org.jetbrains.annotations.NotNull;

import static com.lxy.tools.crboss.extension.config.CrBossConfiguration.URL_PLACEHOLDER;

public class CrBossListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);

        String gitlabUrl = CrBossPersistent.getInstance().getGitlabUrl();
        String gitlabToken = CrBossPersistent.getInstance().getGitlabToken();

        if (StringUtil.isBlank(gitlabUrl) || URL_PLACEHOLDER.equals(gitlabUrl) || StringUtil.isBlank(gitlabToken) || URL_PLACEHOLDER.equals(gitlabToken)) {
            Notification notification = new Notification("Print", "CrBoss", "请配置Gitlab API Token", NotificationType.INFORMATION);
            // 在提示消息中，增加一个 Action，可以通过 Action 一步打开配置界面
            notification.addAction(new OpenCrBossSettingAction());
            Notifications.Bus.notify(notification, project);
            return;
        }
        APIManager.registerAPI(project);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        ProjectManagerListener.super.projectClosed(project);
        APIManager.unRegisterAPI(project);
    }

    static class OpenCrBossSettingAction extends NotificationAction {

        OpenCrBossSettingAction() {
            super("打开CrBoss配置界面");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            // IntelliJ SDK 提供的一个工具类，可以通过配置项名字，直接显示对应的配置界面
            ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), "CrBoss");
            notification.expire();
        }
    }
}
