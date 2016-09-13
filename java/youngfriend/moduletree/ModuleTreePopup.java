package youngfriend.moduletree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.gui.NewCatalogDlg;
import youngfriend.gui.NewModuleDlg;
import youngfriend.gui.SortCatalogDlg;
import youngfriend.gui.UpdateDialog;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceInvoker;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static youngfriend.moduletree.ModuleTreePnl.CATALOG_TOSTRING;

/**
 * @author xiong
 */
public class ModuleTreePopup {
    private static final Logger logger = LoggerFactory.getLogger(ModuleTreePopup.class);
    private JPopupMenu menu = new JPopupMenu("菜单");
    private ModuleTreePnl moduleTreePnl;

    private DefaultMutableTreeNode selectNode;
    private BeanDto moduleCatalogBean;
    private DefaultTreeModel moduleTreeModel;
    private JTree moduleTree;
    private BeanDto moduleInfoBean;
    private String projectId;


    private void moduleAddSaveItem() {
        final JMenuItem saveItem = new JMenuItem("新增组件");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    List<String> codes = UpdateDialog.getSortCode(selectNode);
                    NewModuleDlg dlg = new NewModuleDlg(moduleTreePnl);
                    dlg.showDlg(moduleCatalogBean, codes);
                    if (!dlg.isOk()) {
                        return;
                    }
                    BeanDto dto = dlg.getDto();
                    //排序
                    int index = UpdateDialog.getSortIndex(selectNode, dto.getValue("code"), codes);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
                    selectNode.insert(node, index);
                    moduleTreeModel.reload(selectNode);
                    TreeNode[] pathToRoot = moduleTreeModel.getPathToRoot(node);
                    TreePath treePath = new TreePath(pathToRoot);
                    moduleTree.setSelectionPath(treePath);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("保存失败");
                }
            }
        });
    }

    private void moduleRemoveItem() {
        final JMenuItem removeItem = new JMenuItem("删除组件");
        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moduleCatalogBean == null && moduleInfoBean == null) {
                    PubUtil.showMsg("请选择删除节点");
                    return;
                }
                if (!PubUtil.showConfirm(App.instance, "确定删除吗?")) {
                    return;
                }
                try {
                    if (moduleInfoBean != null) {
                        ServiceInvoker.delModule(moduleInfoBean.getValue("id"));
                    } else {
                        ServiceInvoker.delCatalog(moduleCatalogBean.getValue("id"));
                    }
                    moduleTreeModel.removeNodeFromParent(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("删除失败");
                }
            }
        });
    }

//    private void moduleEditItem() {
//        final JMenuItem editModuleItem = new JMenuItem("修改组件");
//        editModuleItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                UpdateDialog dialog = new UpdateDialog(App.instance, true);
//                dialog.setNameValue(moduleCatalogBean.getValue("name"));
//                if (moduleInfoBean != null) {
//                    dialog.setDesc(moduleInfoBean.getValue("description"));
//                } else {
//                    dialog.setDesc(moduleCatalogBean.getValue("description"));
//                }
//                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
//                BeanDto parent_dto = (BeanDto) parentNode.getUserObject();
//                List<String> codes = UpdateDialog.getSortCode(parentNode);
//                String pre_code = moduleCatalogBean.getValue("code");
//                codes.remove(pre_code);
//                dialog.showDlg(parent_dto.getValue("code"), moduleCatalogBean, codes);
//                if (!dialog.isOk()) {
//                    return;
//                }
//                String nameValue = dialog.getNameValue();
//                String desc = dialog.getDesc();
//                String alias = dialog.getAlias();
//                String code = dialog.getCode();
//                try {
//                    if (moduleInfoBean != null) {
//                        JsonObject jsonData = getInputParamObj(moduleInfoBean);
//                        if (jsonData != null) {
//                            JsonObject inparam = jsonData.get("inparam").getAsJsonArray().get(0).getAsJsonObject();
//                            if (!nameValue.equals(PubUtil.getProp(inparam, "label"))) {
//                                inparam.addProperty("label", nameValue);
//                            } else {
//                                jsonData = null;
//                            }
//                        }
//                        ServiceInvoker.saveModule(moduleInfoBean.getValue("id"), projectId, nameValue, desc, alias, jsonData == null ? null : jsonData.toString(), null, null);
//                        moduleInfoBean.setItem("name", nameValue);
//                        moduleInfoBean.setToString("name");
//                        moduleInfoBean.setItem("description", desc);
//                        moduleInfoBean.setItem("modulealias", alias);
//                    }
//                    ServiceInvoker.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, code, desc, null, projectId, null);
//                    moduleCatalogBean.setItem("name", nameValue);
//                    moduleCatalogBean.setItem("code", code);
//                    moduleCatalogBean.setToString(MODULE_TOSTRING);
//                    if (!pre_code.equals(code)) {
//                        int sortIndex = UpdateDialog.getSortIndex(parentNode, code, codes);
//                        if (sortIndex > 0) {
//                            sortIndex--;
//                        }
//                        parentNode.insert(selectNode, sortIndex);
//                        moduleTree.setSelectionPath(new TreePath(moduleTreeModel.getPathToRoot(selectNode)));
//                        SwingUtilities.updateComponentTreeUI(moduleTree);
//                    } else {
//                        moduleTreeModel.nodeChanged(selectNode);
//                    }
//                    reload_btnActionPerformed(null);
//                } catch (ServiceInvokerException e) {
//                    logger.error(e.getMessage(), e);
//                    PubUtil.showMsg("保存失败");
//                }
//            }
//        });
//    }

    private void moduleCopyItem() {
        final JMenuItem copyModuleItem = new JMenuItem("复制组件");

        copyModuleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String id = moduleCatalogBean.getValue("id");
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
                BeanDto parentDto = (BeanDto) parentNode.getUserObject();
                String parentCode = parentDto.getValue("code");
                try {
                    BeanDto dto = ServiceInvoker.moduleCopy(id, parentCode);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
                    parentNode.add(node);
                    moduleTreeModel.nodeStructureChanged(parentNode);
                } catch (ServiceInvokerException e) {
                    PubUtil.showMsg("复制失败");
                    logger.error(e.getMessage(), e);
                }


            }
        });
    }

    private void catalogAddItem() {
        final JMenuItem addCatalogItem = new JMenuItem("新增目录");
        addCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectNode == null) {
                    PubUtil.showMsg("请选择目录");
                    return;
                }
                NewCatalogDlg newCatalogDlg = new NewCatalogDlg(App.instance);
                java.util.List<String> codes = UpdateDialog.getSortCode(selectNode);
                newCatalogDlg.showDlg(moduleCatalogBean, codes);
                if (!newCatalogDlg.isOk()) {
                    return;
                }
                try {
                    JsonObject obj = newCatalogDlg.getObj();
                    String data = ServiceInvoker.saveCatalog(null, obj.get("name").getAsString(), obj.get("code").getAsString(),//
                            obj.get("description").getAsString(), obj.get("catalogalias").getAsString(), projectId, null);
                    JsonElement jsonElement = PubUtil.parseJson(data);
                    if (jsonElement == null) {
                        throw new RuntimeException("未知错误");
                    }
                    JsonArray asJsonArray = jsonElement.getAsJsonArray();
                    JsonObject jsonElement1 = asJsonArray.get(0).getAsJsonObject();
                    String id = jsonElement1.get("id").getAsString();
                    String code = jsonElement1.get("code").getAsString();

                    //排序
                    int index = UpdateDialog.getSortIndex(selectNode, code, codes);

                    obj.addProperty("id", id);
                    obj.addProperty("code", code);
                    BeanDto dto = new BeanDto(obj, CATALOG_TOSTRING);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, true);
                    selectNode.insert(node, index);
                    moduleTreeModel.reload(selectNode);
                    TreeNode[] pathToRoot = moduleTreeModel.getPathToRoot(node);
                    TreePath treePath = new TreePath(pathToRoot);
                    moduleTree.setSelectionPath(treePath);
                    moduleTree.expandPath(treePath);
                    moduleTree.collapsePath(treePath);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("新增失败");
                }
            }
        });
    }

    private void catalogRemoveItem() {
        final JMenuItem removeCatalogItem = new JMenuItem("删除目录");
        removeCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moduleCatalogBean == null) {
                    PubUtil.showMsg("请选择删除目录");
                    return;
                }
                if (selectNode.getChildCount() > 0) {
                    PubUtil.showMsg("请删除子节点");
                    return;
                }
                if (!PubUtil.showConfirm(App.instance, "确定删除吗?")) {
                    return;
                }
                try {
                    ServiceInvoker.delCatalog(moduleCatalogBean.getValue("id"));
                    moduleTreeModel.removeNodeFromParent(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("删除失败");
                }
            }
        });
    }

    private void catalogEditItem() {
        final JMenuItem editCatalogItem = new JMenuItem("修改目录");
        editCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateDialog dialog = new UpdateDialog(App.instance, false);
                dialog.setNameValue(moduleCatalogBean.getValue("name"));
                dialog.setDesc(moduleCatalogBean.getValue("description"));
                dialog.setAlias(moduleCatalogBean.getValue("catalogalias"));
                dialog.showDlg(null, moduleCatalogBean, null);
                if (!dialog.isOk()) {
                    return;
                }
                String nameValue = dialog.getNameValue();
                String desc = dialog.getDesc();
                String alias = dialog.getAlias();
                try {
                    ServiceInvoker.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, null, desc, alias, projectId, null);
                    moduleCatalogBean.setItem("name", nameValue);
                    moduleCatalogBean.setItem("description", desc);
                    moduleCatalogBean.setItem("catalogalias", alias);
                    moduleCatalogBean.setToString(CATALOG_TOSTRING);
                    moduleTreeModel.nodeChanged(selectNode);
                } catch (ServiceInvokerException e) {
                    logger.error(e.getMessage(), e);
                    PubUtil.showMsg("保存失败");
                }
            }
        });
    }

    private void nodeMoveItem() {
        final JMenuItem sortCatalogItem = new JMenuItem("移动节点");
        sortCatalogItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) moduleTree.getLastSelectedPathComponent();
                BeanDto userObject = (BeanDto) sourceNode.getUserObject();
                SortCatalogDlg dlg = new SortCatalogDlg(App.instance, moduleTree, userObject);
                dlg.setVisible(true);
                if (!dlg.isOk()) {
                    return;
                }
                BeanDto selectDto = dlg.getSelectDto();
                if (selectDto == null) {
                    return;
                }
                try {
                    ServiceInvoker.sortCatalogTree(userObject.getValue("id"), selectDto.getValue("id"), dlg.getSortType());
//                    reloadtree_btnActionPerformed(null);
                } catch (ServiceInvokerException e) {
                    PubUtil.showMsg("更新错误");
                    logger.error(e.getMessage());
                }
            }
        });
    }
}
//            public ModuleTreePopup() {
//                menu.add(saveItem);
//                menu.add(removeItem);
//                menu.add(editModuleItem);
//                menu.add(copyModuleItem);
//                menu.addSeparator();
//                menu.add(addCatalogItem);
//                menu.add(removeCatalogItem);
//                menu.add(editCatalogItem);
//                menu.add(sortCatalogItem);
//                menu.list
//
//            }
