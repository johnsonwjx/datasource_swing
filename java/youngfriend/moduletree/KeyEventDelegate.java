package youngfriend.moduletree;

import youngfriend.moduletree.menus.CatalogMenuUtil;
import youngfriend.moduletree.menus.ModuleMenuUtil;

import javax.swing.JTree;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by xiong on 9/21/16.
 */
public class KeyEventDelegate {
    private ModuleTreePnl moduleTreePnl;
    private ModuleMenuUtil moduleMenuUtil;
    private CatalogMenuUtil catalogMenuUtil;

    public KeyEventDelegate(final ModuleTreePnl moduleTreePnl) {
        this.moduleTreePnl = moduleTreePnl;
        moduleMenuUtil = new ModuleMenuUtil(moduleTreePnl);
        catalogMenuUtil = new CatalogMenuUtil(moduleTreePnl);
        JTree moduleTree = moduleTreePnl.getModuleTree();
        moduleTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                boolean module = moduleTreePnl.isModule();
                moduleMenuUtil.updateSelectNode();
                catalogMenuUtil.updateSelectNode();
                if (keyCode == KeyEvent.VK_DELETE) {
                    if (module) moduleMenuUtil.remove();
                    else catalogMenuUtil.remove();
                }
            }
        });
    }
}
