/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.gui;

import com.google.common.base.Strings;
import youngfriend.bean.BeanDto;
import youngfriend.utils.PubUtil;

import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author xiong
 */
public class UpdateDialog extends javax.swing.JDialog {


    private final boolean ismodule;
    private List<String> codes;
    private String parent_code;

    public static boolean check(Window parent, java.util.List<String> codes, String parent_code, JTextField code_tf, JTextField name_tf) {
        if (Strings.isNullOrEmpty(name_tf.getText().trim())) {
            PubUtil.showMsg(parent, "名称不能空");
            name_tf.requestFocus();
            return false;
        }
        if (parent_code != null) {
            String code = code_tf.getText().trim();
            if (!code.startsWith(parent_code)) {
                PubUtil.showMsg(parent, "请设置正确的代码");
                code_tf.requestFocus();
                return false;
            }
            if (code.length() != parent_code.length() + 2) {
                PubUtil.showMsg(parent, "代码必须为父节点代码长度加2");
                code_tf.requestFocus();
                return false;
            }
            if (codes.contains(code)) {
                PubUtil.showMsg(parent, "代码重复");
                code_tf.requestFocus();
                return false;
            }
        }

        return true;
    }

    public static java.util.List<String> getSortCode(DefaultMutableTreeNode node) {
        Enumeration<DefaultMutableTreeNode> children = node.children();
        java.util.List<String> codes = new ArrayList();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode defaultMutableTreeNode = children.nextElement();
            BeanDto dto = (BeanDto) defaultMutableTreeNode.getUserObject();
            String code = dto.getValue("code");
            if (!Strings.isNullOrEmpty(code)) {
                codes.add(code);
            }
        }
        return codes;
    }

    public String getCode() {
        return code_tf.getText().trim();
    }

    public static int getSortIndex(DefaultMutableTreeNode node, String code, java.util.List<String> codes) {
        int index = node.getChildCount();
        if (!codes.isEmpty()) {
            for (int i = codes.size() - 1; i >= 0; i--) {
                if (code.compareTo(codes.get(i)) < 1) {
                    index--;
                }
            }
        }
        return index;
    }

    private boolean ok = false;

    /**
     * Creates new form UpdateDialog
     */
    public UpdateDialog(Window parent, boolean ismodule) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        setLocationRelativeTo(parent);
        PubUtil.registerDlgBtn(this, jButton1, jButton2, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ok = false;
                dispose();
            }
        });
        this.ismodule = ismodule;
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onOk();

            }
        });
        if (ismodule) {
            alias_tf.setText("");
            alias_tf.setEditable(false);
            alias_tf.setEnabled(false);
        }
    }

    public void showDlg(String parent_code, BeanDto dto, java.util.List<String> codes) {
        ok = false;
        code_tf.setText(dto.getValue("code"));
        if (parent_code != null) {
            this.codes = codes;
            this.parent_code = parent_code;
        } else {
            code_tf.setEditable(false);
        }
        setVisible(true);
    }

    private void onOk() {
        if (!check(this, codes, parent_code, code_tf, name_tf)) {
            return;
        }
        ok = true;
        dispose();
    }

    public boolean isOk() {
        return ok;
    }

    public String getNameValue() {
        return name_tf.getText().trim();
    }

    public void setNameValue(String name) {
        name_tf.setText(name);
    }

    public String getDesc() {
        return desc_tf.getText().trim();
    }

    public void setDesc(String desc) {
        desc_tf.setText(desc);
    }

    public String getAlias() {
        return alias_tf.getText().trim();
    }

    public void setAlias(String alias) {
        alias_tf.setText(alias);
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
                jPanel2 = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                name_tf = new javax.swing.JTextField();
                jLabel2 = new javax.swing.JLabel();
                desc_tf = new javax.swing.JTextField();
                jLabel3 = new javax.swing.JLabel();
                alias_tf = new javax.swing.JTextField();
                jLabel4 = new javax.swing.JLabel();
                code_tf = new javax.swing.JTextField();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setResizable(false);

                jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

                jButton1.setText("确定");
                jPanel1.add(jButton1);

                jButton2.setText("取消");
                jPanel1.add(jButton2);

                getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

                jLabel1.setText("名称");

                jLabel2.setText("描述");

                jLabel3.setText("别名");

                jLabel4.setText("排序代码");

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(code_tf)
                                        .addComponent(alias_tf)
                                        .addComponent(desc_tf)
                                        .addComponent(name_tf, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
                                .addGap(33, 33, 33))
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(name_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(desc_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(alias_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(code_tf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                );

                getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

                pack();
        }// </editor-fold>//GEN-END:initComponents


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JTextField alias_tf;
        private javax.swing.JTextField code_tf;
        private javax.swing.JTextField desc_tf;
        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JTextField name_tf;
        // End of variables declaration//GEN-END:variables
}
