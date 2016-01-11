/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A class that extends the BoxAndWhiskerRenderer. It makes sure boxes are drawn
 * and filled with the right colors, and that shapes for outliers and farouters
 * points are not too big!
 *
 * @author Paola
 */
public class ExtendedBoxAndWhiskerRenderer extends BoxAndWhiskerRenderer {

    // the color for the outliers
    private final Color outlierPaint;
    // the color for the farout points
    private final Color farOutColor;

    /**
     * Constructor.
     *
     */
    public ExtendedBoxAndWhiskerRenderer() {
        super();
        this.outlierPaint = Color.GRAY;
        this.farOutColor = Color.GRAY;
    }

    /**
     * Draws the visual representation of a single data item when the plot has a
     * vertical orientation.
     *
     * @param g2 the graphics device.
     * @param state the renderer state.
     * @param dataArea the area within which the plot is being drawn.
     * @param plot the plot (can be used to obtain standard color information
     * etc).
     * @param domainAxis the domain axis.
     * @param rangeAxis the range axis.
     * @param dataset the dataset.
     * @param row the row index (zero-based).
     * @param column the column index (zero-based).
     */
    @Override
    public void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot,
              CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {

        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;
        }

        //Determine the catgory start and end.
        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;

        double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = categoryEnd - categoryStart;

        domainAxis.setCategoryMargin(0.25);

        rangeAxis.setUpperMargin(0.3);
        rangeAxis.setLowerMargin(0.3);
        rangeAxis.setLowerBound(-0.5);

        double xx = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();

        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin() / (categoryCount * (seriesCount - 1));
            double usedWidth = (state.getBarWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
            // offset the start of the boxes if the total width used is smaller
            // than the category width
            double offset = (categoryWidth - usedWidth) / 2;
            xx = xx + offset + (row * (state.getBarWidth() + seriesGap));
        } else {
            // offset the start of the box if the box width is smaller than the category width
            double offset = (categoryWidth - state.getBarWidth()) / 2;
            xx = xx + offset;
        }
        double xxmid = xx + state.getBarWidth() / 2.0;

        //Draw the box.
        Paint p = getItemPaint(row, column);
        if (p != null) {
            g2.setPaint(p);
        }
        Stroke s = getItemStroke(row, column);
        g2.setStroke(s);

        RectangleEdge location = plot.getRangeAxisEdge();
        Shape box = null;

        Number yQ1 = bawDataset.getQ1Value(row, column);
        Number yQ3 = bawDataset.getQ3Value(row, column);
        Number yMax = bawDataset.getMaxRegularValue(row, column);
        Number yMin = bawDataset.getMinRegularValue(row, column);

        if (yQ1 != null && yQ3 != null && yMax != null && yMin != null) {

            double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
            double yyQ3 = rangeAxis.valueToJava2D(yQ3.doubleValue(), dataArea, location);
            double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
            double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);

            // set the paint according to the right technical replicate
            int length = GuiUtils.getAvailableColors().length;
            int colorIndex = row % length;
            Color color = GuiUtils.getAvailableColors()[colorIndex];
            g2.setPaint(color);

            // draw the upper whisker
            g2.draw(new Line2D.Double(xxmid, yyMax, xxmid, yyQ3));
            g2.draw(new Line2D.Double(xx, yyMax, xx + state.getBarWidth(), yyMax));
            // draw the lower whisker
            g2.draw(new Line2D.Double(xxmid, yyMin, xxmid, yyQ1));
            g2.draw(new Line2D.Double(xx, yyMin, xx + state.getBarWidth(), yyMin));

            // draw the body
            box = new Rectangle2D.Double(xx, Math.min(yyQ1, yyQ3), state.getBarWidth(), Math.abs(yyQ1 - yyQ3));
            g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));

            if (getFillBox()) {
                g2.fill(box);
            }
            g2.draw(box);
        }

        // draw mean 
        g2.setPaint(getArtifactPaint());
        double yyAverage = 0.0;
        double aRadius = 2.0; // mean radius                       
        Number yMean = bawDataset.getMeanValue(row, column);
        if (yMean != null) {
            yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
            Ellipse2D.Double avgEllipse = new Ellipse2D.Double((xxmid - aRadius), (yyAverage - aRadius), aRadius * 2, aRadius * 2);
            g2.draw(avgEllipse);
        }

        //draw median
        double yyMedian = 0.0;
        Number yMedian = bawDataset.getMedianValue(row, column);
        if (yMedian != null) {
            yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
            g2.draw(new Line2D.Double(xx, yyMedian, xx + state.getBarWidth(), yyMedian));
        }

        //Outliers and Farouts		 			 
        double oRadius = 2.0; //outlier radius
        double foRadius = 1.0; //farout radius

        // From outlier array sort out which are outliers and put these into a 
        // list. If there are any farouts, add them to the farout list.	 
        // draw the outliers and farouts only if they are within the data area.
        double yyOutlier;
        double yyFarout;
        List outliers = new ArrayList();
        List farOutValues = new ArrayList();
        List yOutliers = bawDataset.getOutliers(row, column);
        if (yOutliers != null) {
            for (int i = 0; i < yOutliers.size(); i++) {
                Number outlierNum = (Number) yOutliers.get(i);
                double outlier = outlierNum.doubleValue();
                Number minOutlier = bawDataset.getMinOutlier(row, column);
                Number maxOutlier = bawDataset.getMaxOutlier(row, column);
                Number minRegular = bawDataset.getMinRegularValue(row, column);
                Number maxRegular = bawDataset.getMaxRegularValue(row, column);
                if (outlier > maxOutlier.doubleValue() || outlier < minOutlier.doubleValue()) {
                    yyFarout = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    Outlier faroutToAdd = new Outlier(xxmid, yyFarout, foRadius);
                    if (dataArea.contains(faroutToAdd.getPoint())) {
                        farOutValues.add(faroutToAdd);
                    }
                } else if (outlier > maxRegular.doubleValue() || outlier < minRegular.doubleValue()) {
                    yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    Outlier outlierToAdd = new Outlier(xxmid, yyOutlier, oRadius);
                    if (dataArea.contains(outlierToAdd.getPoint())) {
                        outliers.add(outlierToAdd);
                    }
                }
            }

            //draw the outliers
            g2.setPaint(this.outlierPaint);
            for (Iterator iterator = outliers.iterator(); iterator.hasNext();) {
                Outlier outlier = (Outlier) iterator.next();
                Point2D point = outlier.getPoint();
                Shape dot = createEllipse(point, oRadius);
                g2.draw(dot);
            }

            //draw the farout values
            g2.setPaint(this.farOutColor);
            for (Iterator iterator = farOutValues.iterator(); iterator.hasNext();) {
                Outlier outlier = (Outlier) iterator.next();
                Point2D point = outlier.getPoint();
                Shape triangle = createTriangleVertical(point, foRadius);
                g2.draw(triangle);
            }
        }
    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex the dataset index (zero-based).
     * @param series the series index (zero-based).
     *
     * @return The legend item.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }

        CategoryDataset dataset;
        dataset = cp.getDataset(datasetIndex);
        dataset.getRowCount();

        String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
        String description = label;
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        Shape shape = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);

        // set the paint according to the right technical replicate
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = series % length;

        Paint paint = GuiUtils.getAvailableColors()[colorIndex];
        Stroke outlineStroke = getItemOutlineStroke(datasetIndex, series);

        return new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, paint);
    }

    /**
     * Creates a dot to represent an outlier.
     *
     * @param point the location.
     * @param oRadius the outlier radius.
     *
     */
    private Shape createEllipse(Point2D point, double oRadius) {
        Ellipse2D dot = new Ellipse2D.Double(point.getX(), point.getY(), oRadius * 2.0, oRadius * 2.0);
        return dot;
    }

    /**
     * Creates a triangle to indicate the presence of far-out values when the
     * plot orientation is vertical.
     *
     * @param foRadius the farout radius.
     * @param point the location.
     */
    private Shape createTriangleVertical(Point2D point, double foRadius) {
        double side = foRadius * 2;
        double x = point.getX();
        double y = point.getY();

        int[] xpoints = {(int) (x), (int) (x + side), (int) (x + (side / 2.0))};
        int[] ypoints = {(int) (y), (int) (y), (int) (y + side)};

        return new Polygon(xpoints, ypoints, 3);
    }
}
