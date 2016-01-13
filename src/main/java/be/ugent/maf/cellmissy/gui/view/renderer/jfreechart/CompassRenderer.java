/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Paola
 */
public class CompassRenderer extends DefaultPolarItemRenderer {

    // the index of the technical replicate or condition
    private final int index;

    /**
     * Constructor.
     *
     * @param index
     */
    public CompassRenderer(int index) {
        this.index = index;
    }
    
    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {

        // compute the right color for the paint
        int length = GuiUtils.getAvailableColors().length;
        Color color = GuiUtils.getAvailableColors()[index % length];
        // get all the data points
        int numPoints = dataset.getItemCount(seriesIndex);
        
        for (int i = 0; i < numPoints; i++) {
            double theta = dataset.getXValue(seriesIndex, i); // the angle at the center         
            double radius = dataset.getYValue(seriesIndex, i); // the frequency

            Point p0 = plot.translateToJava2D(0, 0, plot.getAxis(), dataArea);
            Point p1 = plot.translateToJava2D(theta, radius, plot.getAxis(), dataArea);
            
            Line2D line = new Line2D.Double(p0, p1);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));
            g2.draw(line);
        }
        
    }
    
}
