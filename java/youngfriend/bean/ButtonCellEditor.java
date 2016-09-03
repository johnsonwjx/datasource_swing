package youngfriend.bean;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;

/**
 * Created by xiong on 6/6/16.
 */
public class ButtonCellEditor extends DefaultCellEditor {
    private JButton btn = new JButton("...");
    private JTextField renderText = new JTextField();
    private TableCellRenderer render;
    private JTextField text;
    private final JPanel reanderPanel = new JPanel(new BorderLayout());
    private final JPanel editPanel = new JPanel(new BorderLayout());
    private final JButton renderBtn = new JButton("...");

    public ButtonCellEditor(JTextField text) {
        super(text);
        clickCountToStart = 1;
        this.text = text;
        reanderPanel.add(renderBtn, BorderLayout.EAST);
        reanderPanel.add(renderText, BorderLayout.CENTER);

        editPanel.add(btn, BorderLayout.EAST);
        editPanel.add(text, BorderLayout.CENTER);

        render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jTable, Object o, boolean b, boolean b1, int i, int i1) {
                if (b) {
                    renderText.setBackground(jTable.getSelectionBackground());
                } else {
                    renderText.setBackground(jTable.getBackground());
                }
                renderText.setText((String) o);
                return reanderPanel;
            }
        };

    }


    public TableCellRenderer getRender() {
        return render;
    }

    public void afterEditor(String value) {
        text.setText(value);
        fireEditingStopped();
    }

    public void initAction(ActionListener actionListener) {
        btn.addActionListener(actionListener);
    }


    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        text.setBackground(jTable.getSelectionBackground());
        text.setText((String) o);
        return editPanel;
    }
}
