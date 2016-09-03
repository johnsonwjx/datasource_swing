package youngfriend.main_pnl.deleagte;

import com.google.gson.JsonObject;
import youngfriend.bean.BeanDto;
import youngfriend.gui.SortFieldDlg;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 排序字段代理
 *
 * @author xiong
 */
public class SortFieldDelegate {
    private final JTextField sortTf;
    private static final SortFieldDlg sortFieldDlg = new SortFieldDlg(PubUtil.mainFrame);
    public static final String PROP = "sortfields";

    public SortFieldDelegate(final JTextField sortTf, JButton sortBtn, final List<BeanDto> fields) {
        this.sortTf = sortTf;
        sortBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String sortfields = sortTf.getText().trim();
                sortFieldDlg.edit(fields, sortfields);
                if (!sortFieldDlg.isOk()) {
                    return;
                }
                sortfields = sortFieldDlg.getValue();
                sortTf.setText(sortfields);
            }
        });
    }

    public void save(JsonObject jsonData) {
        jsonData.addProperty(PROP, sortTf.getText().trim());
    }

    public void load(JsonObject jsonData) {
        String prop = PubUtil.getProp(jsonData, PROP);
        sortTf.setText(prop);
    }

}
