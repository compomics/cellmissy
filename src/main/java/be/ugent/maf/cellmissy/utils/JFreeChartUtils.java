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
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * This class contains some helpful JFreeChart utilities
 *
 * @author Paola Masuzzo
 */
public class JFreeChartUtils {

    // private methods
    /**
     * Setup a xy plot
     *
     * @param xYPlot
     */
    private static void setupPlot(XYPlot xYPlot) {
        // set background to white and grid color to black
        xYPlot.setBackgroundPaint(Color.white);
        xYPlot.setRangeGridlinePaint(Color.black);
        // hide the border of the sorrounding box
        xYPlot.setOutlinePaint(Color.white);
        // get domanin and range axes
        ValueAxis domainAxis = xYPlot.getDomainAxis();
        ValueAxis rangeAxis = xYPlot.getRangeAxis();
        // set label paint for axes to black
        domainAxis.setLabelPaint(Color.black);
        rangeAxis.setLabelPaint(Color.black);
        // set font for labels, both on domain and range axes
        domainAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
        rangeAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
    }

    /**
     * Given a list of wells and one well's coordinate, get the index of the
     * well in the List
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
     * Get well coordinates from series in oder to render the lines color
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
        densityChart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
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
        BasicStroke wideLine = new BasicStroke(1.3f);
        // get imaged wells and number of samples for each one
        List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
        int counter = 0;
        for (Well well : processedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
            for (int i = counter; i < xYSeriesCollection.getSeriesCount(); i++) {
                // wide line
                renderer.setSeriesStroke(i, wideLine);
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
     * Generate Series for (x,y) Area plotting from two arrays of double
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
     * Setup replicates area chart
     *
     * @param chart: chart to setup
     * @param wellList: keep track of wells added, removed: list needed
     * @param plotLines: show lines on plot?
     * @param plotLines: show points on plot?
     */
    public static void setupReplicatesAreaChart(JFreeChart chart, List<Well> wellList, boolean plotLines, boolean plotPoints) {
        // set title font 
        chart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
        // put legend on the right edge
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        XYPlot xYPlot = chart.getXYPlot();
        setupPlot(xYPlot);
        // get the xyseriescollection from the plot
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) xYPlot.getDataset();
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xYPlot.getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, wideLine);
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
     * SEtup global area chart
     *
     * @param chart: chart to setup
     * @param plotLines: show lines on plot?
     * @param plotLines: show points on plot?
     */
    public static void setupGlobalAreaChart(JFreeChart chart, boolean plotLines, boolean plotPoints) {
        // set title font 
        chart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
        // get xyplot from the chart
        XYPlot xYPlot = chart.getXYPlot();
        setupPlot(xYPlot);
        // get the xyseriescollection from the plot
        XYSeriesCollection xYSeriesCollection = (XYSeriesCollection) xYPlot.getDataset();
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xYPlot.getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            // wide line
            renderer.setSeriesStroke(i, wideLine);
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
     * Compute Max value of Y for density plot
     *
     * @param xYSeriesCollection
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
}
