/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Well;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 *
 * @author Paola
 */
public class WellGUI {

    private static final Color[] availableWellColors = {Color.BLACK, Color.GREEN, Color.GRAY, Color.GREEN, Color.ORANGE};
    private List<Color> wellColors;
    private int rowNumber;
    private int columnNumber;
    private Well well;
    private List<Ellipse2D> ellipsi;

    public WellGUI(int rowNumber, int columnNumber) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public static Color[] getAvailableWellColors() {
        return availableWellColors;
    }

    public void setWell(Well well) {
        this.well = well;
    }

    public Well getWell() {
        return well;
    }

    public List<Ellipse2D> getEllipsi() {
        return ellipsi;
    }

    public void setEllipsi(List<Ellipse2D> ellipsi) {
        this.ellipsi = ellipsi;
    }

    public List<Color> getWellColors() {
        return wellColors;
    }

    public void setWellColors(List<Color> wellColors) {
        this.wellColors = wellColors;
    }
}
