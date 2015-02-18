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
 * This class is used in the setup step: show wells and let the user select
 * them, assigning conditions. Drawing the mouse on the plate view, we select
 * conditions (group of wells).
 *
 * @author Paola
 */
public class SetupPlatePanel extends AbstractPlatePanel {

    // start point of mouse dragging
    private Point startPoint;
    // end point of mouse dragging
    private Point endPoint;
    // map between a Condition and a list of rectangles
    private final Map<PlateCondition, List<Rectangle>> rectangles;
    // current plate condition
    private PlateCondition currentCondition;

    /**
     * Constructor: initialize map with rectangles and set to null start and end
     * point
     */
    public SetupPlatePanel() {
        startPoint = null;
        endPoint = null;
        rectangles = new HashMap<>();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public Map<PlateCondition, List<Rectangle>> getRectangles() {
        return rectangles;
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

    @Override
    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public void setCurrentCondition(PlateCondition currentCondition) {
        this.currentCondition = currentCondition;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // if both start point and end point are not null, draw rectangle while dragging the mouse
        if (startPoint != null && endPoint != null) {
            drawRect(g);
        }

        // if rectangles have already been drawn, keep them in the paint
        // this is needed because of the repaint method that is being called from the JComponent
        if (!rectangles.values().isEmpty()) {
            drawRectangles(g);
        }
    }

    /**
     * Render one Rectangle (for current condition) -- This is used when
     * dragging on the plate panel and showing the current rectangle.
     *
     * @param g
     */
    private void drawRect(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
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
        int lenght = GuiUtils.getAvailableColors().length;
        int conditionIndex = currentCondition.getConditionIndex() - 1;
        int indexOfColor = conditionIndex % lenght;
        g2d.setColor(GuiUtils.getAvailableColors()[indexOfColor]);
        g2d.drawRect(x, y, width, height);
    }

    /**
     * Render all rectangles already drawn by the user (for all conditions in
     * the map).
     *
     * @param g
     */
    private void drawRectangles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        for (PlateCondition plateCondition : rectangles.keySet()) {
            int lenght = GuiUtils.getAvailableColors().length;
            int conditionIndex = plateCondition.getConditionIndex() - 1;
            int indexOfColor = conditionIndex % lenght;
            g2d.setColor(GuiUtils.getAvailableColors()[indexOfColor]);
            List<Rectangle> rectList = rectangles.get(plateCondition);
            if (rectList != null) {
                for (Rectangle rectangle : rectList) {

                    for (WellGui wellGui : wellGuiList) {
                        if (rectangle.contains(wellGui.getEllipsi().get(0).getX(), wellGui.getEllipsi().get(0).getY(), wellGui.getEllipsi().get(0).getWidth(), wellGui.getEllipsi().get(0).getHeight())) {
                            int x = (int) wellGui.getEllipsi().get(0).getX() - SetupPlatePanel.pixelsGrid / 4;
                            int y = (int) wellGui.getEllipsi().get(0).getY() - SetupPlatePanel.pixelsGrid / 4;

                            int width = (int) wellGui.getEllipsi().get(0).getWidth() + SetupPlatePanel.pixelsGrid / 2;
                            int height = (int) wellGui.getEllipsi().get(0).getHeight() + SetupPlatePanel.pixelsGrid / 2;

                            //create rectangle that sorrounds the wellGui and draw it
                            Rectangle rect = new Rectangle(x, y, width, height);
                            g2d.draw(rect);
                            wellGui.setRectangle(rect);
                        }
                    }
                }
            }
        }
    }

    /**
     * Render wells Override method from Abstract Plate Panel: if wells have
     * already been rendered, just redraw them
     *
     * @param g
     */
    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
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
