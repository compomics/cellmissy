/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo
 */
public class PValuesTableRenderer extends DefaultTableCellRenderer {

    private Format formatter;
    private boolean[][] significances;

    public PValuesTableRenderer(Format formatter, boolean[][] significances) {
        this.formatter = formatter;
        this.significances = significances;
    }

    /**
     * Overriding this method, background of table is set to Green if p-value is less than alpha
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            if (significances[row][column - 1]) {
                setForeground(Color.green);
                setFont(new Font("Tahoma", Font.BOLD, 11));
            } else {
                setForeground(Color.black);
            }
        }
        // if p value is not null, format it
        if (value != null && (Double) value < 1.0) {
            value = formatter.format(value);
        } else if (value == null) {
            // else, show a dash(-)
            value = "-";
        } else if ((Double) value > 1.0) {
            // for values greater than 1, only show 1
            value = 1.0;
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.RIGHT);
        return this;
    }
}
