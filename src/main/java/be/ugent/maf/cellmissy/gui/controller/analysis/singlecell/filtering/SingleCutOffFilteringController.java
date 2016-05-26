/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.SingleCutOffPanel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    // view
    private SingleCutOffPanel singleCutOffPanel;
    private ChartPanel filteredKdeChartPanel;
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
            } catch (NumberFormatException ex) {
                // warn the user and log the error for info
                filteringController.showMessage("Please insert a valid number for the cut-off!" + "\n " + ex.getMessage(),
                        "number format exception", JOptionPane.ERROR_MESSAGE);
                LOG.error(ex.getMessage());
            }
            FilterSwingWorker filterSwingWorker = new FilterSwingWorker();
            filterSwingWorker.execute();
        });

        // add view to parent container
        filteringController.getFilteringPanel().getSingleCutOffParentPanel().add(singleCutOffPanel, gridBagConstraints);
    }

    // initialize the other views
    private void initOtherViews() {
        filteredKdeChartPanel = new ChartPanel(null);
        filteredKdeChartPanel.setOpaque(false);

        // add chart panel to parent container
        singleCutOffPanel.getFilteredPlotParentPanel().add(filteredKdeChartPanel, gridBagConstraints);
    }

    /**
     *
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
     *
     * @return
     */
    private XYSeriesCollection getRetainedKdeDataset() {
        List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredDensityFunction();
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(estimateFilteredDensityFunction);
        return densityFunction;
    }

    /**
     *
     * @return
     */
    private List<List<double[]>> estimateFilteredDensityFunction() {
        String kernelDensityEstimatorBeanName = filteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();

        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = filteringController.getFilteringMap();
        for (SingleCellConditionDataHolder conditionDataHolder : filteringMap.keySet()) {
            Double[] retainedDisplacements = getRetainedDisplacements(conditionDataHolder);
            List<double[]> oneConditionTrackDisplDensityFunction
                    = filteringController.estimateDensityFunction(retainedDisplacements, kernelDensityEstimatorBeanName);
            densityFunction.add(oneConditionTrackDisplDensityFunction);
        }

        return densityFunction;
    }

    /**
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
     *
     */
    private void filter() {
        Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap = new HashMap<>();
        // for each condition, check the tracks, if mean displ < cut-off, exclude, else retain
        // put results in a map and pass it to the parent controller
        for (PlateCondition plateCondition : filteringController.getPreProcessingMap().keySet()) {
            List<TrackDataHolder> retainedTrackDataHolders = new ArrayList<>();
            SingleCellConditionDataHolder conditionDataHolder = filteringController.getPreProcessingMap().get(plateCondition);
            for (TrackDataHolder trackDataHolder : conditionDataHolder.getTrackDataHolders()) {
                if (trackDataHolder.getCellCentricDataHolder().getMedianDisplacement() >= cutOff) {
                    retainedTrackDataHolders.add(trackDataHolder);
                }
            }
            filteringMap.put(conditionDataHolder, retainedTrackDataHolders);
        }
        filteringController.setFilteringMap(filteringMap);
    }

    /**
     * A swing worker that filters cell tracks based on a set of motile step
     * values and a percentage of motility for a cell trajectory.
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

}
