package youngfriend.moduletree;

import youngfriend.common.util.StringUtils;

import javax.swing.AbstractButton;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

/**
 * Created by xiong on 9/21/16.
 */
public class SearchNodeDelegate {
    private final JTree tree;
    private DefaultMutableTreeNode rootNode;
    private Enumeration<DefaultMutableTreeNode> enumeration;
    private String searchText;
    private JTextField moduleSearchTf;

    public SearchNodeDelegate(JTree tree, DefaultMutableTreeNode rootNode, JTextField moduleSearchTf, AbstractButton moduleSearchBtn) {
        this.moduleSearchTf = moduleSearchTf;
        this.rootNode = rootNode;
        this.tree = tree;

        moduleSearchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchTreeNode();
            }
        });
        moduleSearchTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchTreeNode();
                }
            }

        });
    }

    private void searchTreeNode() {
        String text = moduleSearchTf.getText().trim();
        if (StringUtils.nullOrBlank(text)) {
            return;
        }
        if (!text.equals(searchText) || !enumeration.hasMoreElements()) {
            enumeration = rootNode.breadthFirstEnumeration();
            searchText = text;
        }
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode current = enumeration.nextElement();
            if (current.toString().contains(text)) {
                TreeUtil.selectNode(tree, current);
                return;
            }
        }
    }
}
