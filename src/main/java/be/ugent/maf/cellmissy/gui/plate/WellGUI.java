/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Well;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Paola
 */
public class WellGUI {

    private Ellipse2D wellShape;
    private Color wellColor;
    private int rowNumber;
    private int columnNumber;
    private Well well;

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

    public void setWellShape(Ellipse2D wellShape) {
        this.wellShape = wellShape;
    }

    public Ellipse2D getWellShape() {
        return wellShape;
    }

    public void setWellColor(Color wellColor) {
        this.wellColor = wellColor;
    }

    public Color getWellColor() {
        return wellColor;
    }

    public void setWell(Well well) {
        this.well = well;
    }

    public Well getWell() {
        return well;
    }
}
