package youngfriend.utils;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.common.util.encoding.Base64;
import youngfriend.common.util.net.ServiceInvokerUtil;
import youngfriend.common.util.net.exception.ServiceInvokerException;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiong on 15/7/16.
 */
public class ServiceInvoker {
    public static final String COMMOM_CALL = "/datasource/commonsimple.do";
    public static final String SERVICE_CALL = "/datasource/customservicedatasource.do";
    public static final String BTN_CALL = "/datasource/commonbuttonevent.do";
    private static final Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);

    public static Hashtable<String, String> serviceInvoke(Hashtable<String, String> inparam) throws ServiceInvokerException {
        Hashtable<String, String> out = ServiceInvokerUtil.invoker(inparam);
        return out;
    }

    public static String serviceInvoke(Hashtable<String, String> inparam, String url, Boolean webproxy) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String key : inparam.keySet()) {
            sb.append(key).append(" := ").append(inparam.get(key)).append("\n");
        }
        String reMsg = sendData(sb.toString(), url, webproxy);//调用服务并返回值
        if (Strings.isNullOrEmpty(reMsg)) {
            throw new Exception("返回数据为空");
        } else {
            reMsg = reMsg.substring(reMsg.indexOf(":=") + 2).trim();
            if (Strings.isNullOrEmpty(reMsg)) {
                logger.error(reMsg);
                throw new Exception("返回数据异常");
            }
        }
        if (reMsg.indexOf("未找到") != -1 || reMsg.indexOf("服务已停止") != -1) {
            throw new RuntimeException(reMsg);
        }
        return reMsg;
    }

    public static List<BeanDto> getServices2(String url) throws Exception {
        List<BeanDto> services = getServices(url, false);
        PubUtil.serviceBeans_2 = services;
        PubUtil.serviceConfig.addConfig("service_url_2", url);
        PubUtil.serviceConfig.writeConfig();
        return services;
    }

    public static List<BeanDto> getServices(String url, boolean webproxy) throws Exception {
        Hashtable<String, String> inparam = new Hashtable<String, String>();
        inparam.put("service", "system.poperties.serviceslocation");
        inparam.put("xml", "");
        String reMsg = serviceInvoke(inparam, url, webproxy);

        //解析xml数据
        Document doc = DocumentHelper.parseText(reMsg);
        Element root = doc.getRootElement();
        List eLs = root.elements("service");
        List<BeanDto> lst = new ArrayList<BeanDto>();
        for (Iterator iter = eLs.iterator(); iter.hasNext(); ) {
            Element element = (Element) iter.next();
            String key = element.attributeValue("name");
            String cnname = element.attributeValue("cnname");
            String addr = element.attributeValue("addr");
            JsonObject obj = new JsonObject();
            obj.addProperty("name", key);
            obj.addProperty("cnname", cnname);
            obj.addProperty("enname", key);
            obj.addProperty("addr", addr);
            BeanDto dto = new BeanDto(obj, "enname", "cnname");
            lst.add(dto);
        }
        Collections.sort(lst, new Comparator<BeanDto>() {
            @Override
            public int compare(BeanDto o1, BeanDto o2) {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                String name1 = o1.getValue("name");
                String name2 = o2.getValue("name");
                if (Strings.isNullOrEmpty(name1)) {
                    return -1;
                }
                if (Strings.isNullOrEmpty(name2)) {
                    return 1;
                }
                return name1.compareTo(name2);
            }
        });
        return lst;
    }

    /**
     * 调用服务“system.poperties. serviceslocation”读取所有服务的地址映射表,
     * <br>并把服务信息写入系统内存
     *
     * @param serviceUrl  服务器地址
     * @param useWebProxy 是否使用
     * @return 服务的地址映射表
     * @Exception 错误信息
     */
    public static String setAllServiceInfo(String serviceUrl, boolean useWebProxy) throws Exception {
        List<BeanDto> servicebeans = getServices(serviceUrl, useWebProxy);
        PubUtil.serviceBeans.clear();
        for (BeanDto dto : servicebeans) {
            PubUtil.serviceBeans.add(dto);
            //String value = serviceUrl;//用输入的地址作为服务地址
            //因为有可能有服务器集群，所以不能用输入的地址作为服务地址
            String value = "http://" + dto.getValue("addr");
            if (useWebProxy)//使用集群，那样所有服务都用web地址
                value = serviceUrl;
            String key = dto.getValue("name");
            if (!StringUtils.nullOrBlank(key)) {
                System.setProperty(key, value);
            }
        }
        return "ok";
    }

    /**
     * 发送信息到服务器
     *
     * @param msgVar     消息字符串
     * @param serviceUrl 服务地址
     * @return 返回消息结果
     */
    public static String sendData(String msgVar, String serviceUrl, boolean useWebProxy) throws Exception {
        //检验地址的合法性
        if (!serviceUrl.toLowerCase().startsWith("http://"))
            serviceUrl = "http://" + serviceUrl;

        if (useWebProxy)
            serviceUrl = serviceUrl + "/webproxy";
        String output = null;
        URL url = new URL(serviceUrl);
        URLConnection client = url.openConnection();
        client.setDoOutput(true);
        client.getOutputStream().write(msgVar.getBytes());
        client.getOutputStream().flush();
        client.getOutputStream().close();
        //client.connect();
        int dataLen = client.getContentLength();
        if (dataLen > 0) {
            byte[] data = new byte[dataLen];
            int p = 0;
            while (p < dataLen) {
                int r = client.getInputStream().read(data, p, dataLen - p);
                if (r < 0)
                    break;
                p = p + r;
            }
            output = new String(data);
        }
        return output;
    }

    /**
     * 调用服务“useraccess.login”验证用户权限,
     * <br>并把服务信息写入系统内存
     *
     * @param registerName 注册名
     * @param password     密码
     * @param ip           登录IP地址
     * @param computerName 用户机器名
     * @return 服务的地址映射表
     * @Exception 错误信息
     */
    public static String loginSystem(String registerName, String password, String ip, String computerName) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "useraccess3.login");
        sendTab.put("registerName", registerName);
        sendTab.put("password", password);
        sendTab.put("ip", ip);
        sendTab.put("computerName", computerName);
        sendTab.put("keyNum", "");
        sendTab.put("ptype", "client");
        //UserAccess服务地址为空的时候报错
        if (Strings.isNullOrEmpty(System.getProperty("useraccess3")))
            throw new ServiceInvokerException(ServiceInvoker.class, "验证用户失败！", "身份认证服务没有启用！");

        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        //对结果进行解析
        if (reTab == null || reTab.isEmpty() || reTab.size() == 0)
            throw new ServiceInvokerException(ServiceInvoker.class, "验证用户失败！", "服务返回信息为空！");

        String assID = reTab.get("sysAccessID");
        if (!Strings.isNullOrEmpty(assID)) {
            //将系统访问sysAccessID写入内存中
            System.setProperty("sysAccessID", assID);
            PubUtil.accessid = assID;
            //根据系统存储ID取出用户ID,写入内存
            Hashtable<String, String> sendTab2 = new Hashtable<String, String>();
            sendTab2.put("service", "useraccess3.getUserID");
            sendTab2.put("sysAccessID", assID);

            Hashtable<String, String> reTab2 = serviceInvoke(sendTab2);
            String userID = reTab2.get("userID");
            if (!Strings.isNullOrEmpty(userID)) {
                System.setProperty("userID", userID);
            }
            return "ok";
        } else {
            throw new ServiceInvokerException(ServiceInvoker.class, "验证用户失败!", "验证用户权限服务返回消息为空！");
        }

    }

    /**
     * 系统登录
     *
     * @param url
     * @param username
     * @param ps
     * @param webproxy
     * @return
     * @throws Exception
     */
    public static void login(String url, String username, String ps, boolean webproxy) throws Exception {
        ServiceInvoker.setAllServiceInfo(url, webproxy);
        //调用服务“useraccess.login”验证用户权限,并把服务信息写入系统内存
        InetAddress ipAdd = InetAddress.getLocalHost();
        ServiceInvoker.loginSystem(username, ps, ipAdd.getHostAddress(), ipAdd.getHostName());
        //登录成功，
        //保存服务器地址
        System.setProperty("yf_service_url", url);
        //保存用户名
        System.setProperty("yf_username", username);
        //保存是否使用web代理
        System.setProperty("yf_webproxy", webproxy + "");

    }


    public static String getTables(String service) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", service + ".getinittablexml");
        tab = serviceInvoke(tab);
        return tab.get("tableXml");
    }


    public static List<BeanDto> parseTable(String service, String tableXML) throws DocumentException {
        return parseTable(service, tableXML, null);
    }


    public static List<BeanDto> parseTable(String service, String tableXML, Map<BeanDto, List<Element>> tablesMap) throws DocumentException {
        Document document = DocumentHelper.parseText(tableXML);
        List<BeanDto> lst = new ArrayList<BeanDto>();
        if ("codecenter".equals(service)) {
            java.util.List<Element> tableEles = document.getRootElement().elements();
            if (tableEles.isEmpty()) {
                return lst;
            }
            for (Element tableEle : tableEles) {
                java.util.List<Element> paras = tableEle.elements();
                JsonObject object = new JsonObject();
                for (Element para : paras) {
                    object.addProperty(para.getName().toLowerCase(), para.getText());
                }
                String table_name = tableEle.elementText("alias");
                String table_desc = tableEle.elementText("name");
                object.addProperty("table_name", table_name);
                object.addProperty("table_desc", table_desc);
                BeanDto table_dto = new BeanDto(object, "table_desc", "table_name");
                lst.add(table_dto);
            }
        } else {
            java.util.List<Element> tableEles = document.getRootElement().selectNodes("//Table");
            if (tableEles.isEmpty()) {
                return lst;
            }
            for (Element tableEle : tableEles) {
                String table_name = tableEle.elementText("TABLE_NAME");
                String table_desc = tableEle.elementText("TABLE_DESC");
                JsonObject object = new JsonObject();
                object.addProperty("table_name", table_name);
                object.addProperty("table_desc", table_desc);
                BeanDto table_dto = new BeanDto(object, "table_desc", "table_name");
                if (tablesMap != null) {
                    java.util.List<Element> fieldEles = tableEle.elements("Field");
                    tablesMap.put(table_dto, fieldEles);
                }
                lst.add(table_dto);
            }

        }
        return lst;
    }


    /**
     * 2.0 获取服务表格
     *
     * @param service
     * @param url
     * @return
     * @throws Exception
     */
    public static String getTables(String service, String url) throws Exception {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        if (service.equals("codecenter")) {
            tab.put("service", "codecenter.table.getlist");
            tab.put("hasFields", "false");
        } else {
            tab.put("service", service + ".getinittablexml");
        }
        return serviceInvoke(tab, url, false);
    }


    public static List<BeanDto> getDataSource(String service, boolean isverson3, ModuleType moduleType) throws Exception {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", service + ".v3.serverlist");
        tab.put("serviceType", moduleType.getServiceType());
        String json;
        if (isverson3) {
            tab = serviceInvoke(tab);
            json = tab.get("serverlist");
        } else {
            json = serviceInvoke(tab, PubUtil.getService2Url(), false);
        }
        if (json.startsWith("无法定位服务")) {
            PubUtil.showMsg(json);
            return null;
        }
        JsonArray asJsonArray = new JsonParser().parse(json).getAsJsonArray();
        Iterator<JsonElement> iterator = asJsonArray.iterator();
        List<BeanDto> list = new ArrayList<BeanDto>();
        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            JsonObject asJsonObject = next.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = asJsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                BeanDto dto = new BeanDto(value, key);
                list.add(dto);
            }
        }
        return list;
    }

    public static List<BeanDto> getFields(String service, boolean isverson3) throws Exception {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", service);
        tab.put("querytype", "getstructure");
        String json;
        if (isverson3) {
            tab = serviceInvoke(tab);
            if (tab.containsKey("json")) {
                json = tab.get("json");
            } else {
                json = tab.get("out");
            }
        } else {
            json = serviceInvoke(tab, PubUtil.getService2Url(), false);
        }
        JsonArray asJsonArray = new JsonParser().parse(json).getAsJsonArray();
        Iterator<JsonElement> iterator = asJsonArray.iterator();
        List<BeanDto> list = new ArrayList<BeanDto>();
        while (iterator.hasNext()) {
            JsonElement next = iterator.next();
            JsonObject asJsonObject = next.getAsJsonObject();
            BeanDto dto = new BeanDto(asJsonObject, "field_name");
            list.add(dto);
        }
        return list;
    }

    public static List<Element> getCodeFields(String id, String url) throws Exception {
        Hashtable<String, String> in = new Hashtable<String, String>();
        in.put("service", "codecenter.table.get");// moduleID
        in.put("id", id);
        in.put("hasFields", "true");
        String xml = serviceInvoke(in, url, false);
        if (StringUtils.nullOrBlank(xml)) {
            return null;
        }
        Element fields = (Element) DocumentHelper.parseText(xml).selectSingleNode("//codefields");
        if (fields == null) {
            return null;
        }
        return fields.elements("codefield");
    }


    public static String getModule(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.get");
        tab.put("id", moduleid);
        tab = serviceInvoke(tab);
        return tab.get("redata");
    }

    public static void delModule(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.del");
        tab.put("ids", moduleid);
        serviceInvoke(tab);
    }

    public static void delCatalog(String id) throws ServiceInvokerException {
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.catalog.del");
        tab.put("id", id);
        serviceInvoke(tab);
    }


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
        if ((moduleType == ModuleType.COMMON || moduleType == ModuleType.COMMON_UPDATE) && alias != null) {
            object.addProperty("modulealias", alias);
        }
        if (moduleType != null) {
            object.addProperty("typee", moduleType.getValue());
        }
        if (desc != null) {
            object.addProperty("description", desc);
        }
        if (jsonData != null) {
            logger.debug(jsonData);
            object.addProperty("inparam", Base64.encode(jsonData.getBytes()));
        }
        dataArray.add(object);
        Hashtable<String, String> tab = new Hashtable<String, String>();
        tab.put("service", "module2.module.add");
        tab.put("projectcode", projectcode);
        String data = dataArray.toString();
        logger.debug(data);
        tab.put("data", data);
        tab = serviceInvoke(tab);
        return tab.get("redata");
    }


    /**
     * 取项目
     *
     * @return
     * @throws ServiceInvokerException
     */
    public static String designproject_project_get() throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "designproject.project.get");
        sendTab.put("accessid", PubUtil.accessid);
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        return reTab.get("redata");
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
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String btnmodule_catalog_get(String projectcode) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("projectcode", projectcode);
        sendTab.put("onlycatalog", "T");
        sendTab.put("code", "06");
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String module_catalog_get(String id) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("id", id);
        sendTab.put("onlytype", "T");
//        sendTab.put("onlycatalog", "T");
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        return reTab.get("redata");
    }

    public static String module_catalog_getByModuleid(String moduleid) throws ServiceInvokerException {
        Hashtable<String, String> sendTab = new Hashtable<String, String>();
        sendTab.put("service", "module2.catalog.get");
        sendTab.put("moduleid", moduleid);
        sendTab.put("onlytype", "T");
//        sendTab.put("onlycatalog", "T");
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
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
        Hashtable<String, String> reTab = serviceInvoke(sendTab);
        return reTab.get("redata");
    }


    public static String getWork_dic_v6type() throws ServiceInvokerException {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("service", "designproject.simplequery");
        map.put("querysql", "select v6datatype,name from work_datadicv6type");
        Hashtable<String, String> result = serviceInvoke(map);
        return result.get("XML");
    }

    //id aimid type 0-下一级 1-平级上面 2-平级下面）
    public static void sortCatalogTree(String id, String aimid, CatalogSortType sortType) throws ServiceInvokerException {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("service", "module2.catalog.sortcode");
        map.put("id", id);
        map.put("aimid", aimid);
        map.put("type", sortType.getValue());
        serviceInvoke(map);
    }

    public static BeanDto moduleCopy(String catalogid, String parentCode) throws ServiceInvokerException {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("service", "module2.module.copy");
        map.put("catalogid", catalogid);
        map.put("parentCode", parentCode);
        map = serviceInvoke(map);
        String catalog = map.get("catalog");
        JsonElement cataLogElement = new JsonParser().parse(catalog);
        BeanDto dto = new BeanDto(cataLogElement.getAsJsonObject(), App.MODULE_TOSTRING);
        dto.setItem("ismodule", "true");
        return dto;
    }
}
