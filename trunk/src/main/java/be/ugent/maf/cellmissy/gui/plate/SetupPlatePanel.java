/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.gui.GuiUtils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
public class SetupPlatePanel extends AbstractPlatePanel {

    private Point startPoint;
    private Point endPoint;
    private Map<Integer, List<Rectangle>> rectangles;
    private Integer currentConditionIndex;

    public Map<Integer, List<Rectangle>> getRectangles() {
        return rectangles;
    }

    public void setRectangles(Map<Integer, List<Rectangle>> rectangles) {
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

    public void setCurrentConditionIndex(Integer currentConditionIndex) {
        this.currentConditionIndex = currentConditionIndex;
    }

    public SetupPlatePanel() {
        startPoint = null;
        endPoint = null;
        rectangles = new HashMap<>();
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

        g2d.setColor(GuiUtils.getAvailableColors()[currentConditionIndex]);
        g2d.drawRect(x, y, width, height);

    }

    private void drawRectangles(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        for (Integer conditionIndex : rectangles.keySet()) {
            g2d.setColor(GuiUtils.getAvailableColors()[conditionIndex]);
            for (Rectangle rectangle : rectangles.get(conditionIndex)) {
                g2d.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }

    }

    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        for (WellGui wellGui : wellGuiList) {
            //get only the bigger default ellipse2D
            Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
            g2d.fill(defaultWell);
            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                drawPlateLabel(defaultWell, g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
            }
        }
    }
}
