/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Abstract Class extending JPanel
 * Plate View Panel with a List of Well Gui and a plate format
 * @author Paola
 */
public abstract class AbstractPlatePanel extends JPanel {

    /**
     * Constructor; setOpaque to false.
     */
    public AbstractPlatePanel() {
        setOpaque(false);
    }
    protected List<WellGui> wellGuiList;
    protected PlateFormat plateFormat;
    protected static final int pixelsGrid = 7;
    protected static final int pixelsBorders = 25;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // width and heigth of squares around wells (wellSize)
        int wellSize = (int) ((double) (this.getWidth()) - ((plateFormat.getNumberOfCols() - 1) * pixelsGrid) - (2 * pixelsBorders)) / plateFormat.getNumberOfCols();

        //check if the list of wellGui is empty;
        //if yes, drawWells for the first time, otherwise call the reDrawWells method of this same class
        if (wellGuiList.isEmpty()) {
            drawWells(wellSize, g);
        } else {
            reDrawWells(g);
        }
    }

    /**
     * getters
     * 
     */
    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public PlateFormat getPlateFormat() {
        return plateFormat;
    }

    /**
     * Given a plate format and a parent dimension, this methods computes the right space to show the plate panel
     * @param plateFormat :format, numbers of rows, number of columns
     * @param parentDimension :Dimension of parent panel where the plate need to be added
     */
    public void initPanel(PlateFormat plateFormat, Dimension parentDimension) {
        this.plateFormat = plateFormat;
        wellGuiList = new ArrayList<>();
        doResize(parentDimension);
    }

    /**
     * making use of Graphics 2D, render wells
     * @param wellSize: size of wells according to format
     * @param g: graphics 
     */
    public void drawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        for (int i = 0; i < plateFormat.getNumberOfRows(); i++) {
            for (int j = 0; j < plateFormat.getNumberOfCols(); j++) {

                int topLeftX = (int) Math.round(wellSize * j + (j + 1) * pixelsGrid + pixelsBorders);
                int topLeftY = (int) Math.round(wellSize * i + (i + 1) * pixelsGrid + pixelsBorders);

                List<Ellipse2D> ellipsi = new ArrayList<>();

                // create new WellGui object (rowNumber, columnNumber)
                WellGui wellGui = new WellGui(i + 1, j + 1);
                // create new Ellipse2D object to draw the well
                Ellipse2D ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);

                ellipsi.add(ellipse2D);
                wellGui.setEllipsi(ellipsi);
                // wells drawn for the first time are always shown in default color
                // the default color is the first object of the WellGui's AvailableWellColors()List
                Color defaultColor = GuiUtils.getAvailableColors()[0];
                g2d.setColor(defaultColor);
                g2d.draw(ellipse2D);

                wellGuiList.add(wellGui);

                if (i == 0 || j == 0) {
                    // draw the labels on the plate
                    drawPlateLabel(ellipse2D, g2d, j + 1, i + 1);
                }
            }
        }
    }

    /**
     * set graphics: implementing rendering process
     * @param g2d 
     */
    public void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }

    /**
     * Render plate labels on upper-side and left-side of the plate view
     * @param ellipse2D
     * @param g2d
     * @param columnNumber
     * @param rowNumber 
     */
    protected void drawPlateLabel(Ellipse2D ellipse2D, Graphics2D g2d, int columnNumber, int rowNumber) {
        Font font = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(font);
        String columnLabel = "" + columnNumber;
        if (columnLabel.length() > 1) {
            g2d.drawString(columnLabel, (int) Math.round(ellipse2D.getCenterX()) - 5, pixelsBorders);
        } else {
            g2d.drawString(columnLabel, (int) Math.round(ellipse2D.getCenterX()) - 3, pixelsBorders);
        }

        String rowLabel = "" + rowNumber;
        if (rowLabel.length() > 1) {
            g2d.drawString(rowLabel, pixelsBorders - 12, (int) Math.round(ellipse2D.getCenterY()) + 3);
        } else {
            g2d.drawString(rowLabel, pixelsBorders - 8, (int) Math.round(ellipse2D.getCenterY()) + 3);
        }
    }

    /**
     * This method is called in the initPanel method:
     * Compute plate panel sizes according to JFrame resize
     * @param parentDimension 
     */
    public void doResize(Dimension parentDimension) {
        int minimumParentDimension = Math.min(parentDimension.height, parentDimension.width);

        if (plateFormat != null) {
            int panelHeight = parentDimension.height;
            int panelWidth = parentDimension.width;
            if (plateFormat.getNumberOfCols() >= plateFormat.getNumberOfRows()) {
                if (minimumParentDimension == parentDimension.width) {
                    panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                } else {
                    if ((int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows())) < panelWidth) {
                        panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                    } else {
                        panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                    }
                }
            } else {
                if (minimumParentDimension == parentDimension.width) {
                    if ((int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols())) < panelHeight) {
                        panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                    } else {
                        panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                    }
                } else {
                    panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                }
            }
            this.setSize(panelWidth, panelHeight);
        }
    }

    /**
     * abstract method to be implemented
     * Redraw wells according to events: Show rectangles for conditions, show ellipsis for imaging types and son on. 
     * @param g 
     */
    protected abstract void reDrawWells(Graphics g);
}
