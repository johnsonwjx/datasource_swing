package youngfriend.main_pnl.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.gui.ListDlg;
import youngfriend.main_pnl.MainHeaderPnl;
import youngfriend.main_pnl.deleagte.InparamTableDelegateAbs;
import youngfriend.main_pnl.deleagte.OutParamTableDeletate;
import youngfriend.main_pnl.utils.GetInparamFieldsUtil;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceType;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static youngfriend.main_pnl.MainPnlFactory.TABLE_NAME;


/**
 * 操作主界面父类
 *
 * @author xiong
 */
public abstract class AbstractMainPnl extends JPanel {
    //TODO 抽出接口
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    protected OutParamTableDeletate outParamTableDeletate;

    protected InparamTableDelegateAbs inparamTableDeletage;

    private JCheckBox readOnlyCb;

    private MainHeaderPnl mainHeaderPnl;
    private BeanDto moduleInfoBean;

    /**
     * ----------------------------------------------------
     * 暴露方法
     * ----------------------------------------------------
     */

    public JTable getTable() {
        return inparamTableDeletage.getTable();
    }


    public void saveParam(String modulelabel, JsonObject jsonData) {
        jsonData.addProperty("readOnly", readOnlyCb.isSelected() ? "true" : "false");
        String tablename = mainHeaderPnl.getTablename();
        //入口参数表格保存处理
        inparamTableDeletage.save(jsonData);
        JsonArray inparamLevel2 = inparamTableDeletage.getInparamLevel2();
        //保存 inparamLevel1 fieldinParams也被保存成inParams
        JsonObject inparamLevel1 = saveInorOutParamLevel1(tablename, modulelabel, jsonData, inparamLevel2, true);
        saveInparamLevel2Custom(inparamLevel1);
        JsonArray outParamsLevel2 = outParamTableDeletate.getOutParamsLevel2();
        if (outParamsLevel2 != null) {
            saveInorOutParamLevel1(tablename, modulelabel, jsonData, outParamsLevel2, false);
        }
        jsonData.addProperty(TABLE_NAME, tablename);
        jsonData.addProperty("hiddenfields", "");
    }

    public abstract void loadData(JsonObject jsonData) throws Exception;


    /**
     * ----------------------------------------------------------------------------
     * load方法
     * ----------------------------------------------------------------------------
     */

    protected Map<String, JsonObject> getInParamFieldMap(JsonObject inparamLevel1) {
        JsonArray fieldArray = PubUtil.getJsonObj(inparamLevel1, InparamTableDelegateAbs.INPARAMS_PROPNAME, JsonArray.class);
        if (fieldArray != null) {
            HashMap<String, JsonObject> inParamFieldMap = new HashMap<String, JsonObject>(fieldArray.size());
            for (JsonElement fieldEle : fieldArray) {
                JsonObject fieldObj = fieldEle.getAsJsonObject();
                String name = PubUtil.getProp(fieldObj, "name");
                inParamFieldMap.put(name, fieldObj);
            }
            return inParamFieldMap;
        }
        return null;
    }

    protected ListDlg fieldListDlg = new ListDlg(App.instance);
    protected List<BeanDto> fields = new ArrayList<BeanDto>();

    /**
     * 表格 选择 初始化 入口参数表字段 , 选择字段fieldListDlg,fields
     *
     * @param moduleInfoBean
     * @param mainHeaderPnl
     * @param commonModule
     */
    public void tableSelect(BeanDto moduleInfoBean, MainHeaderPnl mainHeaderPnl, boolean commonModule, boolean isVersion2) {
        this.mainHeaderPnl = mainHeaderPnl;
        this.moduleInfoBean = moduleInfoBean;
        BeanDto tableBean = mainHeaderPnl.getTableBean();
        if (tableBean == null) {
            inparamTableDeletage.clear();
            //清空字段
            fieldListDlg.clear();
            fields.clear();

        } else {
            try {
                JTable table = inparamTableDeletage.getTable();
                if (!commonModule) {
                    GetInparamFieldsUtil.initTableDataService(table, tableBean, fieldListDlg, fields, isVersion2);
                } else {
                    ServiceType servicetype = mainHeaderPnl.getServicetype();
                    if (servicetype == ServiceType.SERVICE2_CODECENTER) {
                        GetInparamFieldsUtil.initTableDataCodetableServer2(table, tableBean, fieldListDlg, fields);
                    } else {
                        Map<BeanDto, List<Element>> tablesMap = mainHeaderPnl.getTablesMap();
                        //Service3 Service2 都通用 这个 取字段了
                        GetInparamFieldsUtil.initTableDataCommon(table, tablesMap, tableBean, fieldListDlg, fields);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("获取表格数据失败" + e.getMessage());
            }
        }
    }


    /**
     * 公共导入数据
     *
     * @param inparamObj 数据
     * @param readOnlyCb
     * @return inparamLevel1 第一层
     * @throws Exception
     */
    protected JsonObject commomLoadData(JsonObject inparamObj, JCheckBox readOnlyCb) throws Exception {
        readOnlyCb.setSelected("true".equals(PubUtil.getProp(inparamObj, "readOnly")));
        JsonObject inparamLevel1 = PubUtil.getJsonObj(inparamObj, InparamTableDelegateAbs.INPARAM_PROPNAME, JsonArray.class).get(0).getAsJsonObject();
        outParamTableDeletate.load(inparamObj);
        return inparamLevel1;
    }


    /**
     * 初始化 文本框的内容
     *
     * @param inparamObj
     * @param comKeyMap
     */
    protected void commondLoadJTextField(JsonObject inparamObj, Map<JTextField, String> comKeyMap) {
        Set<JTextField> keySet = comKeyMap.keySet();
        for (JTextField tf : keySet) {
            String prop = comKeyMap.get(tf);
            String value = PubUtil.getProp(inparamObj, prop);
            tf.setText(value);
        }
    }

    /**
     * UI控件完成后
     */
    protected void afterUi(final JTable outTable, JButton outParamsAddBtn, JButton outParamsDelBtn, JCheckBox readOnlyCb) {
        //公共共同控件
        this.readOnlyCb = readOnlyCb;
        outParamTableDeletate = new OutParamTableDeletate(outTable, outParamsAddBtn, outParamsDelBtn);
    }

    public boolean checkValidate() {
        return true;
    }

    public void clear() {
    }


    /**
     * ----------------------------------------------------------------------------
     * 保存方法
     * ----------------------------------------------------------------------------
     */


    private JsonObject saveInorOutParamLevel1(String tablename, String modulelabel, JsonObject jsonData, JsonArray fieldinParams, boolean isInparam) {
        JsonObject inparamLevel1 = new JsonObject();
        JsonArray inparamArr = new JsonArray();
        inparamLevel1.addProperty("propertyDefine", "{}");
        inparamLevel1.addProperty("name", tablename);
        inparamLevel1.addProperty("label", modulelabel);

        inparamLevel1.addProperty("moduleid", moduleInfoBean.getValue("id"));
        inparamLevel1.addProperty(isInparam ? "inParamType" : "outParamType", "definite");
        inparamLevel1.add(isInparam ? "inParams" : "outParams", fieldinParams);
        inparamLevel1.addProperty("maxLevel", "20");
        inparamLevel1.addProperty("maxLength", "64");
        inparamArr.add(inparamLevel1);
        jsonData.add(isInparam ? "inparam" : "outparam", inparamArr);
        return inparamLevel1;
    }


    /**
     * inparamLevel1 自定义保存,各自特殊处理
     *
     * @param inparamLevel1
     */
    abstract void saveInparamLevel2Custom(JsonObject inparamLevel1);

}
