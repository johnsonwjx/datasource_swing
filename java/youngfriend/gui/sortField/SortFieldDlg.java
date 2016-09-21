/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.gui.sortField;

import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.main_pnl.deleagte.InparamTableDelegateAbs;
import youngfriend.utils.PubUtil;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiong
 */
public class SortFieldDlg extends javax.swing.JDialog {
    private boolean ok;
    private DefaultListModel fieldModel = new DefaultListModel();
    private String value;

    /**
     * Creates new form SortFieldDlg
     */
    public SortFieldDlg() {
        super(App.instance, ModalityType.APPLICATION_MODAL);
        initComponents();
        src_list.setModel(fieldModel);
        src_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    toTarget();
                }
            }
        });
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    toSrc();
                }
            }
        });
        PubUtil.registerDlgBtn(this, jButton1, jButton2, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ok = false;
                dispose();
            }
        });
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onOk();
            }
        });
        setLocationRelativeTo(getOwner());
    }

    public void edit(List<BeanDto> fields, String sortfields) {
        this.value = sortfields;
        fieldModel.clear();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        if (!fields.isEmpty()) {
            List<String> list = null;
            if (!StringUtils.nullOrBlank(sortfields)) {
                list = Arrays.asList(sortfields.toUpperCase().split(","));
            }
            List<BeanDto> selectDtos = new ArrayList<BeanDto>();
            for (BeanDto beanDto : fields) {
                String field_name = beanDto.getValue(InparamTableDelegateAbs.FIELD_NAME_PROPNAME);
                if (list != null) {
                    if (list.contains(field_name.toUpperCase())) {
                        selectDtos.add(beanDto);
                    } else if (list.contains(field_name.toUpperCase() + " DESC")) {
                        selectDtos.add(beanDto);

                    } else {
                        fieldModel.addElement(beanDto);
                    }
                } else {
                    fieldModel.addElement(beanDto);
                }
            }
            if (!selectDtos.isEmpty()) {
                for (String field : list) {
                    for (BeanDto dto : selectDtos) {
                        String field_name = dto.getValue(InparamTableDelegateAbs.FIELD_NAME_PROPNAME);
                        if (field_name.toUpperCase().equals(field)) {
                            model.addRow(new Object[]{dto, false});
                        } else if (field.equals(field_name.toUpperCase() + " DESC")) {
                            model.addRow(new Object[]{dto, true});
                        }
                    }
                }
            }
        }
        this.setVisible(true);

    }

    public boolean isOk() {
        return ok;
    }

    public String getValue() {
        return value;
    }

    private void onOk() {

        if (jTable1.getRowCount() < 1) {
            this.value = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                BeanDto dto = (BeanDto) jTable1.getValueAt(i, 0);
                sb.append(dto.getValue(InparamTableDelegateAbs.FIELD_NAME_PROPNAME));
                if (Boolean.TRUE.equals(jTable1.getValueAt(i, 1))) {
                    sb.append(" DESC");
                }
                sb.append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            this.value = sb.toString();
        }
        this.ok = true;
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        clearbtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        src_list = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("确定");
        jPanel1.add(jButton1);

        jButton2.setText("取消");
        jPanel1.add(jButton2);

        clearbtn.setText("清空");
        clearbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearbtnActionPerformed(evt);
            }
        });
        jPanel1.add(clearbtn);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jButton3.setText(">");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("<");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(154, 154, 154)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(202, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4, java.awt.BorderLayout.LINE_START);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "字段", "是否逆序"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean[]{
                    false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jScrollPane2.setViewportView(jTable1);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel3);

        jScrollPane1.setViewportView(src_list);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel2.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        toTarget();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void toTarget() {
        Object[] selectedValues = src_list.getSelectedValues();
        if (selectedValues.length < 1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (Object value : selectedValues) {
            model.addRow(new Object[]{value, false});
            fieldModel.removeElement(value);
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        toSrc();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void clearbtnActionPerformed(java.awt.event.ActionEvent evt) {
        this.value = "";
        ok = true;
        dispose();
    }

    private void toSrc() {
        int selIndexs[] = jTable1.getSelectedRows();
        if (selIndexs.length <= 0) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < selIndexs.length; ) {
            int index = selIndexs[i];
            fieldModel.addElement(jTable1.getValueAt(index, 0));
            model.removeRow(index);// 因为移除后，表格的总行数也随着变化，所以要
            i++;
            for (int j = i; j < selIndexs.length; j++) {
                selIndexs[j] = selIndexs[j] - 1;
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearbtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JList src_list;
    // End of variables declaration//GEN-END:variables
}
