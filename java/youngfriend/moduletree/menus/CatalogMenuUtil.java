package youngfriend.moduletree.menus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.moduletree.ModuleTreePnl;
import youngfriend.moduletree.TreeUtil;
import youngfriend.moduletree.menus.gui.NewCatalogDlg;
import youngfriend.moduletree.menus.gui.UpdateDialog;
import youngfriend.service.CatalogServiceUtil;
import youngfriend.utils.PubUtil;

import javax.swing.tree.DefaultMutableTreeNode;

import static youngfriend.moduletree.ModuleTreePnl.CATALOG_TOSTRING;

/**
 * Created by xiong on 9/20/16.
 */
public class CatalogMenuUtil extends AbsMenuUtil {
    public CatalogMenuUtil(ModuleTreePnl moduleTreePnl) {
        super(moduleTreePnl);
    }

    public void add() {
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
            String data = CatalogServiceUtil.saveCatalog(null, obj.get("name").getAsString(), obj.get("code").getAsString(),//
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
            TreeUtil.selectNode(moduleTree, node);
        } catch (ServiceInvokerException e) {
            logger.error(e.getMessage(), e);
            PubUtil.showMsg("新增失败");
        }
    }

    public void remove() {
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
            CatalogServiceUtil.delCatalog(moduleCatalogBean.getValue("id"));
            moduleTreeModel.removeNodeFromParent(selectNode);
        } catch (ServiceInvokerException e) {
            logger.error(e.getMessage(), e);
            PubUtil.showMsg("删除失败");
        }
    }

    public void edit() {
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
            CatalogServiceUtil.saveCatalog(moduleCatalogBean.getValue("id"), nameValue, null, desc, alias, projectId, null);
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
}
