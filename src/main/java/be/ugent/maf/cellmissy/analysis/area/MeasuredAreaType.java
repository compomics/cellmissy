/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area;

/**
 * Is the area cell covered area or open area?
 *
 * @author Paola Masuzzo
 */
public enum MeasuredAreaType {

    CELL_COVERED_AREA(1), OPEN_AREA(2);
    private final int type;

    private MeasuredAreaType(int type) {
        this.type = type;
    }

    /**
     * Decide how to show the type
     *
     * @return
     */
    public String getStringForType() {
        String string = "";
        switch (type) {
            case 1:
                string = "Cell-covered area (wound closure)";
                break;
            case 2:
                string = "Open area (wound area)";
                break;
        }
        return string;

    }
}
