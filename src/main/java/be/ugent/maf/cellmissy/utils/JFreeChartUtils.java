/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
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
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains some helpful JFreeChart utilities.
 *
 * @author Paola Masuzzo
 */
public class JFreeChartUtils {

    // default basic stroke: normal line
    private static final BasicStroke normalLine = new BasicStroke(1.5f);
    // ticker basic stroke: wide line
    private static final BasicStroke wideLine = new BasicStroke(2.5f);
    // new line, enter
    private final static String newLine = "\n";
    // dashed line
    private static final BasicStroke dashedLine = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
    // font for the chart elements
    private static final Font chartFont = new Font("Tahoma", Font.BOLD, 12);
    private static final Font smallChartFont = new Font("Tahoma", Font.BOLD, 12);
    // line widths
    private static final List<Float> lineWidths = Arrays.asList(1.0f, 1.5f, 2.0f, 2.5f, 3.0f);
    // decimal format for the plot axis
    private static final DecimalFormat format = new DecimalFormat("####.##");

    public static BasicStroke getWideLine() {
        return wideLine;
    }

    public static String getNewLine() {
        return newLine;
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
     * Setup a x-y plot
     *
     * @param xYPlot
     */
    public static void setupXYPlot(XYPlot xYPlot) {
        // set background to white and grid color to black
        xYPlot.setBackgroundPaint(Color.white);
//        xYPlot.setRangeGridlinePaint(Color.black);
        // hide the border of the sorrounding box
        xYPlot.setOutlinePaint(Color.white);
        // get domanin and range axes
        NumberAxis domainAxis = (NumberAxis) xYPlot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) xYPlot.getRangeAxis();
        // set their label font and color
        rangeAxis.setLabelPaint(Color.black);
        rangeAxis.setLabelFont(chartFont);
        domainAxis.setLabelPaint(Color.black);
        domainAxis.setLabelFont(chartFont);
        // override number format for the axes
        rangeAxis.setNumberFormatOverride(format);
        domainAxis.setNumberFormatOverride(format);
    }

    /**
     * This method is generating a chart for the density function given a
     * certain index for the condition, a xYSeriesCollection made up of the
     * density functions (x and y values) and a string for the main title.
     *
     * @param plateCondition
     * @param conditionIndex
     * @param xYSeriesCollection
     * @param chartTitle
     * @return the chart
     */
    public static JFreeChart generateDensityFunctionChart(PlateCondition plateCondition, int conditionIndex,
            XYSeriesCollection xYSeriesCollection, String chartTitle) {
        String specificChartTitle = chartTitle + " Condition " + conditionIndex + " (replicates)";
        JFreeChart densityChart = ChartFactory.createXYLineChart(specificChartTitle, "% increase (Area)", "Density",
                xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        densityChart.getTitle().setFont(chartFont);
        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        setupXYPlot(xYPlot);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xYSeriesCollection.getDomainLowerBound(true) - 0.05, xYSeriesCollection
                .getDomainUpperBound(true) + 0.05);
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
     * This method is generating a chart for the density function given a
     * certain index for the condition, a xYSeriesCollection made up of the
     * density functions (x and y values) and a string for the main title.
     *
     * @param singleCellConditionDataHolder
     * @param xYSeriesCollection
     * @param chartTitle
     * @param xLabel
     * @return the chart
     */
    public static JFreeChart generateDensityFunctionChart(SingleCellConditionDataHolder singleCellConditionDataHolder,
            XYSeriesCollection xYSeriesCollection, String chartTitle, String xLabel, boolean legend) {
        String specificChartTitle = chartTitle + " - " + singleCellConditionDataHolder.getPlateCondition() + " (replicates)";
        JFreeChart densityChart = ChartFactory.createXYLineChart(specificChartTitle, xLabel, "Density",
                xYSeriesCollection, PlotOrientation.VERTICAL, legend, true, false);
        densityChart.getTitle().setFont(chartFont);
        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        setupXYPlot(xYPlot);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xYSeriesCollection.getDomainLowerBound(true) - 0.05, xYSeriesCollection
                .getDomainUpperBound(true) + 0.05);
        xYPlot.getRangeAxis().setUpperBound(computeMaxY(xYSeriesCollection) + 0.05);
        xYPlot.setBackgroundPaint(Color.white);
        //renderer for wide line
        XYItemRenderer renderer = xYPlot.getRenderer();
        // get imaged wells and number of samples for each one
        List<Well> processedWells = singleCellConditionDataHolder.getPlateCondition().getSingleCellAnalyzedWells();
        int counter = 0;
        for (Well well : processedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSingleCellAnalyzedSamplesPerWell(well);
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
     * 
     * @param xYSeriesCollection
     * @param chartTitle
     * @param xLabel
     * @param legend
     * @return 
     */
    public static JFreeChart generateDensityFunctionChart(XYSeriesCollection xYSeriesCollection, String chartTitle, String xLabel, boolean legend) {
        String specificChartTitle = chartTitle + " - " + " (conditions)";
        JFreeChart densityChart = ChartFactory.createXYLineChart(specificChartTitle, xLabel, "Density",
                xYSeriesCollection, PlotOrientation.VERTICAL, legend, true, false);
        densityChart.getTitle().setFont(chartFont);
        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        setupXYPlot(xYPlot);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xYSeriesCollection.getDomainLowerBound(true) - 0.05, xYSeriesCollection
                .getDomainUpperBound(true) + 0.05);
        xYPlot.getRangeAxis().setUpperBound(computeMaxY(xYSeriesCollection) + 0.05);
        xYPlot.setBackgroundPaint(Color.white);
        //renderer for wide line
        XYItemRenderer renderer = xYPlot.getRenderer();

        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, normalLine);
            int length = GuiUtils.getAvailableColors().length;
            int colorIndex = i % length;
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[colorIndex]);
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
     * Generate a series for a (x, y) plot, given two arrays of double values.
     *
     * @param xValues
     * @param yValues
     * @return the series
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
     * Generate a series for a (x, y) plot, given a single x double value, and
     * an array of y double values.
     *
     * @param xValue
     * @param yValues
     * @return the series
     */
    public static XYSeries generateXYSeries(double xValue, double[] yValues) {
        // autosort False
        XYSeries series = new XYSeries("", false);
        for (double y : yValues) {
            series.add(xValue, y);
        }
        return series;
    }

    /**
     * Generate a series for a (x, y) plot, given single x and y double values.
     *
     * @param xValue
     * @param yValue
     * @return the series
     */
    public static XYSeries generateXYSeries(double xValue, double yValue) {
        // autosort False
        XYSeries series = new XYSeries("", false);
        series.add(xValue, yValue);
        return series;
    }

    /**
     * Generate a series for a (x, y) plot, a 2D array containing both x and y
     * double values.
     *
     * @param coordinatesToPlot
     * @return the series
     */
    public static XYSeries generateXYSeries(Double[][] coordinatesToPlot) {
        // transpose the matrix
        Double[][] transposed = AnalysisUtils.transpose2DArray(coordinatesToPlot);
        // take first row: x coordinates
        double[] xCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[0]));
        // take second row: y coordinates
        double[] yCoordinates = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[1]));
        // generate xy series for the plot
        return JFreeChartUtils.generateXYSeries(xCoordinates, yCoordinates);
    }

    public static XYSeries generateXYSeries(double[][] data) {
        // transpose the array
        double[][] transposed = AnalysisUtils.transpose2DArray(data);
        return JFreeChartUtils.generateXYSeries(transposed[0], transposed[1]);
    }

    /**
     * Setup replicates area chart
     *
     * @param chart: chart to setup
     * @param wellList: keep track of wells added, removed: list needed
     * @param plotLines: show lines on plot?
     * @param plotPoints
     */
    public static void setupReplicatesChart(JFreeChart chart, List<Well> wellList, boolean plotLines, boolean plotPoints) {
        // set title font
        chart.getTitle().setFont(chartFont);
        // put legend on the right edge
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        XYPlot xYPlot = chart.getXYPlot();
        setupXYPlot(xYPlot);
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
     */
    public static void setupGlobalAreaChart(JFreeChart chart, boolean plotLines, boolean plotPoints) {
        // set title font
        chart.getTitle().setFont(chartFont);
        // get xyplot from the chart
        XYPlot xYPlot = chart.getXYPlot();
        setupXYPlot(xYPlot);
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
     *
     * @param chart
     * @param conditions
     * @param plotLines
     * @param plotPoints
     */
    public static void setupConditionsChart(JFreeChart chart, List<PlateCondition> conditions, boolean plotLines, boolean plotPoints) {
        // set title font
        chart.getTitle().setFont(chartFont);
        // get xyplot from the chart
        XYPlot xYPlot = chart.getXYPlot();
        setupXYPlot(xYPlot);
        // get the xyseriescollection from the plot
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) xYPlot.getDataset();
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xYPlot.getRenderer();
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, normalLine);
            // plot lines according to conditions indexes
            int colorIndex = i % GuiUtils.getAvailableColors().length;
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
        setupXYPlot(xYPlot);
        xYPlot.setBackgroundPaint(Color.white);
        xYPlot.setOutlineStroke(wideLine);
        xYPlot.setRangeGridlinePaint(Color.black);
        xYPlot.setDomainGridlinePaint(Color.black);
        Range maxRange = computeMaxRange(xYPlot);
        xYPlot.getDomainAxis().setRange(maxRange);
        xYPlot.getRangeAxis().setRange(maxRange);
    }

    /**
     * Create a XYPlot for dose-response graphs containing 2 overlapping
     * datasets.
     *
     * @param dataset1 The imported data
     * @param dataset2  Simulated data representing the best fit
     * @param axesNames Names of the X and Y axis
     * @param extremes How far the axes should scale
     * @return
     */
    public static XYPlot setupDoseResponseDatasets(XYSeriesCollection dataset1, XYSeriesCollection dataset2, List<String> axesNames, List<Double> extremes) {
        //a single plot contains both scatter data and fitted line
        XYPlot plot = new XYPlot();

        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();   // Shapes only
        renderer1.setSeriesLinesVisible(0, false);
        renderer1.setSeriesShapesVisible(0, true);
        XYItemRenderer renderer2 = new XYSplineRenderer();   // Lines only
        XYLineAndShapeRenderer tempRenderer = (XYLineAndShapeRenderer) renderer2;
        tempRenderer.setSeriesLinesVisible(0, true);
        tempRenderer.setSeriesShapesVisible(0, false);

        ValueAxis domain1 = new NumberAxis(axesNames.get(0));
        ValueAxis range1 = new NumberAxis(axesNames.get(1));

        plot.setRangeAxis(0, range1);

        //upper and lowerbounds may vary for migration versus generic data!!!
        domain1.setUpperBound(extremes.get(1));
        domain1.setLowerBound(extremes.get(0));
        //range1.setLowerBound(-50.0);
        // Set the scatter data, renderer, and axis into plot
        plot.setDataset(0, dataset1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        // Map the scatter to the first Domain and first Range
        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);

        // Set the line data, renderer, and axis into plot
        plot.setDataset(1, dataset2);
        plot.setRenderer(1, renderer2);
        return plot;
    }

    public static void setupDoseResponseChart(JFreeChart chart, String title) {
        // set title font
        chart.getTitle().setFont(chartFont);
        chart.setTitle(title);
        // get xyplot from the chart
        XYPlot xYPlot = chart.getXYPlot();
        setupXYPlot(xYPlot);
    }

    /**
     * Setup a plot for a Box And Whisker.
     *
     * @param chart
     */
    public static void setupBoxPlotChart(JFreeChart chart) {
        // set title font
        chart.getTitle().setFont(chartFont);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.white);
        // get domanin and range axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        // set their label font and color
        rangeAxis.setLabelPaint(Color.black);
        rangeAxis.setLabelFont(chartFont);
        domainAxis.setLabelPaint(Color.black);
        domainAxis.setLabelFont(chartFont);
    }

    /**
     * Set up a polar chart.
     *
     * @param chart
     * @param wellIndex
     */
    public static void setupPolarChart(JFreeChart chart, int wellIndex) {
        chart.getTitle().setFont(chartFont);
        PolarPlot plot = (PolarPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.white);
        plot.setCounterClockwise(true);
        plot.setAxisLocation(PolarAxisLocation.EAST_BELOW);
        plot.setAngleOffset(0);
        int length = GuiUtils.getAvailableColors().length;
        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) plot.getRenderer();
        Color color = GuiUtils.getAvailableColors()[wellIndex % length];
        renderer.setSeriesPaint(0, color);
        renderer.setSeriesFilled(0, true);
        renderer.setShapesVisible(false);
    }

    /**
     * Set up an histogram chart.
     *
     * @param chart
     * @param wellIndex
     */
    public static void setUpHistogramChart(JFreeChart chart, int wellIndex) {
        chart.getTitle().setFont(chartFont);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.white);
        ValueAxis rangeAxis = plot.getRangeAxis();
        // set their label font and color
        rangeAxis.setLabelPaint(Color.black);
        rangeAxis.setLabelFont(smallChartFont);
        // oveerride a domain axis to show only certain tick marks
        NumberAxis domainAxis = new NumberAxis(plot.getDomainAxis().getLabel()) {
            @Override
            public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {

                List allTicks = super.refreshTicks(g2, state, dataArea, edge);
                List myTicks = new ArrayList();

                for (Object tick : allTicks) {
                    NumberTick numberTick = (NumberTick) tick;

                    if ((numberTick.getValue() % 6 == 0)) {
                        myTicks.add(new NumberTick(TickType.MINOR, numberTick.getValue(), "" + numberTick.getValue(),
                                numberTick.getTextAnchor(), numberTick.getRotationAnchor(),
                                numberTick.getAngle()));
                        continue;
                    }
                    myTicks.add(tick);
                }
                return myTicks;
            }
        };

        domainAxis.setTickUnit(new NumberTickUnit(60));
        domainAxis.setLabelPaint(Color.black);
        domainAxis.setLabelFont(smallChartFont);
        plot.setDomainAxis(domainAxis);
        int length = GuiUtils.getAvailableColors().length;
        XYItemRenderer renderer = plot.getRenderer();
        Color color = GuiUtils.getAvailableColors()[wellIndex % length];
        renderer.setSeriesPaint(0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 175));
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
                XYLineAnnotation vertical = new XYLineAnnotation(x, y - dy, x, y + dy, stroke, GuiUtils
                        .getAvailableColors()[indexOfColor]);
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
        for (XYSeries aSeriesList : seriesList) {
            if (aSeriesList.getMaxY() > maxY) {
                maxY = aSeriesList.getMaxY();
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
        for (XYSeries aSeriesList : seriesList) {
            if (aSeriesList.getMaxY() < minY) {
                minY = aSeriesList.getMinY();
            }
        }
        return minY;
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
        setupXYPlot(xyPlot);
        if (!inTime) {
            setupTrackChart(chart);
        }
        xyPlot.setOutlineStroke(wideLine);
//        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setDomainGridlinePaint(Color.white);
        // set title font
        chart.getTitle().setFont(chartFont);
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        renderer.setSeriesStroke(0, wideLine);
        int length = GuiUtils.getAvailableColors().length;
        int colorIndex = trackIndex % length;
        renderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
        // show line AND points
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
    }

    /**
     * Create a dataset corresponding a sigmoid model according to the
     * parameters given. This method is used when putting the fitted
     * dose-response model on a chart.
     *
     * @param top Best-fit parameter
     * @param bottom Best-fit parameter
     * @param hillslope Best-fit parameter
     * @param logEC50 Best-fit parameter
     * @param extremes Contains the min and max value of the fitted dataset
     * range
     * @return 1000 X-Y couples that follow the fitted function (ranging between
     * the min and max)
     */
    public static XYSeries createFittedDataset(final double top, final double bottom, final double hillslope, final double logEC50, List<Double> extremes) {
        Function2D fittedFunction = new Function2D() {

            @Override
            public double getValue(double conc) {
                return (bottom + (top - bottom) / (1 + Math.pow(10, (logEC50 - conc) * hillslope)));
            }
        };
        //min-max range will vary between migration and generic data!!!
        return DatasetUtilities.sampleFunction2DToSeries(fittedFunction, extremes.get(0), extremes.get(1), 1000, "fittedfunction");
    }

    /**
     * Given a plot, compute the greatest range between the domain and the range
     * axes.
     *
     * @param plot
     * @return the Range
     */
    public static Range computeMaxRange(XYPlot plot) {
        Range domain = plot.getDomainAxis().getRange();
        Range range = plot.getRangeAxis().getRange();
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
