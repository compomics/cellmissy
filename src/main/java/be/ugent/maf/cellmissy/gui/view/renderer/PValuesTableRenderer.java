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
 *
 * @author Paola Masuzzo
 */
public class PValuesTableRenderer extends DefaultTableCellRenderer {

    // alpha level for significance
    private Double alpha;
    private Format formatter;

    public PValuesTableRenderer(Double alpha, Format formatter) {
        this.alpha = alpha;
        this.formatter = formatter;
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
            if (value != null && (Double) value < alpha) {
                setForeground(Color.green);
            } else {
                setForeground(Color.black);
            }
        }
        // if p value is not null, format it
        if (value != null) {
            value = formatter.format(value);
        } else {
            // else, show a dash(-)
            value = "-";
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.RIGHT);

        return this;
    }
}
