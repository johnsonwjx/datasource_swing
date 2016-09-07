/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.gui;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.utils.ModuleType;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceInvoker;

import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * @author xiong
 */
public class NewModuleDlg extends javax.swing.JDialog {

    public BeanDto service;
    private static final Logger logger = LoggerFactory.getLogger(NewModuleDlg.class);
    private String parent_code;
    private List<String> codes;
    private BeanDto dto;

    public boolean isOk() {
        return ok;
    }

    public String getServerVersion() {
        return server2_radio.isSelected() ? "2" : null;
    }

    private boolean ok = false;

    /**
     * Creates new form newModuleDlg
     */
    public NewModuleDlg(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        comTypePnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup bg = new ButtonGroup();
        bg.add(server3_radio);
        bg.add(server2_radio);
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(common_radio);
        bg1.add(service_radio);
        bg1.add(btn_radio);
        bg1.add(commonUpdate_radio);
        bg1.getSelection().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                server_combo.setSelectedIndex(0);
                table_combo.removeAllItems();
            }
        });
        setLocationRelativeTo(parent);
        after();

        this.server_combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    table_combo.removeAllItems();
                    service = (BeanDto) server_combo.getSelectedItem();
                    if (service == null) {
                        return;
                    }
                    String name = service.getValue("name");
                    try {
                        List<BeanDto> lst = null;
                        ModuleType moduleType = getModuleType();
                        boolean version3 = getServerVersion() == null;
                        switch (moduleType) {
                            case COMMON:
                            case COMMON_UPDATE:
                                String tableXML = version3 ? ServiceInvoker.getTables(name) : ServiceInvoker.getTables(name, PubUtil.getService2Url());
                                lst = ServiceInvoker.parseTable(name, tableXML);
                                break;
                            case SERVICE:
                                lst = ServiceInvoker.getDataSource(name, version3, ModuleType.SERVICE);
                                break;
                            case BUTTON:
                                lst = ServiceInvoker.getDataSource(name, version3, ModuleType.BUTTON);
                                break;

                        }

                        if (lst != null && !lst.isEmpty()) {
                            for (BeanDto dto : lst) {
                                table_combo.addItem(dto);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        PubUtil.showMsg("获取表格失败");
                    }
                }

            }
        });

        addService();

        server2_radio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (event_lock == true) {
                    return;
                }
                event_lock = true;
                try {
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                        addService2();
                    } else {
                        addService();
                    }
                    table_combo.removeAllItems();
                } finally {
                    event_lock = false;
                }
            }
        });

    }

    private boolean event_lock = false;

    public ModuleType getModuleType() {
        if (common_radio.isSelected()) {
            return ModuleType.COMMON;
        } else if (service_radio.isSelected()) {
            return ModuleType.SERVICE;
        } else if (commonUpdate_radio.isSelected()) {
            return ModuleType.COMMON_UPDATE;
        } else {
            return ModuleType.BUTTON;
        }
    }

    /**
     * 2.0服务
     */
    private void addService2() {
        String serverurl = PubUtil.getService2Url();
        if (StringUtils.nullOrBlank(serverurl)) {
            serverurl = PubUtil.mainFrame.setServe2Url();
        }
        if (StringUtils.nullOrBlank(serverurl)) {
            server3_radio.setSelected(true);
            return;
        }
        List<BeanDto> serverbeans = null;
        if (serverurl.equals(PubUtil.getService2Url()) && PubUtil.serviceBeans_2 != null) {
            serverbeans = PubUtil.serviceBeans_2;
        } else {
            try {
                serverbeans = ServiceInvoker.getServices2(serverurl);
            } catch (Exception e) {
                logger.error(e.getMessage());
                PubUtil.showMsg("获取服务报错" + e.getMessage());
                server3_radio.setSelected(true);
                throw new RuntimeException(e);
            }
        }

        this.server_combo.removeAllItems();
        this.server_combo.addItem(null);
        for (BeanDto dto : serverbeans) {
            this.server_combo.addItem(dto);
        }
    }

    private void addService() {
        this.server_combo.removeAllItems();
        this.server_combo.addItem(null);
        for (BeanDto dto : PubUtil.serviceBeans) {
            this.server_combo.addItem(dto);
        }
    }

    public void showDlg(BeanDto parent, java.util.List<String> codes) {
        ok = false;
        this.codes = codes;
        this.parent_code = parent.getValue("code");
        if (this.parent_code.startsWith("06")) {
            comTypePnl.remove(common_radio);
            comTypePnl.remove(service_radio);
            btn_radio.setSelected(true);
        } else {
            comTypePnl.remove(commonUpdate_radio);
            comTypePnl.remove(btn_radio);
            common_radio.setSelected(true);
        }
        comTypePnl.doLayout();
        parent_tf.setText(String.valueOf(parent));
        code_tf.setText(PubUtil.getLastCode(parent_code,codes.isEmpty()?null: codes.get(codes.size() - 1)));
        this.setVisible(true);
    }

    private void after() {
        PubUtil.registerDlgBtn(this, save_btn, cancel_btn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onCancel();
            }
        });
    }

    public BeanDto getService() {
        return service;
    }

    public String getTable() {

        BeanDto selectedItem = (BeanDto) table_combo.getSelectedItem();
        ModuleType moduleType = getModuleType();
        switch (moduleType) {
            case COMMON:
            case COMMON_UPDATE:
                return selectedItem.getValue("table_name");
            case SERVICE:
            case BUTTON:
                return selectedItem.getValue("value");
        }
        return "";
    }


    public String getDesc() {
        return desc_tf.getText().trim();
    }

    private JsonObject getObj() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name_tf.getText().trim());
        obj.addProperty("code", code_tf.getText().trim());
        obj.addProperty("ismodule", "true");
        obj.addProperty("description", getDesc());
        return obj;
    }

    private void onOk() {
        if (getService() == null) {
            PubUtil.showMsg(this, "请选择服务");
            return;
        }
        if (getTable() == null) {
            PubUtil.showMsg(this, "请选择表格");
            return;
        }
        if (!UpdateDialog.check(this, codes, parent_code, code_tf, name_tf)) {
            return;
        }
        JsonObject obj = getObj();
        dto = new BeanDto(obj, App.MODULE_TOSTRING);
        String tablename = getTable();
        JsonObject jsonData = new JsonObject();
        JsonArray inparamArr = new JsonArray();
        JsonObject inparam = new JsonObject();
        inparam.addProperty("name", tablename);
        inparam.addProperty("label", dto.getValue("name"));
        inparam.addProperty("maxLevel", "20");
        inparam.addProperty("maxLength", "64");
        inparamArr.add(inparam);
        jsonData.addProperty("tablename", tablename);
        jsonData.addProperty("servicename", service.getValue("name"));
        jsonData.add("inparam", inparamArr);
        String moduleid = null;
        try {
            moduleid = ServiceInvoker.saveModule(null, PubUtil.mainFrame.getProjectid(), dto.getValue("name"), dto.getValue("description"),//
                    tablename, jsonData.toString(), getServerVersion(), getModuleType());
        } catch (ServiceInvokerException e) {
            Throwables.propagate(e);
        }
        obj.addProperty("moduleid", moduleid);

        String data = null;
        try {
            data = ServiceInvoker.saveCatalog(null, obj.get("name").getAsString(), dto.getValue("code"),//
                    obj.get("description").getAsString(), null, PubUtil.mainFrame.getProjectid(), moduleid);
            JsonElement jsonElement = PubUtil.parseJson(data);
            if (jsonElement == null) {
                throw new RuntimeException("未知错误");
            }
            JsonArray asJsonArray = jsonElement.getAsJsonArray();
            JsonObject jsonElement1 = asJsonArray.get(0).getAsJsonObject();
            String id = jsonElement1.get("id").getAsString();
            if (Strings.isNullOrEmpty(id)) {
                throw new RuntimeException("id为空");
            }
            String code = jsonElement1.get("code").getAsString();
            dto.setItem("id", id);
            dto.setItem("code", code);
        } catch (ServiceInvokerException e) {
            logger.error(e.getMessage(), e);
            try {
                ServiceInvoker.delModule(moduleid);
            } catch (ServiceInvokerException e1) {
                Throwables.propagate(e1);
            }
            throw new RuntimeException("报错目录信息失败");
        }
        ok = true;
        dispose();
    }

    public BeanDto getDto() {
        return this.dto;
    }

    private void onCancel() {
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

        jButton1 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        save_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        parent_tf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        server_combo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        table_combo = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        name_tf = new javax.swing.JTextField();
        desc_tf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        code_tf = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        server3_radio = new javax.swing.JRadioButton();
        server2_radio = new javax.swing.JRadioButton();
        url_lb = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        comTypePnl = new javax.swing.JPanel();
        common_radio = new javax.swing.JRadioButton();
        service_radio = new javax.swing.JRadioButton();
        btn_radio = new javax.swing.JRadioButton();
        commonUpdate_radio = new javax.swing.JRadioButton();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        save_btn.setText("确定");
        save_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_btnActionPerformed(evt);
            }
        });
        jPanel1.add(save_btn);

        cancel_btn.setText("取消");
        jPanel1.add(cancel_btn);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jLabel1.setText("父目录");

        parent_tf.setEditable(false);
        parent_tf.setText(" ");

        jLabel2.setText("服务名");

        jLabel3.setText("数据源");

        table_combo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                table_comboItemStateChanged(evt);
            }
        });

        jLabel4.setText("组件名称");

        jLabel5.setText("组件描述");

        jLabel6.setText("排序代码");

        jLabel7.setText("服务版本");

        server3_radio.setSelected(true);
        server3_radio.setText("3.0");

        server2_radio.setText("2.0");

        url_lb.setText(" ");

        jLabel8.setText("组件类型");

        common_radio.setSelected(true);
        common_radio.setText("通用数据源");
        comTypePnl.add(common_radio);

        service_radio.setText("专用服务数据源");
        comTypePnl.add(service_radio);

        btn_radio.setText("专用业务组件");
        comTypePnl.add(btn_radio);

        commonUpdate_radio.setText("通用更新组件");
        comTypePnl.add(commonUpdate_radio);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(server3_radio)
                                .addGap(42, 42, 42)
                                .addComponent(server2_radio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(url_lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap(323, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jLabel8))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(comTypePnl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(parent_tf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                                                        .addComponent(server_combo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 488, Short.MAX_VALUE)
                                                        .addComponent(table_combo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 488, Short.MAX_VALUE)
                                                        .addComponent(name_tf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                                                        .addComponent(desc_tf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                                                        .addComponent(code_tf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)))
                                        .addComponent(jLabel1))
                                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(url_lb)
                                                .addGap(26, 26, 26))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel7)
                                                        .addComponent(server3_radio)
                                                        .addComponent(server2_radio))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(parent_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(comTypePnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel8)
                                                .addGap(18, 18, 18)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(server_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(table_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(name_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(desc_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(code_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_btnActionPerformed
        onOk();
    }//GEN-LAST:event_save_btnActionPerformed

    private void table_comboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_table_comboItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            BeanDto dto = (BeanDto) table_combo.getSelectedItem();
            ModuleType moduleType = getModuleType();
            switch (moduleType) {
                case COMMON:
                case COMMON_UPDATE:
                    name_tf.setText(dto.getValue("table_desc"));
                    break;
                case SERVICE:
                case BUTTON:
                    name_tf.setText(dto.getValue("key"));
                    break;
            }
        }
    }//GEN-LAST:event_table_comboItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btn_radio;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancel_btn;
    private javax.swing.JTextField code_tf;
    private javax.swing.JPanel comTypePnl;
    private javax.swing.JRadioButton commonUpdate_radio;
    private javax.swing.JRadioButton common_radio;
    private javax.swing.JTextField desc_tf;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JTextField name_tf;
    private javax.swing.JTextField parent_tf;
    private javax.swing.JButton save_btn;
    private javax.swing.JRadioButton server2_radio;
    private javax.swing.JRadioButton server3_radio;
    private javax.swing.JComboBox server_combo;
    private javax.swing.JRadioButton service_radio;
    private javax.swing.JComboBox table_combo;
    private javax.swing.JLabel url_lb;
    // End of variables declaration//GEN-END:variables
}
