/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for Area Increase Table: outliers are highlighted in Table
 * @author Paola Masuzzo
 */
public class AreaIncreaseRenderer extends DefaultTableCellRenderer {

    private boolean[][] outliers;
    private Format formatter;

    /**
     * Constructor
     * @param outliers
     * @param formatter 
     */
    public AreaIncreaseRenderer(boolean[][] outliers, Format formatter) {
        this.outliers = outliers;
        this.formatter = formatter;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            if (outliers[row][column - 1]) {
                setForeground(Color.red);
            } else {
                setForeground(Color.black);
            }
        }


        if (value != null) {
            value = formatter.format(value);
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.RIGHT);
        setOpaque(true);
        return this;
    }
}
