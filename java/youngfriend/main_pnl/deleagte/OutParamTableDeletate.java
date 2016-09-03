package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import youngfriend.exception.ParamValidateExcption;
import youngfriend.main_pnl.utils.MainPnlUtil;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * Created by xiong on 9/2/16.
 */
public class OutParamTableDeletate {

    //出口参数表格index
    public static final int INDEX_OUTPARAMS_FIELD = 0;
    public static final int INDEX_OUTPARAMS_FIELD_DESC = 1;

    private final DefaultTableModel model;
    private JTable table;

    public OutParamTableDeletate(final JTable table, JButton addBtn, JButton delBtn) {
        this.table=table;
        model = (DefaultTableModel) table.getModel();
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PubUtil.stopTabelCellEditor(table);
                int selectedRow = table.getSelectedRow();
                selectedRow++;
                model.insertRow(selectedRow, new String[2]);
                PubUtil.tableSelect(table, selectedRow);
            }
        });
        PubUtil.tableDelRow(delBtn, table);
    }

    public void clear() {
        MainPnlUtil.clearTable(table);
    }

    public void load(JsonObject inparamObj) {
        JsonArray outparam = PubUtil.getJsonObj(inparamObj, "outparam", JsonArray.class);
        if (outparam != null) {
            JsonArray outParams = PubUtil.getJsonObj(outparam.get(0).getAsJsonObject(), "outParams", JsonArray.class);
            if(outParams!=null){
                Iterator<JsonElement> iterator = outParams.iterator();
                while (iterator.hasNext()) {
                    JsonObject fieldObj = iterator.next().getAsJsonObject();
                    model.addRow(new String[]{PubUtil.getProp(fieldObj, "name"), PubUtil.getProp(fieldObj, "label")});
                }
            }

        }
    }

    public JsonArray getOutParamsLevel2() {
        PubUtil.stopTabelCellEditor(table);
        int rowCount_out = table.getRowCount();
        JsonArray fieldoutParams = null;
        if (rowCount_out > 0) {
            fieldoutParams = new JsonArray();
            for (int i = 0; i < rowCount_out; i++) {
                String fieldname = (String) table.getValueAt(i, INDEX_OUTPARAMS_FIELD);
                String fielddesc = (String) table.getValueAt(i, INDEX_OUTPARAMS_FIELD_DESC);
                if (PubUtil.existNull(fieldname, fielddesc)) {
                    PubUtil.tableSelect(table, i);
                    throw new ParamValidateExcption("出口参数设错误");
                }
                JsonObject fieldObj = new JsonObject();
                fieldObj.addProperty("name", fieldname);
                fieldObj.addProperty("label", fielddesc);
                fieldoutParams.add(fieldObj);
            }
        }
        return fieldoutParams;
    }
}
