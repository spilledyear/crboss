package com.lxy.tools.crboss.extension.window;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.lxy.tools.crboss.model.MrInfo;
import com.lxy.tools.crboss.service.APIManager;
import com.lxy.tools.crboss.service.APIService;
import org.gitlab4j.api.models.MergeRequest;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class MrMainWindow {
    private static final String[] COLUMNS = {"ID", "IID", "名称", "源分支", "目标分支", "状态", "提交人", "提交时间"};

    private static final Map<Integer, Content> CONTENT_CACHE = new HashMap<>();

    private Project project;
    private ToolWindow toolWindow;
    private ContentFactory contentFactory;

    /**
     * MR列表
     */
    private final JPanel listMainPanel = new JPanel();

    private final JTable mrListTable = new com.intellij.ui.table.JBTable();

    private String selectedMrState = "ALL";

    public MrMainWindow(Project project, ToolWindow toolWindow, ContentFactory contentFactory) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.contentFactory = contentFactory;
        inti();
    }

    public void inti() {
        JPanel headPanel = new JPanel();
//        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headPanel.setLayout(new BorderLayout(5, 1));

        JComboBox<String> boxList = new com.intellij.openapi.ui.ComboBox<>();
        boxList.addItem("OPENED");
        boxList.addItem("MERGED");
        boxList.addItem("CLOSED");
        boxList.addItem("ALL");
        this.selectedMrState = boxList.getItemAt(0);

        boxList.addActionListener(e -> {
            ComboBox<String> comboBox = (ComboBox<String>) e.getSource();
            this.selectedMrState = comboBox.getItem();
            System.out.printf("选中了 " + this.selectedMrState);

            refreshTableData();
        });
        headPanel.add(boxList, BorderLayout.WEST);

        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(mrListTable)
                .addExtraAction(new AnActionButton("刷新", AllIcons.Actions.Refresh) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        refreshTableData();
                    }
                })
                .addExtraAction(new AnActionButton("创建Merge Request", AllIcons.General.Add) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        createMr(e);
                    }
                }).setToolbarPosition(ActionToolbarPosition.TOP);

        JPanel toolPanel = toolbarDecorator.createPanel();
        toolPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        headPanel.add(toolPanel, BorderLayout.EAST);
        headPanel.add(new JSeparator());

        listMainPanel.setLayout(new BorderLayout(5, 1));
        listMainPanel.add(headPanel, BorderLayout.NORTH);
        listMainPanel.add(new JSeparator());

        initMrTable();
    }


    private void initMrTable() {
        DefaultTableModel tableModel = new DefaultTableModel(null, COLUMNS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        refreshTableData();

        mrListTable.setShowVerticalLines(false);
        mrListTable.setRowHeight(30);
        mrListTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        mrListTable.setModel(tableModel);

        TableColumn tableColumn = mrListTable.getTableHeader().getColumnModel().getColumn(1);
        tableColumn.setMaxWidth(0);
        tableColumn.setMinWidth(0);
        tableColumn.setPreferredWidth(0);

        mrListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    System.out.printf("mouseClicked" + e.getSource());
                    return;
                }

                int selectedRow = mrListTable.getSelectedRow();
                int mergeIid = (int) mrListTable.getValueAt(selectedRow, 1);
                System.out.printf("selectedRow: %d, mergeIid: %d", selectedRow, mergeIid);

                if (CONTENT_CACHE.get(mergeIid) != null) {
                    System.out.printf("该MR对应的Tab已存在 %d", mergeIid);
                    return;
                }

                MrDetailWindow mrDetailWindow = new MrDetailWindow();
                Content content = contentFactory.createContent(mrDetailWindow.getDetailMainPanel(), "MR_" + mergeIid, false);
                toolWindow.getContentManager().addContent(content);
                CONTENT_CACHE.put(mergeIid, content);
            }
        });

        JScrollPane mrMainScrollPane = new com.intellij.ui.components.JBScrollPane(mrListTable);
        mrMainScrollPane.setSize(1000, 600);
        listMainPanel.add(mrMainScrollPane, BorderLayout.CENTER);
        listMainPanel.add(new JPanel(), BorderLayout.SOUTH);
    }

    private void refreshTableData() {
        new Thread(() -> {
            DefaultTableModel tableModel = (DefaultTableModel) mrListTable.getModel();
            tableModel.getDataVector().clear();
            tableModel.fireTableDataChanged();
            mrListTable.updateUI();

            final String state = this.selectedMrState.toLowerCase();
            List<MergeRequest> mergeRequests = Optional.ofNullable(APIManager.getAPI(project).getMergeRequests())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(v -> "ALL".equalsIgnoreCase(state) || state.equals(v.getState()))
                    .collect(Collectors.toList());
            Object[][] data = buildRowData(getMrList(mergeRequests));
            Optional.ofNullable(data).ifPresent(v -> Arrays.stream(v).forEach(tableModel::addRow));
        }).start();
    }

    private void createMr(AnActionEvent e) {
        CreateMrDialog dialog = new CreateMrDialog(APIManager.getAPI(project));
        dialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (screenSize.width - 680) / 2;
        int h = (screenSize.height * 95 / 100 - 250) / 2;
        dialog.setLocation(w, h);
        dialog.setTitle("创建Merge Request");
        dialog.setVisible(true);

//        JBPopupFactory popupFactory = JBPopupFactory.getInstance();
//        Runnable runnable = () -> {
//            Messages.showMessageDialog("aaa", "hello", Messages.getInformationIcon());
//        };
//        ListPopup popup = popupFactory.createConfirmation("hello", runnable, 1);
//        popup.showInBestPositionFor(e.getDataContext());
    }


    private List<MrInfo> getMrList(java.util.List<MergeRequest> mergeRequests) {
        return Optional.ofNullable(mergeRequests)
                .orElse(Collections.emptyList())
                .stream()
                .map(v -> {
                    MrInfo mrInfo = new MrInfo();
                    mrInfo.setId(v.getId());
                    mrInfo.setIid(v.getIid());
                    mrInfo.setName(v.getTitle());
                    mrInfo.setSource(v.getSourceBranch());
                    mrInfo.setTarget(v.getTargetBranch());
                    mrInfo.setState(v.getState());
                    mrInfo.setUserName(v.getAuthor().getUsername());
                    mrInfo.setUpdateTime(v.getAuthor().getUsername());
                    return mrInfo;
                }).collect(Collectors.toList());
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
            rowItem[5] = list.get(i).getState();
            rowItem[6] = list.get(i).getUserName();
            rowItem[7] = list.get(i).getUpdateTime();
            data[i] = rowItem;
        }
        return data;
    }

    public JPanel getListMainPanel() {
        return listMainPanel;
    }
}
