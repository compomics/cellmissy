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

    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {

        g2.setPaint(Color.ORANGE);
        int numPoints = dataset.getItemCount(seriesIndex);
        for (int i = 0; i < numPoints; i++) {//
//            double theta1 = (double) dataset.getX(seriesIndex, i);
//            double x1 = radius * Math.cos(theta1);
//            double y1 = radius * Math.sin(theta1);
//
//            double theta2 = (double) dataset.getY(seriesIndex, i);
//            double x2 = radius * Math.cos(theta2);
//            double y2 = radius * Math.sin(theta2);
//
//            Line2D line = new Line2D.Double(x1, y1, x2, y2);
//            g2.setPaint(Color.blue);
//            g2.draw(line);

            double xCenter = dataset.getXValue(seriesIndex, i);
//            System.out.println("theta - xCenter: " + xCenter);
            double yCenter = dataset.getYValue(seriesIndex, i);
//            System.out.println("radius - yCenter: " + yCenter);

            double alphaCenter = (xCenter + yCenter) / 2;

            g2.setPaint(Color.orange);

//            double x = frequency * Math.cos(alphaCenter);
//            double y = frequency * Math.sin(alphaCenter);
//            
//            Ellipse2D el = new Ellipse2D.Double(x, y, 10, 10);
//            g2.fill(el);
//            double t = yCenter - xCenter;
//
//            double sectorRadius = (frequency / Math.PI) * (360 / t);
//
////            double t = -(phi + Math.PI * 3 / 2) % (2 * Math.PI);
//            double x1 = xCenter + sectorRadius * Math.cos(t);
//            double y1 = yCenter + sectorRadius * Math.sin(t);
//
////            t = -(phi + 10 + Math.PI * 3 / 2) % (2 * Math.PI);
//            double x2 = xCenter + sectorRadius * Math.cos(t);
//            double y2 = yCenter + sectorRadius * Math.sin(t);
//
            Point p = plot.translateValueThetaRadiusToJava2D(xCenter, yCenter,
                      dataArea);
            Ellipse2D el = new Ellipse2D.Double(alphaCenter, alphaCenter, 5, 5);
            g2.fill(el);
//            g2.setPaint(Color.orange);
//            Polygon triangle = new Polygon();
//            triangle.npoints = 3;
//            int[] xPoints = new int[]{(int) x1, (int) x2, (int) xCenter};
//            int[] yPoints = new int[]{(int) (frequency - y1), (int) (frequency - y2), (int) (frequency - yCenter)};
//            triangle.xpoints = xPoints;
//            triangle.ypoints = yPoints;
//            g2.fill(triangle);
//            g2.fillPolygon(new int[]{(int) x1, (int) x2, (int) xCenter}, new int[]{(int) (frequency - y1), (int) (frequency - y2), (int) (frequency - yCenter)}, 3);
        }
    }
}
