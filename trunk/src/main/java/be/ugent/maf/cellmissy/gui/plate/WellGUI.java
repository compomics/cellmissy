/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paola
 */
public class WellGUI {

    private static final Color[] availableWellColors = {Color.BLACK, Color.RED, Color.GRAY, Color.GREEN, Color.ORANGE};
    
    private Well well;
    private List<Ellipse2D> ellipsi;

    public WellGUI(int rowNumber, int columnNumber) {
        well = new Well();
        well.setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
        well.setRowNumber(rowNumber);
        well.setColumnNumber(columnNumber);
    }

    public int getRowNumber() {
        return well.getRowNumber();
    }

    public int getColumnNumber() {
        return well.getColumnNumber();
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
}
