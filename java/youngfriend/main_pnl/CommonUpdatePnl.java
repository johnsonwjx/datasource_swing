/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.main_pnl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.bean.ButtonCellEditor;
import youngfriend.common.util.StringUtils;
import youngfriend.main_pnl.deleagte.InparamTableDelegateAbs;
import youngfriend.main_pnl.deleagte.InparamTableDelegateCommonAbs;
import youngfriend.utils.ModuleType;
import youngfriend.utils.PubUtil;

import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

/**
 * @author xiong
 */
public class CommonUpdatePnl extends AbstractMainPnl {
    @Override
    protected ModuleType getModuleType() {
        return ModuleType.COMMON_UPDATE;
    }
    public void clear() {
        super.clear();
        inparamTableDeletage.clear();
        outParamTableDeletate.clear();
    }


    @Override
    void saveInparamLevel2Custom(JsonObject inparamLevel1) {
    }

    @Override
    public void loadData(JsonObject jsonData) throws Exception {
        try {
            init = true;
            commomLoadData(jsonData, readOnlyCb);
            JsonObject inparamLevel1 = PubUtil.getJsonObj(jsonData, INPARAM_PROPNAME, JsonArray.class).get(0).getAsJsonObject();
            Map<String, JsonObject> inParamFieldMap = getInParamFieldMap(inparamLevel1);
            inparamTableDeletage.loadInTableDatas(jsonData, inParamFieldMap);
            //更新参数
            JsonElement updateParams = jsonData.get("updateParams");
            if (updateParams != null) {
                DefaultTableModel model = (DefaultTableModel) update_table.getModel();
                JsonArray updateParamsArr = updateParams.getAsJsonArray();
                Iterator<JsonElement> iterator = updateParamsArr.iterator();
                while (iterator.hasNext()) {
                    JsonObject item = iterator.next().getAsJsonObject();
                    model.addRow(new String[]{PubUtil.getProp(item, InparamTableDelegateAbs.FIELD_NAME_PROPNAME), PubUtil.getProp(item, "value")});
                }
            }
        } finally {
            init = false;
        }
    }


    @Override
    public boolean checkValidate() {
        if (!super.checkValidate()) {
            return false;
        }
        int rowCount = update_table.getRowCount();
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                Object valueAt = update_table.getValueAt(i, 0);
                if (valueAt == null || StringUtils.nullOrBlank((String) valueAt)) {
                    PubUtil.showMsg("更新参数字段不能为空");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void saveParam(String modulelabel, JsonObject jsonData) {
        super.saveParam(modulelabel, jsonData);
        PubUtil.stopTabelCellEditor(update_table);
        JsonArray updateParams = new JsonArray();
        int rowCount = update_table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            JsonObject item = new JsonObject();
            item.addProperty(InparamTableDelegateAbs.FIELD_NAME_PROPNAME, (String) update_table.getValueAt(i, 0));
            item.addProperty("value", (String) update_table.getValueAt(i, 1));
            updateParams.add(item);
        }
        if (updateParams.size() > 0) {
            jsonData.add("updateParams", updateParams);
        }
    }

    /**
     * Creates new form CommonPnl
     */
    public CommonUpdatePnl() {
        initComponents();
        afterUi(table_combo, outParams_table, outParamsAdd_btn, outParamsDel_btn, readOnlyCb);
        inparamTableDeletage = new InparamTableDelegateCommonAbs(fieldtable) {
            @Override
            protected void loadFieldCustom(BeanDto field) {
            }
        };
        initUpdateTable();
    }

    private void initUpdateTable() {
        TableColumn updateFieldColumn = update_table.getColumnModel().getColumn(0);
        final ButtonCellEditor buttonCellEditor = new ButtonCellEditor(new JTextField());
        buttonCellEditor.initAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String value = (String) buttonCellEditor.getCellEditorValue();
                BeanDto field = PubUtil.getDto(fields, InparamTableDelegateAbs.FIELD_NAME_PROPNAME, value);
                fieldListDlg.setSelect(field);
                fieldListDlg.showDlg();
                if (!fieldListDlg.isOk()) {
                    return;
                }
                field = fieldListDlg.getSelect();
                buttonCellEditor.afterEditor(field == null ? "" : field.toString());
            }
        });
        updateFieldColumn.setCellEditor(buttonCellEditor);
        updateFieldColumn.setCellRenderer(buttonCellEditor.getRender());
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
                table_combo = new javax.swing.JComboBox();
                jLabel8 = new javax.swing.JLabel();
                readOnlyCb = new javax.swing.JCheckBox();
                jPanel2 = new javax.swing.JPanel();
                outParams_pnl = new javax.swing.JPanel();
                outParams_spnl = new javax.swing.JScrollPane();
                outParams_table = new javax.swing.JTable();
                jPanel5 = new javax.swing.JPanel();
                outParamsAdd_btn = new javax.swing.JButton();
                outParamsDel_btn = new javax.swing.JButton();
                update_pnl = new javax.swing.JPanel();
                update_spnl = new javax.swing.JScrollPane();
                update_table = new javax.swing.JTable();
                jPanel6 = new javax.swing.JPanel();
                updateAdd_btn = new javax.swing.JButton();
                updateDel_btn = new javax.swing.JButton();
                jPanel3 = new javax.swing.JPanel();
                jScrollPane2 = new javax.swing.JScrollPane();
                fieldtable = new javax.swing.JTable();

                setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                setLayout(new java.awt.BorderLayout());

                jLabel8.setText("数据表");

                readOnlyCb.setText("是否只读");

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(table_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(readOnlyCb)
                                .addContainerGap(629, Short.MAX_VALUE))
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(table_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8)
                                        .addComponent(readOnlyCb))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                add(jPanel1, java.awt.BorderLayout.PAGE_START);

                jPanel2.setPreferredSize(new java.awt.Dimension(1131, 307));
                jPanel2.setLayout(new java.awt.GridLayout(1, 0));

                outParams_pnl.setBorder(javax.swing.BorderFactory.createTitledBorder("出口参数设置"));
                outParams_pnl.setLayout(new java.awt.BorderLayout());

                outParams_spnl.setPreferredSize(new java.awt.Dimension(100, 50));

                outParams_table.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {

                        },
                        new String [] {
                                "字段名", "显示名称"
                        }
                ) {
                        Class[] types = new Class [] {
                                java.lang.String.class, java.lang.String.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types [columnIndex];
                        }
                });
                outParams_table.setRowHeight(25);
                outParams_spnl.setViewportView(outParams_table);

                outParams_pnl.add(outParams_spnl, java.awt.BorderLayout.CENTER);

                outParamsAdd_btn.setText("增加");
                jPanel5.add(outParamsAdd_btn);

                outParamsDel_btn.setText("删除");
                jPanel5.add(outParamsDel_btn);

                outParams_pnl.add(jPanel5, java.awt.BorderLayout.PAGE_END);

                jPanel2.add(outParams_pnl);

                update_pnl.setBorder(javax.swing.BorderFactory.createTitledBorder("更新参数设置"));
                update_pnl.setLayout(new java.awt.BorderLayout());

                update_spnl.setPreferredSize(new java.awt.Dimension(100, 50));

                update_table.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {

                        },
                        new String [] {
                                "字段名", "更新值"
                        }
                ) {
                        Class[] types = new Class [] {
                                java.lang.String.class, java.lang.String.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types [columnIndex];
                        }
                });
                update_table.setRowHeight(25);
                update_spnl.setViewportView(update_table);

                update_pnl.add(update_spnl, java.awt.BorderLayout.CENTER);

                updateAdd_btn.setText("增加");
                jPanel6.add(updateAdd_btn);

                updateDel_btn.setText("删除");
                jPanel6.add(updateDel_btn);

                update_pnl.add(jPanel6, java.awt.BorderLayout.PAGE_END);

                jPanel2.add(update_pnl);

                add(jPanel2, java.awt.BorderLayout.PAGE_END);

                fieldtable.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
                fieldtable.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {

                        },
                        new String [] {
                                "字段", "显示名称", "不返回字段", "作为入口参数", "操作符", "为空时,保留条件", "查询时,固定值", "保存时,固定值"
                        }
                ) {
                        Class[] types = new Class [] {
                                java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
                        };
                        boolean[] canEdit = new boolean [] {
                                false, true, true, true, true, true, true, true
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types [columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return canEdit [columnIndex];
                        }
                });
                fieldtable.setRowHeight(25);
                fieldtable.getTableHeader().setReorderingAllowed(false);
                jScrollPane2.setViewportView(fieldtable);

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1129, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, 0)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1129, Short.MAX_VALUE)
                                        .addGap(0, 0, 0)))
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 278, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, 0)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                                        .addGap(0, 0, 0)))
                );

                add(jPanel3, java.awt.BorderLayout.CENTER);
        }// </editor-fold>//GEN-END:initComponents


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JTable fieldtable;
        private javax.swing.JLabel jLabel8;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel5;
        private javax.swing.JPanel jPanel6;
        private javax.swing.JScrollPane jScrollPane2;
        private javax.swing.JButton outParamsAdd_btn;
        private javax.swing.JButton outParamsDel_btn;
        private javax.swing.JPanel outParams_pnl;
        private javax.swing.JScrollPane outParams_spnl;
        private javax.swing.JTable outParams_table;
        private javax.swing.JCheckBox readOnlyCb;
        private javax.swing.JComboBox table_combo;
        private javax.swing.JButton updateAdd_btn;
        private javax.swing.JButton updateDel_btn;
        private javax.swing.JPanel update_pnl;
        private javax.swing.JScrollPane update_spnl;
        private javax.swing.JTable update_table;
        // End of variables declaration//GEN-END:variables
}
