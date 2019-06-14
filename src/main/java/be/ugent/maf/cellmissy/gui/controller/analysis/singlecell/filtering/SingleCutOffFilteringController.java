/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.SingleCutOffPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.SingleFilteringSummaryTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.ArrayUtils;
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
    List<List<double[]>> rawDisplKDE;
    List<List<double[]>> rawSpeedKDE;
    // view
    private SingleCutOffPanel singleCutOffPanel;
    private ChartPanel rawDisplChartPanel;
    private ChartPanel filteredDisplChartPanel;
    private ChartPanel rawSpeedChartPanel;
    private ChartPanel filteredSpeedChartPanel;
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
     * Reset everything when cancelling analysis. Called by parent controller.
     */
    protected void resetOnCancel() {
        singleCutOffPanel.getMedianDisplList().setModel(new DefaultListModel());
        singleCutOffPanel.getSummaryTable().setModel(new DefaultTableModel());
        rawDisplChartPanel = new ChartPanel(null);
        filteredDisplChartPanel = new ChartPanel(null);
        rawSpeedChartPanel = new ChartPanel(null);
        filteredSpeedChartPanel = new ChartPanel(null);
    }

    /**
     * Plot the raw KDE for track displacements.
     *
     */
    public void showMedianDisplInList() {
        DefaultListModel model = (DefaultListModel) singleCutOffPanel.getMedianDisplList().getModel();
        model.clear();
        filteringController.getPlateConditions().stream().map((condition)
                -> filteringController.getMedianDisplAcrossReplicates(condition)).forEach((Double medianDisplForCondition) -> {
            model.addElement(AnalysisUtils.roundThreeDecimals(medianDisplForCondition));
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
        if (rawDisplKDE == null) {
            DensityFunctionSwingWorker densityFunctionSwingWorker = new DensityFunctionSwingWorker();
            densityFunctionSwingWorker.execute();
        } else {
            XYSeriesCollection densityFunction = filteringController.generateDensityFunction(rawDisplKDE);
            JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track displ", "track speed", false);
            rawDisplChartPanel.setChart(densityChart);
            densityFunction = filteringController.generateDensityFunction(rawSpeedKDE);
            densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track speed", "track speed", false);
            rawSpeedChartPanel.setChart(densityChart);
        }

    }

    /**
     * Initialize the main view.
     */
    private void initMainView() {
        // make a new view
        singleCutOffPanel = new SingleCutOffPanel();
        singleCutOffPanel.getMedianDisplList().setModel(new DefaultListModel());
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

        AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.LEFT);
        for (int i = 0; i < singleCutOffPanel.getSummaryTable().getColumnModel().getColumnCount(); i++) {
            singleCutOffPanel.getSummaryTable().getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
        }
        singleCutOffPanel.getSummaryTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));

        // add view to parent container
        filteringController.getFilteringPanel().getSingleCutOffParentPanel().add(singleCutOffPanel, gridBagConstraints);
    }

    // initialize the other views
    private void initOtherViews() {
        rawDisplChartPanel = new ChartPanel(null);
        filteredDisplChartPanel = new ChartPanel(null);
        rawSpeedChartPanel = new ChartPanel(null);
        filteredSpeedChartPanel = new ChartPanel(null);

        // add chart panels to parent containers
        singleCutOffPanel.getRawDisplKDEParentPanel().add(rawDisplChartPanel, gridBagConstraints);
        singleCutOffPanel.getFilteredDisplKDEParentPanel().add(filteredDisplChartPanel, gridBagConstraints);
        singleCutOffPanel.getRawSpeedKDEParentPanel().add(rawSpeedChartPanel, gridBagConstraints);
        singleCutOffPanel.getFilteredSpeedKDEParentPanel().add(filteredSpeedChartPanel, gridBagConstraints);
    }

    /**
     * Plot the KDE of the retained tracks.
     */
    private void plotRetainedDisplKDE() {
        // create the dataset for the plot logic
        XYSeriesCollection retainedKdeDataset = getRetainedDisplKDE();
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(retainedKdeDataset,
                "cut-off: " + AnalysisUtils.roundTwoDecimals(cutOff) + " filtered KDE track displ", "track displ", false);
        filteredDisplChartPanel.setChart(densityChart);
        singleCutOffPanel.getFilteredDisplKDEParentPanel().revalidate();
        singleCutOffPanel.getFilteredDisplKDEParentPanel().repaint();
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
        singleCutOffPanel.getFilteredSpeedKDEParentPanel().revalidate();
        singleCutOffPanel.getFilteredSpeedKDEParentPanel().repaint();
    }

    /**
     * Get KDE datasets for the retained tracks.
     *
     * @return
     */
    private XYSeriesCollection getRetainedDisplKDE() {
        List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredDisplKDE();
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(estimateFilteredDensityFunction);
        return densityFunction;
    }

    private XYSeriesCollection getFilteredSpeedKDEDataset() {
        List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredSpeedKDE();
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(estimateFilteredDensityFunction);
        return densityFunction;
    }

    /**
     * Estimate the density function for the retained data.
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredDisplKDE() {
        String kernelDensityEstimatorBeanName = filteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = filteringController.getFilteringMap();
        filteringMap.keySet().stream().map((conditionDataHolder)
                -> getRetainedDisplacements(conditionDataHolder)).map((retainedDisplacements)
                -> filteringController.estimateDensityFunction(retainedDisplacements, kernelDensityEstimatorBeanName)).forEach((oneConditionTrackDisplDensityFunction) -> {
            densityFunction.add(oneConditionTrackDisplDensityFunction);
        });

        return densityFunction;
    }

    private List<List<double[]>> estimateFilteredSpeedKDE() {
        String kernelDensityEstimatorBeanName = filteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = filteringController.getFilteringMap();
        filteringMap.keySet().stream().map((conditionDataHolder)
                -> getRetainedSpeeds(conditionDataHolder)).map((retainedSpeeds)
                -> filteringController.estimateDensityFunction(retainedSpeeds, kernelDensityEstimatorBeanName)).forEach((oneConditionTrackSpeedDensityFunction) -> {
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
        List<TrackDataHolder> retainedTracks = filteringController.getFilteringMap().get(cellConditionDataHolder);
        Double[] trackDisplacementsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMeanDisplacement = retainedTrack.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        return trackDisplacementsVector;
    }

    private Double[] getRetainedSpeeds(SingleCellConditionDataHolder cellConditionDataHolder) {
        List<TrackDataHolder> retainedTracks = filteringController.getFilteringMap().get(cellConditionDataHolder);
        Double[] trackSpeedsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackSpeedsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMedianSpeed = retainedTrack.getCellCentricDataHolder().getMedianSpeed();
            trackSpeedsVector[i] = trackMedianSpeed;
        }
        return trackSpeedsVector;
    }

    /**
     * Do the actual filter: for each condition, check the tracks, if mean
     * displacement < cut-off, exclude, else retain >. Put results in a map and
     * pass it to the parent controller.
     */
    private void filter() {
        List<Integer> originalNumberTracks = new ArrayList<>();
        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = new LinkedHashMap<>();
        filteringController.getPreProcessingMap().keySet().stream().forEach((plateCondition) -> {
            List<TrackDataHolder> retainedTrackDataHolders = new ArrayList<>();
            SingleCellConditionDataHolder conditionDataHolder = filteringController.getPreProcessingMap().get(plateCondition);
            List<Integer> retainedIndices = new ArrayList<>();
            // go through all track data holders in the condition data holder with i
            // if the track is retained, save dataholder and i in lists
            for (int i = 0; i < conditionDataHolder.getTrackDataHolders().size(); i++) {
                TrackDataHolder trackDataHolder = conditionDataHolder.getTrackDataHolders().get(i);
                if (trackDataHolder.getCellCentricDataHolder().getMedianDisplacement() >= cutOff) {
                    retainedTrackDataHolders.add(trackDataHolder);
                    retainedIndices.add(i);
                    }
            }
            SingleCellConditionDataHolder filteredCondDataHolder = filteringController.transferFilteredData(conditionDataHolder, retainedIndices);
            
            // copy retained track data holders to new conditiondataholder
            filteredCondDataHolder.setTrackDataHolders(retainedTrackDataHolders);
            filteringMap.put(filteredCondDataHolder, retainedTrackDataHolders);
            originalNumberTracks.add(conditionDataHolder.getTrackDataHolders().size());
        });
        filteringController.setFilteringMap(filteringMap);
        filteringController.setOriginalNumberTracks(originalNumberTracks);
    }

    /**
     * Update the summary table.
     */
    private void updateSummaryTable() {
        singleCutOffPanel.getSummaryTable().setModel(new SingleFilteringSummaryTableModel(filteringController.getFilteringMap(), filteringController.getOriginalNumberTracks()));
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
                plotRetainedDisplKDE();
                plotRetainedSpeedKDE();
                // update summary table
                updateSummaryTable();
                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                //filteringController.getConditionsList().setEnabled(false);
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
            filteringController.showWaitingDialog("Please wait, estimating KDE for the experiment (all conditions)...");
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);
            rawDisplKDE = filteringController.estimateRawDisplKDE();
            rawSpeedKDE = filteringController.estimateRawSpeedKDE();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();

                XYSeriesCollection densityFunction = filteringController.generateDensityFunction(rawDisplKDE);
                JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track displ", "track displ", false);
                rawDisplChartPanel.setChart(densityChart);
                densityFunction = filteringController.generateDensityFunction(rawSpeedKDE);
                densityChart = JFreeChartUtils.generateDensityFunctionChart(densityFunction, "raw KDE track speed", "track speed", false);
                rawSpeedChartPanel.setChart(densityChart);

                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                //filteringController.getConditionsList().setEnabled(false);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

}
