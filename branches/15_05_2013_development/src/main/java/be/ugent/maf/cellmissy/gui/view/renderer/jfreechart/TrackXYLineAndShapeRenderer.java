/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    // show the lines?
    private boolean plotLines;
    // show the points?
    private boolean plotPoints;
    // this is the index for the current series (i.e. the track to highlight in the plot)
    private int trackSeriesIndex;

    /**
     * Constructor
     *
     * @param plotLines
     * @param plotPoints
     * @param trackSeriesIndex
     */
    public TrackXYLineAndShapeRenderer(boolean plotLines, boolean plotPoints, int trackSeriesIndex) {
        this.plotLines = plotLines;
        this.plotPoints = plotPoints;
        this.trackSeriesIndex = trackSeriesIndex;
    }

    @Override
    public Paint getSeriesPaint(int series) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = series % length;
        if (trackSeriesIndex != -1) {
            if (trackSeriesIndex == series) {
                return GuiUtils.getAvailableColors()[colorIndex];
            } else {
                return GuiUtils.getNonImagedColor();
            }
        } else {
            return GuiUtils.getAvailableColors()[colorIndex];
        }
    }

    @Override
    public Boolean getSeriesLinesVisible(int series) {
        return plotLines;
    }

    @Override
    public Boolean getSeriesShapesVisible(int series) {
        return plotPoints;
    }

    @Override
    public Stroke getSeriesStroke(int series) {
        if (trackSeriesIndex != -1) {
            if (trackSeriesIndex == series) {
                return JFreeChartUtils.getWideLine();
            } else {
                return JFreeChartUtils.getNormalLine();
            }
        } else {
            return JFreeChartUtils.getNormalLine();
        }
    }
}
