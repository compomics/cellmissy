/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell Renderer for 2 decimals rounding
 *
 * @author Paola Masuzzo
 */
public class FormatRenderer extends DefaultTableCellRenderer {

    private int alignment;
    private Format formatter;

    /**
     * 
     * @param alignment
     * @param formatter 
     */
    public FormatRenderer(int alignment, Format formatter) {
        this.alignment = alignment;
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            if (value instanceof Number) {
                value = formatter.format(value);
            } else {
                value = formatter.format(Double.parseDouble(value.toString()));
            }
        }
        setValue(value);
        setHorizontalAlignment(alignment);
        setOpaque(true);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}
