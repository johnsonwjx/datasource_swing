package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.common.util.StringUtils;
import youngfriend.gui.ListDlg;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by xiong on 8/29/16.
 */
public class BuildTreeDataDelegate {
    public static final String CODE = "codeField";
    public static final String NAME = "nameField";

    public static final String ROOT = "rootName";
    public static final String CODEINC = "codeInc";
    public static final String HASPARENT = "hasParent";
    private final JCheckBox hasParentCb;

    private JCheckBox tree_checkbox;
    //根目录名称
    private JTextField rootName_tf;
    //级次字段名称
    private JTextField nameField_tf;
    //级次字段
    private JTextField codeField_tf;
    //级次结构
    private JTextField codeInc_tf;


    public BuildTreeDataDelegate(JCheckBox tree_checkbox, final ListDlg fieldListDlg, final JTextField rootName_tf, final JTextField codeField_tf, final JTextField nameField_tf, final JTextField codeInc_tf, final JButton codeFieldBtn, final JButton nameFieldBtn, JCheckBox hasparentCb) {
        this.tree_checkbox = tree_checkbox;
        this.rootName_tf = rootName_tf;
        this.nameField_tf = nameField_tf;
        this.codeInc_tf = codeInc_tf;
        this.codeField_tf = codeField_tf;
        this.hasParentCb = hasparentCb;
        tree_checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                boolean select = itemEvent.getStateChange() == ItemEvent.SELECTED;
                PubUtil.enableComponents(select, codeFieldBtn, codeField_tf, nameField_tf, nameFieldBtn, rootName_tf, codeInc_tf);
                if (!select) {
                    setCodefieldBean(null);
                    setNamefieldBean(null);
                }
            }
        });
        codeFieldBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fieldListDlg.setSelect(InparamTableDelegateAbs.FIELD_NAME_PROPNAME, codeField_tf.getText());
                fieldListDlg.showDlg();
                if (!fieldListDlg.isOk()) {
                    return;
                }
                BeanDto select = fieldListDlg.getSelect();
                setCodefieldBean(select);
            }
        });
        nameFieldBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fieldListDlg.setSelect(InparamTableDelegateAbs.FIELD_NAME_PROPNAME, nameField_tf.getText());
                fieldListDlg.showDlg();
                if (!fieldListDlg.isOk()) {
                    return;
                }
                BeanDto select = fieldListDlg.getSelect();
                setNamefieldBean(select);
            }
        });
    }


    private void setCodefieldBean(BeanDto codefieldBean) {
        codeField_tf.setText(codefieldBean == null ? "" : String.valueOf(codefieldBean));
    }

    private void setNamefieldBean(BeanDto namefieldBean) {
        nameField_tf.setText(namefieldBean == null ? "" : String.valueOf(namefieldBean));
    }


    public void clear() {
        tree_checkbox.setSelected(false);
    }


    public boolean save(JsonObject inparamLevel1) {
        String rootname = rootName_tf.getText();
        String codeInc = codeInc_tf.getText();
        inparamLevel1.addProperty(ROOT, rootname);
        inparamLevel1.addProperty(CODE, codeField_tf.getText());
        inparamLevel1.addProperty(NAME, nameField_tf.getText());
        inparamLevel1.addProperty(CODEINC, codeInc);
        inparamLevel1.addProperty(HASPARENT, hasParentCb.isSelected() ? PubUtil.TRUESTR : PubUtil.FALSESTR);
        return true;
    }

    public void load(JsonObject inparamLevel1) {
        String code = PubUtil.getProp(inparamLevel1, CODE);
        tree_checkbox.setSelected(!StringUtils.nullOrBlank(code));
        String root = PubUtil.getProp(inparamLevel1, ROOT);
        String name = PubUtil.getProp(inparamLevel1, NAME);
        String codeInc = PubUtil.getProp(inparamLevel1, CODEINC);
        codeField_tf.setText(code);
        rootName_tf.setText(root);
        nameField_tf.setText(name);
        codeInc_tf.setText(codeInc);
        hasParentCb.setSelected(PubUtil.TRUESTR.equals(PubUtil.getProp(inparamLevel1, HASPARENT)));
    }

    public void loadBuildTreeBean(BeanDto field) {
        String fieldName = field.getValue(InparamTableDelegateCommonAbs.FIELD_NAME_PROPNAME);
        String codeField = codeField_tf.getText();
        String nameField = nameField_tf.getText();
        if (fieldName.equalsIgnoreCase(codeField)) {
            setCodefieldBean(field);

        } else if (fieldName.equalsIgnoreCase(nameField)) {
            setNamefieldBean(field);
        }
    }
}
