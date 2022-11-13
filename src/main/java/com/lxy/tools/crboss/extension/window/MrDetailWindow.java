package com.lxy.tools.crboss.extension.window;

import com.intellij.ui.table.JBTable;
import com.lxy.tools.crboss.model.MrInfo;
import org.gitlab4j.api.models.MergeRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MrDetailWindow {
    private static final String[] COLUMNS = {"ID", "IID", "名称", "源分支", "目标分支", "提交人", "提交时间"};

    private final JPanel detailMainPanel = new JPanel();

    public MrDetailWindow() {
        JPanel treeFilePanel = new JPanel();
        treeFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JComboBox<String> boxList = new com.intellij.openapi.ui.ComboBox<>();
        boxList.addItem("OPENED");
        boxList.addItem("ALL");
        boxList.addActionListener(e -> System.out.printf("选中了 " + e.getActionCommand()));
        treeFilePanel.add(boxList);
        treeFilePanel.add(new JSeparator());

        detailMainPanel.setLayout(new BorderLayout(5, 1));
        detailMainPanel.add(treeFilePanel, BorderLayout.EAST);
        detailMainPanel.add(new JSeparator());

        initNoteTable();
    }


    private void initNoteTable() {
        DefaultTableModel tableModel = new DefaultTableModel(null, COLUMNS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        new Thread(() -> {
            List<MergeRequest> mergeRequests = Collections.emptyList();
            Object[][] data = buildRowData(getMrList(mergeRequests));
            Optional.ofNullable(data).ifPresent(v -> Arrays.stream(v).forEach(tableModel::addRow));
        }).start();

        JBTable noteTable = new com.intellij.ui.table.JBTable();
        noteTable.setShowVerticalLines(false);
        noteTable.setRowHeight(30);
        noteTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        noteTable.setModel(tableModel);

        TableColumn tableColumn = noteTable.getTableHeader().getColumnModel().getColumn(1);
        tableColumn.setMaxWidth(0);
        tableColumn.setMinWidth(0);
        tableColumn.setPreferredWidth(0);

        noteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    System.out.printf("mouseClicked" + e.getSource());
                    return;
                }

                int selectedRow = noteTable.getSelectedRow();
                int mergeIid = (int) noteTable.getValueAt(selectedRow, 1);
                System.out.printf("selectedRow: %d, mergeIid: %d", selectedRow, mergeIid);
            }
        });

        JScrollPane mrMainScrollPane = new com.intellij.ui.components.JBScrollPane(noteTable);
        mrMainScrollPane.setSize(1000, 600);
        detailMainPanel.add(mrMainScrollPane, BorderLayout.WEST);
        detailMainPanel.add(new JPanel(), BorderLayout.SOUTH);
    }


    private java.util.List<MrInfo> getMrList(java.util.List<MergeRequest> mergeRequests) {
        return Optional.ofNullable(mergeRequests)
                .orElse(Collections.emptyList())
                .stream()
                .map(v -> {
                    MrInfo mrInfo = new MrInfo();
                    mrInfo.setId(v.getId());
                    mrInfo.setId(v.getIid());
                    mrInfo.setName(v.getTitle());
                    mrInfo.setSource(v.getSourceBranch());
                    mrInfo.setTarget(v.getTargetBranch());
                    mrInfo.setUserName(v.getAuthor().getUsername());
                    mrInfo.setUpdateTime(v.getAuthor().getUsername());
                    return mrInfo;
                })
                .collect(Collectors.toList());
    }

    public Object[][] buildRowData(List<MrInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            Object[] rowItem = new Object[COLUMNS.length];
            rowItem[0] = list.get(i).getId();
            rowItem[1] = list.get(i).getIid();
            rowItem[2] = list.get(i).getName();
            rowItem[3] = list.get(i).getSource();
            rowItem[4] = list.get(i).getTarget();
            rowItem[5] = list.get(i).getUserName();
            rowItem[6] = list.get(i).getUpdateTime();
            data[i] = rowItem;
        }
        return data;
    }

    public JPanel getDetailMainPanel() {
        return detailMainPanel;
    }
}
