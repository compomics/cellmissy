/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 * This class extends a JFreeChart XYLineAndShapeRenderer and it is used to
 * customize the tracks plot. If a certain track is selected in the tracks list,
 * the correspondent series paint is set to a certain color, and all the tracks
 * left in the background are simply in shadow (colored in gray). Also, the
 * lines and points are controlled depending on check box selections. Finally,
 * endpoints of tracks can be shown or not depending on a boolean. If yes, a
 * list of integers is passed to the class, containing all the endpoints.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private final boolean plotLines; //control the lines
    private final boolean plotPoints; //control the points
    private final boolean showEndPoints; //control the endpoints
    private final List<Integer> endPoints; //integers for the endpoints
    private final int selectedTrackIndex; //index for the currently selected track
    private final float lineWidth; // thickness for the line
    private final boolean useCellMissyColor; // use colors of cellmissy?
    private Color chosenColor; // if a single color, the one chosen by the user

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
     * @param useCellMissyColor: use single color or not? - the one CellMissy
     * uses
     * @param useCustomColor: use single color or not? the one the user will
     * select
     */
    public TrackXYLineAndShapeRenderer(boolean plotLines, boolean plotPoints, boolean showEndPoints, List<Integer> endPoints,
              int selectedTrackIndex, float lineWidth, boolean useCellMissyColor) {
        this.plotLines = plotLines;
        this.plotPoints = plotPoints;
        this.showEndPoints = showEndPoints;
        this.endPoints = endPoints;
        this.selectedTrackIndex = selectedTrackIndex;
        this.lineWidth = lineWidth;
        this.useCellMissyColor = useCellMissyColor;
    }

    /**
     * Set the chosen color: this is chosen by the user through a Color Chooser
     * Dialog.
     *
     * @param chosenColor
     */
    public void setChosenColor(Color chosenColor) {
        this.chosenColor = chosenColor;
    }

    public Color getChosenColor() {
        return chosenColor;
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
        if (selectedTrackIndex != -1) { // a track has actually been selected
            if (selectedTrackIndex == series) { // this is the track to highlight
                return GuiUtils.getAvailableColors()[colorIndex];
            } else { // the other tracks will all be gray (background)
                return GuiUtils.getNonImagedColor();
            }
        } else { // no track is selected
            if (useCellMissyColor) {
                return chosenColor;
            } else {
                return GuiUtils.getAvailableColors()[colorIndex];
            }
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
