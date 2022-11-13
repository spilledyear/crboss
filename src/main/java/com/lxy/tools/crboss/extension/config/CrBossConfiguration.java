package com.lxy.tools.crboss.extension.config;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;
import com.lxy.tools.crboss.extension.persistent.CrBossPersistent;
import com.lxy.tools.crboss.utils.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CrBossConfiguration implements Configurable {

    private final JComponent component = new JPanel();

    private final JTextField urlTextField = new JTextField();
    private final JTextField tokenTextField = new JTextField();

    private final static String URL_PLACEHOLDER = StringUtil.utf8("请输入Gitlab API URL");
    private final static String TOKEN_PLACEHOLDER = StringUtil.utf8("请输入Gitlab API TOKEN");


    public CrBossConfiguration() {
        this.component.setLayout(new GridLayout(15, 1));

        // 打开配置界面时，判断持久化对象字段非 null，回填配置信息到输入框中
        String gitlabUrl = CrBossPersistent.getInstance().getGitlabUrl();
        if (StringUtil.isNotBlank(gitlabUrl)) {
            this.urlTextField.setText(gitlabUrl);
        } else {
            this.urlTextField.setText(URL_PLACEHOLDER);
            this.urlTextField.setForeground(JBColor.GRAY);
        }

        String gitlabToken = CrBossPersistent.getInstance().getGitlabToken();
        if (StringUtil.isNotBlank(gitlabToken)) {
            this.tokenTextField.setText(gitlabToken);
        } else {
            this.tokenTextField.setText(TOKEN_PLACEHOLDER);
            this.tokenTextField.setForeground(JBColor.GRAY);
        }

        this.urlTextField.addFocusListener(new TextFieldListener(this.urlTextField, URL_PLACEHOLDER));
        this.tokenTextField.addFocusListener(new TextFieldListener(this.tokenTextField, TOKEN_PLACEHOLDER));

        this.component.add(this.urlTextField);
        this.component.add(this.tokenTextField);
    }


    @Override
    public String getDisplayName() {
        return "CrBoss";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return component;
    }

    @Override
    public boolean isModified() {
        return true;
    }


    // 点击配置页面中的 apply 按钮或者 OK 按钮，会调用该方法，在该方法中保存配置
    @Override
    public void apply() throws ConfigurationException {
        String gitlabUrl = StringUtil.isNotBlank(this.urlTextField.getText()) ? this.urlTextField.getText().trim() : "";
        String gitlabToken = StringUtil.isNotBlank(this.tokenTextField.getText()) ? this.tokenTextField.getText().trim() : "";

        CrBossPersistent.getInstance().setGitlabUrl(gitlabUrl);
        CrBossPersistent.getInstance().setGitlabToken(gitlabToken);

        Notifications.Bus.notify(new Notification("Print", "持久化Gitlab信息成功", gitlabUrl, NotificationType.INFORMATION));
    }


    static class TextFieldListener implements FocusListener {
        private final String defaultHint;
        private final JTextField textField;

        public TextFieldListener(JTextField textField, String defaultHint) {
            this.defaultHint = defaultHint;
            this.textField = textField;
        }

        @Override
        public void focusGained(FocusEvent e) {
            // 清空提示语，设置为黑色字体
            if (textField.getText().equals(defaultHint)) {
                textField.setText("");
                textField.setForeground(JBColor.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            // 如果内容为空，设置提示语
            if (textField.getText().equals("")) {
                textField.setText(defaultHint);
                textField.setForeground(JBColor.GRAY);
            }
        }
    }
}
