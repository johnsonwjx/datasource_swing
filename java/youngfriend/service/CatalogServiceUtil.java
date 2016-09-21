package youngfriend.service;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import youngfriend.common.util.net.exception.ServiceInvokerException;

import java.util.Hashtable;

/**
 * Created by xiong on 9/21/16.
 */
public class CatalogServiceUtil {
    public static void delCatalog(String id) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.catalog.del");
        tab.put("id", id);
        ServiceInvoker.serviceInvoke(tab);
    }

    //    String id = inMessage.getValue("id");
//    String moduleid = inMessage.getValue("moduleid");
//    String code = inMessage.getValue("code");
//    String accessid = inMessage.getValue("accessid");
//    String alias =  inMessage.getValue("alias");
//    String onlyhasmodule = inMessage.getValue("onlyhasmodule");
//    String onlytype = inMessage.getValue("onlytype");
//    String onlycatalog = inMessage.getValue("onlycatalog");
//    String onlymodule = inMessage.getValue("onlymodule");
//    String projectcode = inMessage.getValue("projectcode");
    public static String form_catalog_get(String projectcode) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("projectcode", projectcode);
        sendTab.put("onlycatalog", "T");
        sendTab.put("code", "04");
        Hashtable<String, String> reTab = ServiceInvoker.serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String btnmodule_catalog_get(String projectcode) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("projectcode", projectcode);
        sendTab.put("onlycatalog", "T");
        sendTab.put("code", "06");
        Hashtable<String, String> reTab = ServiceInvoker.serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String module_catalog_get(String id) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("id", id);
        sendTab.put("onlytype", "T");
//        sendTab.put("onlycatalog", "T");
        Hashtable<String, String> reTab = ServiceInvoker.serviceInvoke(sendTab);
        return reTab.get("redata");
    }

    public static String module_catalog_getByModuleid(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("moduleid", moduleid);
        sendTab.put("onlytype", "T");
//        sendTab.put("onlycatalog", "T");
        Hashtable<String, String> reTab = ServiceInvoker.serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String saveCatalog(String id, String name, String code, String description, String catalogalias, String projectcode, String moduleid) throws ServiceInvokerException {
        JsonArray data = new JsonArray();
        JsonObject obj = new JsonObject();
        if (!Strings.isNullOrEmpty(id)) {
            obj.addProperty("id", id);
        }
        if (name != null) {
            obj.addProperty("name", name);
        }
        if (description != null) {
            obj.addProperty("description", description);
        }
        if (code != null) {
            obj.addProperty("code", code);
        }
        if (catalogalias != null) {
            obj.addProperty("catalogalias", catalogalias);
        }
        if (moduleid != null) {
            obj.addProperty("moduleid", moduleid);
        }
        data.add(obj);
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.new");
        if (projectcode != null) {
            sendTab.put("projectcode", projectcode);
        }
        sendTab.put("data", data.toString());
        Hashtable<String, String> reTab = ServiceInvoker.serviceInvoke(sendTab);
        return reTab.get("redata");
    }

    //id aimid type 0-下一级 1-平级上面 2-平级下面）
    public static void sortCatalogTree(String id, String aimid, CatalogSortType sortType) throws ServiceInvokerException {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("service", "module2.catalog.sortcode");
        map.put("id", id);
        map.put("aimid", aimid);
        map.put("type", sortType.getValue());
        ServiceInvoker.serviceInvoke(map);
    }

}
