package youngfriend.moduletree.menus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.moduletree.ModuleTreePnl;
import youngfriend.moduletree.menus.gui.MoveNodeDlg;
import youngfriend.service.CatalogServiceUtil;
import youngfriend.utils.PubUtil;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author xiong
 */
public class ModuleTreePopup {
    private static final Logger logger = LoggerFactory.getLogger(ModuleTreePopup.class);
    private final JTree moduleTree;
    private JPopupMenu menu = new JPopupMenu("菜单");
    private ModuleTreePnl moduleTreePnl;
    private ModuleMenuUtil moduleMenuUtil;
    private CatalogMenuUtil catalogMenuUtil;

    private final JMenuItem saveItem = new JMenuItem("新增组件");
    private final JMenuItem removeItem = new JMenuItem("删除组件");
    private final JMenuItem editModuleItem = new JMenuItem("修改组件");
    private final JMenuItem copyModuleItem = new JMenuItem("复制组件");


    final JMenuItem addCatalogItem = new JMenuItem("新增目录");
    final JMenuItem removeCatalogItem = new JMenuItem("删除目录");
    final JMenuItem editCatalogItem = new JMenuItem("修改目录");

    final JMenuItem moveNodeItem = new JMenuItem("移动节点");

    private void initModuleMenuItems() {
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moduleMenuUtil.save();
            }
        });
        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moduleMenuUtil.remove();
            }
        });
        editModuleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moduleMenuUtil.edit();
            }
        });
        copyModuleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moduleMenuUtil.copy();

            }
        });
        menu.add(saveItem);
        menu.add(removeItem);
        menu.add(editModuleItem);
        menu.add(copyModuleItem);
    }

    private void initCatalogMenuItems() {

        addCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                catalogMenuUtil.add();
            }
        });


        removeCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                catalogMenuUtil.remove();
            }
        });


        editCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                catalogMenuUtil.edit();
            }
        });
        menu.add(addCatalogItem);
        menu.add(removeCatalogItem);
        menu.add(editCatalogItem);
    }

    public ModuleTreePopup(final ModuleTreePnl moduleTreePnl) {
        this.moduleTreePnl = moduleTreePnl;
        moduleTree = moduleTreePnl.getModuleTree();
        moduleMenuUtil = new ModuleMenuUtil(moduleTreePnl);
        catalogMenuUtil = new CatalogMenuUtil(moduleTreePnl);
        initModuleMenuItems();
        menu.addSeparator();
        initCatalogMenuItems();
        nodeMoveItem();
        moduleTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    //make sure has selected a node
                    DefaultMutableTreeNode selectNode = moduleTreePnl.getSelectNode();
                    if (selectNode == null) {
                        return;
                    }
                    int x = mouseEvent.getX();
                    int y = mouseEvent.getY();
                    boolean ismodule = moduleTreePnl.isModule();
                    saveItem.setEnabled(!ismodule);
                    addCatalogItem.setEnabled(!ismodule);
                    removeItem.setEnabled(ismodule);
                    removeCatalogItem.setEnabled(!ismodule);
                    editModuleItem.setEnabled(ismodule);
                    editCatalogItem.setEnabled(!ismodule);
                    copyModuleItem.setEnabled(ismodule);

                    catalogMenuUtil.updateSelectNode();
                    moduleMenuUtil.updateSelectNode();
                    menu.show(moduleTree, x, y);
                }
            }
        });
    }

    private void nodeMoveItem() {
        moveNodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) moduleTree.getLastSelectedPathComponent();
                MoveNodeDlg dlg = new MoveNodeDlg(moduleTree);
                dlg.setVisible(true);
                if (!dlg.isOk()) {
                    return;
                }
                BeanDto selectDto = dlg.getSelectDto();
                if (selectDto == null) {
                    return;
                }
                try {
                    BeanDto userObject = (BeanDto) sourceNode.getUserObject();
                    CatalogServiceUtil.sortCatalogTree(userObject.getValue("id"), selectDto.getValue("id"), dlg.getSortType());
                    moduleTreePnl.reBuildTree();
                } catch (ServiceInvokerException e) {
                    PubUtil.showMsg("更新错误");
                    logger.error(e.getMessage());
                }
            }
        });
        menu.add(moveNodeItem);
    }
}

