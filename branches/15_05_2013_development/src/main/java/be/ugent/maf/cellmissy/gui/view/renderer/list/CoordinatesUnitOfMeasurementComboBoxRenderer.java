/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.list;

import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renderer for the Track Coordinates Unit of Measurement combo box.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class CoordinatesUnitOfMeasurementComboBoxRenderer extends DefaultListCellRenderer {

    public CoordinatesUnitOfMeasurementComboBoxRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        TrackCoordinatesUnitOfMeasurement trackCoordinatesUnitOfMeasurement = (TrackCoordinatesUnitOfMeasurement) value;
        String unitOfMeasurementString = trackCoordinatesUnitOfMeasurement.getUnitOfMeasurementString();
        setText(unitOfMeasurementString);
        return this;
    }
}
