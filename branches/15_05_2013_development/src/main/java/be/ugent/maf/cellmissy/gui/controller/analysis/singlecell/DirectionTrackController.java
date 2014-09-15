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
import java.awt.GridBagConstraints;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
        JFreeChartUtils.setupDirectionAutocorrelationPlot(directionAutocorrelationsChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
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
        JFreeChartUtils.setupDirectionAutocorrelationPlot(directionAutocorrelationTimeOneChart, exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder));
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
        polarChartPanel.setChart(polarChart);
    }

    /**
     * Initialize main view
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
