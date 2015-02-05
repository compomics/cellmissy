/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area;

/**
 * Area Unit of measurement
 *
 * @author Paola Masuzzo
 */
public enum AreaUnitOfMeasurement {

    MICRO_METERS(1), PIXELS(2), PERCENTAGE(3), SPECIAL_MICRO_METERS(4);
    private final int unit;

    private AreaUnitOfMeasurement(int unit) {
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
                string = "\u00B5" + "m" + "\u00B2";

                break;
            case 2:
                string = "pixels";
                break;
            case 3:
                string = "%";
                break;
            case 4:
                string = "(cellM-CELLMIA)" + " " + "\u00B5" + "m" + "\u00B2";
                break;
        }
        return string;
    }
}