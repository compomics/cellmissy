/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo
 */
public class SetupTableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);
        if (column == 0) {
            int length = ((String) value).length();
            String substring = ((String) value).substring(length - 1);
            int conditionIndex = Integer.parseInt(substring);
            setIcon(new RectIcon(GuiUtils.getAvailableColors()[conditionIndex]));
        }
        setHorizontalAlignment(SwingConstants.LEFT);
        return this;
    }
}
