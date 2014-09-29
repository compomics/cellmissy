/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.ExploreTrackPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import static be.ugent.maf.cellmissy.utils.JFreeChartUtils.setupXYPlot;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Shape;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This class takes care of logic for directionality (computations) and
 * visualisation.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("directionTrackController")
public class DirectionTrackController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExploreTrackController.class);
    // model
    // view
    private ChartPanel directionalityRatioChartPanel;
    private ChartPanel directionAutocorrelationsChartPanel;
    private ChartPanel directionAutocorrelationTimeOneChartPanel;
    private ChartPanel polarChartPanel;
    // parent controller
    @Autowired
    private ExploreTrackController exploreTrackController;
    // child controller
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // init main view
        initMainView();
    }

    /**
     * Set up direction autocorrelation plot.
     *
     * @param chart: the chart to get the plot from
     * @param trackDataHolder: to get the right color for the line
     */
    private void setupDirectionAutocorrelationPlot(JFreeChart chart, TrackDataHolder trackDataHolder) {
        // set up the plot
        XYPlot xyPlot = chart.getXYPlot();
        setupXYPlot(xyPlot);
        xyPlot.setOutlineStroke(JFreeChartUtils.getWideLine());
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setDomainGridlinePaint(Color.black);
        // set title font
        chart.getTitle().setFont(JFreeChartUtils.getChartFont());
        // modify renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        int length = GuiUtils.getAvailableColors().length;
        int trackIndex = exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder);
        int colorIndex = trackIndex % length;
        Shape cross = ShapeUtilities.createDiagonalCross(2, 1);
        for (int indexSeries = 0; indexSeries < xyPlot.getSeriesCount(); indexSeries++) {
            renderer.setSeriesStroke(indexSeries, JFreeChartUtils.getWideLine());
            renderer.setSeriesShapesFilled(indexSeries, true);
            renderer.setSeriesShape(indexSeries, cross);
            if (indexSeries != 0) {
                renderer.setSeriesPaint(indexSeries, Color.BLACK);
                renderer.setSeriesLinesVisible(indexSeries, false);
                // the mean values in the right color and with the line visible
            } else {
                renderer.setSeriesPaint(indexSeries, GuiUtils.getAvailableColors()[colorIndex]);
                renderer.setSeriesLinesVisible(indexSeries, true);
            }
        }
        xyPlot.getDomainAxis().setLowerBound(-0.3);
    }

    /**
     *
     * @param chart: the chart to get the plot from
     * @param trackDataHolder: to get the right color for the line
     */
    private void setupPolarPlot(JFreeChart chart, TrackDataHolder trackDataHolder) {
        // set up the plot
        PolarPlot polarPlot = (PolarPlot) chart.getPlot();
        chart.getTitle().setFont(JFreeChartUtils.getChartFont());
        polarPlot.setBackgroundPaint(Color.white);
        polarPlot.setAngleGridlinePaint(Color.black);
        // hide the border of the sorrounding box
        polarPlot.setOutlinePaint(Color.white);
        polarPlot.setAngleLabelFont(JFreeChartUtils.getChartFont());
        polarPlot.setAngleLabelPaint(Color.black);
        // modify renderer
        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) polarPlot.getRenderer();
        renderer.setSeriesStroke(0, JFreeChartUtils.getWideLine());
        int length = GuiUtils.getAvailableColors().length;
        int trackIndex = exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder);
        int colorIndex = trackIndex % length;
        renderer.setSeriesPaint(0, GuiUtils.getAvailableColors()[colorIndex]);
    }

    /**
     * Plot Directionality Ratio in time for a given track.
     *
     * @param trackDataHolder
     */
    public void plotDirectionalityRatioInTime(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        Track track = trackDataHolder.getTrack();
        Double[] directionalityRatios = stepCentricDataHolder.getDirectionalityRatios(); // y axis: the directionality values
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); // x axis: time points
        double[] directionalityValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(directionalityRatios));
        // we create the series and set its key
        XYSeries ytSeries = JFreeChartUtils.generateXYSeries(timeIndexes, directionalityValues);
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        String seriesKey = "track " + trackNumber + ", well " + well;
        ytSeries.setKey(seriesKey);
        // we then create the XYSeriesCollection and use it to make a new line chart
        XYSeriesCollection ytSeriesCollection = new XYSeriesCollection(ytSeries);
        JFreeChart directionalityRatioChart = ChartFactory.createXYLineChart(seriesKey + " - Directionality Ratio", "time index", "directionality ratio", ytSeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        JFreeChartUtils.setupSingleTrackPlot(directionalityRatioChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder), true);
        directionalityRatioChartPanel.setChart(directionalityRatioChart);
    }

    /**
     * Plot the Direction autocorrelation coefficients in time for a given
     * track.
     *
     * @param trackDataHolder
     */
    public void plotDirectionAutocorrelationsInTime(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        // each element of the list is an array of double containing the coefficients computed at overlapping time intervals
        List<Double[]> directionAutocorrelationsList = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] medianDirectionAutocorrelations = stepCentricDataHolder.getMedianDirectionAutocorrelations();
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); // x axis: time points
        double[] timePoints = new double[timeIndexes.length];
        for (int i = 0; i < timePoints.length; i++) {
            timePoints[i] = i;
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // first series with the mean coefficients
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(timePoints, ArrayUtils.toPrimitive(medianDirectionAutocorrelations));
        xySeries.setKey("track " + trackNumber + ", well " + well + "_median coefficient");
        xySeriesCollection.addSeries(xySeries);
        // now all the rest
        for (int i = 0; i < directionAutocorrelationsList.size(); i++) {
            Double[] coefficients = directionAutocorrelationsList.get(i);
            double[] toPrimitive = ArrayUtils.toPrimitive(coefficients);
            xySeries = JFreeChartUtils.generateXYSeries(timePoints[i], toPrimitive);
            xySeries.setKey("track " + trackNumber + ", well " + well + "_" + i);
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart directionAutocorrelationsChart = ChartFactory.createScatterPlot("track " + trackNumber + ", well " + well + " - Direction Autocorrelation", "time", "direction autocorrelation", xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        setupDirectionAutocorrelationPlot(directionAutocorrelationsChart, trackDataHolder);
        directionAutocorrelationsChartPanel.setChart(directionAutocorrelationsChart);
    }

    /**
     * This plots the direction autocorrelation values but only at time 1.
     *
     * @param trackDataHolder
     */
    public void plotDirectionAutocorrelationTimeOne(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        // each element of the list is an array of double containing the coefficients computed at overlapping time intervals
        List<Double[]> directionAutocorrelationsList = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] coefficients = directionAutocorrelationsList.get(1); // these are the coefficients at time one
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); // x axis: time points
        double[] timePoints = new double[timeIndexes.length];
        for (int i = 0; i < timePoints.length; i++) {
            timePoints[i] = i;
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(timePoints, ArrayUtils.toPrimitive(coefficients));
        xySeries.setKey("track " + trackNumber + ", well " + well + "_time 1");
        xySeriesCollection.addSeries(xySeries);
        JFreeChart directionAutocorrelationTimeOneChart = ChartFactory.createScatterPlot("track " + trackNumber + ", well " + well + " - Direction Autocorrelation-Time 1", "time index", "direction autocorrelation-Time 1", xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        setupDirectionAutocorrelationPlot(directionAutocorrelationTimeOneChart, trackDataHolder);
        directionAutocorrelationTimeOneChartPanel.setChart(directionAutocorrelationTimeOneChart);
    }

    /**
     *
     * @param trackDataHolder
     */
    public void plotPolarChart(TrackDataHolder trackDataHolder) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        Double[] turningAngles = stepCentricDataHolder.getTurningAngles();
        Double[] instantaneousDisplacements = stepCentricDataHolder.getInstantaneousDisplacements();
        Track track = trackDataHolder.getTrack();
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(ArrayUtils.toPrimitive(turningAngles), ArrayUtils.toPrimitive(instantaneousDisplacements));
        xySeries.setKey("track " + trackNumber + ", well " + well + "_time 1");
        xySeriesCollection.addSeries(xySeries);
        JFreeChart polarChart = ChartFactory.createPolarChart("track " + trackNumber + ", well " + well + " - Polar Plot", xySeriesCollection, false, true, false);
        setupPolarPlot(polarChart, trackDataHolder);
        polarChartPanel.setChart(polarChart);
    }

    /**
     * Initialize main view.
     */
    private void initMainView() {
        directionalityRatioChartPanel = new ChartPanel(null);
        directionalityRatioChartPanel.setOpaque(false);
        directionAutocorrelationsChartPanel = new ChartPanel(null);
        directionAutocorrelationsChartPanel.setOpaque(false);
        directionAutocorrelationTimeOneChartPanel = new ChartPanel(null);
        directionAutocorrelationTimeOneChartPanel.setOpaque(false);
        polarChartPanel = new ChartPanel(null);
        polarChartPanel.setOpaque(false);
        ExploreTrackPanel exploreTrackPanel = exploreTrackController.getExploreTrackPanel();
        exploreTrackPanel.getDirectionalityRatioGraphicsParentPanel().add(directionalityRatioChartPanel, gridBagConstraints);
        exploreTrackPanel.getDirectionAutocorrelationsGraphicsParentPanel().add(directionAutocorrelationsChartPanel, gridBagConstraints);
        exploreTrackPanel.getDirectionAutocorrelationTimeOneGraphicsParentPanel().add(directionAutocorrelationTimeOneChartPanel, gridBagConstraints);
        exploreTrackPanel.getPolarPlotGraphicsParentPanel().add(polarChartPanel, gridBagConstraints);
    }
}
