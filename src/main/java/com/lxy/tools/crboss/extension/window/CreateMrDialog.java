package com.lxy.tools.crboss.extension.window;

import com.intellij.openapi.ui.Messages;
import com.lxy.tools.crboss.model.BranchInfo;
import com.lxy.tools.crboss.model.CreateMrRequest;
import com.lxy.tools.crboss.service.APIService;
import com.lxy.tools.crboss.utils.StringUtil;
import org.gitlab4j.api.models.MergeRequest;

import javax.swing.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreateMrDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JLabel titleLabel;
    private JTextField titleText;

    private JLabel descLabel;
    private JTextArea descText;

    private JLabel sourceLabel;
    private JComboBox sourceBox;

    private JLabel targetLabel;
    private JComboBox targetBox;

    private APIService apiService;
    private List<BranchInfo> branchInfos;


    public CreateMrDialog(APIService apiService) {
        this.apiService = apiService;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        refreshData();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void refreshData() {
        if (this.apiService == null) {
            return;
        }

        new Thread(() -> {
            this.branchInfos = getBranches();
            Optional.ofNullable(this.branchInfos).orElse(Collections.emptyList()).forEach(v -> {
                sourceBox.addItem(v.getName());
                targetBox.addItem(v.getName());
            });
        }).start();
    }

    private void onOK() {
        String message = checkParams();
        if (StringUtil.isNotBlank(message)) {
            Messages.showErrorDialog(message, "??????Merge Request??????");
            return;
        }

        CreateMrRequest request = new CreateMrRequest();
        request.setTitle(titleText.getText().trim());
        request.setDesc(descText.getText().trim());
        request.setSource(sourceBox.getSelectedItem().toString());
        request.setTarget(targetBox.getSelectedItem().toString());
        createMergeRequest(request);

        // add your code here
        dispose();
    }

    private String checkParams() {
        boolean notBlank = StringUtil.isNotBlank(titleText.getText()) &&
                sourceBox.getSelectedItem() != null &&
                targetBox.getSelectedItem() != null &&
                StringUtil.isNotBlank(descText.getText());
        if (!notBlank) {
            return "???????????????????????????";
        }

        if (sourceBox.getSelectedItem().toString().equals(targetBox.getSelectedItem().toString())) {
            return "??????????????????????????????????????????";
        }
        return "";
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CreateMrDialog dialog = new CreateMrDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    private List<BranchInfo> getBranches() {
        return Optional.ofNullable(this.apiService.getBranches())
                .orElse(Collections.emptyList())
                .stream()
                .map(v -> {
                    BranchInfo branchInfo = new BranchInfo();
                    branchInfo.setName(v.getName());
                    branchInfo.setWebUrl(v.getWebUrl());
                    branchInfo.setMerged(v.getMerged());
                    branchInfo.setProtected(v.getProtected());
                    return branchInfo;
                })
                .collect(Collectors.toList());
    }

    private MergeRequest createMergeRequest(CreateMrRequest request) {
        try {
            return this.apiService.createMergeRequest(request);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "??????Merge Request??????");
            throw new RuntimeException(e);
        }
    }
}
