/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.view.table.model.FilteringSummaryTableModel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * A child controller to show a summary for the choice of a multiple cut-off
 * filtering.
 *
 * @author Paola
 */
@Controller("summaryMultipleCutOffController")
public class SummaryMultipleCutOffController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SummaryMultipleCutOffController.class);
    // model
    private Double cutOff;
    // view
    private ChartPanel rawDisplChartPanel;
    private ChartPanel filteredDisplChartPanel;
    private ChartPanel rawSpeedChartPanel;
    private ChartPanel filteredSpeedChartPanel;
    // parent controller
    @Autowired
    private MultipleCutOffFilteringController multipleCutOffFilteringController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize the controller.
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initMainView();
    }

    public void setCutOff(Double cutOff) {
        this.cutOff = cutOff;
    }

    /**
     * Plot both raw and filtered KDE for the experiment. Get the map from the
     * parent controller and use it to estimate the KDE.
     */
    public void plotKDEs() {
        plotRawDisplKDE();
        plotRetainedDisplKDE();
        plotRawSpeedKDE();
        plotRetainedSpeedKDE();
    }

    /**
     * Update the information on the number of tracks retained, and also on the
     * cut-off values used for each condition.
     */
    public void updateSummaryTable() {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getPercentageTextField().
                setText(multipleCutOffFilteringController.getPercentageMotile() + " %");
        multipleCutOffFilteringController.getMultipleCutOffPanel().getSummaryTable().setModel(new FilteringSummaryTableModel(multipleCutOffFilteringController.getFilteringMap(),
                multipleCutOffFilteringController.getCutOffMap()));
    }

    // initialize the main view
    private void initMainView() {
        rawDisplChartPanel = new ChartPanel(null);
        filteredDisplChartPanel = new ChartPanel(null);
        rawSpeedChartPanel = new ChartPanel(null);
        filteredSpeedChartPanel = new ChartPanel(null);
        // add the panels to the parent container
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawDisplKDEParentPanel().add(rawDisplChartPanel, gridBagConstraints);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredDisplKDEParentPanel().add(filteredDisplChartPanel, gridBagConstraints);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawSpeedKDEParentPanel().add(rawSpeedChartPanel, gridBagConstraints);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredSpeedKDEParentPanel().add(filteredSpeedChartPanel, gridBagConstraints);
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRawDisplKDE() {
        // create the dataset for the plot logic
        XYSeriesCollection rawKdeDataset = getRawDisplKDEDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(rawKdeDataset,
                "Raw KDE track displ", "track displ", false);
        rawDisplChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawDisplKDEParentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawDisplKDEParentPanel().repaint();
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRetainedDisplKDE() {
        // create the dataset for the plot logic
        XYSeriesCollection retainedKdeDataset = getFilteredDisplKDEDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(retainedKdeDataset,
                " filtered KDE track displ", "track displ", false);
        filteredDisplChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredDisplKDEParentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredDisplKDEParentPanel().repaint();
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRawSpeedKDE() {
        // create the dataset for the plot logic
        XYSeriesCollection rawKdeDataset = getRawSpeedKDEDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(rawKdeDataset,
                "Raw KDE track speed", "track speed", false);
        rawSpeedChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawSpeedKDEParentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawSpeedKDEParentPanel().repaint();
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRetainedSpeedKDE() {
        // create the dataset for the plot logic
        XYSeriesCollection retainedKdeDataset = getFilteredSpeedKDEDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(retainedKdeDataset,
                " filtered KDE track speed", "track speed", false);
        filteredSpeedChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredSpeedKDEParentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredSpeedKDEParentPanel().repaint();
    }

    /**
     * Get the raw KDE data.
     *
     * @return
     */
    private XYSeriesCollection getRawDisplKDEDataset() {
        List<List<double[]>> rawKDE = multipleCutOffFilteringController.estimateRawDisplKDE();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(rawKDE);
        return densityFunction;
    }

    private XYSeriesCollection getFilteredDisplKDEDataset() {
        List<List<double[]>> filteredDisplKDE = estimateFilteredDisplKDE();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(filteredDisplKDE);
        return densityFunction;
    }

    private XYSeriesCollection getRawSpeedKDEDataset() {
        List<List<double[]>> rawKDE = multipleCutOffFilteringController.estimateRawSpeedKDE();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(rawKDE);
        return densityFunction;
    }

    private XYSeriesCollection getFilteredSpeedKDEDataset() {
        List<List<double[]>> filteredSpeedKDE = estimateFilteredSpeedKDE();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(filteredSpeedKDE);
        return densityFunction;
    }

    /**
     * Estimate the density function for the retained data.
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredDisplKDE() {
        String kernelDensityEstimatorBeanName = multipleCutOffFilteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = multipleCutOffFilteringController.getFilteringMap();
        filteringMap.keySet().stream().map((conditionDataHolder)
                -> getRetainedDisplacements(conditionDataHolder)).map((retainedDisplacements)
                        -> multipleCutOffFilteringController.estimateDensityFunction(retainedDisplacements, kernelDensityEstimatorBeanName)).forEach((oneConditionTrackDisplDensityFunction) -> {
                    densityFunction.add(oneConditionTrackDisplDensityFunction);
                });

        return densityFunction;
    }

    /**
     * Estimate the density function for the retained data.
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredSpeedKDE() {
        String kernelDensityEstimatorBeanName = multipleCutOffFilteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = multipleCutOffFilteringController.getFilteringMap();
        filteringMap.keySet().stream().map((conditionDataHolder)
                -> getRetainedSpeeds(conditionDataHolder)).map((retainedSpeeds)
                        -> multipleCutOffFilteringController.estimateDensityFunction(retainedSpeeds, kernelDensityEstimatorBeanName)).forEach((oneConditionTrackSpeedDensityFunction) -> {
                    densityFunction.add(oneConditionTrackSpeedDensityFunction);
                });

        return densityFunction;
    }

    /**
     * Get the retained displacements.
     *
     * @param cellConditionDataHolder
     * @return
     */
    private Double[] getRetainedDisplacements(SingleCellConditionDataHolder cellConditionDataHolder) {
        List<TrackDataHolder> retainedTracks = multipleCutOffFilteringController.getFilteringMap().get(cellConditionDataHolder);
        Double[] trackDisplacementsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMedianDispl = retainedTrack.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMedianDispl;
        }
        return trackDisplacementsVector;
    }

    private Double[] getRetainedSpeeds(SingleCellConditionDataHolder cellConditionDataHolder) {
        List<TrackDataHolder> retainedTracks = multipleCutOffFilteringController.getFilteringMap().get(cellConditionDataHolder);
        Double[] trackSpeedsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackSpeedsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMedianSpeed = retainedTrack.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMedianSpeed;
        }
        return trackSpeedsVector;
    }
}
