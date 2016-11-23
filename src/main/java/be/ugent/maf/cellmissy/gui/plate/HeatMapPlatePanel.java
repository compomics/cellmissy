/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.util.List;
import java.util.Map;

/**
 * An extension of the abstract plate panel to show the plate in the form of a
 * heat map.
 *
 * @author Paola
 */
public class HeatMapPlatePanel extends AbstractPlatePanel {

    // the experiment
    private Experiment experiment;
    // the third dimension of the heatmap, i.e. the quantity to show
    // this is computed in the controller class
    private Map<Well, Double> values;
    private double min;
    private double max;

    /**
     * set Experiment
     *
     * @param experiment
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setValues(Map<Well, Double> values) {
        this.values = values;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public Map<Well, Double> getValues() {
        return values;
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

        int length = GuiUtils.getAvailableColors().length;

        for (PlateCondition plateCondition : plateConditions) {
            for (Well well : plateCondition.getWellList()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                        int conditionIndex = plateConditions.indexOf(plateCondition);
                        int indexOfColor = conditionIndex % length;
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
        if (experiment != null && values != null) {
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
                        } else {
                            float[] RGBtoHSB = Color.RGBtoHSB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), null);
                            float blueHue = RGBtoHSB[0];
                            RGBtoHSB = Color.RGBtoHSB(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), null);
                            float redHue = RGBtoHSB[0];

                            for (WellGui wellGui : wellGuiList) {
                                if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {
                                    //get only the bigger default ellipse2D
                                    Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
                                    Double value = values.get(well);

                                    float hue = (float) (blueHue + (redHue - blueHue) * (value - min) / (max - min));
                                    int HSBtoRGB = Color.HSBtoRGB(hue, 0.85f, 0.9f);
                                    g2d.setColor(new Color(HSBtoRGB));

                                    g2d.fill(defaultWell);
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
