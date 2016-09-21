package youngfriend.moduletree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by xiong on 9/21/16.
 */
public class TreeUtil {
    public static void selectNode(JTree tree, DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
        tree.repaint();
    }
}
