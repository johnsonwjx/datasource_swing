package youngfriend.main_pnl.utils;

import youngfriend.utils.PubUtil;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Created by xiong on 9/2/16.
 */
public class MainPnlUtil {
    /**
     * 清空表格
     *
     * @param table {@code JTable}
     * @return model {@code DefaultTableModel}
     */
    public static DefaultTableModel clearTable(JTable table) {
        PubUtil.stopTabelCellEditor(table);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model.fireTableDataChanged();
        return model;
    }


}
