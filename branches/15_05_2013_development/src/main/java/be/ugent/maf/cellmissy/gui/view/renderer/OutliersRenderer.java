/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table Cell Renderer to highlight outliers data in a Table
 *
 * @author Paola Masuzzo
 */
public class OutliersRenderer extends DefaultTableCellRenderer {

    private boolean[][] outliers;
    private Format formatter;

    /**
     * Constructor, takes a 2D array of boolean (TRUE means point is an outlier)
     * + a formatter
     *
     * @param outliers
     * @param formatter
     */
    public OutliersRenderer(boolean[][] outliers, Format formatter) {
        this.outliers = outliers;
        this.formatter = formatter;
    }

    /**
     * Overriding this method, background of table is set to RED if data point
     * has been detected as an outlier
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            // if a value is TRUE, it is an outlier, highlight it in red
            if (outliers[row][column - 1]) {
                setForeground(Color.red);
            } else {
                setForeground(Color.black);
            }
        }
        if (value != null) {
            value = formatter.format(value);
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.RIGHT);
        return this;
    }
}
