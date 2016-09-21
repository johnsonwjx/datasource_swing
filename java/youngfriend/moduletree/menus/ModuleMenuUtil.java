package youngfriend.moduletree.menus;

import com.google.gson.JsonObject;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.main_pnl.MainPnlFactory;
import youngfriend.moduletree.ModuleTreePnl;
import youngfriend.moduletree.menus.gui.NewModuleDlg;
import youngfriend.moduletree.menus.gui.UpdateDialog;
import youngfriend.service.CatalogServiceUtil;
import youngfriend.service.ModuleServiceUtil;
import youngfriend.utils.PubUtil;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.List;

import static youngfriend.moduletree.ModuleTreePnl.MODULE_TOSTRING;

/**
 * Created by xiong on 9/20/16.
 */
public class ModuleMenuUtil extends AbsMenuUtil {
    public ModuleMenuUtil(ModuleTreePnl moduleTreePnl) {
        super(moduleTreePnl);
    }


    public void save() {
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

    public void remove() {
        if (!moduleTreePnl.isModule()) {
            PubUtil.showMsg("请选择删除节点");
            return;
        }
        if (!PubUtil.showConfirm(App.instance, "确定删除吗?")) {
            return;
        }
        try {
            if (moduleInfoBean != null) {
                ModuleServiceUtil.delModule(moduleInfoBean.getValue("id"));
            } else {
                CatalogServiceUtil.delCatalog(moduleCatalogBean.getValue("id"));
            }
            moduleTreeModel.removeNodeFromParent(selectNode);
        } catch (ServiceInvokerException e) {
            logger.error(e.getMessage(), e);
            PubUtil.showMsg("删除失败");
        }
    }

    public void copy() {
        String id = moduleCatalogBean.getValue("id");
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
        BeanDto parentDto = (BeanDto) parentNode.getUserObject();
        String parentCode = parentDto.getValue("code");
        try {
            BeanDto dto = ModuleServiceUtil.moduleCopy(id, parentCode);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
            parentNode.add(node);
            moduleTreeModel.nodeStructureChanged(parentNode);
        } catch (ServiceInvokerException e) {
            PubUtil.showMsg("复制失败");
            logger.error(e.getMessage(), e);
        }

    }

    public void edit() {
        UpdateDialog dialog = new UpdateDialog(App.instance, true);
        dialog.setNameValue(moduleCatalogBean.getValue("name"));
        if (moduleInfoBean != null) {
            dialog.setDesc(moduleInfoBean.getValue("description"));
        } else {
            dialog.setDesc(moduleCatalogBean.getValue("description"));
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectNode.getParent();
        BeanDto parent_dto = (BeanDto) parentNode.getUserObject();
        List<String> codes = UpdateDialog.getSortCode(parentNode);
        String pre_code = moduleCatalogBean.getValue("code");
        codes.remove(pre_code);
        dialog.showDlg(parent_dto.getValue("code"), moduleCatalogBean, codes);
        if (!dialog.isOk()) {
            return;
        }
        String nameValue = dialog.getNameValue();
        String desc = dialog.getDesc();
        String alias = dialog.getAlias();
        String code = dialog.getCode();
        try {
            if (moduleInfoBean != null) {
                JsonObject jsonData = MainPnlFactory.getInputParamObj(moduleInfoBean);
                if (jsonData != null) {
                    JsonObject inparam = jsonData.get("inparam").getAsJsonArray().get(0).getAsJsonObject();
                    if (!nameValue.equals(PubUtil.getProp(inparam, "label"))) {
                        inparam.addProperty("label", nameValue);
                    } else {
                        jsonData = null;
                    }
                }
                ModuleServiceUtil.saveModule(moduleInfoBean.getValue("id"), projectId, nameValue, desc, alias, jsonData == null ? null : jsonData.toString(), null, null);
                moduleInfoBean.setItem("name", nameValue);
                moduleInfoBean.setToString("name");
                moduleInfoBean.setItem("description", desc);
                moduleInfoBean.setItem("modulealias", alias);
            }
            CatalogServiceUtil.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, code, desc, null, projectId, null);
            moduleCatalogBean.setItem("name", nameValue);
            moduleCatalogBean.setItem("code", code);
            moduleCatalogBean.setToString(MODULE_TOSTRING);
            if (!pre_code.equals(code)) {
                int sortIndex = UpdateDialog.getSortIndex(parentNode, code, codes);
                if (sortIndex > 0) {
                    sortIndex--;
                }
                parentNode.insert(selectNode, sortIndex);
                moduleTree.setSelectionPath(new TreePath(moduleTreeModel.getPathToRoot(selectNode)));
                SwingUtilities.updateComponentTreeUI(moduleTree);
            } else {
                moduleTreeModel.nodeChanged(selectNode);
            }
            moduleTreePnl.reLoadData();
        } catch (ServiceInvokerException e) {
            logger.error(e.getMessage(), e);
            PubUtil.showMsg("保存失败");
        }
    }


}
