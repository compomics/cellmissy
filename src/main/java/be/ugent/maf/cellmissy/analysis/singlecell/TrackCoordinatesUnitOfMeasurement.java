/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

/**
 * Track Coordinates Unit of measurement
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public enum TrackCoordinatesUnitOfMeasurement {

    MICRO_METERS(1), PIXELS(2);
    private final int unit;

    TrackCoordinatesUnitOfMeasurement(int unit) {
        this.unit = unit;
    }

    /**
     * Decide how to show the unit
     *
     * @return
     */
    public String getUnitOfMeasurementString() {
        String string = "";
        switch (unit) {
            case 1:
                string = "\u00B5" + "m";
                break;
            case 2:
                string = "pixels";
                break;
        }
        return string;
    }
}
