/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
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
    private ChartPanel rawChartPanel;
    private ChartPanel filteredChartPanel;
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
        plotRawKde();
        plotRetainedKde();
    }

    /**
     * Update the information on the number of tracks retained, and also on the
     * cut-off values used for each condition.
     */
    public void updateInfo() {
        multipleCutOffFilteringController.getMultipleCutOffPanel().getPercentageTextField().
                setText(multipleCutOffFilteringController.getPercentageMotile() + " %");
        // update number of retained tracks for each condition
        DefaultListModel modelList = (DefaultListModel) multipleCutOffFilteringController.getMultipleCutOffPanel().getRetainedTracksList().getModel();
        modelList.clear();
        multipleCutOffFilteringController.getFilteringMap().keySet().stream().forEach((conditionDataHolder) -> {
            List<TrackDataHolder> retainedTracks = multipleCutOffFilteringController.getFilteringMap().get(conditionDataHolder);

            modelList.addElement(conditionDataHolder.getPlateCondition() + ": " + retainedTracks.size());
        });
        // update cut-off values for each condition
        DefaultListModel model = (DefaultListModel) multipleCutOffFilteringController.getMultipleCutOffPanel().getCutOffList().getModel();
        model.clear();
        multipleCutOffFilteringController.getCutOffMap().keySet().stream().forEach((conditionDataHolder) -> {
            Double value = multipleCutOffFilteringController.getCutOffMap().get(conditionDataHolder);
            model.addElement(conditionDataHolder.getPlateCondition() + ": " + value);
        });
    }

    // initialize the main view
    private void initMainView() {
        rawChartPanel = new ChartPanel(null);
        filteredChartPanel = new ChartPanel(null);

        // add the panels to the parent container
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawKDEparentPanel().add(rawChartPanel, gridBagConstraints);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredKDEParentPanel().add(filteredChartPanel, gridBagConstraints);
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRawKde() {
        // create the dataset for the plot logic
        XYSeriesCollection rawKdeDataset = getRawKdeDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(rawKdeDataset,
                "Raw KDE track displ", "track displ", false);
        rawChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawKDEparentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getRawKDEparentPanel().repaint();
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRetainedKde() {
        // create the dataset for the plot logic
        XYSeriesCollection retainedKdeDataset = getRetainedKdeDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(retainedKdeDataset,
                " filtered KDE track displ", "track displ", false);
        filteredChartPanel.setChart(densityChart);
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredKDEParentPanel().revalidate();
        multipleCutOffFilteringController.getMultipleCutOffPanel().getFilteredKDEParentPanel().repaint();
    }

    /**
     * Get the raw KDE data.
     *
     * @return
     */
    private XYSeriesCollection getRawKdeDataset() {
        List<List<double[]>> rawKDE = multipleCutOffFilteringController.estimateRawDensityFunction();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(rawKDE);
        return densityFunction;
    }

    /**
     * Get the filtered KDE data.
     *
     * @return
     */
    private XYSeriesCollection getRetainedKdeDataset() {
        List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredDensityFunction();
        XYSeriesCollection densityFunction = multipleCutOffFilteringController.generateDensityFunction(estimateFilteredDensityFunction);
        return densityFunction;
    }

    /**
     * Estimate the density function for the retained data.
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredDensityFunction() {
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
            double trackMeanDisplacement = retainedTrack.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        return trackDisplacementsVector;
    }
}
