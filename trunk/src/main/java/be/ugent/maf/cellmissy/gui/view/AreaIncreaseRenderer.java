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

    private Double[] outliers;
    private Format formatter;

    public AreaIncreaseRenderer(Double[] outliers, Format formatter) {
        this.outliers = outliers;
        this.formatter = formatter;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);

        Double areaIncrease = (Double) value;
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            if (areaIncrease != null) {
                if (outliers.length != 0) {
                    for (Double outlier : outliers) {
                        if (areaIncrease.equals(outlier)) {
                            setForeground(Color.red);
                            break;
                        } else {
                            setForeground(Color.black);
                        }
                    }
                } else {
                    setForeground(Color.black);
                }
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
