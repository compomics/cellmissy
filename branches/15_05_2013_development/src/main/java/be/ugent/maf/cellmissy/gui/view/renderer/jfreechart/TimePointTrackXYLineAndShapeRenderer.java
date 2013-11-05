/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Paint;
import java.awt.Shape;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 * Renderer for a track x y plot, that will (on top on normal rendering) also
 * shows the time information.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TimePointTrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private int selectedTrackIndex;
    private int timePoint;

    /**
     * Constructor
     *
     * @param selectedTrackIndex: the index of the current series (the track to
     * highlight in the plot)
     * @param timePoint: the time point to highlight in the track
     */
    public TimePointTrackXYLineAndShapeRenderer(int selectedTrackIndex, int timePoint) {
        this.selectedTrackIndex = selectedTrackIndex;
        this.timePoint = timePoint;
    }

    @Override
    public Paint getSeriesPaint(int series) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = selectedTrackIndex % length;
        // we are in the right series to highlight: use the correspondent color
        if (series == selectedTrackIndex) {
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
        if (series == selectedTrackIndex && item == timePoint) {
            return Boolean.TRUE;
        } else {
            // otherwise, only the line
            return Boolean.FALSE;
        }
    }

    @Override
    public Shape getItemShape(int row, int column) {
        // create a cross diagonal shape to visualize the spot in the track
        Shape cellShape = ShapeUtilities.createDiagonalCross(3, 1);
        return cellShape;
    }
}
