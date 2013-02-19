/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Analysis Plate View: Show wells with rectangles around: each rectangle has it own color, according to condition color. This class is used in the analysis step, to show conditions on the plate view
 *
 * @author Paola Masuzzo
 */
public class AnalysisPlatePanel extends AbstractPlatePanel {

    private Experiment experiment;

    /**
     * set Experiment
     *
     * @param experiment
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
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

        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        List<PlateCondition> plateConditions = new ArrayList<>(experiment.getPlateConditionCollection());
        for (PlateCondition plateCondition : plateConditions) {
            for (Well well : plateCondition.getWellCollection()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                        g2d.setColor(GuiUtils.getAvailableColors()[plateConditions.indexOf(plateCondition) + 1]);

                        int x = (int) wellGui.getEllipsi().get(0).getX() - AnalysisPlatePanel.pixelsGrid / 2;
                        int y = (int) wellGui.getEllipsi().get(0).getY() - AnalysisPlatePanel.pixelsGrid / 2;

                        int width = (int) wellGui.getEllipsi().get(0).getWidth() + AnalysisPlatePanel.pixelsGrid;
                        int height = (int) wellGui.getEllipsi().get(0).getHeight() + AnalysisPlatePanel.pixelsGrid;

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
     * Render wells Override method from Abstract Plate Panel: if wells have already been rendered, just redraw them
     *
     * @param g
     */
    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);

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
        List<PlateCondition> plateConditions = new ArrayList<>(experiment.getPlateConditionCollection());
        for (PlateCondition plateCondition : plateConditions) {
            if (plateCondition.isLoaded()) {
                List<Well> wells = new ArrayList<>(plateCondition.getWellCollection());
                for (Well well : wells) {
                    if (well.getWellHasImagingTypeCollection().isEmpty()) {
                        for (WellGui wellGui : wellGuiList) {
                            if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                                //get only the bigger default ellipse2D
                                Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
                                g2d.setColor(Color.LIGHT_GRAY);
                                g2d.fill(defaultWell);
                            }
                        }
                    }
                }
            }
        }
    }
}
