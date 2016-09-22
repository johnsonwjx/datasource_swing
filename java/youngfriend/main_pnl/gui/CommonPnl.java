/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.main_pnl.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.main_pnl.deleagte.BuildTreeDataDelegate;
import youngfriend.main_pnl.deleagte.InparamTableDelegateAbs;
import youngfriend.main_pnl.deleagte.InparamTableDelegateCommonAbs;
import youngfriend.main_pnl.deleagte.SearchTableFieldDelegage;
import youngfriend.main_pnl.deleagte.SortFieldDelegate;
import youngfriend.main_pnl.deleagte.V6TypeDelegate;
import youngfriend.utils.PubUtil;

import javax.swing.JTextField;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiong
 */
public class CommonPnl extends AbstractMainPnl {


    private BuildTreeDataDelegate buildTreeDataDelegate;
    private SortFieldDelegate sortFieldDelegate;
    private V6TypeDelegate v6TypeDelegate;
    public static final String ORDERPARAMS_NAME = "orderparams";
    public static final String ORDERPARAMS_LABEL = "排序入口参数";

    public void clear() {
        super.clear();
        inparamTableDeletage.clear();
        outParamTableDeletate.clear();
        buildTreeDataDelegate.clear();
        v6TypeDelegate.clear();
        orderparams_checkbox.setSelected(false);
    }


    @Override
    protected Map<String, JsonObject> getInParamFieldMap(JsonObject inparamLevel1) {
        JsonArray fieldArray = PubUtil.getJsonObj(inparamLevel1, InparamTableDelegateAbs.INPARAMS_PROPNAME, JsonArray.class);
        if (fieldArray != null) {
            HashMap<String, JsonObject> inParamFieldMap = new HashMap<String, JsonObject>(fieldArray.size());
            for (JsonElement fieldEle : fieldArray) {
                JsonObject fieldObj = fieldEle.getAsJsonObject();
                String name = PubUtil.getProp(fieldObj, "name");
                if (name.equals(ORDERPARAMS_NAME)) {
                    orderparams_checkbox.setSelected(true);
                    continue;
                }
                inParamFieldMap.put(name, fieldObj);
            }
            return inParamFieldMap;
        }
        return null;
    }

    @Override
    public void loadData(JsonObject jsonData) throws Exception {
        JsonObject inparamLevel1 = commomLoadData(jsonData);
        v6TypeDelegate.loadV6PropData(inparamLevel1);
        Map<String, JsonObject> inParamFieldMap = getInParamFieldMap(inparamLevel1);
        inparamTableDeletage.loadInTableDatas(jsonData, inParamFieldMap);
        Map<JTextField, String> tfComKeyMap = new HashMap<JTextField, String>();
        tfComKeyMap.put(saveServiceName_tf, "saveServiceName");
        tfComKeyMap.put(deleteServiceName_tf, "deleteServiceName");
        tfComKeyMap.put(queryServiceName_tf, "queryServiceName");
        tfComKeyMap.put(saveServiceName_tf, "saveServiceName");
        tfComKeyMap.put(saveServiceName_tf, "saveServiceName");
        commondLoadJTextField(jsonData, tfComKeyMap);
        tfComKeyMap.clear();
        tfComKeyMap.put(codeField_tf, "codeField");
        tfComKeyMap.put(rootName_tf, "rootName");
        tfComKeyMap.put(nameField_tf, "nameField");
        tfComKeyMap.put(codeInc_tf, "codeInc");
        commondLoadJTextField(inparamLevel1, tfComKeyMap);
        sortFieldDelegate.load(jsonData);
        buildTreeDataDelegate.load(inparamLevel1);

    }


    @Override
    void saveInparamLevel2Custom(JsonObject inparamLevel1) {
        buildTreeDataDelegate.save(inparamLevel1);
        v6TypeDelegate.saveV6typeInparam(inparamLevel1);
    }


    @Override
    public void saveParam(String modulelabel, JsonObject jsonData) {
        super.saveParam(modulelabel, jsonData);
        if (orderparams_checkbox.isSelected()) {
            JsonObject fieldObj = new JsonObject();
            fieldObj.addProperty("name", ORDERPARAMS_NAME);
            fieldObj.addProperty("label", ORDERPARAMS_LABEL);
            fieldObj.addProperty("defaultValue", "");
            inparamTableDeletage.getInparamLevel2().add(fieldObj);
        }
        sortFieldDelegate.save(jsonData);
        String queryServiceName = queryServiceName_tf.getText().trim(), saveServiceName = saveServiceName_tf.getText().trim(), deleteServiceName = deleteServiceName_tf.getText().trim();
        jsonData.addProperty("queryServiceName", queryServiceName);
        jsonData.addProperty("saveServiceName", saveServiceName);
        jsonData.addProperty("deleteServiceName", deleteServiceName);
    }


    /**
     * Creates new form CommonPnl
     */
    public CommonPnl() {
        initComponents();
        new SearchTableFieldDelegage(searchBtn, searchTf, fieldtable);
        afterUi(outParams_table, outParamsAdd_btn, outParamsDel_btn);
        v6TypeDelegate = new V6TypeDelegate(v6type_btn, v6type_tf);
        buildTreeDataDelegate = new BuildTreeDataDelegate(tree_checkbox, fieldListDlg, rootName_tf, codeField_tf, nameField_tf, codeInc_tf, codeField_btn, name_field_btn);
        sortFieldDelegate = new SortFieldDelegate(sort_tf, sort_btn, fields);
        inparamTableDeletage = new InparamTableDelegateCommonAbs(fieldtable) {
            @Override
            protected void loadFieldCustom(BeanDto field) {
                buildTreeDataDelegate.loadBuildTreeBean(field);
            }
        };
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
        jLabel8 = new javax.swing.JLabel();
        searchTf = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        tree_checkbox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        rootName_tf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        codeField_tf = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        nameField_tf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        codeInc_tf = new javax.swing.JTextField();
        codeField_btn = new javax.swing.JButton();
        name_field_btn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        sort_tf = new javax.swing.JTextField();
        v6type_tf = new javax.swing.JTextField();
        queryServiceName_tf = new javax.swing.JTextField();
        saveServiceName_tf = new javax.swing.JTextField();
        deleteServiceName_tf = new javax.swing.JTextField();
        sort_btn = new javax.swing.JButton();
        v6type_btn = new javax.swing.JButton();
        orderparams_checkbox = new javax.swing.JCheckBox();
        outParams_pnl = new javax.swing.JPanel();
        outParams_spnl = new javax.swing.JScrollPane();
        outParams_table = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        outParamsAdd_btn = new javax.swing.JButton();
        outParamsDel_btn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        fieldtable = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.BorderLayout());

        jLabel8.setText("搜索");

        searchBtn.setText("...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchTf, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(781, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(searchTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchBtn))
                                .addContainerGap(10, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tree_checkbox.setText("数据是否树型展示");

        jLabel2.setText("根节点显示值");

        rootName_tf.setEnabled(false);

        jLabel3.setText("建树字段");

        codeField_tf.setEditable(false);
        codeField_tf.setEnabled(false);

        jLabel4.setText("节点显示字段");

        nameField_tf.setEditable(false);
        nameField_tf.setEnabled(false);

        jLabel5.setText("级次结构");

        codeInc_tf.setText("2,2");
        codeInc_tf.setEnabled(false);

        codeField_btn.setText("...");
        codeField_btn.setEnabled(false);

        name_field_btn.setText("...");
        name_field_btn.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tree_checkbox)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(codeField_tf, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                                        .addComponent(nameField_tf, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(codeField_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(name_field_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(rootName_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(codeInc_tf, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 2, Short.MAX_VALUE)))
                                .addGap(16, 16, 16))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tree_checkbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(rootName_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(codeField_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(codeField_btn))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(nameField_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(name_field_btn))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(codeInc_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel13.setText("排序字段");

        jLabel12.setText("复合数据类型 ");

        jLabel9.setText("检索数据服务");

        jLabel10.setText("保存服务");

        jLabel11.setText("删除服务");

        sort_tf.setEditable(false);

        v6type_tf.setEditable(false);

        sort_btn.setText("...");

        v6type_btn.setText("...");

        orderparams_checkbox.setText("设置排序入口参数");

        outParams_pnl.setBorder(javax.swing.BorderFactory.createTitledBorder("出口参数设置"));
        outParams_pnl.setLayout(new java.awt.BorderLayout());

        outParams_spnl.setPreferredSize(new java.awt.Dimension(100, 50));

        outParams_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "字段名", "显示名称"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel9)
                                                        .addComponent(jLabel10)
                                                        .addComponent(jLabel11)
                                                        .addComponent(jLabel12)
                                                        .addComponent(jLabel13))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(queryServiceName_tf)
                                                        .addComponent(saveServiceName_tf)
                                                        .addComponent(deleteServiceName_tf)))
                                        .addComponent(orderparams_checkbox)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(100, 100, 100)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(sort_tf, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                                        .addComponent(v6type_tf))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(sort_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(v6type_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(outParams_pnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(orderparams_checkbox)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel13)
                                                        .addComponent(sort_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(sort_btn))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel12)
                                                        .addComponent(v6type_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(v6type_btn))
                                                .addGap(4, 4, 4)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(jLabel9)
                                                        .addComponent(queryServiceName_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(jLabel10)
                                                        .addComponent(saveServiceName_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(deleteServiceName_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel11))))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(outParams_pnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2, java.awt.BorderLayout.PAGE_END);

        fieldtable.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        fieldtable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "字段", "显示名称", "不返回字段", "作为入口参数", "操作符", "为空时,保留条件", "固定值(查询时)", "固定值(保存时)", "分类字段", "汇总函数"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean[]{
                    false, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
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
                        .addGap(0, 373, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, 0)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                                        .addGap(0, 0, 0)))
        );

        add(jPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton codeField_btn;
    private javax.swing.JTextField codeField_tf;
    private javax.swing.JTextField codeInc_tf;
    private javax.swing.JTextField deleteServiceName_tf;
    private javax.swing.JTable fieldtable;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameField_tf;
    private javax.swing.JButton name_field_btn;
    private javax.swing.JCheckBox orderparams_checkbox;
    private javax.swing.JButton outParamsAdd_btn;
    private javax.swing.JButton outParamsDel_btn;
    private javax.swing.JPanel outParams_pnl;
    private javax.swing.JScrollPane outParams_spnl;
    private javax.swing.JTable outParams_table;
    private javax.swing.JTextField queryServiceName_tf;
    private javax.swing.JTextField rootName_tf;
    private javax.swing.JTextField saveServiceName_tf;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTf;
    private javax.swing.JButton sort_btn;
    private javax.swing.JTextField sort_tf;
    private javax.swing.JCheckBox tree_checkbox;
    private javax.swing.JButton v6type_btn;
    private javax.swing.JTextField v6type_tf;
    // End of variables declaration//GEN-END:variables
}
