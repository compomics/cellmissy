/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.filtering;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.filtering.MultipleCutOffPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.table.AlignedTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.FilterTrackTableRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.table.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.FilterTrackTableModel;
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
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * This controller takes care of the logic for multiple cut-off filtering for
 * single-cell trajectories.
 *
 * @author Paola
 */
@Controller("multipleCutOffFilteringController")
public class MultipleCutOffFilteringController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MultipleCutOffFilteringController.class);
    // model
    private List<Double> motileSteps;
    private double percentageMotile;
    private Map<SingleCellConditionDataHolder, Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>>> motileStepsFilterMap;
    private Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap;
    private Map<SingleCellConditionDataHolder, Double> cutOffMap;

    // view 
    private MultipleCutOffPanel multipleCutOffPanel;
    private ChartPanel rawKdeChartPanel;
    private ChartPanel filteredKdeChartPanel;
    // parent controller
    @Autowired
    private FilteringController filteringController;
    // child controllers
    @Autowired
    private SummaryMultipleCutOffController summaryMultipleCutOffController;
    // services
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize the controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        motileStepsFilterMap = new LinkedHashMap<>();
        filteringMap = new LinkedHashMap<>();
        cutOffMap = new LinkedHashMap<>();
        // initialize the main view
        initMainView();
        // initialize the other views
        initOtherViews();
        // init child controllers
        summaryMultipleCutOffController.init();
    }

    /**
     * Reset everything when cancelling analysis. Called by parent controller.
     */
    protected void resetOnCancel() {
        filteringMap = new LinkedHashMap<>();
        cutOffMap = new LinkedHashMap<>();
        motileStepsFilterMap = new LinkedHashMap<>();
        rawKdeChartPanel = new ChartPanel(null);
        rawKdeChartPanel.setOpaque(false);
        filteredKdeChartPanel = new ChartPanel(null);
        filteredKdeChartPanel.setOpaque(false);
        multipleCutOffPanel.getFilterTrackTable().setModel(new DefaultTableModel());
        multipleCutOffPanel.getSummaryTable().setModel(new DefaultTableModel());
        // set default to micrometer
        multipleCutOffPanel.getMicroMeterRadioButton().setSelected(true);
        // and therefore no need for the conversion factor
        multipleCutOffPanel.getConversionFactorTextField().setEnabled(false);
        multipleCutOffPanel.getConversionFactorTextField().setText("");
        // reset some default values
        multipleCutOffPanel.getBottomLimTextField().setText("0.4");
        multipleCutOffPanel.getTopLimTextField().setText("2.8");
        multipleCutOffPanel.getTranslocationStepTextField().setText("0.8");
        multipleCutOffPanel.getPercentageMotileStepsTextField().setText("30");
        percentageMotile = 33;
        multipleCutOffPanel.getPercentageTextField().setText("");
        // reset the logic of the panel
        multipleCutOffPanel.getFilteredPlotParentPanel().removeAll();
        multipleCutOffPanel.getFilteredPlotParentPanel().revalidate();
        multipleCutOffPanel.getFilteredPlotParentPanel().repaint();
        multipleCutOffPanel.getFilteredPlotParentPanel().add(filteredKdeChartPanel, gridBagConstraints);
        //reset child controller (chart panels)
        summaryMultipleCutOffController.resetOnCancel();
    }

    public MultipleCutOffPanel getMultipleCutOffPanel() {
        return multipleCutOffPanel;
    }

    public Map<SingleCellConditionDataHolder, List<TrackDataHolder>> getFilteringMap() {
        return filteringMap;
    }

    public String getKernelDensityEstimatorBeanName() {
        return filteringController.getKernelDensityEstimatorBeanName();
    }

    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        return filteringController.estimateDensityFunction(data, kernelDensityEstimatorBeanName);
    }

    public List<List<double[]>> estimateRawDisplKDE() {
        return filteringController.estimateRawDisplKDE();
    }

    public List<List<double[]>> estimateRawSpeedKDE() {
        return filteringController.estimateRawSpeedKDE();
    }

    public double getPercentageMotile() {
        return percentageMotile;
    }

    public Map<SingleCellConditionDataHolder, Double> getCutOffMap() {
        return cutOffMap;
    }

    public void setCutOffMap(Map<SingleCellConditionDataHolder, Double> cutOffMap) {
        this.cutOffMap = cutOffMap;
    }

    /**
     * Plot the raw KDE for track displacements.
     *
     * @param plateCondition
     */
    public void plotRawKdeMultipleCutOff(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = filteringController.getConditionDataHolder(plateCondition);
        List<List<double[]>> estimateRawDensityFunction = filteringController.estimateRawDensityFunction(conditionDataHolder);
        XYSeriesCollection densityFunction = filteringController.generateDensityFunction(conditionDataHolder, estimateRawDensityFunction);
        JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(conditionDataHolder, densityFunction, "raw KDE track displ", "track displ", false);
        rawKdeChartPanel.setChart(densityChart);
    }

    public XYSeriesCollection generateDensityFunction(List<List<double[]>> densityFunctions) {
        return filteringController.generateDensityFunction(densityFunctions);
    }

    /**
     * Private methods and classes
     */
    // initialize the main view
    private void initMainView() {
        // make a new view
        multipleCutOffPanel = new MultipleCutOffPanel();
        // make a new radio button group for the radio buttons
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(multipleCutOffPanel.getPixelRadioButton());
        radioButtonGroup.add(multipleCutOffPanel.getMicroMeterRadioButton());

        // pixels or µm?
        multipleCutOffPanel.getPixelRadioButton().addActionListener((ActionEvent e) -> {
            multipleCutOffPanel.getConversionFactorTextField().setEnabled(true);
        });

        multipleCutOffPanel.getMicroMeterRadioButton().addActionListener((ActionEvent e) -> {
            // Âµm: we do not need a conversion factor
            multipleCutOffPanel.getConversionFactorTextField().setEnabled(false);
        });

        // set default to micrometer
        multipleCutOffPanel.getMicroMeterRadioButton().setSelected(true);
        // and therefore no need for the conversion factor
        multipleCutOffPanel.getConversionFactorTextField().setEnabled(false);
        // set some default values for the top and bottom translocaton limits
        multipleCutOffPanel.getBottomLimTextField().setText("0.4");
        multipleCutOffPanel.getTopLimTextField().setText("2.8");
        multipleCutOffPanel.getTranslocationStepTextField().setText("0.8");
        multipleCutOffPanel.getPercentageMotileStepsTextField().setText("30");
        percentageMotile = 33;

        // actually do the filtering
        multipleCutOffPanel.getFilterButton().addActionListener((ActionEvent e) -> {

            // try to read the user-inserted values for up and down limit
            // check for number format exception
            try {
                if (multipleCutOffPanel.getPixelRadioButton().isSelected()) {
                    double conversionFactor = Double.parseDouble(multipleCutOffPanel.getConversionFactorTextField().getText());
                    multipleCutOffPanel.getBottomLimTextField().setText(""
                            + AnalysisUtils.roundTwoDecimals(Double.parseDouble(multipleCutOffPanel.getBottomLimTextField().getText()) / conversionFactor));
                    multipleCutOffPanel.getTopLimTextField().setText(""
                            + AnalysisUtils.roundTwoDecimals(Double.parseDouble(multipleCutOffPanel.getTopLimTextField().getText()) / conversionFactor));
                }

                int topLimit = (int) (Double.parseDouble(multipleCutOffPanel.getTopLimTextField().getText()) * 10);
                int bottomLimit = (int) (Double.parseDouble(multipleCutOffPanel.getBottomLimTextField().getText()) * 10);
                double step = Double.parseDouble(multipleCutOffPanel.getTranslocationStepTextField().getText());
                percentageMotile = Double.parseDouble(multipleCutOffPanel.getPercentageMotileStepsTextField().getText());

                int numberSteps = (int) ((topLimit - bottomLimit) / (10 * step)) + 1;
                motileSteps = new ArrayList<>();

                for (int i = 0; i < numberSteps; i++) {
                    motileSteps.add(((double) bottomLimit / 10) + (step * i));
                }
                FilterSwingWorker filterSwingWorker = new FilterSwingWorker();
                filterSwingWorker.execute();

            } catch (NumberFormatException ex) {
                // warn the user and log the error for info
                filteringController.showMessage("Please insert valid numbers!" + "\n " + ex.getMessage(),
                        "number format exception", JOptionPane.ERROR_MESSAGE);
                LOG.error(ex.getMessage());
            }

        });

        // apply a specific cut-off value to all the conditions at once
        multipleCutOffPanel.getApplyCutOffToConditionsButton().addActionListener((ActionEvent e) -> {
            // get the cut-off from the list (sure this is a Double)
            Double value = (Double) multipleCutOffPanel.getCutOffValuesComboBox().getSelectedItem();
            FilterConditionSwingWorker filterConditionSwingWorker = new FilterConditionSwingWorker(value);
            filterConditionSwingWorker.execute();
        });

        // select cut-off for a condition
        multipleCutOffPanel.getSelectCutOffForConditionButton().addActionListener((ActionEvent e) -> {
            if (multipleCutOffPanel.getCutOffValuesComboBox().getSelectedItem() != null) {
                SelectCutOffConditionSwingWorker selectCutOffConditionSwingWorker = new SelectCutOffConditionSwingWorker();
                selectCutOffConditionSwingWorker.execute();
            } else {
                filteringController.showMessage("Please select a cut-off!", "info", JOptionPane.WARNING_MESSAGE);
            }

        });

        AlignedTableRenderer alignedTableRenderer = new AlignedTableRenderer(SwingConstants.LEFT);
        for (int i = 0; i < multipleCutOffPanel.getFilterTrackTable().getColumnModel().getColumnCount(); i++) {
            multipleCutOffPanel.getFilterTrackTable().getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
            multipleCutOffPanel.getSummaryTable().getColumnModel().getColumn(i).setCellRenderer(alignedTableRenderer);
        }
        multipleCutOffPanel.getFilterTrackTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));
        multipleCutOffPanel.getSummaryTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.LEFT));

        // add view to parent component
        filteringController.getFilteringPanel().getMultipleCutOffParentPanel().add(multipleCutOffPanel, gridBagConstraints);
    }

    // initialize the other views
    private void initOtherViews() {
        rawKdeChartPanel = new ChartPanel(null);
        rawKdeChartPanel.setOpaque(false);
        filteredKdeChartPanel = new ChartPanel(null);
        filteredKdeChartPanel.setOpaque(false);

        // add chart panels to parent containers
        multipleCutOffPanel.getRawPlotParentPanel().add(rawKdeChartPanel, gridBagConstraints);
        multipleCutOffPanel.getFilteredPlotParentPanel().add(filteredKdeChartPanel, gridBagConstraints);
    }

    /**
     * Estimate the filtered density functions for the replicates of a single
     * cell condition data holder and given a motile step.
     *
     * @param singleCellConditionDataHolder
     * @param motileStepIndex
     * @return
     */
    private List<List<double[]>> estimateFilteredDensityFunction(SingleCellConditionDataHolder singleCellConditionDataHolder, int motileStepIndex) {
        String kernelDensityEstimatorBeanName = filteringController.getKernelDensityEstimatorBeanName();
        List<List<double[]>> densityFunction = new ArrayList<>();
        Map<SingleCellWellDataHolder, List<TrackDataHolder>> retainedTrackMap = getRetainedTracks(singleCellConditionDataHolder, motileStepIndex);

        retainedTrackMap.keySet().stream().map((singleCellWellDataHolder)
                -> getRetainedDisplacements(retainedTrackMap, singleCellWellDataHolder)).map((retainedDisplacements) -> filteringController.estimateDensityFunction(retainedDisplacements, kernelDensityEstimatorBeanName)).forEach((oneReplicateTrackDisplDensityFunction) -> {
            densityFunction.add(oneReplicateTrackDisplDensityFunction);
        });
        return densityFunction;
    }

    /**
     * Get the tracks that are retained after applying a filter motile step. The
     * method returns a map with key-single cell well data holder and value-the
     * list of retained tracks. This makes sure that the relationship -
     * replicate data/track is maintained in place.
     *
     * @param singleCellConditionDataHolder
     * @param motileStepIndex
     * @return
     */
    private Map<SingleCellWellDataHolder, List<TrackDataHolder>> getRetainedTracks(SingleCellConditionDataHolder singleCellConditionDataHolder, int motileStepIndex) {
        Map<SingleCellWellDataHolder, List<TrackDataHolder>> map = new LinkedHashMap<>();
        Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> tempMap = motileStepsFilterMap.get(singleCellConditionDataHolder);
        tempMap.keySet().stream().forEach((singleCellWellDataHolder) -> {
            List<TrackDataHolder> retainedTracks = new ArrayList<>();

            Map<TrackDataHolder, boolean[]> tempMap2 = tempMap.get(singleCellWellDataHolder);
            tempMap2.keySet().stream().forEach((trackDataHolder) -> {
                boolean isRetained = tempMap2.get(trackDataHolder)[motileStepIndex];
                if (isRetained) {
                    retainedTracks.add(trackDataHolder);
                }
            });
            map.put(singleCellWellDataHolder, retainedTracks);
        });
        return map;
    }

    /**
     * Having the retained tracks, take the displacements.
     *
     * @param retainedTrackMap
     * @param singleCellWellDataHolder
     * @return
     */
    private Double[] getRetainedDisplacements(Map<SingleCellWellDataHolder, List<TrackDataHolder>> retainedTrackMap, SingleCellWellDataHolder singleCellWellDataHolder) {
        List<TrackDataHolder> retainedTracks = retainedTrackMap.get(singleCellWellDataHolder);
        Double[] trackDisplacementsVector = new Double[retainedTracks.size()];
        for (int i = 0; i < trackDisplacementsVector.length; i++) {
            TrackDataHolder retainedTrack = retainedTracks.get(i);
            double trackMeanDisplacement = retainedTrack.getCellCentricDataHolder().getMedianDisplacement();
            trackDisplacementsVector[i] = trackMeanDisplacement;
        }
        return trackDisplacementsVector;
    }

    /**
     * Create the datasets for the filtered KDE.
     *
     * @param plateCondition
     */
    private List<XYSeriesCollection> getRetainedKdeDatasets(PlateCondition plateCondition) {
        SingleCellConditionDataHolder conditionDataHolder = filteringController.getConditionDataHolder(plateCondition);
        List<XYSeriesCollection> xYSeriesCollections = new ArrayList<>();
        for (int i = 0; i < motileSteps.size(); i++) {
            List<List<double[]>> estimateFilteredDensityFunction = estimateFilteredDensityFunction(conditionDataHolder, i);
            XYSeriesCollection densityFunction = filteringController.generateDensityFunction(conditionDataHolder, estimateFilteredDensityFunction);
            xYSeriesCollections.add(densityFunction);
        }
        return xYSeriesCollections;
    }

    /**
     * Plot KDE after filtering -- retained track displacements.
     *
     * @param nCols
     */
    private void plotRetainedKde(PlateCondition plateCondition, int nCols) {
        // reset the logic first
        multipleCutOffPanel.getFilteredPlotParentPanel().removeAll();
        multipleCutOffPanel.getFilteredPlotParentPanel().revalidate();
        multipleCutOffPanel.getFilteredPlotParentPanel().repaint();

        SingleCellConditionDataHolder conditionDataHolder = filteringController.getConditionDataHolder(plateCondition);
        // create the datasets for the plot logic
        List<XYSeriesCollection> filteredKdeDatasets = getRetainedKdeDatasets(plateCondition);
        // the number of plots needed
        int nPlots = filteredKdeDatasets.size();
        for (int i = 0; i < nPlots; i++) {
            XYSeriesCollection collection = filteredKdeDatasets.get(i);
            JFreeChart densityChart = JFreeChartUtils.generateDensityFunctionChart(conditionDataHolder, collection,
                    "step: " + AnalysisUtils.roundTwoDecimals(motileSteps.get(i)) + " filtered KDE track displ", "track displ", false);

            ChartPanel filteredChartPanel = new ChartPanel(densityChart);
            GridBagConstraints tempBagConstraints = GuiUtils.getTempBagConstraints(nPlots, i, nCols);
            multipleCutOffPanel.getFilteredPlotParentPanel().add(filteredChartPanel, tempBagConstraints);

            multipleCutOffPanel.getFilteredPlotParentPanel().revalidate();
            multipleCutOffPanel.getFilteredPlotParentPanel().repaint();
        }
    }

    /**
     * Filter motile steps for a given track and a motile step threshold: if a
     * displacement is equal or greater than the motile step--mark the
     * displacement with a true. Else, the default is false.
     *
     * @param trackDataHolder
     * @param motileStep
     * @return
     */
    private boolean[] filterMotileSteps(TrackDataHolder trackDataHolder, double motileStep) {
        Double[] displacements = trackDataHolder.getStepCentricDataHolder().getInstantaneousDisplacements();
        boolean[] results = new boolean[displacements.length];

        for (int j = 0; j < displacements.length; j++) {
            if (displacements[j] >= motileStep) {
                results[j] = true;
            }
        }
        return results;
    }

    /**
     * Filter a single track. First mark motile steps as True (if >= threshold)
     * or False (otherwise). Then compute the percentage of True motile steps.
     * If this percentage is >= than percentageMotile, return true (and
     * therefore mark track as True), else default to false.
     *
     * @param trackDataHolder
     * @param motileStep
     * @return
     */
    private boolean filterSingleTrack(TrackDataHolder trackDataHolder, double motileStep) {
        boolean[] filterMotileSteps = filterMotileSteps(trackDataHolder, motileStep);
        int count = 0;
        for (int j = 0; j < filterMotileSteps.length; j++) {
            if (filterMotileSteps[j] == true) {
                count++;
            }
        }
        return (count / filterMotileSteps.length) * 100 >= percentageMotile;
    }

    /**
     * Filter motile tracks for a condition: update the filter map.
     */
    private void updateFilterMap(SingleCellConditionDataHolder conditionDataHolder) {

        Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> tempMap2 = new LinkedHashMap<>();

        conditionDataHolder.getSingleCellWellDataHolders().stream().forEach((singleCellWellDataHolder) -> {
            Map<TrackDataHolder, boolean[]> tempMap = new LinkedHashMap<>();
            singleCellWellDataHolder.getTrackDataHolders().stream().forEach((trackDataHolder) -> {
                // an empty default to false array of boolean
                boolean[] filter = new boolean[motileSteps.size()];
                for (int j = 0; j < motileSteps.size(); j++) {
                    filter[j] = filterSingleTrack(trackDataHolder, motileSteps.get(j));
                }
                tempMap.put(trackDataHolder, filter);
            });
            tempMap2.put(singleCellWellDataHolder, tempMap);
        });
        motileStepsFilterMap.put(conditionDataHolder, tempMap2);
    }

    /**
     * Update the ComboBox with the cutoff values.
     */
    private void updateCutOffComboBox() {
        multipleCutOffPanel.getCutOffValuesComboBox().removeAllItems();
        motileSteps.stream().forEach((step) -> {
            multipleCutOffPanel.getCutOffValuesComboBox().addItem(AnalysisUtils.roundTwoDecimals(step));
        });
    }

    /**
     * Update the table with the right elements.
     */
    private void updateFilterTable(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> map = motileStepsFilterMap.get(singleCellConditionDataHolder);
        multipleCutOffPanel.getFilterTrackTable().setModel(new FilterTrackTableModel(map, motileSteps));

        for (int i = 2; i < multipleCutOffPanel.getFilterTrackTable().getColumnCount(); i++) {
            multipleCutOffPanel.getFilterTrackTable().getColumnModel().getColumn(i).setCellRenderer(new FilterTrackTableRenderer());
        }
    }

    /**
     * Filter all the conditions with a specific cut-off value.
     *
     * @param value
     */
    private void filterConditionsForAValue(Double value) {
        filteringController.getPreProcessingMap().values().stream().forEach((conditionDataHolder) -> {
            filterConditionForAValue(conditionDataHolder, value);
        });
        filteringController.setFilteringMap(filteringMap); //???? also to other controller?
    }

    /**
     * Filter one condition with a specific value
     *
     * @param conditionDataHolder
     * @param value
     */
    private void filterConditionForAValue(SingleCellConditionDataHolder conditionDataHolder, Double value) {
        List<TrackDataHolder> retainedTracks = new ArrayList<>();
        conditionDataHolder.getSingleCellWellDataHolders().stream().forEach((wellDataHolder) -> {
            wellDataHolder.getTrackDataHolders().stream().forEach((trackDataHolder) -> {
                boolean filterSingleTrack = filterSingleTrack(trackDataHolder, value);
                if (filterSingleTrack) {
                    retainedTracks.add(trackDataHolder);
                }
            });
        });
        filteringMap.put(conditionDataHolder, retainedTracks);
    }

    /**
     * A swing worker that filters cell tracks based on a set of motile step
     * values and a percentage of motility for a cell trajectory.
     */
    private class FilterSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            filteringController.showWaitingDialog("Filtering: " + filteringController.getCurrentCondition());
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);
            updateCutOffComboBox();
            updateFilterMap(filteringController.getConditionDataHolder(filteringController.getCurrentCondition()));
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // update the table
                updateFilterTable(filteringController.getConditionDataHolder(filteringController.getCurrentCondition()));
                // plot the filtered KDE plots
                plotRetainedKde(filteringController.getCurrentCondition(), 2);
                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

    /**
     * Update the cut off map.
     *
     * @param cellConditionDataHolder
     * @param value
     */
    private void updateCutOffMap(SingleCellConditionDataHolder cellConditionDataHolder, Double value) {
        cutOffMap.put(cellConditionDataHolder, value);
    }

    /**
     * A swing worker that filters all the conditions with a given cut-off
     * selected from the user.
     */
    private class FilterConditionSwingWorker extends SwingWorker<Void, Void> {

        // the selected cut-off value
        private final Double value;

        public FilterConditionSwingWorker(Double value) {
            this.value = value;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // show waiting dialog
            filteringController.showWaitingDialog("Filtering experiment with cut-off: " + value + " and % motile steps: " + percentageMotile);
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);

            // filter all conditions for this specific value
            // get a filtering map and pass it to the parent/child controllers
            filterConditionsForAValue(value);
            summaryMultipleCutOffController.setCutOff(value);

            filteringMap.keySet().stream().forEach((conditionDataHolder) -> {
                updateCutOffMap(conditionDataHolder, value);
            });
            // plot raw and KDE plots in the summary panel - child controller
            summaryMultipleCutOffController.plotKDEs();
            summaryMultipleCutOffController.updateSummaryTable();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // show an info dialog: go to the summary view and have a look there
                JOptionPane.showMessageDialog(multipleCutOffPanel, "Done! Go to the summary tab!", "info", JOptionPane.INFORMATION_MESSAGE);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

    /**
     * A swing worker that filters all the conditions with a given cut-off
     * selected from the user.
     */
    private class SelectCutOffConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            SingleCellConditionDataHolder conditionDataHolder = filteringController.getConditionDataHolder(filteringController.getCurrentCondition());
            Double value = (Double) multipleCutOffPanel.getCutOffValuesComboBox().getSelectedItem();
            // show waiting dialog
            filteringController.showWaitingDialog("Filtering condition: " + conditionDataHolder.getPlateCondition()
                    + " with cut-off: " + value + " and % motile steps: " + percentageMotile);
            // show a waiting cursor, disable GUI components
            filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            filteringController.controlGuiComponents(false);

            filterConditionForAValue(conditionDataHolder, value);

            cutOffMap.put(conditionDataHolder, value);
            summaryMultipleCutOffController.updateSummaryTable();
            summaryMultipleCutOffController.plotKDEs();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();

                // recontrol GUI
                filteringController.hideWaitingDialog();
                filteringController.controlGuiComponents(true);
                filteringController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // show an info dialog: go to the summary view and have a look there
                JOptionPane.showMessageDialog(multipleCutOffPanel,
                        "Done! Summary is updated, please proceed with the other conditions!", "info", JOptionPane.INFORMATION_MESSAGE);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                filteringController.handleUnexpectedError(ex);
            }
        }

    }

}
