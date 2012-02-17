/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.gui.plate;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Paola
 */
public class WellGUI {

    private Ellipse2D wellShape;
    private Color wellColor;
    private int columnNumber;
    private int rowNumber;

    public WellGUI(int columnNumber, int rowNumber){
        this.columnNumber = columnNumber;
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getRowNumber() {
        return rowNumber;
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
}
