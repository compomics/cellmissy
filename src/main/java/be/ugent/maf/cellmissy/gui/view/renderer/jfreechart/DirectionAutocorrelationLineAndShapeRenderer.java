/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author Paola
 */
public class DirectionAutocorrelationLineAndShapeRenderer extends XYLineAndShapeRenderer {

    private final int trackIndex;

    /**
     * Constructor.
     *
     * @param trackIndex
     */
    public DirectionAutocorrelationLineAndShapeRenderer(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    @Override
    public Paint getItemPaint(int row, int column) {
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        Paint paint;
        if (row == 0) {
            paint = GuiUtils.getAvailableColors()[colorIndex];
        } else {
            double yValue = getPlot().getDataset().getYValue(row, column);
            paint = colorFor((yValue));
        }
        return paint;
    }

    @Override
    public Boolean getSeriesLinesVisible(int series) {
        if (series == 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Stroke getSeriesStroke(int series) {
        return JFreeChartUtils.getWideLine();
    }

    @Override
    public Shape getSeriesShape(int series) {
        return ShapeUtilities.createDiagonalCross(2, 1);
    }

    @Override
    public Boolean getSeriesShapesFilled(int series) {
        return Boolean.TRUE;
    }

    /**
     * Get a color from the y value.
     *
     * @param value
     * @return
     */
    private static Color colorFor(double value) {
        float saturation = 0.8f; //saturation
        float brightness = 0.7f; //brightness
        float hue = 0.2f * (float) value;//hue

        Color color = Color.getHSBColor(hue, saturation, brightness);
        return color;
    }
}
