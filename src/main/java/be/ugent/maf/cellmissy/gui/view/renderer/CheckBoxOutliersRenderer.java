/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * CellRenderer for CheckBox - outliers
 *
 * @author Paola Masuzzo
 */
public class CheckBoxOutliersRenderer extends OutliersRenderer {

    /**
     * Constructor, takes a 2D array of boolean (TRUE means point is an outlier)
     * + a formatter.
     *
     * @param outliers
     * @param formatter
     */
    public CheckBoxOutliersRenderer(boolean[][] outliers, Format formatter) {
        super(outliers, formatter);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;
        // If we are at last row return a CheckBoxCellRenderer, that extends a JCheckbox 
        if (row == table.getRowCount() - 1) {
            result = new CheckBoxCellRenderer((boolean) value, SwingConstants.RIGHT);
        } else {
            // else, call the super
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return result;
    }
}
