/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 * Renderer for a track x y plot, that will (on top on normal rendering) also
 * shows the time information, i.e. the cells is followed in time.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TimePointTrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private final int selectedTrackIndex; //track currently selected in list
    private final int timePoint; //current time point to highlight in the track
    private final float lineWidth; //thickness to use to render the line

    /**
     * Constructor
     *
     * @param selectedTrackIndex: the index of the current series (the track to
     * highlight in the plot)
     * @param timePoint: the time point to highlight in the track
     * @param lineWidth: the thickness to render the line in plot
     */
    public TimePointTrackXYLineAndShapeRenderer(int selectedTrackIndex, int timePoint, float lineWidth) {
        this.selectedTrackIndex = selectedTrackIndex;
        this.timePoint = timePoint;
        this.lineWidth = lineWidth;
    }

    @Override
    public Paint getSeriesPaint(int series) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = series % length;
        if (selectedTrackIndex != -1) {
            // we are in the right series to highlight: use the correspondent color
            if (series == selectedTrackIndex) {
                return GuiUtils.getAvailableColors()[colorIndex];
            } else {
                // not at the right series, take the non imaged color
                // (basically, show the other tracks in gray)
                return GuiUtils.getNonImagedColor();
            }
        } else {
            return GuiUtils.getAvailableColors()[colorIndex];
        }
    }

    @Override
    public boolean getItemShapeVisible(int series, int item) {
        if (selectedTrackIndex != -1) {
            // when we are at the right series and time point(item), we show the point
            if (series == selectedTrackIndex && item == timePoint) {
                return Boolean.TRUE;
            } else {
                // otherwise, only the line
                return Boolean.FALSE;
            }
        } else {
            return Boolean.TRUE;
        }
    }

    @Override
    public Shape getItemShape(int row, int column) {
        // return a cross diagonal shape to visualize the spot in the track
        return ShapeUtilities.createDiagonalCross(3, 1);
    }

    @Override
    public Stroke getSeriesStroke(int series) {
        return new BasicStroke(lineWidth);
    }
}
