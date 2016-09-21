package youngfriend.utils;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.gui.InputDlg;
import youngfriend.login.ServiceConfig;
import youngfriend.service.ServiceInvoker;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiong on 15/7/16.
 */
public class PubUtil {
    private static final Logger logger = LoggerFactory.getLogger(PubUtil.class);
    public static String TRUESTR = "true";
    public static String FALSESTR = "false";
    public static ServiceConfig serviceConfig = new ServiceConfig();
    public static String accessid;
    public static final JsonParser parser = new JsonParser();
    public final static java.util.List<BeanDto> serviceBeans = new ArrayList();
    public static java.util.List<BeanDto> serviceBeans_2;

    public static String getService2Url() {
        return PubUtil.serviceConfig.getPro("service_url_2");
    }

    public static JsonElement parseJson(String jsonstr) {
        try {
            if (Strings.isNullOrEmpty(jsonstr) || "{}".equals(jsonstr) || "[]".equals(jsonstr)) {
                return null;
            }
            return parser.parse(jsonstr);
        } catch (Exception e) {
            logger.error("json数据:{}", jsonstr);
            throw Throwables.propagate(e);
        }
    }

    public static boolean showConfirm(Window owner, String msg) {
        return (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(owner, msg, "提示", JOptionPane.OK_CANCEL_OPTION));
    }

    public static void showMsg(Window owner, String msg) {
        JOptionPane.showMessageDialog(owner, msg);
    }

    public static void showMsg(String msg) {
        showMsg(App.instance, msg);
    }

    public static JDialog getDialog(Window owner, String title, Component com) {
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(com);
        dialog.pack();
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(owner);
        return dialog;
    }


    public static void setWebProxy(String url, String prot) {
        System.setProperty("http.proxyHost", url);
        System.setProperty("http.proxyPort", prot);
    }

    public static void tableDelRow(JButton btn, final JTable table) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selIndexs[] = table.getSelectedRows();
                if (selIndexs.length <= 0) {
                    return;
                }
                stopTabelCellEditor(table);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for (int i = 0; i < selIndexs.length; ) {
                    int index = selIndexs[i];
                    model.removeRow(index);// 因为移除后，表格的总行数也随着变化，所以要
                    i++;
                    for (int j = i; j < selIndexs.length; j++) {
                        selIndexs[j] = selIndexs[j] - 1;
                    }
                }
                if (model.getRowCount() > 0) {
                    int index = selIndexs[0] - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    model.fireTableDataChanged();
                    tableSelect(table, index);
                }
            }
        });
    }

    public static void addTreeNode(DefaultMutableTreeNode parentNode, JsonArray catalogs, String code, Do4objs do4objs, String... text) throws Exception {
        for (JsonElement element : catalogs) {
            JsonObject asJsonObject = element.getAsJsonObject();
            BeanDto dto = new BeanDto(asJsonObject, text);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dto, false);
            if (do4objs != null) {
                do4objs.do4ojbs(node, dto);
            }
            parentNode = getParentNode(asJsonObject.get(code).getAsString(), parentNode, code);
            parentNode.setAllowsChildren(true);
            parentNode.add(node);
            parentNode = node;
        }
    }

    public static boolean existNull(String... fields) {
        for (String field : fields) {
            if (StringUtils.nullOrBlank(field)) {
                return true;
            }
        }
        return false;
    }

    public static void tableSelect(JTable table, int index) {
        table.clearSelection();
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(table.getCellRect(index, 0, true));
    }

    private static DefaultMutableTreeNode getParentNode(String catalog_code, DefaultMutableTreeNode parentNode, String code) {
        if (parentNode.isRoot()) {
            return parentNode;
        }
        BeanDto dto = (BeanDto) parentNode.getUserObject();
        String codeValue = dto.getValue(code);
        if (!Strings.isNullOrEmpty(codeValue) && catalog_code.startsWith(codeValue)) {
            return parentNode;
        }
        return getParentNode(catalog_code, (DefaultMutableTreeNode) parentNode.getParent(), code);
    }

    public static void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }

    public static void enableComponents(boolean enable, Component... componentList) {
        for (Component component : componentList) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }

    public static JsonElement findInJsonArray(JsonArray arr, String prop, String value) {
        if (arr == null || arr.size() < 1) {
            return null;
        }
        for (JsonElement ele : arr) {
            JsonObject asJsonObject = ele.getAsJsonObject();
            if (!asJsonObject.has(prop)) {
                continue;
            }
            if (asJsonObject.get(prop).getAsString().equals(value)) {
                return asJsonObject;
            }
        }
        return null;
    }

    public static void stopTabelCellEditor(JTable table) {
        if (table.getRowCount() <= 0) {
            return;
        }
        TableCellEditor editor = table.getCellEditor();
        if (editor != null) {
            if (table.isEditing()) {
                editor.stopCellEditing();
            }
        }
    }

    public static void registerDlgBtn(JDialog dialog, JButton okbtn, JButton cancelbtn, ActionListener cancelActionListener) {
        JRootPane contentPane = dialog.getRootPane();
        contentPane.setDefaultButton(okbtn);
        cancelbtn.addActionListener(cancelActionListener);
// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(cancelActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void collapseFirstLevel(JTree tree) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        int childCount = root.getChildCount();
        for (int i = 1; i <= childCount; i++) {
            tree.collapseRow(i);
        }
        tree.expandRow(0);
    }

    public static BeanDto getComboItem(JComboBox combo, String key, String value) {
        int itemCount = combo.getItemCount();
        if (itemCount < 1) {
            return null;
        }
        for (int i = 0; i < itemCount; i++) {
            BeanDto dto = (BeanDto) combo.getItemAt(i);
            if (dto == null) {
                continue;
            }
            if (dto.getValue(key).equals(value)) {
                return dto;
            }
        }
        return null;
    }

    public static BeanDto getDto(List<BeanDto> list, String key, String val) {
        if (!StringUtils.nullOrBlank(val) && list != null && !list.isEmpty()) {
            for (BeanDto item : list) {
                if (val.equals(item.getValue(key))) {
                    return item;
                }
            }

        }
        return null;
    }

    public static BeanDto getListItem(JList table_list, String key, String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        DefaultListModel model = (DefaultListModel) table_list.getModel();
        if (model.isEmpty()) {
            return null;
        }
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            BeanDto dto = (BeanDto) model.getElementAt(i);
            if (value.equals(dto.getValue(key))) {
                return dto;
            }
        }
        return null;
    }

    /**
     * 获取Json String 值
     *
     * @param obj
     * @param key
     * @param lowerCase 是否格式化小写
     * @return {@code String}
     */
    public static String getProp(JsonObject obj, String key, boolean lowerCase) {
        if (obj == null) {
            return "";
        }
        JsonElement ele = obj.get(key);
        if (ele == null) {
            return "";
        }
        String value = ele.getAsString();
        return lowerCase ? value.toLowerCase() : value;
    }

    public static String getProp(JsonObject obj, String key) {
        return getProp(obj, key, false);
    }

    public static <T> T getJsonObj(JsonObject obj, String key, Class<T> clazz) {
        if (!obj.has(key)) {
            return null;
        }
        JsonElement jsonElement = obj.get(key);
        if (jsonElement.isJsonNull()) {
            return null;
        }
        if (clazz.equals(JsonObject.class) && jsonElement.isJsonObject()) {
            return (T) jsonElement.getAsJsonObject();
        } else if (clazz.equals(JsonArray.class) && jsonElement.isJsonArray()) {
            return (T) jsonElement.getAsJsonArray();
        }
        return (T) jsonElement;
    }

    public static BeanDto getDtoInJsonValue(BeanDto[] allValues, String valueName, JsonObject jsonValues, String field_name) {
        if (jsonValues != null && jsonValues.has(field_name)) {
            for (int j = 1; j < allValues.length; j++) {
                if (allValues[j].getValue(valueName).equals(jsonValues.get(field_name).getAsString())) {
                    return allValues[j];
                }
            }
        }
        return null;
    }


    public static String getUrlWeb(String serverUrl) {
        String webUtl = "http://" + serverUrl.substring(0, serverUrl.indexOf(":")) + ":8080/v6engine";
        return webUtl;
    }

    public static String getLastCode(String parentCode, String currentLastCode) {
        if (currentLastCode == null) {
            return parentCode + "01";
        }
        //lastCode delete parentCode
        String codeIndex = currentLastCode.substring(parentCode.length());
        Pattern pattern = Pattern.compile("(\\D*)(\\d+)");
        Matcher matcher = pattern.matcher(codeIndex);
        if (matcher.find()) {
            String startStr = matcher.group(1);
            String numberStr = matcher.group(2);
            Integer number = Ints.tryParse(numberStr);
            number++;
            return parentCode + startStr + (number < 10 ? ("0" + number) : number);
        }
        return parentCode + "99";
//
//        if (last_code != null) {
//            last_code++;
//            code_str = String.valueOf(last_code);
//            int code_len = 2 - (code_str.length() - parent_code.length());
//            if (code_len != 0) {
//                for (int i = 0; i < code_len; i++) {
//                    code_str = "0" + code_str;
//                }
//            }
//        }
    }

    public static void setServe2Url() {
        String serverurl = PubUtil.getService2Url() == null ? "" : PubUtil.getService2Url();
        final InputDlg dlg = new InputDlg(App.instance, "2.0服务地址", serverurl) {
            @Override
            public boolean validateData(String txt) {
                if (StringUtils.nullOrBlank(txt)) {
                    return false;
                }
                return true;
            }
        };
        if (dlg.isOk()) {
            serverurl = dlg.getTxt();
            try {
                serviceBeans_2 = ServiceInvoker.getServices2(serverurl);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("获取服务报错" + e.getMessage());
            }
        }
    }

}

