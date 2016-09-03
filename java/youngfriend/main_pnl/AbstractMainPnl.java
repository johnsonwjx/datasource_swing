package youngfriend.main_pnl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.bean.BeanDto;
import youngfriend.gui.ListDlg;
import youngfriend.main_pnl.deleagte.InparamTableDelegateAbs;
import youngfriend.main_pnl.deleagte.OutParamTableDeletate;
import youngfriend.main_pnl.utils.GetInparamFieldsUtil;
import youngfriend.utils.ModuleType;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceInvoker;
import youngfriend.utils.ServiceType;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 操作主界面父类
 *
 * @author xiong
 */
public abstract class AbstractMainPnl extends JPanel {
    //TODO 抽出接口
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //是否通用组件
    protected boolean commonModule = true;


    //保存数据属性
    public static final String INPARAM_PROPNAME = "inparam";
    public static final String INPARAMS_PROPNAME = "inParams";


    public static final String TABLE_NAME = "tablename";

    //数据源 数据表
    String tablebeanProname = "table_name";

    protected BeanDto serviceBean;


    JComboBox table_combo;
    DefaultComboBoxModel table_combo_model;
    protected BeanDto tableBean;


    protected boolean init = false;
    protected ServiceType servicetype = ServiceType.SERVICE3;

    protected OutParamTableDeletate outParamTableDeletate;

    protected InparamTableDelegateAbs inparamTableDeletage;
    protected ListDlg fieldListDlg = new ListDlg(PubUtil.mainFrame);
    protected List<BeanDto> fields = new ArrayList<BeanDto>();
    private JCheckBox readOnlyCb;
    private Map<BeanDto, List<Element>> tablesMap = new HashMap<BeanDto, List<Element>>();


    /**
     * ----------------------------------------------------
     * 暴露方法
     * ----------------------------------------------------
     */
    public JTable getTable() {
        return inparamTableDeletage.getTable();
    }

    public String getModuleAlias() {
        Object selectedItem = table_combo.getSelectedItem();
        if (selectedItem == null) {
            return "";
        }
        BeanDto dto = (BeanDto) selectedItem;
        return dto.getValue(tablebeanProname);
    }

    public void clear() {
        this.serviceBean = null;
        table_combo.removeAllItems();
    }

    public void saveParam(String modulelabel, JsonObject jsonData) {
        jsonData.addProperty("readOnly", readOnlyCb.isSelected() ? "true" : "false");
        String tablename = tableBean.getValue(tablebeanProname);
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


    public boolean checkValidate() {
        if (tableBean == null) {
            PubUtil.showMsg("表格为空,请选择");
            return false;
        }
        return true;
    }

    /**
     * ----------------------------------------------------------------------------
     * load方法
     * ----------------------------------------------------------------------------
     */

    protected Map<String, JsonObject> getInParamFieldMap(JsonObject inparamLevel1) {
        JsonArray fieldArray = PubUtil.getJsonObj(inparamLevel1, INPARAMS_PROPNAME, JsonArray.class);
        if (fieldArray != null) {
            HashMap<String, JsonObject> inParamFieldMap = new HashMap<String, JsonObject>(fieldArray.size());
            for (JsonElement fieldEle : fieldArray) {
                JsonObject fieldObj = fieldEle.getAsJsonObject();
                String name = PubUtil.getProp(fieldObj, "name").toLowerCase();
                inParamFieldMap.put(name, fieldObj);
            }
        }
        return null;
    }


    /**
     * 表格 选择 初始化 入口参数表字段 , 选择字段fieldListDlg,fields
     *
     * @param tablebean
     */
    private void tableSelect(BeanDto tablebean) {
        if (tablebean == null) {
            inparamTableDeletage.clear();
            //清空字段
            fieldListDlg.clear();
            fields.clear();

        } else {
            try {
                JTable table = inparamTableDeletage.getTable();
                if (!commonModule) {
                    GetInparamFieldsUtil.initTableDataService(table, tablebean, fieldListDlg, fields);
                } else {
                    if (servicetype == ServiceType.SERVICE2_CODECENTER) {
                        GetInparamFieldsUtil.initTableDataCodetableServer2(table, tablebean, fieldListDlg, fields);
                    } else {
                        //Service3 Service2 都通用 这个 取字段了
                        GetInparamFieldsUtil.initTableDataCommon(table, tablesMap, tablebean, fieldListDlg, fields);
                    }
                }

            } catch (Exception e) {
                PubUtil.showMsg("获取表格数据错误");
                logger.error(e.getMessage());

            }
        }
        this.tableBean = tablebean;
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

        loadTableBean(inparamObj);
        JsonObject inparamLevel1 = PubUtil.getJsonObj(inparamObj, INPARAM_PROPNAME, JsonArray.class).get(0).getAsJsonObject();

        outParamTableDeletate.load(inparamObj);

        return inparamLevel1;
    }

    /**
     * load入 已选择的 表格
     *
     * @param inparamObj
     */
    private void loadTableBean(JsonObject inparamObj) {
        String tablename = PubUtil.getProp(inparamObj, TABLE_NAME);
        BeanDto tableBeanTemp = PubUtil.getComboItem(table_combo, tablebeanProname, tablename);
        if (tableBeanTemp == null) {
            throw new IllegalArgumentException("没找到已设置表格:" + tablename);
        }
        table_combo.setSelectedItem(tableBeanTemp);
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
    protected void afterUi(final JComboBox table_combo, final JTable outTable, JButton outParamsAddBtn, JButton outParamsDelBtn, JCheckBox readOnlyCb) {
        //公共共同控件
        this.table_combo = table_combo;
        this.readOnlyCb = readOnlyCb;
        table_combo_model = new DefaultComboBoxModel();
        table_combo.setModel(table_combo_model);
        table_combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                    tableSelect(null);
                } else {
                    tableSelect((BeanDto) table_combo.getSelectedItem());
                }

            }
        });
        outParamTableDeletate = new OutParamTableDeletate(outTable, outParamsAddBtn, outParamsDelBtn);

        if (commonModule) {
        } else {
            tablebeanProname = "value";
        }

    }


    public void serviceSelect(BeanDto dto) throws Exception {
        try {
            init = true;
            this.clear();
            tablesMap.clear();
            this.serviceBean = dto;
            if (serviceBean == null) {
                return;
            }
            String servicename = serviceBean.getValue("name");
            List<BeanDto> lst = null;
            if (commonModule) {
                String tableXML = null;
                if (PubUtil.mainFrame.isVersion2()) {
                    tableXML = ServiceInvoker.getTables(servicename, PubUtil.getService2Url());
                    if ("codecenter".equals(servicename)) {
                        servicetype = ServiceType.SERVICE2_CODECENTER;
                        lst = ServiceInvoker.parseTable(servicename, tableXML, null);
                    } else {
                        servicetype = ServiceType.SERVICE2;

                        lst = ServiceInvoker.parseTable(servicename, tableXML, tablesMap);
                    }
                } else {
                    servicetype = ServiceType.SERVICE3;
                    tableXML = ServiceInvoker.getTables(serviceBean.getValue("name"));
                    lst = ServiceInvoker.parseTable(servicename, tableXML, tablesMap);
                }
                //通用的 设置 表格
            } else {
                lst = ServiceInvoker.getDataSource(servicename, !PubUtil.mainFrame.isVersion2(), ModuleType.SERVICE);
            }
            if (lst != null && !lst.isEmpty()) {
                for (BeanDto table : lst) {
                    table_combo.addItem(table);
                }

            }
        } finally {
            init = false;
        }
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

        inparamLevel1.addProperty("moduleid", PubUtil.mainFrame.getModuneInfo() == null ? "" : PubUtil.mainFrame.getModuneInfo().getValue("id"));
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
