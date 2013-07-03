/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell Renderer for 2 decimals rounding
 *
 * @author Paola Masuzzo
 */
public class FormatRenderer extends DefaultTableCellRenderer {

    private Format formatter;
    private int alignment;

    public FormatRenderer(Format formatter, int alignment) {
        this.formatter = formatter;
        this.alignment = alignment;
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
