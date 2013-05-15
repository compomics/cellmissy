/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class is used in the loading data step: imaged wells are shown with related imaging types
 *
 * @author Paola Masuzzo
 */
public class ImagedPlatePanel extends AbstractPlatePanel {

    private List<ImagingType> imagingTypeList;
    private ImagingType currentImagingType;
    private Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap;
    private Experiment experiment;

    /**
     * getters and setters
     *
     * @return
     */
    public List<ImagingType> getImagingTypeList() {
        return imagingTypeList;
    }

    public void setImagingTypeList(List<ImagingType> imagingTypeList) {
        this.imagingTypeList = imagingTypeList;
    }

    public ImagingType getCurrentImagingType() {
        return currentImagingType;
    }

    public void setCurrentImagingType(ImagingType currentImagingType) {
        this.currentImagingType = currentImagingType;
    }

    public void setAlgoMap(Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap) {
        this.algoMap = algoMap;
    }

    public Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> getAlgoMap() {
        return algoMap;
    }

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
        List<PlateCondition> plateConditions = new ArrayList<>();
        plateConditions.addAll(experiment.getPlateConditionCollection());

        for (PlateCondition plateCondition : plateConditions) {
            for (Well well : plateCondition.getWellCollection()) {
                for (WellGui wellGui : wellGuiList) {
                    if (wellGui.getRowNumber() == well.getRowNumber() && wellGui.getColumnNumber() == well.getColumnNumber()) {

                        int lenght = GuiUtils.getAvailableColors().length;
                        int conditionIndex = plateConditions.indexOf(plateCondition);
                        int indexOfColor = conditionIndex % lenght;
                        Color color = GuiUtils.getAvailableColors()[indexOfColor];
                        g2d.setColor(color);

                        int x = (int) wellGui.getEllipsi().get(0).getX() - ImagedPlatePanel.pixelsGrid / 4;
                        int y = (int) wellGui.getEllipsi().get(0).getY() - ImagedPlatePanel.pixelsGrid / 4;

                        int width = (int) wellGui.getEllipsi().get(0).getWidth() + ImagedPlatePanel.pixelsGrid / 2;
                        int height = (int) wellGui.getEllipsi().get(0).getHeight() + ImagedPlatePanel.pixelsGrid / 2;

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
     * render wells Override method from Abstract Plate Panel: if wells have already been rendered, redraw them taking into account full color for imaged wells
     *
     * @param g
     */
    @Override
    protected void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.setGraphics(g2d);
        // a list of WellGui objects is present, iterate through it
        for (WellGui wellGui : wellGuiList) {
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();
            // iterate through the circles
            for (int i = 0; i < ellipsi.size(); i++) {
                Ellipse2D ellipse2D = ellipsi.get(i);

                // if a color of a wellGui has been changed, keep track of it when resizing
                // if a well was not imaged, set its color to the default one (just redraw the well)
                if (wellGui.getWell().getWellHasImagingTypeCollection().isEmpty()) {
                    Color defaultColor = GuiUtils.getDefaultColor();
                    g2d.setColor(defaultColor);
                    g2d.draw(ellipse2D);
                } else {
                    // if it has been imaged, set its color to a different one (for each ellipse2D present)
                    // this time we need to call a fill and not a draw anymore
                    List<ImagingType> uniqueImagingTypes = getUniqueImagingTypes(wellGui.getWell().getWellHasImagingTypeCollection());
                    // how many imaging types we have?
                    int length = GuiUtils.getImagingTypeColors().length;
                    ImagingType currentImagingType = uniqueImagingTypes.get(i);
                    int indexOfImagingType = imagingTypeList.indexOf(currentImagingType);
                    int indexOfColor = indexOfImagingType % length;
                    Color color = GuiUtils.getImagingTypeColors()[indexOfColor];
                    g2d.setColor(color);
                    g2d.fill(ellipse2D);
                }
            }

            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                Color defaultColor = GuiUtils.getDefaultColor();
                g2d.setColor(defaultColor);
                drawPlateLabel(ellipsi.get(0), g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
            }
        }
    }

    /**
     * get Unique imaging types of a wellHasImagingType
     *
     * @param wellHasImagingTypes
     * @return
     */
    public List<ImagingType> getUniqueImagingTypes(Collection<WellHasImagingType> wellHasImagingTypes) {
        List<ImagingType> imagingTypes = new ArrayList<>();

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (!imagingTypes.contains(wellHasImagingType.getImagingType())) {
                imagingTypes.add(wellHasImagingType.getImagingType());
            }
        }
        return imagingTypes;
    }
}
