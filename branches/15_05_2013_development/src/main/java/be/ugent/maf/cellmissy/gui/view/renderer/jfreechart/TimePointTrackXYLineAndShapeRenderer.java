/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 * Renderer for a x y plot, to show the time information.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TimePointTrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    // this is the time point to highlight
    private int currentTimePoint;
    // this is the index for the current series (i.e. the track to highlight in the plot)
    private int trackSeriesIndex;

    /**
     * Constructor
     *
     * @param currentTimePoint
     * @param trackSeriesIndex
     */
    public TimePointTrackXYLineAndShapeRenderer(int currentTimePoint, int trackSeriesIndex) {
        this.currentTimePoint = currentTimePoint;
        this.trackSeriesIndex = trackSeriesIndex;
    }

    @Override
    public Paint getSeriesPaint(int series) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackSeriesIndex % length;
        // we are in the right series to highlight: use the correspondent color
        if (series == trackSeriesIndex) {
            return GuiUtils.getAvailableColors()[colorIndex];
        } else {
            // not at the right series, take the non imaged color
            // (basically, show the other tracks in grey)
            return GuiUtils.getNonImagedColor();
        }
    }

    @Override
    public boolean getItemShapeVisible(int series, int item) {
        // when we are at the right series and time point, we how the point
        if (series == trackSeriesIndex && item == currentTimePoint) {
            return Boolean.TRUE;
        } else {
            // otherwise, only the line
            return Boolean.FALSE;
        }
    }

//    @Override
//    public Stroke getSeriesStroke(int series) {
//        // when we are at the right series, we use a tick line
//        if (series == trackSeriesIndex) {
//            return JFreeChartUtils.getWideLine();
//        } else {
//            // else, we use a thin line
//            return JFreeChartUtils.getNormalLine();
//        }
//    }

    @Override
    public Shape getItemShape(int row, int column) {
        // create a cross diagonal shape to visualize the spot in the track
        Shape cellShape = ShapeUtilities.createDiagonalCross(3, 1);
        return cellShape;
    }
}
