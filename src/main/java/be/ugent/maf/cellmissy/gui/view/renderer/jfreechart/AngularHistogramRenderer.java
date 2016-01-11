/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Paola
 */
public class AngularHistogramRenderer extends DefaultPolarItemRenderer {

    private final List<Double> frequencies;

    public AngularHistogramRenderer(List<Double> frequencies) {
        this.frequencies = frequencies;
    }

    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {

        int numPoints = dataset.getItemCount(seriesIndex);
        for (int i = 0; i < numPoints; i++) {
            double radius = frequencies.get(i);

            double theta1 = (double) dataset.getX(seriesIndex, i);
            double x1 = radius * Math.cos(theta1);
            double y1 = radius * Math.sin(theta1);

            double theta2 = (double) dataset.getY(seriesIndex, i);
            double x2 = radius * Math.cos(theta2);
            double y2 = radius * Math.sin(theta2);

            Line2D line = new Line2D.Double(x1, y1, x2, y2);
            g2.setPaint(Color.blue);
            g2.draw(line);
        }
    }
}
