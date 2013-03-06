/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.analysis.AreaUnitOfMeasurement;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo
 */
public class AreaUnitOfMeasurementComboBoxRenderer extends DefaultListCellRenderer {

    public AreaUnitOfMeasurementComboBoxRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        AreaUnitOfMeasurement areaUnitOfMeasurement = (AreaUnitOfMeasurement) value;
        String unitOfMeasurementString = areaUnitOfMeasurement.getUnitOfMeasurementString();
        setText(unitOfMeasurementString);
        return this;
    }
}
