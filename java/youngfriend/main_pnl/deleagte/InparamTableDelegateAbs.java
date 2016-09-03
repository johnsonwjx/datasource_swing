package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.bean.CheckBoxHeader;
import youngfriend.gui.ListDlg;
import youngfriend.utils.MainPnlUtil;
import youngfriend.utils.PubUtil;
import youngfriend.utils.ServiceType;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;
import java.util.Map;

/**
 * Created by xiong on 9/2/16.
 */
public abstract class InparamTableDelegateAbs {
    public static final int INDEX_FIELD = 0;
    public static final int INDEX_FIELD_DESC = 1;


    //字段dto bean 的属性
    public static final String FIELD_NAME_PROPNAME = "field_name";
    public static final String FIELD_DESC_PROPNAME = "field_desc";
    public static final String FIELD_TYPE_PROPNAME = "field_type";
    public static final String FIELD_LENGTH_PROPNAME = "field_length";

    protected static int[] checkboxIndexs;

    protected JTable table;
    //保存json对象
    JsonArray inParam2Level2 = new JsonArray();

    /**
     * ----------------------------------------------------------
     * 暴露方法
     * ----------------------------------------------------------
     */
    public void save(JsonObject jsonData) {
        PubUtil.stopTabelCellEditor(table);
        preSaveInparam();
        int rowCount = table.getRowCount();
        if (rowCount > 0) {
            JsonArray fields = new JsonArray();
            for (int i = 0; i < rowCount; i++) {
                BeanDto field = (BeanDto) table.getValueAt(i, INDEX_FIELD);
                saveFields(fields, field);
                saveParamGetByInTable(field, i);
            }
            jsonData.add("fields", fields);
            postSaveInparam(jsonData);
        }
    }

    /**
     * @param jsonData
     * @param inparamLevel1
     */
    public abstract void loadInTableDatas(JsonObject jsonData, Map<String, JsonObject> inparamLevel1);

    public JTable getTable() {
        return table;
    }

    public JsonArray getInparamLevel2() {
        return inParam2Level2;
    }


    public void clear() {
        resetSelectAll(checkboxIndexs, table);
        MainPnlUtil.clearTable(table);
    }

    /**
     * 初始化 表格
     */
    public abstract void initTable();

    /**
     * ----------------------------------------------------------
     * 工具方法
     * ----------------------------------------------------------
     */


    /**
     *
     */
    protected void initCheckHeader() {
        TableColumnModel cm = table.getColumnModel();
        for (int col : checkboxIndexs) {
            final TableColumn column = cm.getColumn(col);
            column.setHeaderRenderer(new CheckBoxHeader());
        }

    }

    /**
     * 重置表格 多选类
     *
     * @param checkboxColIndexs 复选框列Indexs
     * @param table             {@code JTable}
     */
    protected void resetSelectAll(int[] checkboxColIndexs, JTable table) {
        for (int col : checkboxColIndexs) {
            final TableColumnModel columnModel = table.getColumnModel();
            final TableColumn column = columnModel.getColumn(col);
            CheckBoxHeader headerRenderer = (CheckBoxHeader) column.getHeaderRenderer();
            headerRenderer.setInit(true);
            headerRenderer.setSelected(false);
            headerRenderer.setInit(false);
        }
        table.getTableHeader().repaint();
    }

    /**
     * 是否为入口参数
     *
     * @param inParamFieldMap
     * @param fieldName
     * @return
     */
    protected boolean isInparam(Map<String, JsonObject> inParamFieldMap, String fieldName, BeanDto oper_dto) {
        if (inParamFieldMap == null || inParamFieldMap.isEmpty()) {
            return false;
        }
        if (inParamFieldMap.containsKey(fieldName)) {
            return true;
        }
        if (oper_dto != null && oper_dto.getValue("value").endsWith("INTERVAL")) {
            if (inParamFieldMap.containsKey(fieldName + "_linterval")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存字段 fields
     *
     * @param fields
     * @param field
     */
    private void saveFields(JsonArray fields, BeanDto field) {
        String fieldName = field.getValue(FIELD_NAME_PROPNAME);
        String fieldDesc = field.getValue(FIELD_DESC_PROPNAME);
        JsonObject fieldjson = new JsonObject();
        fieldjson.addProperty(FIELD_NAME_PROPNAME, fieldName);
        fieldjson.addProperty(FIELD_DESC_PROPNAME, fieldDesc);
        fieldjson.addProperty(FIELD_TYPE_PROPNAME, field.getValue(FIELD_TYPE_PROPNAME));
        fieldjson.addProperty(FIELD_LENGTH_PROPNAME, field.getValue(FIELD_LENGTH_PROPNAME));
        fields.add(fieldjson);
    }


    /**
     * 公共 保存入口参数字段
     *
     * @param row
     * @param indexInparam
     * @param defaultValue
     * @param operVal
     */
    protected void saveParamHandleField(BeanDto field, int row, int indexInparam, String defaultValue, String operVal) {
        String fieldName = field.getValue(FIELD_NAME_PROPNAME);
        String fieldDesc = field.getValue(FIELD_DESC_PROPNAME);
        Boolean is_inparam = (Boolean) table.getValueAt(row, indexInparam);
        if (Boolean.TRUE.equals(is_inparam)) {
            JsonObject fieldObj = new JsonObject();
            if (operVal != null && operVal.endsWith("INTERVAL")) {
                fieldObj.addProperty("name", fieldName + "_LINTERVAL");
                fieldObj.addProperty("label", fieldDesc + "(左区间)");
                fieldObj.addProperty("defaultValue", "");
                inParam2Level2.add(fieldObj);
                JsonObject fieldObjR = new JsonObject();
                fieldObjR.addProperty("name", fieldName + "_RINTERVAL");
                fieldObjR.addProperty("label", fieldDesc + "(右区间)");
                fieldObjR.addProperty("defaultValue", defaultValue);
                inParam2Level2.add(fieldObjR);
            } else {
                fieldObj.addProperty("name", fieldName);
                fieldObj.addProperty("label", fieldDesc);
                fieldObj.addProperty("defaultValue", defaultValue);
                inParam2Level2.add(fieldObj);
            }
        }
    }

    /**
     * ----------------------------------------------------------
     * 虚拟方法
     * ----------------------------------------------------------
     */

    /**
     * 根据入口参数表  获取 保存内容
     *
     * @param field
     * @param row
     */
    protected abstract void saveParamGetByInTable(BeanDto field, int row);


    /**
     * 循环 入口参数表格后,得到保存内容 ,保存
     *
     * @param jsonData
     */
    protected abstract void postSaveInparam(JsonObject jsonData);

    /**
     * 清空 保存 内容的 Json 对象
     */
    protected abstract void preSaveInparam();

    public abstract void initTableData(ServiceType servicetype, BeanDto tablebean, ListDlg fieldListDlg, List<BeanDto> fields);
}
