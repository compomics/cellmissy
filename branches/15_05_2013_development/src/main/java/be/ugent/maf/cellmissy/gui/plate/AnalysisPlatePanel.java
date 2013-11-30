/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Analysis Plate View: Show wells with rectangles around: each rectangle has it
 * own colour, according to condition colour. This class is used in the analysis
 * step, to show conditions on the plate view
 *
 * @author Paola Masuzzo
 */
public class AnalysisPlatePanel extends AbstractPlatePanel {

    private Experiment experiment;
    private PlateCondition currentCondition;

    /**
     * set Experiment
     *
     * @param experiment
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * Set current condition
     *
     * @param currentCondition
     */
    public void setCurrentCondition(PlateCondition currentCondition) {
        this.currentCondition = currentCondition;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        if (experiment != null) {
            showRect(g);
        }
    }

    /**
     * Render rectangles
     *
     * @param g
     */
    private void showRect(Graphics g) {
        // set graphics
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        List<PlateCondition> plateConditions = experiment.getPlateConditionList();

        int lenght = GuiUtils.getAvailableColors().length;

        for (PlateCondition plateCondition : plateConditions) {
            for (Well well : plateCondition.getWellList()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                        int conditionIndex = plateConditions.indexOf(plateCondition);
                        int indexOfColor = conditionIndex % lenght;
                        g2d.setColor(GuiUtils.getAvailableColors()[indexOfColor]);

                        int x = (int) wellGui.getEllipsi().get(0).getX() - AnalysisPlatePanel.pixelsGrid / 4;
                        int y = (int) wellGui.getEllipsi().get(0).getY() - AnalysisPlatePanel.pixelsGrid / 4;

                        int width = (int) wellGui.getEllipsi().get(0).getWidth() + AnalysisPlatePanel.pixelsGrid / 2;
                        int height = (int) wellGui.getEllipsi().get(0).getHeight() + AnalysisPlatePanel.pixelsGrid / 2;

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
     * Render wells Override method from Abstract Plate Panel: if wells have
     * already been rendered, just redraw them
     *
     * @param g
     */
    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        int lenght = GuiUtils.getAvailableColors().length;

        // draw all the wells
        for (WellGui wellGui : wellGuiList) {
            //get only the bigger default ellipse2D
            Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
            g2d.draw(defaultWell);
            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                drawPlateLabel(defaultWell, g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
            }
        }
        // highlight the ones that were not imaged
        if (experiment != null) {
            List<PlateCondition> plateConditions = experiment.getPlateConditionList();
            for (PlateCondition plateCondition : plateConditions) {
                if (plateCondition.isLoaded()) {
                    List<Well> wells = plateCondition.getWellList();
                    for (Well well : wells) {
                        if (well.getWellHasImagingTypeList().isEmpty()) {
                            for (WellGui wellGui : wellGuiList) {
                                if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                                    //get only the bigger default ellipse2D
                                    Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
                                    g2d.setColor(GuiUtils.getNonImagedColor());
                                    g2d.fill(defaultWell);
                                }
                            }
                        }
                    }
                }
                //
                if (plateCondition.equals(currentCondition)) {
                    int conditionIndex = plateConditions.indexOf(currentCondition);
                    int indexOfColor = conditionIndex % lenght;
                    g2d.setColor(GuiUtils.getAvailableColors()[indexOfColor]);

                    List<Well> wells = plateCondition.getWellList();
                    for (Well well : wells) {
                        for (WellGui wellGui : wellGuiList) {
                            if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                                //get only the bigger default ellipse2D
                                Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
                                double height = defaultWell.getHeight();
                                double width = defaultWell.getWidth();
                                double upperLeftCornerX = defaultWell.getX() + AnalysisPlatePanel.pixelsGrid / 2;
                                double upperLeftCornerY = defaultWell.getY() + AnalysisPlatePanel.pixelsGrid / 2;

                                Point2D upperLeftPoint = new Point2D.Double(upperLeftCornerX, upperLeftCornerY);
                                Point2D upperRightPoint = new Point2D.Double(upperLeftCornerX + width - AnalysisPlatePanel.pixelsGrid, upperLeftCornerY);
                                Point2D lowerLeftPoint = new Point2D.Double(upperLeftCornerX, upperLeftCornerY + height - AnalysisPlatePanel.pixelsGrid);
                                Point2D lowerRightPoint = new Point2D.Double(upperLeftCornerX + width - AnalysisPlatePanel.pixelsGrid, upperLeftCornerY + height - AnalysisPlatePanel.pixelsGrid);

                                Point2D verticalUpperPoint = new Point2D.Double(upperLeftCornerX + width / 2 - AnalysisPlatePanel.pixelsGrid / 2, upperLeftCornerY);
                                Point2D verticalLowerPoint = new Point2D.Double(upperLeftCornerX + width / 2 - AnalysisPlatePanel.pixelsGrid / 2, upperLeftCornerY + height - AnalysisPlatePanel.pixelsGrid);
                                Point2D horizontalLeftPoint = new Point2D.Double(upperLeftCornerX, upperLeftCornerY + height / 2 - AnalysisPlatePanel.pixelsGrid / 2);
                                Point2D horizontalRightPoint = new Point2D.Double(upperLeftCornerX + width - AnalysisPlatePanel.pixelsGrid, upperLeftCornerY + height / 2 - AnalysisPlatePanel.pixelsGrid / 2);

                                Line2D firstLine2D = new Line2D.Double(upperLeftPoint, lowerRightPoint);
                                g2d.draw(firstLine2D);
                                Line2D secondLine2D = new Line2D.Double(upperRightPoint, lowerLeftPoint);
                                g2d.draw(secondLine2D);
                                Line2D verticalLine2D = new Line2D.Double(verticalUpperPoint, verticalLowerPoint);
                                g2d.draw(verticalLine2D);
                                Line2D horizontalLine2D = new Line2D.Double(horizontalLeftPoint, horizontalRightPoint);
                                g2d.draw(horizontalLine2D);
                            }
                        }
                    }
                }
            }
        }
    }
}
