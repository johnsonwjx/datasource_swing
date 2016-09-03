package youngfriend.bean;

import youngfriend.App;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CheckBoxHeader extends JPanel
        implements TableCellRenderer, MouseListener {
    protected int column;
    private boolean mousePressed = false;
    private boolean init = false;
    private JTable table;


    public void setInit(boolean init) {
        this.init = init;
    }

    public CheckBoxHeader() {
        setLayout(new BorderLayout());
        add(checkBox, BorderLayout.CENTER);
        checkBox.setForeground(this.getForeground());
        checkBox.setBackground(this.getBackground());
        checkBox.setFont(this.getFont());
        checkBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (init) {
                    return;
                }
                boolean flag = !checkBox.isSelected();
                checkBox.setSelected(flag);
                boolean checked = checkBox.isSelected();
                for (int x = 0, y = table.getRowCount(); x < y; x++) {
                    table.setValueAt(checked, x, getColumn());
                }
            }
        });
    }

    private JCheckBox checkBox = new JCheckBox();

    public void setSelected(boolean select) {
        checkBox.setSelected(select);
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            this.table = table;
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.addMouseListener(this);
            }
        }
        setColumn(column);
        checkBox.setText((String) value);
        this.setBorder(App.border);
        return this;
    }

    protected void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    private void handleClickEvent(MouseEvent e) {
        if (mousePressed) {
            mousePressed = false;
            if (table == null) {
                JTableHeader header = (JTableHeader) (e.getSource());
                table = header.getTable();
            }

            TableColumnModel columnModel = table.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = table.convertColumnIndexToModel(viewColumn);
            if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                checkBox.dispatchEvent(e);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        handleClickEvent(e);
        ((JTableHeader) e.getSource()).repaint();
    }

    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}