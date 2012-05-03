/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paola
 */
public class SetupPlatePanel extends AbstractPlatePanel {

    private Point startPoint;
    private Point endPoint;
    private List<Rectangle> rectanglesToDrawList;

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

    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public SetupPlatePanel() {
        startPoint = null;
        endPoint = null;
        rectanglesToDrawList = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startPoint != null && endPoint != null) {
            drawRect(g);
        }

        if (!rectanglesToDrawList.isEmpty()) {
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

        g2d.drawRect(x, y, width, height);
    }

    private void drawRectangles(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        for (Rectangle r : rectanglesToDrawList) {
            g2d.drawRect(r.x, r.y, r.width, r.height);
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
