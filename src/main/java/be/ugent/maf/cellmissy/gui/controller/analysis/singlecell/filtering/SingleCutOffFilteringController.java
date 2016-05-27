/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.SingleCutOffPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of the logic for a single cut-off filtering on
 * single cell trajectories.
 *
 * @author Paola
 */
@Controller("singleCutOffFilteringController")
public class SingleCutOffFilteringController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCutOffFilteringController.class);
    // model
    private double cutOff;
    List<List<double[]>> rawDensityFunction;
    // view
    private SingleCutOffPanel singleCutOffPanel;
    private ChartPanel filteredKdeChartPanel;
    private ChartPanel rawKdeChartPanel;
    // parent controller
    @Autowired
    private FilteringController filteringController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize the controller.
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        initMainView();
        // initialize the other views
        initOtherViews();
    }

    public SingleCutOffPanel getSingleCutOffPanel() {
        return singleCutOffPanel;
    }

    /**
     * Plot the raw KDE for track displacements.
     *
     */
    public void showMeanDisplInList() {
        filteringController.getPlateConditions().stream().map((condition)
                -> filteringController.getMeanDisplForCondition(condition)).forEach((meanDisplForCondition) -> {
                    DefaultListModel model = (DefaultListModel) singleCutOffPanel.getMeanDisplList().getModel();
                    model.addElement(meanDisplForCondition);
                });
    }

    /**
     * Plot the raw KDE for track displacements.
     *
     */
    public void plotRawKdeSingleCutOff() {
        // estimate the raw density function
        // check if the estimation has already taken place
        // if not, launch a swing worker
        if (rawDensityFunction == null) {
            DensityFunctionSwingWorker densityFunctionSwingWorker = new DensityFunctionSwingWorker();
            densityFunctionSwingWorker.execute();
        } else {
            XYSeriesCollection densityFunction = filteringController.generateDensityFunction(rawDensityFunction);
            JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track displ", "track displ", false);
            rawKdeChartPanel.setChart(densityChart);
        }

    }

    /**
     * Initialize the main view.
     */
    private void initMainView() {
        // make a new view
        singleCutOffPanel = new SingleCutOffPanel();
        singleCutOffPanel.getMeanDisplList().setModel(new DefaultListModel());
        // action listeners
        singleCutOffPanel.getApplyCutOffButton().addActionListener((ActionEvent e) -> {
            // try to read the user-inserted values for up and down limit
            // check for number format exception
            try {
                cutOff = Double.parseDouble(singleCutOffPanel.getCutOffTextField().getText());
                FilterSwingWorker filterSwingWorker = new FilterSwingWorker();
                filterSwingWorker.execute();
            } catch (NumberFormatException ex) {
                // warn the user and log the error for info
                filteringController.showMessage("Please insert a valid number for the cut-off!" + "\n " + ex.getMessage(),
                        "number format exception", JOptionPane.ERROR_MESSAGE);
                LOG.error(ex.getMessage());
            }
        });

        // add view to parent container
        filteringController.getFilteringPanel().getSingleCutOffParentPanel().add(singleCutOffPanel, gridBagConstraints);
    }

    // initialize the other views
    private void initOtherViews() {
        filteredKdeChartPanel = new ChartPanel(null);
        filteredKdeChartPanel.setOpaque(false);

        rawKdeChartPanel = new ChartPanel(null);
        rawKdeChartPanel.setOpaque(false);

        // add chart panels to parent containers
        singleCutOffPanel.getFilteredPlotParentPanel().add(filteredKdeChartPanel, gridBagConstraints);
        singleCutOffPanel.getRawPlotParentPanel().add(rawKdeChartPanel, gridBagConstraints);
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRetainedKde() {
        // create the dataset for the plot logic
        XYSeriesCollection retainedKdeDataset = getRetainedKdeDataset();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(retainedKdeDataset,
                "cut-off: " + AnalysisUtils.roundTwoDecimals(cutOff) + " filtered KDE track displ", "track displ", false);
        filteredKdeChartPanel.setChart(densityChart);
        singleCutOffPanel.getFilteredPlotParentPanel().revalidate();
        singleCutOffPanel.getFilteredPlotParentPanel().repaint();
    }

    /**
     * Get KDE datasets for the retained tracks.
     *
     * @return
     */
    private XYSeriesCollection getRetainedKdeDataset() {
        List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredDensityFunction();
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(estimateFilteredDensityFunction);
        return densityFunction;
    }

    /**
     * Estimate the density function for the retained data.
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredDensityFunction() {
        String kernelDensityEstimatorBeanName = filteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = filteringController.getFilteringMap();
        filteringMap.keySet().stream().map((conditionDataHolder)
                -> getRetainedDisplacements(conditionDataHolder)).map((retainedDisplacements) -> filteringController.estimateDensityFunction(retainedDisplacements, kernelDensityEstimatorBeanName)).forEach((oneConditionTrackDisplDensityFunction) -> {
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
        List<TrackDataHolder> retainedTracks = filteringController.getFilteringMap().get(cellConditionDataHolder);
        Double[] trackDisplacementsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMeanDisplacement = retainedTrack.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        return trackDisplacementsVector;
    }

    /**
     * Do the actual filter: for each condition, check the tracks, if mean
     * displacement < cut-off, exclude, else retain >. Put results in a map and
     * pass it to the parent controller.
     */
    private void filter() {
        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = new HashMap<>();
        filteringController.getPreProcessingMap().keySet().stream().forEach((plateCondition) -> {
            List<TrackDataHolder> retainedTrackDataHolders = new ArrayList<>();
            SingleCellConditionDataHolder conditionDataHolder = filteringController.getPreProcessingMap().get(plateCondition);
            conditionDataHolder.getTrackDataHolders().stream().filter((trackDataHolder)
                    -> (trackDataHolder.getCellCentricDataHolder().getMedianDisplacement() >= cutOff)).forEach((trackDataHolder) -> {
                        retainedTrackDataHolders.add(trackDataHolder);
                    });
            filteringMap.put(conditionDataHolder, retainedTrackDataHolders);
        });
        filteringController.setFilteringMap(filteringMap);
    }

    /**
     * A swing worker that simply calls the filter method in the background.
     */
    private class FilterSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            filteringController.showWaitingDialog("Filtering experiment with cut-off: " + cutOff);
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);
            filter();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // plot the filtered KDE plots
                plotRetainedKde();
                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                filteringController.getConditionsList().setEnabled(false);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

    /**
     * A swing worker that estimates the raw density function (only once!).
     */
    private class DensityFunctionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            filteringController.showWaitingDialog("Please wait, estimating KDE for the experiment");
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);
            rawDensityFunction = filteringController.estimateRawDensityFunction();

            return null;
        }

        @Override
        protected void done() {
            try {
                get();

                XYSeriesCollection densityFunction = filteringController.generateDensityFunction(rawDensityFunction);
                JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track displ", "track displ", false);
                rawKdeChartPanel.setChart(densityChart);

                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                filteringController.getConditionsList().setEnabled(false);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

}
