package youngfriend.main_pnl.deleagte;

import youngfriend.common.util.StringUtils;
import youngfriend.utils.PubUtil;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiong on 9/20/16.
 */
public class SearchTableFieldDelegage {
    private final JTable table;
    private JTextField searchFf;
    private String searchTxt;
    private List<Integer> searchIndexs = new ArrayList<Integer>();

    public SearchTableFieldDelegage(JButton searchBtn, JTextField searchFf, JTable table) {
        this.searchFf = searchFf;
        this.table = table;
        searchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchField(0);
            }
        });
        searchFf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchField(0);
                }
            }

        });

    }

    private void searchField(int index_field) {
        String trim = searchFf.getText().trim();
        if (StringUtils.nullOrBlank(trim) || table.getRowCount() < 1) {
            return;
        }


        if (!trim.equals(searchTxt)) {
            searchIndexs.clear();
            searchTxt = trim;
            int row = table.getRowCount();
            for (int i = 0; i < row; i++) {
                String fieldStr = table.getValueAt(i, index_field).toString() + table.getValueAt(i, index_field + 1);
                if (fieldStr.indexOf(searchTxt) != -1) {
                    searchIndexs.add(i);
                }
            }
        }
        if (searchIndexs.isEmpty()) {
            PubUtil.showMsg("搜索结果为空");
            return;
        }
        int selectedRow = table.getSelectedRow();
        int newSelectRow = -1;
        for (int row : searchIndexs) {
            if (row > selectedRow) {
                newSelectRow = row;
                break;
            }
        }
        if (newSelectRow == -1) {
            newSelectRow = searchIndexs.get(0);
        }
        if (newSelectRow == selectedRow) {
            return;
        }
        table.setRowSelectionInterval(newSelectRow, newSelectRow);
        table.scrollRectToVisible(table.getCellRect(newSelectRow, 0, true));
        table.repaint();
    }
}
