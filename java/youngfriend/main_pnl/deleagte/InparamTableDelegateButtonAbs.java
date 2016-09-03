package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.bean.BeanDto;
import youngfriend.bean.CheckBoxHeader;
import youngfriend.common.util.StringUtils;
import youngfriend.utils.PubUtil;

import javax.swing.JTable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Created by xiong on 9/2/16.
 */
public class InparamTableDelegateButtonAbs extends InparamTableDelegateAbs {
    private static final Logger logger = LoggerFactory.getLogger(InparamTableDelegateButtonAbs.class);
    public static final int INDEX_INPARAM = 2;
    public static final int INDEX_PARAM_VALUE = 3;

    JsonObject fieldDescJsonObj = new JsonObject();

    public InparamTableDelegateButtonAbs(JTable table) {
        this.table = table;
        initTable();
    }

    private void initTable() {
        checkboxIndexs = new int[]{INDEX_INPARAM};
        initCheckHeader();
        table.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if ("tableCellEditor".equals(propertyChangeEvent.getPropertyName())) {
                    if (!table.isEditing()) {
                        int editingColumn = table.getEditingColumn();
                        int editingRow = table.getEditingRow();
                        Object valueAt = table.getValueAt(editingRow, editingColumn);
                        CheckBoxHeader headerRenderer = (CheckBoxHeader) table.getColumnModel().getColumn(editingColumn).getHeaderRenderer();
                        if (headerRenderer != null) {
                            headerRenderer.setInit(true);
                            if (Boolean.FALSE.equals(valueAt)) {
                                headerRenderer.setSelected(false);
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
                            }
                            headerRenderer.setInit(false);
                            table.getTableHeader().repaint();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void loadInTableDatas(JsonObject jsonData, Map<String, JsonObject> inparamLevel1) {
        JsonObject fieldsdescJsonObj = PubUtil.getJsonObj(jsonData, "fields_desc", JsonObject.class);
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            BeanDto field = (BeanDto) table.getValueAt(i, INDEX_FIELD);
            String fieldName = field.getValue(FIELD_NAME_PROPNAME);

            boolean inparam = isInparam(inparamLevel1, fieldName, null);
            String inparamDefaultValue = "";
            if (inparam) {
                JsonElement defaultValue_Ele = inparamLevel1.get(fieldName).get("defaultValue");
                if (!defaultValue_Ele.isJsonNull()) {
                    inparamDefaultValue = defaultValue_Ele.getAsString();
                }
            }
            //获取自定义label,如果没有用默认的
            String desc = PubUtil.getProp(fieldsdescJsonObj, fieldName);
            if (StringUtils.nullOrBlank(desc)) {
                desc = field.getValue(FIELD_DESC_PROPNAME);
            }
            table.setValueAt(desc, i, INDEX_FIELD_DESC);
            table.setValueAt(inparam, i, INDEX_INPARAM);
            table.setValueAt(inparamDefaultValue, i, INDEX_PARAM_VALUE);

        }
    }

    @Override
    protected void saveParamGetByInTable(BeanDto field, int row) {
        saveParamHandleField(field, row, INDEX_INPARAM, "", null);
        String fieldName = field.getValue(InparamTableDelegateAbs.FIELD_NAME_PROPNAME);
        String fieldDesc = field.getValue(InparamTableDelegateAbs.FIELD_DESC_PROPNAME);
        String field_descStr = (String) table.getValueAt(row, INDEX_FIELD_DESC);
        if (field_descStr != null && !fieldDesc.equals(field_descStr.trim())) {
            fieldDescJsonObj.addProperty(fieldName, field_descStr);
        }
    }

    @Override
    protected void postSaveInparam(JsonObject jsonData) {
        jsonData.addProperty("exceptivefields", "");
        jsonData.addProperty("hiddenfields", "");
        jsonData.addProperty("groupfields", "");
    }

    @Override
    protected void preSaveInparam() {
        fieldDescJsonObj = new JsonObject();
        inParam2Level2 = new JsonArray();
    }


}
