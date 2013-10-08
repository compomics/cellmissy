/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class VelocityOutliersRenderer extends DefaultTableCellRenderer {

    private boolean[] outliers;
    private Format formatter;

    /**
     * Constructor
     *
     * @param motileStepsVector
     */
    public VelocityOutliersRenderer(boolean[] outliers, Format formatter) {
        this.outliers = outliers;
        this.formatter = formatter;
    }

    /**
     * Overriding this method, background of table is set to RED if data point
     * of the motile steps vector is False.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            // if a value is TRUE, it is an outlier, highlight it in red
            if (outliers[row]) {
                setForeground(Color.red);
            } else {
                setForeground(Color.black);
            }
        }
        if (value != null) {
            value = formatter.format(value);
        }
        setValue(value);
        setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }
}
