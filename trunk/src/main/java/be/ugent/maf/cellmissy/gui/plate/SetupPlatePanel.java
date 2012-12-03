/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *This class is used in the setup step: show wells and let the user select them, assigning conditions
 * @author Paola
 */
public class SetupPlatePanel extends AbstractPlatePanel {

    private Point startPoint;
    private Point endPoint;
    private Map<PlateCondition, List<Rectangle>> rectangles;
    private PlateCondition currentCondition;

    /**
     * Constructor
     */
    public SetupPlatePanel() {
        startPoint = null;
        endPoint = null;
        rectangles = new HashMap<>();
    }

    /**
     * setters and getters
     * 
     * @return 
     */
    public Map<PlateCondition, List<Rectangle>> getRectangles() {
        return rectangles;
    }

    public void setRectangles(Map<PlateCondition, List<Rectangle>> rectangles) {
        this.rectangles = rectangles;
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

    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public void setCurrentCondition(PlateCondition currentCondition) {
        this.currentCondition = currentCondition;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startPoint != null && endPoint != null) {
            drawRect(g);
        }

        if (!rectangles.values().isEmpty()) {
            drawRectangles(g);
        }
    }

    /**
     * Render one Rectangle
     * @param g 
     */
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

        g2d.setColor(GuiUtils.getAvailableColors()[currentCondition.getConditionIndex()]);
        g2d.drawRect(x, y, width, height);

    }

    /**
     * Render all rectangles already drawn by the user
     * @param g 
     */
    private void drawRectangles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        for (PlateCondition plateCondition : rectangles.keySet()) {
            g2d.setColor(GuiUtils.getAvailableColors()[plateCondition.getConditionIndex()]);
            for (Rectangle rectangle : rectangles.get(plateCondition)) {

                for (WellGui wellGui : wellGuiList) {
                    if (rectangle.contains(wellGui.getEllipsi().get(0).getX(), wellGui.getEllipsi().get(0).getY(), wellGui.getEllipsi().get(0).getWidth(), wellGui.getEllipsi().get(0).getHeight())) {
                        int x = (int) wellGui.getEllipsi().get(0).getX() - SetupPlatePanel.pixelsGrid / 2;
                        int y = (int) wellGui.getEllipsi().get(0).getY() - SetupPlatePanel.pixelsGrid / 2;

                        int width = (int) wellGui.getEllipsi().get(0).getWidth() + SetupPlatePanel.pixelsGrid;
                        int height = (int) wellGui.getEllipsi().get(0).getHeight() + SetupPlatePanel.pixelsGrid;

                        //create rectangle that sorrounds the wellGui and draw it
                        Rectangle rect = new Rectangle(x, y, width, height);
                        g2d.draw(rect);
                        wellGui.setRectangle(rect);
                    }
                }
            }
        }


    }

    /**
     * Render wells
     * @param g 
     */
    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        for (WellGui wellGui : wellGuiList) {
            //get only the bigger default ellipse2D
            Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
            g2d.draw(defaultWell);
            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                drawPlateLabel(defaultWell, g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
            }
        }
    }
}
