/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * This class extends a JFreeChart XYLineAndShapeRenderer and it is used to
 * customise the tracks plot. If a certain track is selected in the tracks list,
 * the correspondent series paint is set to a certain color, and all the tracks
 * left in the background are simply in shadow (coloured in gray). Also, the
 * lines and points are controlled depending on check box selections.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private boolean plotLines;
    private boolean plotPoints;
    private int trackSeriesIndex;
    private float lineWidth;

    /**
     * Constructor
     *
     * @param plotLines: render lines on the plot?
     * @param plotPoints: render points on the plot?
     * @param trackSeriesIndex: index for the current series (i.e. the track to
     * highlight in the plot)
     */
    public TrackXYLineAndShapeRenderer(boolean plotLines, boolean plotPoints, int trackSeriesIndex, float lineWidth) {
        this.plotLines = plotLines;
        this.plotPoints = plotPoints;
        this.trackSeriesIndex = trackSeriesIndex;
        this.lineWidth = lineWidth;
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
        return new BasicStroke(lineWidth);
    }
}
