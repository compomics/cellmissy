/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class CheckBoxConditionsRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // the first column cannot be selected, neither can be put on focus
        if(column == 0){
            super.getTableCellRendererComponent(table, value, false, false, row, column);
        }
        return new CheckBoxCellRenderer((boolean) value, SwingConstants.LEFT);
    }
}
