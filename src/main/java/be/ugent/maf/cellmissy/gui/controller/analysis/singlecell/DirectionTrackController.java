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
import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.DirectionAutocorrelationLineAndShapeRenderer;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
class DirectionTrackController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExploreTrackController.class);
    // model
    // view
    private ChartPanel directionalityRatioChartPanel;
    private ChartPanel directionAutocorrelationsChartPanel;
    private ChartPanel directionAutocorrelationTimeOneChartPanel;
    private ChartPanel directionAutocorrelationDeltaTChartPanel;
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
        JFreeChartUtils.setupXYPlot(xyPlot);
        xyPlot.setOutlineStroke(JFreeChartUtils.getWideLine());
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setDomainGridlinePaint(Color.black);
        // set title font
        chart.getTitle().setFont(JFreeChartUtils.getChartFont());
        // modify renderer
        int trackIndex = exploreTrackController.getTrackDataHolderBindingList().indexOf(trackDataHolder);
        xyPlot.setRenderer(new DirectionAutocorrelationLineAndShapeRenderer(trackIndex));
        xyPlot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xyPlot.getDomainAxis().setLowerBound(-0.3);
    }

    /**
     * Create a chart with a specific delta t value.
     *
     * @param trackDataHolder
     * @param deltaT
     * @return
     */
    private JFreeChart createDeltaTChart(TrackDataHolder trackDataHolder, int deltaT) {
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        List<Double[]> directionAutocorrelationsList = stepCentricDataHolder.getDirectionAutocorrelations();
        Double[] coefficients = directionAutocorrelationsList.get(deltaT);
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); // x axis: time points
        double[] timePoints = new double[timeIndexes.length];
        for (int i = 0; i < timePoints.length; i++) {
            timePoints[i] = i;
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(timePoints, ArrayUtils.toPrimitive(coefficients));
        xySeries.setKey("track " + trackDataHolder.getTrack().getTrackNumber() + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell()
                  + "_Δt=" + deltaT);
        xySeriesCollection.addSeries(xySeries);
        JFreeChart deltaTChart = ChartFactory.createScatterPlot("track " + trackDataHolder.getTrack().getTrackNumber()
                  + ", well " + trackDataHolder.getTrack().getWellHasImagingType().getWell() + " - Direction Autocorrelation-Δt=" + deltaT,
                  "time index", "dir autocorr", xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        setupDirectionAutocorrelationPlot(deltaTChart, trackDataHolder);
        return deltaTChart;
    }

    /**
     * Update the combo box containing the step sizes for the track of a given
     * track data holder.
     *
     * @param trackDataHolder
     */
    public void updateDeltaTComboBox(TrackDataHolder trackDataHolder) {
        // first remove all the items in the component
        exploreTrackController.getExploreTrackPanel().getDeltaTComboBox().removeAllItems();
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        // need to go to time indexes - 1
        for (int i = 0; i < stepCentricDataHolder.getTimeIndexes().length - 2; i++) {
            exploreTrackController.getExploreTrackPanel().getDeltaTComboBox().addItem(i + 1);
        }
        exploreTrackController.getExploreTrackPanel().getDeltaTComboBox().setSelectedIndex(1);
    }

    /**
     * Plot only the direction autocorrelations for a specific delta t.
     *
     * @param trackDataHolder
     * @param deltaT
     */
    public void plotDirectionAutocorrelationForDeltaT(TrackDataHolder trackDataHolder, int deltaT) {
        JFreeChart deltaTChart = createDeltaTChart(trackDataHolder, deltaT);
        directionAutocorrelationDeltaTChartPanel.setChart(deltaTChart);
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
        setupDirectionAutocorrelationPlot(directionalityRatioChart, trackDataHolder);
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
        double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); // x axis: the step sizes
        double[] stepSizes = new double[timeIndexes.length];
        for (int i = 0; i < stepSizes.length; i++) {
            stepSizes[i] = i;
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // first series with the mean coefficients
        XYSeries xySeries = JFreeChartUtils.generateXYSeries(stepSizes, ArrayUtils.toPrimitive(medianDirectionAutocorrelations));
        xySeries.setKey("track " + trackNumber + ", well " + well + "_median coefficient");
        xySeriesCollection.addSeries(xySeries);
        // now all the rest
        for (int i = 0; i < directionAutocorrelationsList.size(); i++) {
            xySeries = JFreeChartUtils.generateXYSeries(stepSizes[i], ArrayUtils.toPrimitive(directionAutocorrelationsList.get(i)));
            xySeries.setKey("track " + trackNumber + ", well " + well + "_" + i);
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart directionAutocorrelationsChart = ChartFactory.createScatterPlot("track " + trackNumber + ", well " + well
                  + " - Direction Autocorrelation", "Δt (step size)", "direction autocorrelation",
                  xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        setupDirectionAutocorrelationPlot(directionAutocorrelationsChart, trackDataHolder);
        directionAutocorrelationsChartPanel.setChart(directionAutocorrelationsChart);
    }

    /**
     * This plots the direction autocorrelation values but only at time 1.
     *
     * @param trackDataHolder
     */
    public void plotDirectionAutocorrelationTimeOne(TrackDataHolder trackDataHolder) {
        JFreeChart deltaTChart = createDeltaTChart(trackDataHolder, 1);
        directionAutocorrelationTimeOneChartPanel.setChart(deltaTChart);
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
        directionAutocorrelationDeltaTChartPanel = new ChartPanel(null);
        directionAutocorrelationDeltaTChartPanel.setOpaque(false);

        ExploreTrackPanel exploreTrackPanel = exploreTrackController.getExploreTrackPanel();
        exploreTrackPanel.getDirectionalityRatioGraphicsParentPanel().add(directionalityRatioChartPanel, gridBagConstraints);
        exploreTrackPanel.getDirectionAutocorrelationsGraphicsParentPanel().add(directionAutocorrelationsChartPanel, gridBagConstraints);
        exploreTrackPanel.getDirectionAutocorrelationTimeOneGraphicsParentPanel().add(directionAutocorrelationTimeOneChartPanel, gridBagConstraints);
        exploreTrackPanel.getDirectionAutocorrelationDeltaTGraphicsParentPanel().add(directionAutocorrelationDeltaTChartPanel, gridBagConstraints);

        /**
         * Add Action Listener to the delta-t combo box:
         */
        exploreTrackPanel.getDeltaTComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected item in the combo box: we know it's an int
                Object selectedItem = exploreTrackPanel.getDeltaTComboBox().getSelectedItem();
                if (selectedItem != null) {
                    int deltaT = (int) exploreTrackPanel.getDeltaTComboBox().getSelectedItem();
                    plotDirectionAutocorrelationForDeltaT(exploreTrackController.getSelectedTrackDataHolder(), deltaT);
                }
            }
        });
    }
}
