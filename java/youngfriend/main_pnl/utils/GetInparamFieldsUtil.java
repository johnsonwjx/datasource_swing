package youngfriend.main_pnl.utils;

import com.google.gson.JsonObject;
import org.dom4j.Element;
import youngfriend.bean.BeanDto;
import youngfriend.gui.ListDlg;
import youngfriend.service.ServiceInvoker;
import youngfriend.utils.PubUtil;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

import static youngfriend.main_pnl.deleagte.InparamTableDelegateAbs.FIELD_DESC_PROPNAME;
import static youngfriend.main_pnl.deleagte.InparamTableDelegateAbs.FIELD_LENGTH_PROPNAME;
import static youngfriend.main_pnl.deleagte.InparamTableDelegateAbs.FIELD_NAME_PROPNAME;
import static youngfriend.main_pnl.deleagte.InparamTableDelegateAbs.FIELD_TYPE_PROPNAME;

/**
 * Created by xiong on 9/3/16.
 */
public class GetInparamFieldsUtil {
    /**
     * 根据选择的table初始化表格字段,并初始化字段和选择字段的diaglog
     *
     * @param tablebean
     */
    public static void initTableDataCommon(JTable table, Map<BeanDto, List<Element>> tableMap, BeanDto tablebean, ListDlg fieldListDlg, List<BeanDto> fields) {
        DefaultTableModel model = MainPnlUtil.clearTable(table);
        java.util.List<Element> elements = tableMap.get(tablebean);
        if (elements == null) {
            return;
        }
        for (Element element : elements) {
            java.util.List<Element> paras = element.elements("Para");
            JsonObject object = new JsonObject();
            for (Element para : paras) {
                String name = para.attributeValue("name");
                String text = para.getText();
                object.addProperty(name.toLowerCase(), text);
            }
            object.addProperty(FIELD_NAME_PROPNAME, object.get(FIELD_NAME_PROPNAME).getAsString().toLowerCase());
            BeanDto fielddto = new BeanDto(object, FIELD_NAME_PROPNAME);
            model.addRow(new Object[]{fielddto, fielddto.getValue(FIELD_DESC_PROPNAME)});
            fieldListDlg.addItem(fielddto);
            fields.add(fielddto);
        }
        model.fireTableDataChanged();
    }

    /**
     * 初始化表格  代码中心
     *
     * @param table
     * @param tablebean
     * @param fieldListDlg
     * @param fields
     * @throws Exception
     */
    public static void initTableDataCodetableServer2(JTable table, BeanDto tablebean, ListDlg fieldListDlg, List<BeanDto> fields) throws Exception {
        DefaultTableModel model = MainPnlUtil.clearTable(table);
        java.util.List<Element> elements = ServiceInvoker.getCodeFields(tablebean.getValue("id"), PubUtil.getService2Url());
        if (elements == null) {
            return;
        }
        for (Element element : elements) {
            java.util.List<Element> paras = element.elements();
            JsonObject object = new JsonObject();
            for (Element para : paras) {
                object.addProperty(para.getName().toLowerCase(), para.getText());
            }
            String name = element.elementText("fieldname");
            String text = element.elementText("fieldlabel");
            String fieldtype = element.elementText("fieldtype");
            String fieldlength = element.elementText("fieldlength");
            object.addProperty(FIELD_NAME_PROPNAME, name.toLowerCase());
            object.addProperty(FIELD_DESC_PROPNAME, text);
            object.addProperty(FIELD_TYPE_PROPNAME, fieldtype);
            object.addProperty(FIELD_LENGTH_PROPNAME, fieldlength);
            BeanDto fielddto = new BeanDto(object, FIELD_NAME_PROPNAME);
            model.addRow(new Object[]{fielddto});
            fieldListDlg.addItem(fielddto);
            fields.add(fielddto);
        }
        model.fireTableDataChanged();
    }

    /**
     * 特殊服务
     *
     * @param table
     * @param tablebean
     * @param fieldListDlg
     * @param fields
     * @throws Exception
     */
    public static void initTableDataService(JTable table, BeanDto tablebean, ListDlg fieldListDlg, List<BeanDto> fields, boolean isVersion2) throws Exception {
        DefaultTableModel model = MainPnlUtil.clearTable(table);
        String value = tablebean.getValue("value");
        List<BeanDto> fieldtos = ServiceInvoker.getFields(value, !isVersion2);
        for (BeanDto fielddto : fieldtos) {
            model.addRow(new Object[]{fielddto, fielddto.getValue(FIELD_DESC_PROPNAME)});
            fieldListDlg.addItem(fielddto);
            fields.add(fielddto);
        }
        model.fireTableDataChanged();
    }
}
