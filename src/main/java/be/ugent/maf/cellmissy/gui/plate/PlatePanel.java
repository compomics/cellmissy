/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.List;

/**
 * This class only shows the plate view (no actions are performed)
 * @author Paola
 */
public class PlatePanel extends JPanel {

    private List<WellGui> wellGuiList;
    private PlateFormat plateFormat;
    private static final int pixelsGrid = 7;
    private static final int pixelsBorders = 25;
    private Point startPoint;
    private Point endPoint;
    private List<Rectangle> rectanglesToDrawList;

    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public List<Rectangle> getRectanglesToDrawList() {
        return rectanglesToDrawList;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void initPanel(PlateFormat plateFormat, Dimension parentDimension) {
        this.plateFormat = plateFormat;
        wellGuiList = new ArrayList<>();
        doResize(parentDimension);
        rectanglesToDrawList = new ArrayList<>();
        startPoint = null;
        endPoint = null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // width and heigth of squares around wells (wellSize)
        int wellSize = (int) ((double) (this.getWidth()) - ((plateFormat.getNumberOfCols() - 1) * pixelsGrid) - (2 * pixelsBorders)) / plateFormat.getNumberOfCols();

        if (wellGuiList.isEmpty()) {
            drawWells(wellSize, g);
        } else {
            reDrawWells(wellSize, g);
        }

        if (startPoint != null && endPoint != null) {
            drawRect(g);
        }

        if (!rectanglesToDrawList.isEmpty()) {
            drawRectangles(g);
        }
    }

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
                Color defaultColor = WellGui.getAvailableWellColors()[0];
                g2d.setColor(defaultColor);
                g2d.fill(ellipse2D);

                wellGuiList.add(wellGui);

                if (i == 0 || j == 0) {
                    // draw the labels on the plate
                    drawPlateLabel(ellipse2D, g2d, j + 1, i + 1);
                }
            }
        }
    }

    // re-draw the wells if rezise event occours (keep color(s) of the wells)
    public void reDrawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        // a list of WellGUI objects is present, iterate through it
        for (WellGui wellGui : wellGuiList) {
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();

            for (int i = 0; i < ellipsi.size(); i++) {
                Ellipse2D ellipse2D = ellipsi.get(i);
                g2d.draw(ellipse2D);

                // if a color of a wellGui has been changed, keep track of it when resizing
                // if a well was not imaged, set its color to the default one
                if (wellGui.getWell().getWellHasImagingTypeCollection().isEmpty()) {
                    g2d.setColor(WellGui.getAvailableWellColors()[0]);
                } else {
                    // if it has been imaged, set its color to a different one
                    g2d.setColor(WellGui.getAvailableWellColors()[i + 1]);
                }

                g2d.fill(ellipse2D);
            }

            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                drawPlateLabel(ellipsi.get(0), g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
            }
        }
    }

    // set Graphics (implement Rendering process)
    private void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }

    // draw numbers (plate labels) on upper-side and left-side of the plate
    private void drawPlateLabel(Ellipse2D ellipse2D, Graphics2D g2d, int columnNumber, int rowNumber) {
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

    // compute plate panel sizes according to JFrame resize
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

    private void drawRect(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        if (x + width > this.getWidth()) {
            width = this.getWidth() - x;
        }

        if (y + height > this.getHeight()) {
            height = this.getHeight() - y;
        }

        g2d.drawRect(x, y, width, height);

    }

    private void drawRectangles(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        for (Rectangle r : rectanglesToDrawList) {
            g2d.drawRect(r.x, r.y, r.width, r.height);
        }
    }
}
