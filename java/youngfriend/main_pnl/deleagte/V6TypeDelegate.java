package youngfriend.main_pnl.deleagte;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.bean.BeanDto;
import youngfriend.gui.ListDlg;
import youngfriend.utils.PubUtil;
import youngfriend.service.ServiceInvoker;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 复合数据类型 代理类
 *
 * @author xiong
 */
public class V6TypeDelegate {
    private static final Logger logger = LoggerFactory.getLogger(V6TypeDelegate.class);
    private BeanDto v6type;
    private final JTextField v6typeTf;
    private static final ListDlg v6typeDlg;
    public static final String V6TYPE_GET = "v6datatype";
    //保存 属性
    public static final String V6TYPE_SAVE = "v6dataprop";


    static {
        /**
         * 初始化 v6type 选择界面
         */
        v6typeDlg = new ListDlg(App.instance);
        try {
            String work_dic_v6type = ServiceInvoker.getWork_dic_v6type();
            Document document = DocumentHelper.parseText(work_dic_v6type);
            List<Element> querydatas = document.getRootElement().element("querydatas").elements();
            for (Element ele : querydatas) {
                String name = ele.elementText("name");
                JsonObject obj = new JsonObject();
                obj.addProperty(V6TYPE_GET, ele.elementText(V6TYPE_GET));
                obj.addProperty("name", name);
                BeanDto dto = new BeanDto(obj, "name", V6TYPE_GET);
                v6typeDlg.addItem(dto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Throwables.propagate(e);
        }
    }

    public V6TypeDelegate(JButton v6typeBtn, JTextField v6typeTf) {
        this.v6typeTf = v6typeTf;
        v6typeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                v6typeDlg.setSelect(v6type);
                v6typeDlg.showDlg();
                if (!v6typeDlg.isOk()) {
                    return;
                }
                changeV6type(v6typeDlg.getSelect());
            }
        });

    }


    protected void changeV6type(BeanDto select) {
        this.v6type = select;
        if (this.v6type == null) {
            v6typeTf.setText("");
        } else {
            v6typeTf.setText(this.v6type.getValue("name"));
        }
    }

    /**
     * 导入
     *
     * @param inparmasObj
     */
    public void loadV6PropData(JsonObject inparmasObj) {
        String v6dataprop = PubUtil.getProp(inparmasObj, V6TYPE_SAVE);
        if (!Strings.isNullOrEmpty(v6dataprop)) {
            v6typeDlg.setSelect(V6TYPE_GET, v6dataprop);
            BeanDto select = v6typeDlg.getSelect();
            changeV6type(select);
        }
    }

    /**
     * 保存
     *
     * @param inparam
     */
    public void saveV6typeInparam(JsonObject inparam) {
        inparam.addProperty(V6TYPE_SAVE, v6type == null ? "" : v6type.getValue(V6TYPE_GET));
    }

    public void clear() {
        v6type = null;
        v6typeTf.setText("");
    }
}
