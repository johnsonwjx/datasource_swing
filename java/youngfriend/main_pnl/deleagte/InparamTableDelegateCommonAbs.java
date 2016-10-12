package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.bean.CheckBoxHeader;
import youngfriend.bean.ColumnGroup;
import youngfriend.bean.GroupableTableHeader;
import youngfriend.common.util.StringUtils;
import youngfriend.utils.PubUtil;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiong on 9/2/16.
 */
public abstract class InparamTableDelegateCommonAbs extends InparamTableDelegateAbs {
    //合计
    public static final BeanDto[] SUMVALUES = new BeanDto[]{null, new BeanDto("合计", "sum"), new BeanDto("取max", "max")};
    //固定值
    public static final BeanDto[] FIXEDVALUES = new BeanDto[]{null, new BeanDto("当前日期", "currentDate"), new BeanDto("员工ID", "userID"), new BeanDto("员工姓名", "username"),
            new BeanDto("操作用户ID", "personid"), new BeanDto("操作用户注册名", "regname"),
            new BeanDto("操作用户角色ID", "sysrole"), new BeanDto("集团ID", "corpid"), new BeanDto("集团代码", "corpcode"), new BeanDto("集团名称", "corpname"),
            new BeanDto("当前会话身份ID", "sysAccessID"), new BeanDto("设计项目代码", "curProjectCode"),
    };
    //操作符
    public static final BeanDto[] OPERVALUES = new BeanDto[]{null, new BeanDto("等于", "EQ"), new BeanDto("不等于", "UNEQ"),//
            new BeanDto("大于", "BG"), new BeanDto("小于", "BESS"), new BeanDto("大于等于", "BGEQ"), new BeanDto("小于等于", "LESSEQ"),//
            new BeanDto("字段匹配值 左匹配", "Llike"), new BeanDto("字段匹配值 右匹配", "Rlike"), new BeanDto("字段匹配值 包含", "Alike"),//
            new BeanDto("值匹配字段 左匹配", "RLlike"), new BeanDto("值匹配字段 右匹配", "RRlike"), new BeanDto("值匹配字段 包含", "RAlike"),//
            new BeanDto("为空", "ISNull"), new BeanDto("包含", "in"), new BeanDto("不包含", "notin"),//
            new BeanDto("开区间(a<x<b)", "OINTERVAL"), new BeanDto("闭区间(a<=x<=b)", "CINTERVAL"),//
            new BeanDto("左开右闭区间(a<x<=b)", "LOINTERVAL"), new BeanDto("左闭右开区间(a<=x<b)", "ROINTERVAL")
    };
    //入口参数表格index

    public static final int INDEX_EXCEPT = 2;
    public static final int INDEX_INPARAM = 3;
    public static final int INDEX_OPER = 4;
    public static final int INDEX_INPARAM_NULL_IGNORE = 5;
    //查询时
    public static final int INDEX_FIXED_VALUE_QUERY = 6;
    //保存时
    public static final int INDEX_FIXED_VALUE_SAVE = 7;

    public static final int INDEX_GROUPBY = 8;
    public static final int INDEX_SUM = 9;


    public InparamTableDelegateCommonAbs(JTable table) {
        this.table = table;
        initTable();
    }

    private void initTable() {
        TableColumnModel cm = table.getColumnModel();
        boolean isUpdatePnl = cm.getColumnCount() <= INDEX_GROUPBY;
        if (!isUpdatePnl) {
            checkboxIndexs = new int[]{INDEX_EXCEPT, INDEX_INPARAM, INDEX_INPARAM_NULL_IGNORE, INDEX_GROUPBY};
        } else {
            checkboxIndexs = new int[]{INDEX_EXCEPT, INDEX_INPARAM, INDEX_INPARAM_NULL_IGNORE};
        }
        initCheckHeader();

        Map<String, int[]> groupColumnMap = new LinkedHashMap();
        groupColumnMap.put("入口参数", new int[]{INDEX_INPARAM, INDEX_INPARAM_NULL_IGNORE, INDEX_OPER, INDEX_FIXED_VALUE_QUERY});
        if (!isUpdatePnl) {
            groupColumnMap.put("分类汇总", new int[]{INDEX_GROUPBY, INDEX_SUM});
        }

        GroupableTableHeader groupableTableHeader = new GroupableTableHeader(cm);
        table.setTableHeader(groupableTableHeader);
        Set<String> groupColumnTitles = groupColumnMap.keySet();
        for (String title : groupColumnTitles) {
            int[] indexArr = groupColumnMap.get(title);
            ColumnGroup columnGroup = new ColumnGroup(title);
            for (int i : indexArr) {
                columnGroup.add(cm.getColumn(i));
            }
            groupableTableHeader.addColumnGroup(columnGroup);
        }


        cm.getColumn(INDEX_EXCEPT).setMinWidth(100);
        cm.getColumn(INDEX_EXCEPT).setMaxWidth(100);
        cm.getColumn(INDEX_INPARAM).setMinWidth(100);
        cm.getColumn(INDEX_INPARAM).setMaxWidth(100);
        cm.getColumn(INDEX_INPARAM_NULL_IGNORE).setMinWidth(120);
        cm.getColumn(INDEX_INPARAM_NULL_IGNORE).setMaxWidth(140);
        cm.getColumn(INDEX_FIXED_VALUE_QUERY).setMaxWidth(130);
        cm.getColumn(INDEX_FIXED_VALUE_QUERY).setMinWidth(130);

        cm.getColumn(INDEX_FIXED_VALUE_QUERY).setCellEditor(new DefaultCellEditor(new JComboBox(FIXEDVALUES)));
        cm.getColumn(INDEX_FIXED_VALUE_SAVE).setCellEditor(new DefaultCellEditor(new JComboBox(FIXEDVALUES)));

        cm.getColumn(INDEX_OPER).setCellEditor(new DefaultCellEditor(new JComboBox(OPERVALUES)));
        if (!isUpdatePnl) {
            cm.getColumn(INDEX_GROUPBY).setMaxWidth(100);
            cm.getColumn(INDEX_GROUPBY).setMinWidth(100);
            cm.getColumn(INDEX_SUM).setMaxWidth(100);
            cm.getColumn(INDEX_SUM).setMinWidth(100);
            cm.getColumn(INDEX_SUM).setCellEditor(new DefaultCellEditor(new JComboBox(SUMVALUES)));

        }

        if (checkboxIndexs != null) {
            for (int col : checkboxIndexs) {
                final TableColumn column = cm.getColumn(col);
                column.setHeaderRenderer(new CheckBoxHeader());
            }
        }


        table.addPropertyChangeListener(new PropertyChangeListener() {
            private boolean editorFlag = false;

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (editorFlag) {
                    return;
                }
                if ("tableCellEditor".equals(propertyChangeEvent.getPropertyName())) {
                    if (!table.isEditing()) {
                        editorFlag = true;
                        int editingColumn = table.getEditingColumn();
                        int editingRow = table.getEditingRow();
                        Object valueAt = table.getValueAt(editingRow, editingColumn);
                        CheckBoxHeader headerRenderer = (CheckBoxHeader) table.getColumnModel().getColumn(editingColumn).getHeaderRenderer();
                        if (headerRenderer != null) {
                            headerRenderer.setInit(true);
                            if (Boolean.FALSE.equals(valueAt)) {
                                headerRenderer.setSelected(false);
                                if (editingColumn == INDEX_INPARAM) {
                                    table.setValueAt(OPERVALUES[0], editingRow, INDEX_OPER);
                                }
                            } else {
                                boolean flag = true;
                                for (int i = 0; i < table.getRowCount(); i++) {
                                    Object value = table.getValueAt(i, editingColumn);
                                    if (Boolean.FALSE.equals(value)) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    headerRenderer.setSelected(true);
                                }
                                if (editingColumn == INDEX_INPARAM) {
                                    table.setValueAt(OPERVALUES[1], editingRow, INDEX_OPER);
                                }
                            }
                            headerRenderer.setInit(false);
                            table.getTableHeader().repaint();
                        } else if (editingColumn == INDEX_SUM && valueAt != null) {
                            BeanDto field = (BeanDto) table.getValueAt(editingRow, INDEX_FIELD);
                            BeanDto dto = (BeanDto) valueAt;
                            if (!"N".equals(field.getValue(FIELD_TYPE_PROPNAME)) && "sum".equals(dto.getValue("value"))) {
                                PubUtil.showMsg("字段不是数字类型");
                                table.setValueAt(null, editingRow, INDEX_SUM);
                            }
                        }
                        editorFlag = false;
                    }
                }
            }
        });
    }


    /**
     * boolean 列 append fieldName 到{@code StringBuilder}
     *
     * @param data
     * @param row
     * @param index
     * @param fieldName
     */
    protected void appendField2StrBd4BooleanCol(StringBuilder data, int row, int index, String fieldName) {
        Boolean is_inparam_null_ignore = (Boolean) table.getValueAt(row, index);
        if (Boolean.TRUE.equals(is_inparam_null_ignore)) {
            data.append(fieldName).append(",");
        }
    }

    /**
     * {@code BeanDto} 列 (Combo列,Button 选择列 etc),保存为JsonObject fieldName 为property ,BeanDto value值 为 value
     *
     * @param json
     * @param row
     * @param index
     * @param fieldName
     */
    protected void addFieldValue2Json4BeanDtoCol(JsonObject json, int row, int index, String fieldName) {
        BeanDto beanDto = (BeanDto) table.getValueAt(row, index);
        if (beanDto != null) {
            json.addProperty(fieldName, beanDto.getValue("value"));
        }
    }

    @Override
    protected void saveParamGetByInTable(BeanDto field, int row) {
        BeanDto operDto = (BeanDto) table.getValueAt(row, INDEX_OPER);
        String operVal = operDto == null ? "" : operDto.getValue("value");
        saveParamHandleField(field, row, INDEX_INPARAM, "", operVal);
        String fieldName = field.getValue(FIELD_NAME_PROPNAME);
        String fieldDesc = field.getValue(FIELD_DESC_PROPNAME);
        operatorsJsonObj.addProperty(fieldName, operVal);
        appendField2StrBd4BooleanCol(inparamNullIgnoreStrBd, row, INDEX_INPARAM_NULL_IGNORE, fieldName);
        appendField2StrBd4BooleanCol(exceptivefieldsStrBd, row, INDEX_EXCEPT, fieldName);
        appendField2StrBd4BooleanCol(groupfieldsStrBd, row, INDEX_GROUPBY, fieldName);

        addFieldValue2Json4BeanDtoCol(fixedvalueQueryJsonObj, row, INDEX_FIXED_VALUE_QUERY, fieldName);
        addFieldValue2Json4BeanDtoCol(fixedvalueSaveJsonObj, row, INDEX_FIXED_VALUE_SAVE, fieldName);

        addFieldValue2Json4BeanDtoCol(sumfieldsJsonObj, row, INDEX_SUM, fieldName);


        String field_descStr = (String) table.getValueAt(row, INDEX_FIELD_DESC);
        if (field_descStr != null && !fieldDesc.equals(field_descStr.trim())) {
            //保存条件: 设置了自定义desc && 跟数据库取出来的不同
            fieldsdescJsonObj.addProperty(fieldName, field_descStr);
        }
    }

    StringBuilder exceptivefieldsStrBd = new StringBuilder();
    StringBuilder groupfieldsStrBd = new StringBuilder();
    StringBuilder inparamNullIgnoreStrBd = new StringBuilder();
    JsonObject fixedvalueQueryJsonObj = new JsonObject();
    JsonObject fixedvalueSaveJsonObj = new JsonObject();
    JsonObject sumfieldsJsonObj = new JsonObject();
    JsonObject operatorsJsonObj = new JsonObject();
    JsonObject fieldsdescJsonObj = new JsonObject();


    @Override
    protected void preSaveInparam() {
        exceptivefieldsStrBd.setLength(0);
        groupfieldsStrBd.setLength(0);
        inparamNullIgnoreStrBd.setLength(0);
        fixedvalueQueryJsonObj = new JsonObject();
        fixedvalueSaveJsonObj = new JsonObject();
        sumfieldsJsonObj = new JsonObject();
        operatorsJsonObj = new JsonObject();
        fieldsdescJsonObj = new JsonObject();
        inParam2Level2 = new JsonArray();
    }


    @Override
    protected void postSaveInparam(JsonObject jsonData) {
        if (exceptivefieldsStrBd.length() > 0) {
            exceptivefieldsStrBd.deleteCharAt(exceptivefieldsStrBd.length() - 1);
            jsonData.addProperty("exceptivefields", exceptivefieldsStrBd.toString());
        }
        if (groupfieldsStrBd.length() > 0) {
            groupfieldsStrBd.deleteCharAt(groupfieldsStrBd.length() - 1);
            jsonData.addProperty("groupfields", groupfieldsStrBd.toString());
        }
        if (inparamNullIgnoreStrBd.length() > 0) {
            inparamNullIgnoreStrBd.deleteCharAt(inparamNullIgnoreStrBd.length() - 1);
            jsonData.addProperty("inparam_null_ignore", inparamNullIgnoreStrBd.toString());
        }
        if (!fixedvalueQueryJsonObj.toString().equals("{}")) {
            jsonData.add("query_fixed_value", fixedvalueQueryJsonObj);
        }
        if (!fixedvalueSaveJsonObj.toString().equals("{}")) {
            jsonData.add("fixed_value", fixedvalueSaveJsonObj);
        }
        if (!sumfieldsJsonObj.toString().equals("{}")) {
            jsonData.add("sumfields", sumfieldsJsonObj);
        }
        if (!operatorsJsonObj.toString().equals("{}")) {
            jsonData.add("operators", operatorsJsonObj);
        }
        if (!fieldsdescJsonObj.toString().equals("{}")) {
            jsonData.add("fields_desc", fieldsdescJsonObj);
        }
    }


    /**
     * load
     */


    /**
     * 获取 属性 为 field1,field2 并转化为 List
     *
     * @param inparamObj
     * @param prop
     * @return
     */
    protected List<String> loadProp2List(JsonObject inparamObj, String prop) {
        String value = PubUtil.getProp(inparamObj, prop, false);
        if (StringUtils.nullOrBlank(value)) {
            return null;
        }
        return Arrays.asList(value.split(","));
    }

    /**
     * field1,field2 转化为 List 有 判断 field是否存在
     *
     * @param fields
     * @param field
     * @return
     */
    protected boolean containsField(List<String> fields, String field) {
        if (fields == null) {
            return false;
        }
        return fields.contains(field);
    }


    /**
     * 导入数据 字段处理时候  每个界面 不同,
     * 所有 设计成虚拟方法,在具体类中实现
     * {@link youngfriend.main_pnl.CommonPnl#afterUi(JComboBox, JTable, JButton, JButton, JCheckBox)}
     *
     * @param field
     */
    protected abstract void loadFieldCustom(BeanDto field);

    public void loadInTableDatas(JsonObject jsonData, Map<String, JsonObject> inParamFieldMap) {
        JsonObject fixedvalueQueryJsonObj = PubUtil.getJsonObj(jsonData, "query_fixed_value", JsonObject.class);
        JsonObject fixedvalueSaveJsonObj = PubUtil.getJsonObj(jsonData, "fixed_value", JsonObject.class);
        JsonObject sumfieldsJsonObj = PubUtil.getJsonObj(jsonData, "sumfields", JsonObject.class);
        JsonObject operatorsJsonObj = PubUtil.getJsonObj(jsonData, "operators", JsonObject.class);
        JsonObject fieldsdescJsonObj = PubUtil.getJsonObj(jsonData, "fields_desc", JsonObject.class);
        List<String> exceptivefields_lst = loadProp2List(jsonData, "exceptivefields");
        List<String> groupfields_lst = loadProp2List(jsonData, "groupfields");
        List<String> inparam_null_ignore_list = loadProp2List(jsonData, "inparam_null_ignore");
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            BeanDto field = (BeanDto) table.getValueAt(i, INDEX_FIELD);
            loadFieldCustom(field);
            String fieldName = field.getValue(FIELD_NAME_PROPNAME);

            BeanDto fixedvalueJsonObj_dto = PubUtil.getDtoInJsonValue(FIXEDVALUES, "value", fixedvalueQueryJsonObj, fieldName);
            BeanDto fixedvalueSaveJsonObj_dto = PubUtil.getDtoInJsonValue(FIXEDVALUES, "value", fixedvalueSaveJsonObj, fieldName);
            BeanDto sumfield_dto = PubUtil.getDtoInJsonValue(SUMVALUES, "value", sumfieldsJsonObj, fieldName);
            BeanDto oper_dto = PubUtil.getDtoInJsonValue(OPERVALUES, "value", operatorsJsonObj, fieldName);

            //获取自定义label,如果没有用默认的
            String desc = PubUtil.getProp(fieldsdescJsonObj, fieldName);
            if (StringUtils.nullOrBlank(desc)) {
                desc = field.getValue(FIELD_DESC_PROPNAME);
            }
            boolean exceptivefield = containsField(exceptivefields_lst, fieldName);
            boolean groupfield = containsField(groupfields_lst, fieldName);
            boolean is_inparam_null_ignore = containsField(inparam_null_ignore_list, fieldName);
            boolean inparam = isInparam(inParamFieldMap, fieldName, oper_dto);
            table.setValueAt(desc, i, INDEX_FIELD_DESC);
            table.setValueAt(exceptivefield, i, INDEX_EXCEPT);
            table.setValueAt(inparam, i, INDEX_INPARAM);
            table.setValueAt(is_inparam_null_ignore, i, INDEX_INPARAM_NULL_IGNORE);
            table.setValueAt(fixedvalueJsonObj_dto, i, INDEX_FIXED_VALUE_QUERY);
            table.setValueAt(fixedvalueSaveJsonObj_dto, i, INDEX_FIXED_VALUE_SAVE);
            table.setValueAt(oper_dto, i, INDEX_OPER);
            table.setValueAt(groupfield, i, INDEX_GROUPBY);
            table.setValueAt(sumfield_dto, i, INDEX_SUM);
        }
    }


}

