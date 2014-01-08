/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * This class contains some helpful JFreeChart utilities.
 *
 * @author Paola Masuzzo
 */
public class JFreeChartUtils {

    // default basic stroke: normal line
    private static BasicStroke normalLine = new BasicStroke(1.5f);
    // ticker basic stroke: wide line
    private static BasicStroke wideLine = new BasicStroke(2.5f);
    // dashed line
    private static BasicStroke dashedLine = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
    // font for the chart elements
    private static Font chartFont = new Font("Tahoma", Font.BOLD, 12);
    // line widths
    private static List<Float> lineWidths = Arrays.asList(1.0f, 1.5f, 2.0f, 2.5f, 3.0f);
    // decimal format for the plot axis
    private static DecimalFormat format = new DecimalFormat("####.##");

    /**
     * Getters
     *
     * @return
     */
    public static BasicStroke getNormalLine() {
        return normalLine;
    }

    public static BasicStroke getWideLine() {
        return wideLine;
    }

    public static BasicStroke getDashedLine() {
        return dashedLine;
    }

    public static List<Float> getLineWidths() {
        return lineWidths;
    }

    public static Font getChartFont() {
        return chartFont;
    }

    // public methods
    /**
     * This method is generating a chart for the density function given a
     * certain index for the condition, a xYSeriesCollection made up of the
     * density functions (x and y values) and a string for the main title.
     *
     * @param plateCondition
     * @param conditionIndex
     * @param xYSeriesCollection
     * @param chartTitle
     * @return
     */
    public static JFreeChart generateDensityFunctionChart(PlateCondition plateCondition, int conditionIndex, XYSeriesCollection xYSeriesCollection, String chartTitle) {
        String specificChartTitle = chartTitle + " Condition " + conditionIndex + " (replicates)";
        JFreeChart densityChart = ChartFactory.createXYLineChart(specificChartTitle, "% increase (Area)", "Density", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        densityChart.getTitle().setFont(chartFont);
        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        setupPlot(xYPlot);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xYSeriesCollection.getDomainLowerBound(true) - 0.05, xYSeriesCollection.getDomainUpperBound(true) + 0.05);
        xYPlot.getRangeAxis().setUpperBound(computeMaxY(xYSeriesCollection) + 0.05);
        xYPlot.setBackgroundPaint(Color.white);
        //renderer for wide line
        XYItemRenderer renderer = xYPlot.getRenderer();
        // get imaged wells and number of samples for each one
        List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
        int counter = 0;
        for (Well well : processedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfAreaAnalyzedSamplesPerWell(well);
            for (int i = counter; i < xYSeriesCollection.getSeriesCount(); i++) {
                // wide line
                renderer.setSeriesStroke(i, normalLine);
                String wellCoordinates = getWellCoordinates(xYSeriesCollection, i);
                int wellIndex = getWellIndex(wellCoordinates, processedWells);
                int length = GuiUtils.getAvailableColors().length;
                int colorIndex = wellIndex % length;
                renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[colorIndex]);
            }
            counter += numberOfSamplesPerWell;
        }
        return densityChart;
    }

    /**
     * Control shadow of JFreeChart
     *
     * @param chart
     * @param state
     */
    public static void setShadowVisible(final JFreeChart chart, final boolean state) {
        if (chart != null) {
            final Plot p = chart.getPlot();
            if (p instanceof XYPlot) {
                final XYPlot xyplot = (XYPlot) p;
                final XYItemRenderer xyItemRenderer = xyplot.getRenderer();
                if (xyItemRenderer instanceof XYBarRenderer) {
                    final XYBarRenderer br = (XYBarRenderer) xyItemRenderer;
                    br.setBarPainter(new StandardXYBarPainter());
                    br.setShadowVisible(state);
                }
            } else if (p instanceof CategoryPlot) {
                final CategoryPlot categoryPlot = (CategoryPlot) p;
                final CategoryItemRenderer categoryItemRenderer = categoryPlot.getRenderer();
                if (categoryItemRenderer instanceof BarRenderer) {
                    final BarRenderer br = (BarRenderer) categoryItemRenderer;
                    br.setBarPainter(new StandardBarPainter());
                    br.setShadowVisible(state);
                }
            }
        }
    }

    /**
     * Setup a xy plot
     *
     * @param xYPlot
     */
    public static void setupPlot(XYPlot xYPlot) {
        // set background to white and grid color to black
        xYPlot.setBackgroundPaint(Color.white);
        xYPlot.setRangeGridlinePaint(Color.black);
        // hide the border of the sorrounding box
        xYPlot.setOutlinePaint(Color.white);
        // get domanin and range axes
        NumberAxis domainAxis = (NumberAxis) xYPlot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) xYPlot.getRangeAxis();
        rangeAxis.setLabelPaint(Color.black);
        rangeAxis.setLabelFont(chartFont);
        domainAxis.setLabelPaint(Color.black);
        domainAxis.setLabelFont(chartFont);
        rangeAxis.setNumberFormatOverride(format);
        domainAxis.setNumberFormatOverride(format);
    }

    /**
     *
     * @param convexHullChart
     * @param trackIndex
     */
    public static void setupConvexHullChart(JFreeChart convexHullChart, int trackIndex) {
        // set title font
        convexHullChart.getTitle().setFont(chartFont);
        XYPlot xyPlot = convexHullChart.getXYPlot();
        setupPlot(xyPlot);
        xyPlot.setBackgroundPaint(new Color(177, 177, 60, 50));
        xyPlot.setOutlinePaint(new Color(177, 177, 60, 100));
        xyPlot.setOutlineStroke(wideLine);
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setDomainGridlinePaint(Color.black);
        // assign 2 renderers: one for the coordinates line and one for the convex hull plot
        XYLineAndShapeRenderer coordinatesRenderer = new XYLineAndShapeRenderer();
        coordinatesRenderer.setSeriesStroke(0, wideLine);
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        coordinatesRenderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
        // show both lines and points
        coordinatesRenderer.setSeriesLinesVisible(0, true);
        coordinatesRenderer.setSeriesShapesVisible(0, true);
        xyPlot.setRenderer(0, coordinatesRenderer);
        XYLineAndShapeRenderer convexHullRenderer = new XYLineAndShapeRenderer();
        convexHullRenderer.setSeriesStroke(0, dashedLine);
        convexHullRenderer.setSeriesPaint(0, Color.black);
        xyPlot.setRenderer(1, convexHullRenderer);
        XYSeriesCollection dataset = (XYSeriesCollection) xyPlot.getDataset(0);
        double minY = dataset.getSeries(0).getMinY();
        double maxY = dataset.getSeries(0).getMaxY();
        xyPlot.getRangeAxis().setRange(minY, maxY);
    }

    /**
     * Set up a combined domain chart. This chart uses a combined domain xy plot
     * as plot.
     *
     * @param combinedChart
     * @param trackIndex
     */
    public static void setupCombinedChart(JFreeChart combinedChart, int trackIndex) {
        CombinedDomainXYPlot combinedDomainXYPlot = (CombinedDomainXYPlot) combinedChart.getXYPlot();
        combinedDomainXYPlot.setBackgroundPaint(Color.white);
        combinedDomainXYPlot.setRangeGridlinePaint(Color.black);
        combinedDomainXYPlot.setOutlineStroke(wideLine);
        // modify renderer
        combinedDomainXYPlot.setRenderer(new XYLineAndShapeRenderer());
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) combinedDomainXYPlot.getRenderer();
        renderer.setSeriesStroke(0, wideLine);
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        renderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
        // show only line and no points
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
    }

    /**
     * Generate Series for (x,y).
     *
     * @param xValues
     * @param yValues
     * @return
     */
    public static XYSeries generateXYSeries(double[] xValues, double[] yValues) {
        // autosort False
        XYSeries series = new XYSeries("", false);
        for (int i = 0; i < yValues.length; i++) {
            double x = xValues[i];
            double y = yValues[i];
            series.add(x, y);
        }
        return series;
    }

    /**
     * Generate XYSeries from a 2D array
     *
     * @param coordinatesToPlot
     * @return
     */
    public static XYSeries generateXYSeries(Double[][] coordinatesToPlot) {
        // transpose the matrix
        Double[][] transposed = AnalysisUtils.transpose2DArray(coordinatesToPlot);
        // take first row: x coordinates
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[0]));
        // take second row: y coordinates
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[1]));
        // generate xy series for the plot
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xCoordinates, yCoordinates);
        return xySeries;
    }

    /**
     * Setup replicates area chart
     *
     * @param chart: chart to setup
     * @param wellList: keep track of wells added, removed: list needed
     * @param plotLines: show lines on plot?
     * @param plotLines: show points on plot?
     */
    public static void setupReplicatesAreaChart(JFreeChart chart, List<Well> wellList, boolean plotLines, boolean plotPoints) {
        // set title font
        chart.getTitle().setFont(chartFont);
        // put legend on the right edge
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        XYPlot xYPlot = chart.getXYPlot();
        setupPlot(xYPlot);
        // get the xyseriescollection from the plot
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) xYPlot.getDataset();
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xYPlot.getRenderer();
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, normalLine);
            // plot lines with colors according to well (replicate) index
            String wellCoordinates = getWellCoordinates(xYSeriesCollection, i);
            int wellIndex = getWellIndex(wellCoordinates, wellList);
            int length = GuiUtils.getAvailableColors().length;
            int colorIndex = wellIndex % length;
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[colorIndex]);
            // show lines?
            renderer.setSeriesLinesVisible(i, plotLines);
            // show points?
            renderer.setSeriesShapesVisible(i, plotPoints);
        }
    }

    /**
     * Setup global area chart
     *
     * @param chart: chart to setup
     * @param plotLines: show lines on plot?
     * @param plotLines: show points on plot?
     */
    public static void setupGlobalAreaChart(JFreeChart chart, boolean plotLines, boolean plotPoints) {
        // set title font
        chart.getTitle().setFont(chartFont);
        // get xyplot from the chart
        XYPlot xYPlot = chart.getXYPlot();
        setupPlot(xYPlot);
        // get the xyseriescollection from the plot
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) xYPlot.getDataset();
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xYPlot.getRenderer();
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, normalLine);
            // plot lines according to conditions indexes
            int length = GuiUtils.getAvailableColors().length;
            String conditionName = xYSeriesCollection.getSeriesKey(i).toString();
            String subsString = conditionName.substring(10);
            int conditionIndex = Integer.parseInt(subsString) - 1;
            int colorIndex = conditionIndex % length;
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[colorIndex]);
            // show lines?
            renderer.setSeriesLinesVisible(i, plotLines);
            // show points?
            renderer.setSeriesShapesVisible(i, plotPoints);
        }
    }

    /**
     * Set up a plot for track coordinates. Given the chart, set font for the
     * title, set outline and background paint and compute max range. The max
     * range is used to set both the axes and maintain the plot squared.
     *
     * @param chart: the chart to get the plot from.
     */
    public static void setupTrackChart(JFreeChart chart) {
        // set title font
        chart.getTitle().setFont(chartFont);
        XYPlot xYPlot = chart.getXYPlot();
        setupPlot(xYPlot);
        xYPlot.setBackgroundPaint(new Color(177, 177, 60, 50));
        xYPlot.setOutlinePaint(new Color(177, 177, 60, 100));
        xYPlot.setOutlineStroke(wideLine);
        xYPlot.setRangeGridlinePaint(Color.black);
        xYPlot.setDomainGridlinePaint(Color.black);
        Range maxRange = computeMaxRange(xYPlot);
        xYPlot.getDomainAxis().setRange(maxRange);
        xYPlot.getRangeAxis().setRange(maxRange);
    }

    /**
     * Plot error bars in both directions, vertical and horizontal
     *
     * @param chart
     * @param values
     * @param errors
     */
    public static void plotErrorBars(JFreeChart chart, XYSeries values, XYSeries errors) {
        Stroke stroke = new BasicStroke();
        Paint paint = Color.black;
        XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < values.getItemCount(); i++) {
            double x = values.getX(i).doubleValue();
            double y = values.getY(i).doubleValue();
            double dx = errors.getX(i).doubleValue();
            double dy = errors.getY(i).doubleValue();
            // add vertical and horizontal annotations on plot
            XYLineAnnotation vertical = new XYLineAnnotation(x, y - dy, x, y + dy, stroke, paint);
            plot.addAnnotation(vertical);
            XYLineAnnotation horizontal = new XYLineAnnotation(x - dx, y, x + dx, y, stroke, paint);
            plot.addAnnotation(horizontal);
        }
    }

    /**
     * Plot Error bars only on vertical direction
     *
     * @param chart
     * @param valuesCollection
     * @param verticalErrors
     */
    public static void plotVerticalErrorBars(JFreeChart chart, XYSeriesCollection valuesCollection, List<Double[]> verticalErrors) {
        Stroke stroke = new BasicStroke();
        // get the plot from the chart
        XYPlot plot = chart.getXYPlot();
        for (int i = 0; i < valuesCollection.getSeriesCount(); i++) {
            Double[] errors = verticalErrors.get(i);
            XYSeries values = valuesCollection.getSeries(i);
            for (int j = 0; j < values.getItemCount(); j++) {
                double x = values.getX(j).doubleValue();
                double y = values.getY(j).doubleValue();
                double dy = errors[j];
                // compute the right index of color to be used in the rendering
                int lenght = GuiUtils.getAvailableColors().length;
                String conditionName = valuesCollection.getSeriesKey(i).toString();
                String subString = conditionName.substring(10);
                int conditionIndex = Integer.parseInt(subString) - 1;
                int indexOfColor = conditionIndex % lenght;
                // add vertical annotation on plot
                XYLineAnnotation vertical = new XYLineAnnotation(x, y - dy, x, y + dy, stroke, GuiUtils.getAvailableColors()[indexOfColor]);
                plot.addAnnotation(vertical);
            }
        }
    }

    /**
     * Compute Max value of Y from a dataset.
     *
     * @param xYSeriesCollection: the dataset
     * @return
     */
    public static double computeMaxY(XYSeriesCollection xYSeriesCollection) {
        double maxY = 0;
        List<XYSeries> seriesList = xYSeriesCollection.getSeries();
        for (int i = 0; i < seriesList.size(); i++) {
            if (seriesList.get(i).getMaxY() > maxY) {
                maxY = seriesList.get(i).getMaxY();
            }
        }
        return maxY;
    }

    /**
     * Compute min value of Y from a dataset.
     *
     * @param xYSeriesCollection: the dataset
     * @return
     */
    public static double computeMinY(XYSeriesCollection xYSeriesCollection) {
        double minY = 0;
        List<XYSeries> seriesList = xYSeriesCollection.getSeries();
        for (int i = 0; i < seriesList.size(); i++) {
            if (seriesList.get(i).getMaxY() < minY) {
                minY = seriesList.get(i).getMinY();
            }
        }
        return minY;
    }

    /**
     * Add circle annotations on the track plot: an empty circle will annotate
     * the starting point of the track, while a filled one will annotate the end
     * point.
     *
     * @param plot: the plot to add the annotations on
     * @param seriesIndex: needed to get the right Color
     */
    public static void addCirclePointersOnTrackPlot(XYPlot plot, int seriesIndex) {
        Stroke stroke = new BasicStroke(1.5f);
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = seriesIndex % length;
        Color color = GuiUtils.getAvailableColors()[colorIndex];
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) plot.getDataset();
        XYSeries currentSeries = xYSeriesCollection.getSeries(seriesIndex);
        int itemCount = currentSeries.getItemCount();
        // get the first data item: first (x, y)
        XYDataItem firstDataItem = currentSeries.getDataItem(0);
        double firstX = firstDataItem.getXValue();
        double firstY = firstDataItem.getYValue();
        // get the last data item: last (x, y)
        XYDataItem lastDataItem = currentSeries.getDataItem(itemCount - 1);
        double lastX = lastDataItem.getXValue();
        double lastY = lastDataItem.getYValue();
        // size for the circle pointer
        double circleSize = 4;
        // first top left x and y
        int firstTopLeftX = (int) Math.round(firstX - circleSize / 2);
        int firstTopLeftY = (int) Math.round(firstY - circleSize / 2);
        // empty circle to annotate the starting point
        Ellipse2D emptyCircle = new Ellipse2D.Double(firstTopLeftX, firstTopLeftY, circleSize, circleSize);
        XYShapeAnnotation emptyCircleAnnotation = new XYShapeAnnotation(emptyCircle, stroke, color);
        // last top left x and y
        int lastTopLeftX = (int) Math.round(lastX - circleSize / 2);
        int lastTopLeftY = (int) Math.round(lastY - circleSize / 2);
        // filled circle to annotate the end point
        Ellipse2D filledCircle = new Ellipse2D.Double(lastTopLeftX, lastTopLeftY, circleSize, circleSize);
        XYShapeAnnotation filledCircleAnnotation = new XYShapeAnnotation(filledCircle, stroke, color, color);
        // add the two annotations on the plot
        plot.getRenderer().addAnnotation(emptyCircleAnnotation);
        plot.getRenderer().addAnnotation(filledCircleAnnotation);
    }

    /**
     * Set up the single track plot.
     *
     * @param chart: the chart to get the plot from
     * @param trackIndex: we need this to get the right color
     * @param inTime: if true, the plot is in time, thus background is set to
     * white and range does not have to be kept squared
     */
    public static void setupSingleTrackPlot(JFreeChart chart, int trackIndex, boolean inTime) {
        // set up the plot
        XYPlot xyPlot = chart.getXYPlot();
        setupPlot(xyPlot);
        if (!inTime) {
            setupTrackChart(chart);
        }
        xyPlot.setOutlineStroke(wideLine);
        // set title font
        chart.getTitle().setFont(chartFont);
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        renderer.setSeriesStroke(0, wideLine);
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        renderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
        // show only line and no points
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
    }

    /**
     * Given a plot, compute the greatest range between the domain and the range
     * axes.
     *
     * @param plot
     * @return the Range
     */
    private static Range computeMaxRange(XYPlot plot) {
        Range domain = plot.getDataRange(plot.getDomainAxis());
        Range range = plot.getDataRange(plot.getRangeAxis());
        double lowerBound = Math.min(domain.getLowerBound(), range.getLowerBound());
        double upperdBound = Math.max(domain.getUpperBound(), range.getUpperBound());
        if (lowerBound < 0) {
            return new Range(lowerBound - 20, upperdBound + 20);
        } else {
            return new Range(lowerBound + 20, upperdBound + 20);
        }
    }

    /**
     * Given a list of wells and one well's coordinate, get the index of the
     * well in the List.
     *
     * @param wellCoordinates
     * @param wellList
     * @return
     */
    private static int getWellIndex(String wellCoordinates, List<Well> wellList) {
        int wellIndex = 0;
        for (Well well : wellList) {
            if (well.toString().equals(wellCoordinates)) {
                wellIndex = wellList.indexOf(well);
            }
        }
        return wellIndex;
    }

    /**
     * Get well coordinates from series in oder to render the lines colour
     *
     * @param xYSeriesCollection
     * @param indexOfSeries
     * @return
     */
    private static String getWellCoordinates(XYSeriesCollection xYSeriesCollection, int indexOfSeries) {
        String toString = xYSeriesCollection.getSeriesKey(indexOfSeries).toString();
        int lastIndexOf = toString.lastIndexOf(")");
        return toString.substring(0, lastIndexOf + 1);
    }
}
