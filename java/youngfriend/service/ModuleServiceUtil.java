package youngfriend.service;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.encoding.Base64;
import youngfriend.common.util.net.exception.ServiceInvokerException;
import youngfriend.utils.ModuleType;

import java.util.Hashtable;

import static youngfriend.moduletree.ModuleTreePnl.MODULE_TOSTRING;
import static youngfriend.moduletree.ModuleTreePnl.MODULE_TYPE_PROP;

/**
 * Created by xiong on 9/21/16.
 */
public class ModuleServiceUtil {
    public static final String COMMOM_CALL = "/datasource/commonsimple.do";
    public static final String SERVICE_CALL = "/datasource/customservicedatasource.do";
    public static final String BTN_CALL = "/datasource/commonbuttonevent.do";

    public static String saveModule(String id, String projectcode, String name, String desc,//
                                    String alias, String jsonData, String serververson, ModuleType moduleType) throws ServiceInvokerException {
        JsonArray dataArray = new JsonArray();
        JsonObject object = new JsonObject();
        if (!Strings.isNullOrEmpty(id)) {
            object.addProperty("id", id);
        }
        if (moduleType != null) {
            switch (moduleType) {
                case COMMON:
                    object.addProperty("callparam", COMMOM_CALL);
                    break;
                case SERVICE:
                    object.addProperty("callparam", SERVICE_CALL);
                    break;
                case BUTTON:
                case COMMON_UPDATE:
                    object.addProperty("callparam", BTN_CALL);
                    break;
            }
        }

        if (serververson != null) {
            object.addProperty("typed", serververson);
        }

        if (name != null) {
            object.addProperty("name", name);
        }
        //modulealias保存
        object.addProperty("modulealias", alias);
        if (moduleType != null) {
            object.addProperty(MODULE_TYPE_PROP, moduleType.getValue());
        }
        if (desc != null) {
            object.addProperty("description", desc);
        }
        if (jsonData != null) {
            object.addProperty("inparam", Base64.encode(jsonData.getBytes()));
        }
        dataArray.add(object);
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.add");
        tab.put("projectcode", projectcode);
        String data = dataArray.toString();
        tab.put("data", data);
        tab = ServiceInvoker.serviceInvoke(tab);
        return tab.get("redata");
    }


    public static BeanDto moduleCopy(String catalogid, String parentCode) throws ServiceInvokerException {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("service", "module2.module.copy");
        map.put("catalogid", catalogid);
        map.put("parentCode", parentCode);
        map = ServiceInvoker.serviceInvoke(map);
        String catalog = map.get("catalog");
        JsonElement cataLogElement = new JsonParser().parse(catalog);
        BeanDto dto = new BeanDto(cataLogElement.getAsJsonObject(), MODULE_TOSTRING);
        dto.setItem("ismodule", "true");
        return dto;
    }


    public static String getModule(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.get");
        tab.put("id", moduleid);
        tab = ServiceInvoker.serviceInvoke(tab);
        return tab.get("redata");
    }

    public static void delModule(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.del");
        tab.put("ids", moduleid);
        ServiceInvoker.serviceInvoke(tab);
    }

}
