/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class MotileStepsRenderer extends DefaultTableCellRenderer {

    private Object[] motileStepsVector;

    /**
     * Constructor
     *
     * @param motileStepsVector
     */
    public MotileStepsRenderer(Object[] motileStepsVector) {
        this.motileStepsVector = motileStepsVector;
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
            if (motileStepsVector[row] != null) {
                if (motileStepsVector[row].equals(Boolean.FALSE)) {
                    setValue("F");
                    setForeground(Color.red);
                } else if (motileStepsVector[row].equals(Boolean.TRUE)) {
                    setValue("T");
                    setForeground(Color.black);
                }
            }
        }
        setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }
}
