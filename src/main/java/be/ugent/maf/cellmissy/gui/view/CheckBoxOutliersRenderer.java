/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;

/**
 *
 * @author Paola Masuzzo
 */
public class CheckBoxOutliersRenderer extends OutliersRenderer {

    public CheckBoxOutliersRenderer(boolean[][] outliers, Format formatter) {
        super(outliers, formatter);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component result = null;
        if (row == table.getRowCount() - 1) {
            result = new CheckBoxCellRenderer((boolean)value);
        } else {
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return result;
    }
}
