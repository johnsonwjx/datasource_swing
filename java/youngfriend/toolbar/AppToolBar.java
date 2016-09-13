package youngfriend.toolbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import youngfriend.App;
import youngfriend.gui.LoginDlg;
import youngfriend.moduletree.ModuleTreePnl;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by xiong on 9/8/16.
 */
public class AppToolBar extends JToolBar {
    private static final Logger logger = LoggerFactory.getLogger(AppToolBar.class);
    private final JButton reloadtreeBtn;
    private final JButton eidtServer2Btn;
    private final JButton closeBtn;
    private final JButton reloginBtn;
    private final ModuleTreePnl moduleTreePnl;

    public AppToolBar(ModuleTreePnl moduleTreePnl) {
        this.moduleTreePnl=moduleTreePnl;
        this.setFloatable(false);
        this.setRollover(true);

        closeBtn = createToolBtn("退出");
        reloginBtn = createToolBtn("重登录");
        reloadtreeBtn = createToolBtn("重新加载树");
        eidtServer2Btn = createToolBtn("修改2.0服务地址");
    
        addEvents();
    }

    private void addEvents() {
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeApp();
            }
        });

        reloginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                relogin();

            }
        });

        reloadtreeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moduleTreePnl.reBuildTree();
            }
        });
    }

    private JButton createToolBtn(String text) {
        JButton btn = new JButton();
        btn.setText(text);
        btn.setFocusable(false);
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add(btn);
        return btn;
    }


    //    private JsonObject getInputParamObj(BeanDto moduleInfoBean) {
//        String inparam = moduleInfoBean.getValue("inparam");
//        if (!Strings.isNullOrEmpty(inparam)) {
//            inparam = new String(Base64.decode(inparam));
//            JsonElement inparamEle = PubUtil.parseJson(inparam);
//            if (inparamEle != null) {
//                return inparamEle.getAsJsonObject();
//            }
//        }
//        return null;
//    }
//
//

    private void relogin() {
        App.instance.setVisible(false);
        LoginDlg loginDlg = new LoginDlg(null);
        if (!loginDlg.isOk()) {
            //show exist origin App and return
            App.instance.setVisible(true);
            return;
        }

        //resart App
        App.startApp();
    }

    public static void closeApp() {
        try {
            if (PubUtil.showConfirm(App.instance, "确定要退出程序吗？")) {
                System.exit(0);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            System.gc();
        }
    }
}
