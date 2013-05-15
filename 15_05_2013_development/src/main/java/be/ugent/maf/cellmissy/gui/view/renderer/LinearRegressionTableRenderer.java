/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for the Linear Regression Table
 *
 * @author Paola Masuzzo
 */
public class LinearRegressionTableRenderer extends DefaultTableCellRenderer {

    //Formatter
    private Format formatter;

    public LinearRegressionTableRenderer(Format formatter) {
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // for the last two columns, format the value
        if (column == table.getColumnCount() - 1 | column == table.getColumnCount() - 2) {
            if (value != null) {
                if (value instanceof Number) {
                    value = formatter.format(value);
                } else {
                    value = formatter.format(Double.parseDouble(value.toString()));
                }
            }
        }

        setValue(value);
        setOpaque(true);

        // for the fist, and last two columns, set the background in selection mode with the condition index color
        if (column == 0 | column == table.getColumnCount() - 1 | column == table.getColumnCount() - 2) {
            if (isSelected) {
                int lenght = GuiUtils.getAvailableColors().length;
                int indexOfColor = row % lenght;
                setBackground(GuiUtils.getAvailableColors()[indexOfColor]);
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
        }

        setHorizontalAlignment(SwingConstants.LEFT);
        return this;
    }
}
