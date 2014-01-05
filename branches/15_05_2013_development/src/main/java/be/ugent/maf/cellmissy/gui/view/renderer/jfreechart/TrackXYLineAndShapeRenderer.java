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
import java.util.List;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 * This class extends a JFreeChart XYLineAndShapeRenderer and it is used to
 * customise the tracks plot. If a certain track is selected in the tracks list,
 * the correspondent series paint is set to a certain color, and all the tracks
 * left in the background are simply in shadow (coloured in gray). Also, the
 * lines and points are controlled depending on check box selections. Finally,
 * endpoints of tracks can be shown or not depending on a boolean. If yes, a
 * list of integers is passed to the class, containing all the endpoints.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private boolean plotLines; //control the line
    private boolean plotPoints; //control the points
    private boolean showEndPoints; //control the endpoints
    private List<Integer> endPoints; //integers for the endpoints
    private int selectedTrackIndex; //index for the currently selected track
    private float lineWidth; // thickness for the line

    /**
     * Constructor
     *
     * @param plotLines: render lines on the plot?
     * @param plotPoints: render points on the plot?
     * @param showEndPoints: show the endpoints in the plot?
     * @param endPoints: which endpoints do we need to show?
     * @param selectedTrackIndex: index for the current series (i.e. the track
     * to highlight in the plot)
     * @param lineWidth: thickness to render the line
     */
    public TrackXYLineAndShapeRenderer(boolean plotLines, boolean plotPoints, boolean showEndPoints, List<Integer> endPoints, int selectedTrackIndex, float lineWidth) {
        this.plotLines = plotLines;
        this.plotPoints = plotPoints;
        this.showEndPoints = showEndPoints;
        this.endPoints = endPoints;
        this.selectedTrackIndex = selectedTrackIndex;
        this.lineWidth = lineWidth;
    }

    @Override
    public boolean getItemShapeVisible(int series, int item) {
        // if we want to show the endpoints, we only show them (not the other points)
        if (showEndPoints) {
            Integer endPoint = endPoints.get(series);
            // when we are at the right time point (endpoint), we show it
            if (item == endPoint) {
                return Boolean.TRUE;
            } else {
                // otherwise, only the line
                return Boolean.FALSE;
            }
        } else {
            // if we don't want to show the endpoint, return the super
            return super.getItemShapeVisible(series, item);
        }
    }

    @Override
    public Paint getSeriesPaint(int series) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = series % length;
        if (selectedTrackIndex != -1) {
            if (selectedTrackIndex == series) {
                return GuiUtils.getAvailableColors()[colorIndex];
            } else {
                return GuiUtils.getNonImagedColor();
            }
        } else {
            return GuiUtils.getAvailableColors()[colorIndex];
        }
    }

    @Override
    public Shape getItemShape(int row, int column) {
        // if we want to show the endpoints, return a cross to visualize the endpoint in the track
        if (showEndPoints) {
            return ShapeUtilities.createRegularCross(4, 2);
        }
        // else, return the super
        return super.getItemShape(row, column);
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
        return new BasicStroke(lineWidth);
    }
}
