/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class RectIconCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int lenght = GuiUtils.getAvailableColors().length;
        int indexOfColor = row % lenght;
        Color color = GuiUtils.getAvailableColors()[indexOfColor];
        setIcon(new RectIcon(color));
        setHorizontalAlignment(SwingConstants.CENTER);
        setText("");
        return this;
    }
}
