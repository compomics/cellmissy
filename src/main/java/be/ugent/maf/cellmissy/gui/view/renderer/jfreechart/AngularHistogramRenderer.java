/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarAxisLocation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * A renderer to display angle histogram in a polar-rose plot.
 *
 * @author Paola
 */
public class AngularHistogramRenderer extends DefaultPolarItemRenderer {

    // the index of the technical replicate or condition
    private final int index;
    // the size of the bin
    private final int binSize;

    public AngularHistogramRenderer(int index, int binSize) {
        this.index = index;
        this.binSize = binSize;
    }

    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {

        // set the origin
        plot.setCounterClockwise(true);
        plot.setAxisLocation(PolarAxisLocation.EAST_BELOW);
        plot.setAngleOffset(0);

        // compute the right color for the paint
        int length = GuiUtils.getAvailableColors().length;
        Color color = GuiUtils.getAvailableColors()[index % length];
        // get all the data points
        int numPoints = dataset.getItemCount(seriesIndex);

        for (int i = 0; i < numPoints; i++) {
            double theta = dataset.getXValue(seriesIndex, i); // the angle at the center         
            double radius = dataset.getYValue(seriesIndex, i); // the frequency

            Point p0 = plot.translateToJava2D(0, 0, plot.getAxis(), dataArea);
            Point p1 = plot.translateToJava2D(theta - binSize, radius, plot.getAxis(), dataArea);
            Point p2 = plot.translateToJava2D(theta + binSize, radius, plot.getAxis(), dataArea);

            Polygon poly = new Polygon(new int[]{p0.x, p1.x, p2.x}, new int[]{p0.y, p1.y, p2.y}, 3);

            g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));
            g2.fill(poly);
        }
    }
}
