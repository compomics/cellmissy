/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.table;

import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for track data holder table cells.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataHolderTableRenderer extends DefaultTableCellRenderer {

    private Format formatter;

    public TrackDataHolderTableRenderer(Format formatter) {
        this.formatter = formatter;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null && row != 0 && column != 0) {
            if (value instanceof Number) {
                value = formatter.format(value);
            } else {
                value = formatter.format(Double.parseDouble(value.toString()));
            }
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.RIGHT);

        if (column == 0) {
            setFont(new Font("Tahoma", Font.BOLD, 12));
        } else {
            setFont(new Font("Tahoma", Font.PLAIN, 12));
        }

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
