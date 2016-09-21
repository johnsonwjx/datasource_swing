/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youngfriend.moduletree.menus.gui;

import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.service.CatalogSortType;
import youngfriend.utils.PubUtil;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author xiong
 */
public class MoveNodeDlg extends javax.swing.JDialog {
    private boolean ok = false;

    /**
     * Creates new form TreeSelectDlg
     */
    public MoveNodeDlg(JTree sourceTree) {
        super(App.instance, ModalityType.APPLICATION_MODAL);
        initComponents();
        type_btn_group.add(pre);
        type_btn_group.add(leaf);
        type_btn_group.add(next);
        tree.setModel(sourceTree.getModel());
        tree.setRootVisible(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        PubUtil.registerDlgBtn(this, save_btn, cancel_btn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ok = false;
                dispose();
            }
        });
        save_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ok = true;
                DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                //如果是组件,并且选了子节点
                if (lastSelectedPathComponent.isLeaf() && leaf.isSelected()) {
                    PubUtil.showMsg("不能选择组件并且设为子节点");
                    return;
                }
                dispose();
            }
        });
        setLocationRelativeTo(getOwner());
    }

    public BeanDto getSelectDto() {
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (lastSelectedPathComponent == null) {
            return null;
        }
        return (BeanDto) lastSelectedPathComponent.getUserObject();
    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        type_btn_group = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        save_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        content_scrolpnl = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jPanel3 = new javax.swing.JPanel();
        leaf = new javax.swing.JRadioButton();
        pre = new javax.swing.JRadioButton();
        next = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        save_btn.setText("确定");
        jPanel1.add(save_btn);

        cancel_btn.setText("取消");
        jPanel1.add(cancel_btn);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        content_scrolpnl.setViewportView(tree);

        jPanel2.add(content_scrolpnl, java.awt.BorderLayout.CENTER);

        leaf.setSelected(true);
        leaf.setText("子节点");
        jPanel3.add(leaf);

        pre.setText("节点前");
        jPanel3.add(pre);

        next.setText("节点后");
        jPanel3.add(next);

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_btn;
    private javax.swing.JScrollPane content_scrolpnl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton leaf;
    private javax.swing.JRadioButton next;
    private javax.swing.JRadioButton pre;
    private javax.swing.JButton save_btn;
    private javax.swing.JTree tree;
    private javax.swing.ButtonGroup type_btn_group;

    // End of variables declaration//GEN-END:variables


    public boolean isOk() {
        return ok;
    }

    public CatalogSortType getSortType() {

        CatalogSortType sortType = CatalogSortType.NEXT_SIBLING;
        if (pre.isSelected()) {
            sortType = CatalogSortType.PRE_SIBLING;
        } else if (leaf.isSelected()) {
            sortType = CatalogSortType.LEAF;
        }
        return sortType;
    }
}