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
import java.util.List;

/**
 * PDF plate panel: to get the plate panel view for PDF set-up report
 *
 * @author Paola Masuzzo
 */
public class PdfPlatePanel extends AbstractPlatePanel {

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
        // set graphics
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        List<PlateCondition> plateConditionList = experiment.getPlateConditionList();
        int lenght = GuiUtils.getAvailableColors().length;

        for (PlateCondition plateCondition : plateConditionList) {
            for (Well well : plateCondition.getWellList()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                        int conditionIndex = plateCondition.getConditionIndex() - 1;
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
    }
}
