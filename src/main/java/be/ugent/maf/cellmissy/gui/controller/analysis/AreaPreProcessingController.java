/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.analysis.AreaUnitOfMeasurement;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache.DataCategory;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeInterval;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.view.table.model.ComputedDataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.analysis.impl.CellCoveredAreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.impl.OpenAreaPreProcessor;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.CorrectedAreaPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.ReplicatesSelectionDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.TimeFramesSelectionDialog;
import be.ugent.maf.cellmissy.gui.view.renderer.CheckBoxOutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.OutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.table.model.DistanceMatrixTableModel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jfree.data.Range;

/**
 * Bulk Cell Analysis Controller: Collective Cell Migration Data Analysis Parent Controller: Data Analysis Controller
 *
 * @author Paola Masuzzo
 */
@Controller("areaPreProcessingController")
public class AreaPreProcessingController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaPreProcessingController.class);
    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepsBindingList;
    private ObservableList<Double> timeFramesBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable dataTable;
    private Map<PlateCondition, AreaPreProcessingResults> preProcessingMap;
    private boolean globalPlotForFirstTime;
    private boolean proceedToAnalysis;
    //view
    private AreaAnalysisPanel areaAnalysisPanel;
    private CorrectedAreaPanel correctedAreaPanel;
    private ReplicatesSelectionDialog replicatesSelectionDialog;
    private TimeFramesSelectionDialog timeFramesSelectionDialog;
    private ChartPanel rawDataChartPanel;
    private ChartPanel transformedAreaChartPanel;
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    private ChartPanel correctedAreaChartPanel;
    private ChartPanel globalAreaChartPanel;
    private JScrollPane distanceMatrixScrollPane;
    //parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    //child controllers
    //services
    @Autowired
    private KernelDensityEstimator kernelDensityEstimator;
    @Autowired
    private DensityFunctionHolderCache densityFunctionHolderCache;
    @Autowired
    private CellCoveredAreaPreProcessor cellCoveredAreaPreProcessor;
    @Autowired
    private OpenAreaPreProcessor openAreaPreProcessor;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init views
        initAreaAnalysisPanel();
        initCorrectedAreaPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public ObservableList<TimeStep> getTimeStepsBindingList() {
        return timeStepsBindingList;
    }

    public AreaAnalysisPanel getAreaAnalysisPanel() {
        return areaAnalysisPanel;
    }

    public Map<PlateCondition, AreaPreProcessingResults> getPreProcessingMap() {
        return preProcessingMap;
    }

    public ChartPanel getGlobalAreaChartPanel() {
        return globalAreaChartPanel;
    }

    public void setGlobalPlotForFirstTime(boolean globalPlotForFirstTime) {
        this.globalPlotForFirstTime = globalPlotForFirstTime;
    }

    public boolean isProceedToAnalysis() {
        return proceedToAnalysis;
    }

    public ObservableList<Double> getTimeFramesBindingList() {
        return timeFramesBindingList;
    }

    /**
     * Get conditions that were actually imaged/processed
     *
     * @return
     */
    public List<PlateCondition> getProcessedConditions() {
        List<PlateCondition> processedConditions = new ArrayList<>();
        for (PlateCondition plateCondition : preProcessingMap.keySet()) {
            if (preProcessingMap.get(plateCondition) != null) {
                processedConditions.add(plateCondition);
            }
        }
        return processedConditions;
    }

    /**
     * public methods and classes
     */
    /**
     * Initialize map with plate conditions as keys and null objects as values
     */
    public void initMapWithConditions() {
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            // each condition is not loaded at the beginning
            plateCondition.setLoaded(false);
            preProcessingMap.put(plateCondition, null);
        }
    }

    /**
     * When a condition is selected pre processing results are computed and condition is put into the map together with its results holder object
     *
     * @param plateCondition
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        if (preProcessingMap.get(plateCondition) == null) {
            AreaPreProcessingResults areaPreProcessingResults = new AreaPreProcessingResults();
            // based on area raw data, do computations for pre-processig step
            areaPreProcessingResults.setAreaRawData(getAreaRawData(plateCondition));
            // normalization depends on type of measured area
            MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
            // call the pre-processors according to measured area type
            switch (measuredAreaType) {
                case CELL_COVERED_AREA:
                    // normalize area
                    cellCoveredAreaPreProcessor.computeNormalizedArea(areaPreProcessingResults);
                    // delta area
                    cellCoveredAreaPreProcessor.computeDeltaArea(areaPreProcessingResults);
                    // % area increase
                    cellCoveredAreaPreProcessor.computeAreaIncrease(areaPreProcessingResults);
                    // correct and normalize area
                    cellCoveredAreaPreProcessor.normalizeCorrectedArea(areaPreProcessingResults);
                    // compute distance matrix
                    cellCoveredAreaPreProcessor.computeDistanceMatrix(areaPreProcessingResults);
                    // exclude replicates
                    cellCoveredAreaPreProcessor.excludeReplicates(areaPreProcessingResults, plateCondition);
                    // set time interval for analysis
                    cellCoveredAreaPreProcessor.setTimeInterval(areaPreProcessingResults);
                    break;
                case OPEN_AREA:
                    dataAnalysisController.getAreaAnalysisHolder().setAreaUnitOfMeasurement(AreaUnitOfMeasurement.PERCENTAGE);
                    // normalize area
                    openAreaPreProcessor.computeNormalizedArea(areaPreProcessingResults);
                    // transform data to cell covered area
                    openAreaPreProcessor.transformAreaData(areaPreProcessingResults);
                    // delta area
                    openAreaPreProcessor.computeDeltaArea(areaPreProcessingResults);
                    // % area increase
                    openAreaPreProcessor.computeAreaIncrease(areaPreProcessingResults);
                    // correct and normalize area
                    openAreaPreProcessor.normalizeCorrectedArea(areaPreProcessingResults);
                    // compute distance matrix
                    openAreaPreProcessor.computeDistanceMatrix(areaPreProcessingResults);
                    // exclude replicates
                    openAreaPreProcessor.excludeReplicates(areaPreProcessingResults, plateCondition);
                    // set time interval for analysis
                    openAreaPreProcessor.setTimeInterval(areaPreProcessingResults);
                    break;
            }
            // fill in map
            preProcessingMap.put(plateCondition, areaPreProcessingResults);
        }
    }

    /**
     * Reset chart panels
     */
    public void resetViews() {
        dataTable.setModel(new DefaultTableModel());
        rawDataChartPanel.setChart(null);
        transformedAreaChartPanel.setChart(null);
        densityChartPanel.setChart(null);
        correctedDensityChartPanel.setChart(null);
        correctedAreaChartPanel.setChart(null);
        globalAreaChartPanel.setChart(null);
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * Called from parent controller if a cancel is called while analysis is performed
     */
    public void resetOnCancel() {
        proceedToAnalysis = false;
        globalPlotForFirstTime = true;
        densityFunctionHolderCache.clearCache();
        resetViews();

        areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
        areaAnalysisPanel.getGraphicsParentPanel().revalidate();
        areaAnalysisPanel.getGraphicsParentPanel().repaint();
    }

    /**
     * show table with TimeSteps results from CellMIA analysis (timeSteps fetched from DB) this is populating the JTable in the ResultsImporter Panel
     */
    public void showTimeStepsInTable() {
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, areaAnalysisPanel.getTimeStepsTable());
        //add column bindings
        ColumnBinding columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.columnNumber}"));
        columnBinding.setColumnName("Column");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${wellHasImagingType.well.rowNumber}"));
        columnBinding.setColumnName("Row");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${timeStepSequence}"));
        columnBinding.setColumnName("Time point");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${area}"));
        String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
        columnBinding.setColumnName("Area " + "(" + areaUnitOfMeasurement + ")");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * for each replicate (well) of a certain selected condition, show delta area values, close to time frames
     *
     * @param plateCondition
     */
    public void showDeltaAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, deltaArea, dataAnalysisController.getTimeFrames()));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     *
     * @param plateCondition
     */
    public void showAreaIncreaseInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] percentageAreaIncrease = preProcessingMap.get(plateCondition).getPercentageAreaIncrease();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, percentageAreaIncrease, dataAnalysisController.getTimeFrames()));
            //format first column
            dataTable.getColumnModel().getColumn(0).setCellRenderer(new FormatRenderer(dataAnalysisController.getFormat()));
            boolean[][] outliers = cellCoveredAreaPreProcessor.detectOutliers(percentageAreaIncrease);
            //show outliers in red from second column on
            OutliersRenderer outliersRenderer = new OutliersRenderer(outliers, dataAnalysisController.getFormat());
            for (int i = 1; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(outliersRenderer);
            }
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        }
        areaAnalysisPanel.getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     *
     * @param plateCondition
     */
    public void showNormalizedAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedArea, dataAnalysisController.getTimeFrames()));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Area is normalized in terms of starting location at time zero");
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show transformed data from open area to cell covered area
     *
     * @param plateCondition
     */
    public void showTransformedDataInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] transformedData = areaPreProcessingResults.getTransformedData();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, transformedData, dataAnalysisController.getTimeFrames()));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Open area is converted to (complementary) cell covered area");
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected (for outliers) area values, close to time frames
     *
     * @param plateCondition
     */
    public void showCorrectedAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedCorrectedArea, dataAnalysisController.getTimeFrames()));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
    }

    /**
     * Plot area raw data (before preprocessing data) for a certain condition
     *
     * @param plateCondition
     */
    public void plotRawDataReplicates(PlateCondition plateCondition) {
        String measuredAreaTypeString = getMeasuredAreaTypeString();
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            // get raw data, not corrected yet but only normalized
            Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
            // Transpose Normalized Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedArea);
            List<Well> processedWells = plateCondition.getProcessedWells();
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = dataAnalysisController.getTimeFrames();
            int counter = 0;
            for (Well well : processedWells) {
                int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
                if (numberOfSamplesPerWell == 1) {
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                        xySeries.setKey("" + (well));
                        xYSeriesCollection.addSeries(xySeries);
                    }
                } else {
                    int label = 0;
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                        xySeries.setKey("" + (well) + ", " + (label + 1));
                        xYSeriesCollection.addSeries(xySeries);
                        label++;
                    }
                }
                counter += numberOfSamplesPerWell;
            }
            // Plot Logic
            String chartTitle = measuredAreaTypeString + "  - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart rawDataAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(rawDataAreaChart, xYSeriesCollection, processedWells);
            rawDataChartPanel.setChart(rawDataAreaChart);
            areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().revalidate();
            areaAnalysisPanel.getGraphicsParentPanel().repaint();
            areaAnalysisPanel.getGraphicsParentPanel().add(rawDataChartPanel, gridBagConstraints);
        }
    }

    /**
     * Plot cell covered area form open area for a certain selected condition
     *
     * @param plateCondition
     */
    public void plotTransformedDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            // get raw data, not corrected yet but only normalized
            Double[][] transformedData = areaPreProcessingResults.getTransformedData();
            // Transpose Normalized Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(transformedData);
            List<Well> processedWells = plateCondition.getProcessedWells();

            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = dataAnalysisController.getTimeFrames();
            int counter = 0;
            for (Well well : processedWells) {
                int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
                if (numberOfSamplesPerWell == 1) {
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                        xySeries.setKey("" + (well));
                        xYSeriesCollection.addSeries(xySeries);
                    }
                } else {
                    int label = 0;
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                        XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                        xySeries.setKey("" + (well) + ", " + (label + 1));
                        xYSeriesCollection.addSeries(xySeries);
                        label++;
                    }
                }
                counter += numberOfSamplesPerWell;
            }
            // Plot Logic
            String chartTitle = "Cell covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart transformedDataAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(transformedDataAreaChart, xYSeriesCollection, processedWells);
            transformedAreaChartPanel.setChart(transformedDataAreaChart);
            areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().revalidate();
            areaAnalysisPanel.getGraphicsParentPanel().repaint();
            areaAnalysisPanel.getGraphicsParentPanel().add(transformedAreaChartPanel, gridBagConstraints);
        }
    }

    /**
     * Show Area Replicates for a certain selected condition
     *
     * @param plateCondition
     */
    public void plotCorrectedDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            // Transpose Normalized Corrected Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
            List<Well> processedWells = plateCondition.getProcessedWells();
            // check if some replicates need to be hidden from plot (this means these replicates are outliers)
            boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
            List excludedWells = new ArrayList();
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = dataAnalysisController.getTimeFrames();
            int counter = 0;
            for (Well well : processedWells) {
                int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
                if (numberOfSamplesPerWell == 1) {
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        // if boolean is false, replicate has to be considered in the plot
                        if (!excludeReplicates[i]) {
                            // array for y axis
                            double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                            XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                            xySeries.setKey("" + (well));
                            xYSeriesCollection.addSeries(xySeries);
                        } else {
                            // replicates excluded
                            excludedWells.add(well);
                        }
                    }
                } else {
                    int label = 0;
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        // if boolean is false, replicate has to be considered in the plot
                        if (!excludeReplicates[i]) {
                            // array for y axis
                            double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                            XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                            xySeries.setKey("" + (well) + ", " + (label + 1));
                            xYSeriesCollection.addSeries(xySeries);
                            label++;
                        } else {
                            // replicates excluded
                            excludedWells.add(well);
                        }
                    }
                }
                counter += numberOfSamplesPerWell;
            }
            // Plot Logic
            String chartTitle = "Cell covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, xYSeriesCollection, processedWells);
            correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
            correctedAreaChartPanel.setChart(correctedAreaChart);
            correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
            correctedAreaPanel.getCutOffCheckBox().setSelected(false);
            // time frame info
            int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
            correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
            correctedAreaPanel.getExcludedReplicatesTextArea().setText(excludedWells.toString());
            areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Plot Density Functions for both raw and corrected area data. A Swing Worker is used, and a cache to hold density functions values.
     *
     * @param plateCondition
     */
    public void plotDensityFunctions(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            PlotDensityFunctionSwingWorker plotDensityFunctionSwingWorker = new PlotDensityFunctionSwingWorker(plateCondition);
            plotDensityFunctionSwingWorker.execute();
        }
    }

    /**
     * Create chart with global view (all biological conditions, with area evolution in time)
     *
     * @param plateConditionList
     * @param useCorrectedData
     * @param plotErrorBars
     * @param measuredAreaType
     * @return
     */
    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, MeasuredAreaType measuredAreaType) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<Double[]> yErrorsList = new ArrayList<>();
        Double[][] dataToShow = null;
        for (PlateCondition plateCondition : plateConditionList) {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            if (areaPreProcessingResults != null) {
                switch (measuredAreaType) {
                    case CELL_COVERED_AREA:
                        if (useCorrectedData) {
                            dataToShow = areaPreProcessingResults.getNormalizedCorrectedArea();
                        } else {
                            dataToShow = areaPreProcessingResults.getNormalizedArea();
                        }
                        break;
                    case OPEN_AREA:
                        if (useCorrectedData) {
                            dataToShow = areaPreProcessingResults.getNormalizedCorrectedArea();
                        } else {
                            dataToShow = areaPreProcessingResults.getTransformedData();
                        }
                        break;
                }
                // Boolean (Exclude Replicates from dataset)
                boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
                // time interval to use
                TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                // array for x axis: sub selection of time frames
                double[] xValues = new double[timeInterval.getLastTimeFrame() - timeInterval.getFirstTimeFrame() + 1];
                // array for y axis: same length of x axis
                double[] yValues = new double[xValues.length];
                Double[] yErrors = new Double[xValues.length];

                int index = timeInterval.getFirstTimeFrame();
                for (int i = 0; i < xValues.length; i++) {
                    xValues[i] = timeFramesBindingList.get(index);
                    index++;
                }
                //time frames direction
                index = timeInterval.getFirstTimeFrame();
                for (int i = 0; i < yValues.length; i++) {
                    double[] allReplicateValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(dataToShow[index]));
                    List<Double> replicatesToIncludeList = new ArrayList();
                    for (int j = 0; j < allReplicateValues.length; j++) {
                        // check if replicate has to be excluded from dataset
                        if (!excludeReplicates[j]) {
                            replicatesToIncludeList.add(allReplicateValues[j]);
                        }
                    }
                    Double[] replicatesToIncludeArray = replicatesToIncludeList.toArray(new Double[replicatesToIncludeList.size()]);
                    if (replicatesToIncludeArray.length != 0) {
                        double median = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(replicatesToIncludeArray));
                        yValues[i] = median;
                        double mad = AnalysisUtils.computeSEM(ArrayUtils.toPrimitive(replicatesToIncludeArray));
                        yErrors[i] = mad;
                        index++;
                    }
                }
                yErrorsList.add(yErrors);
                XYSeries values = JFreeChartUtils.generateXYSeries(xValues, yValues);
                values.setKey("Condition " + (dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1));
                xySeriesCollection.addSeries(values);
            }
        }
        String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        if (plotErrorBars) {
            JFreeChartUtils.plotVerticalErrorBars(globalAreaChart, xySeriesCollection, yErrorsList);
            globalAreaChart.getXYPlot().getRangeAxis().setUpperBound(JFreeChartUtils.computeMaxY(xySeriesCollection) + AnalysisUtils.getMaxOfAList(yErrorsList));
        }
        JFreeChartUtils.setupGlobalAreaChart(globalAreaChart, xySeriesCollection);
        return globalAreaChart;
    }

    /**
     * Called by parent controller, show global view
     */
    public void onGlobalView() {
        // check if global area is for the first time
        if (globalPlotForFirstTime) {
            // create and execute a swing worker
            FetchAllConditionsSwingWorker fetchAllConditionsSwingWorker = new FetchAllConditionsSwingWorker();
            // add property change listener to worker
            fetchAllConditionsSwingWorker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        dataAnalysisController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setValue(progress);
                    }
                }
            });
            fetchAllConditionsSwingWorker.execute();
        } else {
            List<PlateCondition> processedConditions = getProcessedConditions();
            List<PlateCondition> selectedConditions = getSelectedConditions();
            List<Integer> numberOfReplicates = getNumberOfReplicates();
            // enable now tab for analysis
            // enable check box to show error bars
            areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
            ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, areaAnalysisPanel.getConditionsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
            areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, numberOfReplicates));
            boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
            boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
            MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
            if (selectedConditions.isEmpty()) {
                plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, measuredAreaType);
            } else {
                plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, measuredAreaType);
            }
        }
    }

    /**
     * Called by parent controller, show linear regression model results
     */
    public void onLinearRegressionModel() {
        boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
        // show Linear Model Results from the other child controller
        dataAnalysisController.showLinearModelInTable(useCorrectedData);
    }

    /**
     * private methods and classes
     */
    /**
     * Get area unit of measurement
     *
     * @return
     */
    private String getAreaUnitOfMeasurementString() {
        return dataAnalysisController.getAreaUnitOfMeasurement();
    }

    private String getMeasuredAreaTypeString() {
        return dataAnalysisController.getMeasuredAreaType();
    }

    /**
     * Show Table with Euclidean Distances between all replicates for a certain selected condition
     *
     * @param plateCondition
     */
    private void showDistanceMatrix(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] distanceMatrix = areaPreProcessingResults.getDistanceMatrix();
            boolean[][] outliersMatrix = cellCoveredAreaPreProcessor.detectOutliers(distanceMatrix);
            boolean[][] transposedOutliersMatrix = AnalysisUtils.transposeBooleanMatrix(outliersMatrix);
            DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(distanceMatrix, outliersMatrix, plateCondition);
            // if user already had interaction through check boxes overwrite distance matrix table behavior 
            if (areaPreProcessingResults.isUserSelectedReplicates()) {
                distanceMatrixTableModel.setCheckboxOutliers(areaPreProcessingResults.getExcludeReplicates());
            }
            JTable distanceMatrixTable = new JTable(distanceMatrixTableModel);
            // Renderer
            CheckBoxOutliersRenderer checkBoxOutliersRenderer = new CheckBoxOutliersRenderer(transposedOutliersMatrix, dataAnalysisController.getFormat());
            // Cell Editor
            CheckBoxCellEditor checkBoxCellEditor = new CheckBoxCellEditor(distanceMatrixTableModel, plateCondition);
            for (int i = 1; i < distanceMatrixTable.getColumnCount(); i++) {
                //@todo: cell editor is set for each column and row, but needs to be set only for last row
                distanceMatrixTable.getColumnModel().getColumn(i).setCellEditor(checkBoxCellEditor);
                distanceMatrixTable.getColumnModel().getColumn(i).setCellRenderer(checkBoxOutliersRenderer);
            }
            distanceMatrixTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
            // disable row selection
            distanceMatrixTable.setRowSelectionAllowed(false);
            distanceMatrixScrollPane.setViewportView(distanceMatrixTable);
            distanceMatrixScrollPane.getViewport().setBackground(Color.white);
            replicatesSelectionDialog.getDistanceMatrixTableParentPanel().add(distanceMatrixScrollPane, gridBagConstraints);
        }
    }

    /**
     * Plot Corrected data Area for selected condition, taking into account both time selection and eventual replicate exclusion
     *
     * @param plateCondition
     */
    private void plotCorrectedDataInTimeInterval(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            // Transpose Normalized Corrected Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
            List<Well> processedWells = plateCondition.getProcessedWells();
            // check if some replicates need to be hidden from plot (this means these replicates are outliers)
            boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
            // check for time frames interval
            TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
            XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
            // array for x axis: sub selection of time frames
            double[] xValues = new double[timeInterval.getLastTimeFrame() - timeInterval.getFirstTimeFrame() + 1];
            int index = timeInterval.getFirstTimeFrame();
            for (int i = 0; i < xValues.length; i++) {
                xValues[i] = timeFramesBindingList.get(index);
                index++;
            }
            int counter = 0;
            for (Well well : processedWells) {
                int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
                if (numberOfSamplesPerWell == 1) {
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        index = timeInterval.getFirstTimeFrame();
                        // if boolean is false, replicate has to be considered in the plot
                        if (!excludeReplicates[i]) {
                            // array for y axis (no need to exclude null values)
                            double[] yValues = new double[xValues.length];
                            for (int j = 0; j < yValues.length; j++) {
                                yValues[j] = transposedArea[i][index];
                                index++;
                            }
                            XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                            xySeries.setKey("" + (well));
                            xySeriesCollection.addSeries(xySeries);
                        }
                    }
                    counter += numberOfSamplesPerWell;
                } else {
                    int label = 0;
                    for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                        index = timeInterval.getFirstTimeFrame();
                        // if boolean is false, replicate has to be considered in the plot
                        if (!excludeReplicates[i]) {
                            // array for y axis (no need to exclude null values)
                            double[] yValues = new double[xValues.length];
                            for (int j = 0; j < yValues.length; j++) {
                                yValues[j] = transposedArea[i][index];
                                index++;
                            }
                            XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                            xySeries.setKey("" + (well) + ", " + (label + 1));
                            xySeriesCollection.addSeries(xySeries);
                            label++;
                        }
                    }
                    counter += numberOfSamplesPerWell;
                }
            }
            // Plot Logic
            String chartTitle = "Cell covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, xySeriesCollection, processedWells);
            correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
            correctedAreaChartPanel.setChart(correctedAreaChart);
            correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
            // time frame info
            int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
            correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
            areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Plot global area for a list of certain biological conditions, using or not corrected data and plotting or not error bars on top
     *
     * @param plateConditionList
     * @param useCorrectedData
     * @param plotErrorBars
     */
    private void plotGlobalArea(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, MeasuredAreaType measuredAreaType) {
        JFreeChart globalAreaChart = createGlobalAreaChart(plateConditionList, useCorrectedData, plotErrorBars, measuredAreaType);
        globalAreaChartPanel.setChart(globalAreaChart);
        areaAnalysisPanel.getGlobalAreaPanel().add(globalAreaChartPanel, gridBagConstraints);
        areaAnalysisPanel.getGlobalAreaPanel().repaint();
    }

    /**
     * from time steps List to 2D array of Double
     *
     * @param plateCondition
     * @return 2D array with area raw data
     */
    private Double[][] getAreaRawData(PlateCondition plateCondition) {
        int numberOfSamplesPerCondition = AnalysisUtils.getNumberOfSamplesPerCondition(plateCondition);
        boolean firstAreaIsZero = false;
        Double[][] areaRawData = new Double[dataAnalysisController.getTimeFrames().length][numberOfSamplesPerCondition];
        int counter = 0;
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                // check for first row: sometimes area raw data is already equal to zero at first time 
                if (rowIndex != 0) {
                    if (timeStepsBindingList.get(counter).getArea() != 0) {
                        areaRawData[rowIndex][columnIndex] = timeStepsBindingList.get(counter).getArea();
                    } else if (timeStepsBindingList.get(counter).getArea() == 0 && firstAreaIsZero) {
                        areaRawData[rowIndex][columnIndex] = 0.0;
                    } else {
                        areaRawData[rowIndex][columnIndex] = null;
                    }
                } else {
                    areaRawData[rowIndex][columnIndex] = timeStepsBindingList.get(counter).getArea();
                    if (areaRawData[rowIndex][columnIndex] == 0) {
                        firstAreaIsZero = true;
                    }
                }
                counter++;
            }
        }
        return areaRawData;
    }

    /**
     * Given a chart for the raw data density function, show it
     *
     * @param densityChart
     */
    private void plotRawDataDensityFunctions(JFreeChart densityChart) {
        densityChartPanel.setChart(densityChart);
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
        areaAnalysisPanel.getGraphicsParentPanel().revalidate();
        areaAnalysisPanel.getGraphicsParentPanel().repaint();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        areaAnalysisPanel.getGraphicsParentPanel().add(densityChartPanel, gridBagConstraints);
    }

    /**
     * Given a chart for the corrected data density function, show it
     *
     * @param densityChart
     */
    private void plotCorrectedDataDensityFunctions(JFreeChart densityChart) {
        correctedDensityChartPanel.setChart(densityChart);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        areaAnalysisPanel.getGraphicsParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
    }

    /**
     * Given a map with density functions inside, create xySeriesCollection
     *
     * @param plateCondition
     * @param dataCategory
     * @param densityFunctionsMap
     * @return
     */
    private XYSeriesCollection generateDensityFunction(PlateCondition plateCondition, Map<DataCategory, List<List<double[]>>> densityFunctionsMap, DataCategory dataCategory) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<Well> imagedWells = plateCondition.getImagedWells();
        List<List<double[]>> densityFunctions = densityFunctionsMap.get(dataCategory);
        int counter = 0;
        for (Well well : imagedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
            if (numberOfSamplesPerWell == 1) {
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    // x values
                    double[] xValues = densityFunctions.get(i).get(0);
                    // y values
                    double[] yValues = densityFunctions.get(i).get(1);
                    XYSeries series = new XYSeries("" + well, false);
                    for (int j = 0; j < xValues.length; j++) {
                        double x = xValues[j];
                        double y = yValues[j];
                        series.add(x, y);
                    }
                    xySeriesCollection.addSeries(series);
                }
                counter += numberOfSamplesPerWell;
            } else {
                int label = 0;
                for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                    // x values
                    double[] xValues = densityFunctions.get(i).get(0);
                    // y values
                    double[] yValues = densityFunctions.get(i).get(1);
                    XYSeries series = new XYSeries("" + (well) + ", " + (label + 1), false);
                    for (int j = 0; j < xValues.length; j++) {
                        double x = xValues[j];
                        double y = yValues[j];
                        series.add(x, y);
                    }
                    xySeriesCollection.addSeries(series);
                    label++;
                }
                counter += numberOfSamplesPerWell;
            }
        }
        return xySeriesCollection;
    }

    /**
     * This is the only method that makes use of the kernel density estimator interface. Given a condition, this is estimating the density functions for both raw and corrected data.
     *
     * @param plateCondition
     * @return a map of DataCategory (enum of type: raw data or corrected data) and a list of list of double arrays: each list of array of double has two components: x values and y values.
     */
    private Map<DataCategory, List<List<double[]>>> estimateDensityFunctions(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>> densityFunctions = new HashMap<>();
        List<List<double[]>> rawDataDensityFunctions = new ArrayList<>();
        List<List<double[]>> correctedDataDensityFunctions = new ArrayList<>();
        // raw data
        Double[][] percentageAreaIncrease = areaPreProcessingResults.getPercentageAreaIncrease();
        Double[][] transposedRawData = AnalysisUtils.transpose2DArray(percentageAreaIncrease);
        // corrected data (after outliers detection)
        Double[][] correctedArea = cellCoveredAreaPreProcessor.correctForOutliers(percentageAreaIncrease);
        Double[][] transposedCorrectedData = AnalysisUtils.transpose2DArray(correctedArea);
        for (int i = 0; i < transposedRawData.length; i++) {
            // compute density function for each replicate of the raw data
            List<double[]> oneReplicateRawDataDensityFunction = kernelDensityEstimator.estimateDensityFunction(transposedRawData[i]);
            // compute density function for each replicate of the corrected data
            List<double[]> oneReplicateCorrectedDataDensityFunction = kernelDensityEstimator.estimateDensityFunction(transposedCorrectedData[i]);
            // per replicate
            rawDataDensityFunctions.add(oneReplicateRawDataDensityFunction);
            correctedDataDensityFunctions.add(oneReplicateCorrectedDataDensityFunction);
        }
        // per condition
        // raw data density functions into map
        densityFunctions.put(DensityFunctionHolderCache.DataCategory.RAW_DATA, rawDataDensityFunctions);
        // corrected data density functions into map
        densityFunctions.put(DensityFunctionHolderCache.DataCategory.CORRECTED_DATA, correctedDataDensityFunctions);
        return densityFunctions;
    }

    /**
     * initialize main panel
     */
    private void initAreaAnalysisPanel() {
        // init main view and add it to parent panel
        areaAnalysisPanel = new AreaAnalysisPanel();

        // time steps table can not be edit, but it can be selected through columns
        areaAnalysisPanel.getTimeStepsTable().setColumnSelectionAllowed(true);
        areaAnalysisPanel.getTimeStepsTable().setRowSelectionAllowed(false);
        // set background to white 
        areaAnalysisPanel.getTimeStepsTableScrollPane().getViewport().setBackground(Color.white);

        //init Tables
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        areaAnalysisPanel.getDataTablePanel().add(scrollPane);
        //init timeStepsBindingList 
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());

        globalPlotForFirstTime = true;
        proceedToAnalysis = false;
        //init subview
        correctedAreaPanel = new CorrectedAreaPanel();
        //init chart panels
        rawDataChartPanel = new ChartPanel(null);
        rawDataChartPanel.setOpaque(false);
        transformedAreaChartPanel = new ChartPanel(null);
        transformedAreaChartPanel.setOpaque(false);
        densityChartPanel = new ChartPanel(null);
        densityChartPanel.setOpaque(false);
        correctedDensityChartPanel = new ChartPanel(null);
        correctedDensityChartPanel.setOpaque(false);
        correctedAreaChartPanel = new ChartPanel(null);
        correctedAreaChartPanel.setOpaque(false);
        globalAreaChartPanel = new ChartPanel(null);
        globalAreaChartPanel.setOpaque(false);

        distanceMatrixScrollPane = new JScrollPane();
        replicatesSelectionDialog = new ReplicatesSelectionDialog(dataAnalysisController.getCellMissyFrame(), true);
        timeFramesSelectionDialog = new TimeFramesSelectionDialog(dataAnalysisController.getCellMissyFrame(), true);
        //center the dialogs on the main screen
        replicatesSelectionDialog.setLocationRelativeTo(dataAnalysisController.getCellMissyFrame());
        timeFramesSelectionDialog.setLocationRelativeTo(dataAnalysisController.getCellMissyFrame());

        timeFramesSelectionDialog.getDefaultCutOffTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // justify text info 
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = replicatesSelectionDialog.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        styledDocument = timeFramesSelectionDialog.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        styledDocument = areaAnalysisPanel.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);

        // hide warning message
        timeFramesSelectionDialog.getWarningLabel().setVisible(false);
        preProcessingMap = new LinkedHashMap<>();
        areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(false);
        areaAnalysisPanel.getUseCorrectedDataCheckBox().setSelected(true);

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup radioButtonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        radioButtonGroup.add(areaAnalysisPanel.getNormalizeAreaButton());
        radioButtonGroup.add(areaAnalysisPanel.getDeltaAreaButton());
        radioButtonGroup.add(areaAnalysisPanel.getPercentageAreaIncreaseButton());
        radioButtonGroup.add(areaAnalysisPanel.getCorrectedAreaButton());
        radioButtonGroup.add(areaAnalysisPanel.getCellCoveredAreaRadioButton());

        //select as default first button (Normalized Area values Computation)
        areaAnalysisPanel.getNormalizeAreaButton().setSelected(true);

        /**
         * Calculate Normalized Area (with corrected values for Jumps)
         */
        areaAnalysisPanel.getNormalizeAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show normalized values in the table
                    showNormalizedAreaInTable(dataAnalysisController.getCurrentCondition());
                    //set charts panel to null
                    densityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    rawDataChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show raw data plot (replicates)
                    plotRawDataReplicates(dataAnalysisController.getCurrentCondition());
                }
            }
        });

        /**
         *
         */
        areaAnalysisPanel.getCellCoveredAreaRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    showTransformedDataInTable(dataAnalysisController.getCurrentCondition());
                    //set charts panel to null
                    densityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    rawDataChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawDataChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show transformed data plot (replicates)
                    plotTransformedDataReplicates(dataAnalysisController.getCurrentCondition());
                }
            }
        });

        /**
         * Show Delta Area Values
         */
        areaAnalysisPanel.getDeltaAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show delta area values in the table            
                    showDeltaAreaInTable(dataAnalysisController.getCurrentCondition());
                    // remove other panels
                    rawDataChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    densityChartPanel.setChart(null);
                    correctedDensityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                }
            }
        });

        /**
         * Show %Area increase values
         */
        areaAnalysisPanel.getPercentageAreaIncreaseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getCurrentCondition() != null) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    showAreaIncreaseInTable(dataAnalysisController.getCurrentCondition());
                    // remove other panels
                    rawDataChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    plotDensityFunctions(dataAnalysisController.getCurrentCondition());
                }
            }
        });

        /**
         * show Corrected values for Area (corrected for outliers intra replicate) show table with Euclidean distances between all replicates plot area replicates according to distance matrix
         */
        areaAnalysisPanel.getCorrectedAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    // show values in table
                    showCorrectedAreaInTable(dataAnalysisController.getCurrentCondition());
                    // remove other panels
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawDataChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().revalidate();
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // plot corrected area (all replicates for selected condition)
                    plotCorrectedDataReplicates(dataAnalysisController.getCurrentCondition());
                    // enable global view and analysis
                    proceedToAnalysis = true;
                    dataAnalysisController.getAnalysisExperimentPanel().getNextButton().setEnabled(proceedToAnalysis);
                }
            }
        });

        /**
         * Add Item Listener to error bars Check Box: plot area increases with or without error bars on top
         */
        areaAnalysisPanel.getPlotErrorBarsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // check if conditions are selected, if not plot entire dataset
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, true, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, true, measuredAreaType);
                    }
                } else {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, false, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, false, measuredAreaType);
                    }
                }
            }
        });


        /**
         * Add item listener to use corrected data check box: use or not corrected data for global plot and linear model?
         */
        areaAnalysisPanel.getUseCorrectedDataCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, true, plotErrorBars, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, true, plotErrorBars, measuredAreaType);
                    }
                } else {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, false, plotErrorBars, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, false, plotErrorBars, measuredAreaType);
                    }
                }
            }
        });

        /**
         * Plot only some conditions
         */
        areaAnalysisPanel.getPlotSelectedConditionsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // plot global area only for selected conditions
                // make a distinction if error bars needed to be plot or not, and if the user wants to plot raw data or corrected data
                List<PlateCondition> selectedConditions = getSelectedConditions();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
                plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, measuredAreaType);
            }
        });

        /**
         * Clear selection from the list and plot all conditions together
         */
        areaAnalysisPanel.getPlotAllConditionsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<PlateCondition> processedConditions = getProcessedConditions();
                // clear selection from list
                areaAnalysisPanel.getConditionsList().clearSelection();
                // plot global area for all conditions, checking if error bars need to be shown or not, and if raw data or corrected data 
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
                plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, measuredAreaType);
            }
        });

        // add view to parent panel
        dataAnalysisController.getDataAnalysisPanel().getAreaAnalysisParentPanel().add(areaAnalysisPanel, gridBagConstraints);
    }

    /**
     * Swing Worker for Density Function(s) Plot
     */
    private class PlotDensityFunctionSwingWorker extends SwingWorker<Void, Void> {

        private PlateCondition plateCondition;
        // xySeriesCollections needed for raw data and for corrected data
        private XYSeriesCollection rawDataXYSeriesCollection;
        private XYSeriesCollection correctedDataXYSeriesCollection;
        private Map<DataCategory, List<List<double[]>>> densityFunctionsMap;

        public PlotDensityFunctionSwingWorker(PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
        }

        @Override
        protected Void doInBackground() throws Exception {
            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            // check if density functions have already been computed: in this case, they are stored in the cache
            if (densityFunctionHolderCache.containsKey(plateCondition)) {
                // if results are in cache, get them from cache
                densityFunctionsMap = densityFunctionHolderCache.getFromCache(plateCondition);
            } else {
                // else estimate results and put them in cache
                densityFunctionsMap = estimateDensityFunctions(plateCondition);
                densityFunctionHolderCache.putInCache(plateCondition, densityFunctionsMap);
            }
            // set xyseriesCollections calling the generateDensityFunctions method
            rawDataXYSeriesCollection = generateDensityFunction(plateCondition, densityFunctionsMap, DataCategory.RAW_DATA);
            correctedDataXYSeriesCollection = generateDensityFunction(plateCondition, densityFunctionsMap, DataCategory.CORRECTED_DATA);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // once xySeriesCollections are generated, generate also Charts and show results
                int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
                JFreeChart rawDensityChart = JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, rawDataXYSeriesCollection, "KDE raw data");
                plotRawDataDensityFunctions(rawDensityChart);
                JFreeChart correctedDensityChart = JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, correctedDataXYSeriesCollection, "KDE corrected data");
                plotCorrectedDataDensityFunctions(correctedDensityChart);
                dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                dataAnalysisController.showMessage("Unexpected error occured: " + ex.getMessage() + ", please try to restart the application.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Get the number of conditions that have already been analyzed The user has clicked on them and pre-process results were already computed
     *
     * @return
     */
    private int getNumberOfFetchedCondition() {
        int progress = 0;
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            if (preProcessingMap.get(plateCondition) != null) {
                progress++;
            }
        }
        return progress;
    }

    /**
     * Get List of Selected conditions from RectIcon List
     *
     * @return List of Plate Conditions to be used to refresh plot
     */
    private List<PlateCondition> getSelectedConditions() {
        // get selected indices from rect icon list
        int[] selectedIndices = areaAnalysisPanel.getConditionsList().getSelectedIndices();
        List<PlateCondition> selectedConditions = new ArrayList<>();
        for (int selectedIndex : selectedIndices) {
            PlateCondition selectedCondition = dataAnalysisController.getPlateConditionList().get(selectedIndex);
            selectedConditions.add(selectedCondition);
        }
        return selectedConditions;
    }

    /**
     * Get all numbers of replicates (for all conditions)
     *
     * @return
     */
    private List<Integer> getNumberOfReplicates() {
        List<Integer> numberOfReplicates = new ArrayList<>();
        for (PlateCondition plateCondition : getProcessedConditions()) {
            int numberOfReplicatesPerCondition = 0;
            boolean[] excludeReplicates = preProcessingMap.get(plateCondition).getExcludeReplicates();
            for (boolean excludeReplicate : excludeReplicates) {
                if (!excludeReplicate) {
                    numberOfReplicatesPerCondition++;
                }
            }
            numberOfReplicates.add(numberOfReplicatesPerCondition);
        }
        return numberOfReplicates;
    }

    /**
     * I am keeping this Editor in this controller since it has to update area image
     */
    private final class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

        private JCheckBox checkBox;
        private final PlateCondition plateCondition;
        private DistanceMatrixTableModel distanceMatrixTableModel;

        // Contructor
        public CheckBoxCellEditor(DistanceMatrixTableModel distanceMatrixTableModel, PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
            this.distanceMatrixTableModel = distanceMatrixTableModel;
            checkBox = new JCheckBox();
            checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
            checkBox.addItemListener(this);
        }

        @Override
        public Object getCellEditorValue() {
            return Boolean.valueOf(checkBox.isSelected());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // if value is true, select checkbox, else do nothing
            checkBox.setSelected((boolean) value);
            return checkBox;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            fireEditingStopped();
            updateAreaPlot();
        }

        // Determine with replicates need to be shown and update Area image on the right panel
        private void updateAreaPlot() {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            // Get boolean from table model and pass it to the results holder
            areaPreProcessingResults.setExcludeReplicates(distanceMatrixTableModel.getCheckboxOutliers());
            // update area image excluding selected technical replicates
            plotCorrectedDataReplicates(plateCondition);
            // keep note of the fact that the user had interaction with check boxes
            preProcessingMap.get(plateCondition).setUserSelectedReplicates(true);
            // recompute time interval for selected condition
            cellCoveredAreaPreProcessor.setTimeInterval(areaPreProcessingResults);
            int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
            correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        }
    }

    /**
     * Swing Worker for Global Area Plot: we check how many conditions were already fetched, and we update the map of bulk cell analysis controller in background, all the computations needed for the
     * global area view plot are performed.
     */
    private class FetchAllConditionsSwingWorker extends SwingWorker<Void, Void> {

        List<PlateCondition> plateConditionList = dataAnalysisController.getPlateConditionList();

        @Override
        protected Void doInBackground() throws Exception {
            // disable buttons
            dataAnalysisController.onButtonsState(false);
            int progress = getNumberOfFetchedCondition();
            // initialize progress property.
            setProgress(progress);
            // show progress bar
            dataAnalysisController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setVisible(true);
            // set max value of progress bar to size of conditions' list
            dataAnalysisController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setMaximum(plateConditionList.size());
            // show waiting cursor
            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            for (PlateCondition plateCondition : plateConditionList) {
                // if for current condition computations were not performed yet
                if (preProcessingMap.get(plateCondition) == null) {
                    // fetch current condition
                    dataAnalysisController.fetchConditionTimeSteps(plateCondition);
                    if (!timeStepsBindingList.isEmpty()) {
                        // update map (this is actually doing all the computations)
                        updateMapWithCondition(plateCondition);
                    }
                    // the condition is loaded, and plate view is refreshed
                    dataAnalysisController.showNotImagedWells(plateCondition);
                    progress++;
                    setProgress(Math.min(progress, plateConditionList.size()));
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable buttons
                dataAnalysisController.onButtonsState(true);
                globalPlotForFirstTime = false;
                List<PlateCondition> processedConditions = getProcessedConditions();
                // when the thread is done, hide progress bar again
                dataAnalysisController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setVisible(false);
                dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
                // show all conditions in one plot (Global Area View)
                MeasuredAreaType measuredAreaType = dataAnalysisController.getAreaAnalysisHolder().getMeasuredAreaType();
                plotGlobalArea(processedConditions, true, false, measuredAreaType);
                // enable check box to show error bars
                areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
                ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
                JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, areaAnalysisPanel.getConditionsList());
                bindingGroup.addBinding(jListBinding);
                bindingGroup.bind();
                areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, getNumberOfReplicates()));
                if (processedConditions.size() != plateConditionList.size()) {
                    // inform the user that not all conditions were imaged
                    dataAnalysisController.showMessage("Note that not every condition was imaged!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                dataAnalysisController.showMessage("Unexpected error occured: " + ex.getMessage() + ", please try to restart the application.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Initialize Matrix Panel
     */
    private void initCorrectedAreaPanel() {
        // set Border to empty one for text fields
        correctedAreaPanel.getCutOffTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        correctedAreaPanel.getExcludedReplicatesTextArea().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // initialize Binding List for time frames (2 combo boxes binded)
        timeFramesBindingList = ObservableCollections.observableList(new ArrayList<Double>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionDialog.getCutOffTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionDialog.getFirstTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * Show the effect that the cut off time frame has on the plot
         */
        correctedAreaPanel.getCutOffCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // update plot when cut off has to be shown
                    plotCorrectedDataInTimeInterval(dataAnalysisController.getCurrentCondition());
                } else {
                    // if check box is delesected show entire dataset
                    plotCorrectedDataReplicates(dataAnalysisController.getCurrentCondition());
                }
            }
        });

        /**
         * If the user decides to modify the current cut off value I need to check if the chosen value is greater or lesser than the computed cut off.
         */
        timeFramesSelectionDialog.getCutOffTimeFrameComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // (re)set to invisible the warning message
                timeFramesSelectionDialog.getWarningLabel().setVisible(false);
                // results holder for currently selected condition
                PlateCondition currentCondition = dataAnalysisController.getCurrentCondition();
                if (currentCondition != null) {
                    AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(currentCondition);
                    TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                    // get last time frame selected
                    Double selected = (Double) timeFramesSelectionDialog.getCutOffTimeFrameComboBox().getSelectedItem();
                    if (selected != null) {
                        int selectedLastTimeFrame = timeFramesBindingList.indexOf(selected);
                        // if last time frame provided by the user is equal or smaller than cut off time frame (and greater than first time frame): No problem
                        if (selectedLastTimeFrame <= timeInterval.getProposedCutOff() && selectedLastTimeFrame > timeInterval.getFirstTimeFrame()) {
                            timeInterval.setLastTimeFrame(selectedLastTimeFrame);
                            // update plot
                            plotCorrectedDataInTimeInterval(dataAnalysisController.getCurrentCondition());
                        } else if (selectedLastTimeFrame > timeInterval.getProposedCutOff()) {
                            // if last time frame provided by the user is greater than cut off time frame: Warn the user!
                            timeFramesSelectionDialog.getWarningLabel().setVisible(true);
                        } else if (selectedLastTimeFrame < timeInterval.getFirstTimeFrame()) {
                            // last time frame can not be smaller than first one: warn the user and ignore selection
                            dataAnalysisController.showMessage("Last time frame cannot be smaller than first one!", "Error in chosing time frames", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

            }
        });

        /**
         * If the user decides to start analysis from a value different from zero This is updating the plot and at the same time setting the first double for the time interval of the condition
         */
        timeFramesSelectionDialog.getFirstTimeFrameComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = dataAnalysisController.getCurrentCondition();
                if (currentCondition != null) {
                    // results holder for currently selected condition
                    AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(dataAnalysisController.getCurrentCondition());
                    TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                    // get first time frame selected
                    Double selected = (Double) timeFramesSelectionDialog.getFirstTimeFrameComboBox().getSelectedItem();
                    if (selected != null) {
                        int selectedFirstTimeFrame = timeFramesBindingList.indexOf(selected);
                        if (selectedFirstTimeFrame <= timeInterval.getLastTimeFrame()) {
                            timeInterval.setFirstTimeFrame(selectedFirstTimeFrame);
                            // update plot
                            plotCorrectedDataInTimeInterval(dataAnalysisController.getCurrentCondition());
                        } else {
                            // first time frame can not be greater than last one: warn the user and ignore selection
                            dataAnalysisController.showMessage("First time frame cannot be greater than last one!", "Error in chosing time frames", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

            }
        });

        /**
         * If the user is not happy with new selection, reset cut off value back to preciously computed one.
         */
        timeFramesSelectionDialog.getResetCutOffButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = dataAnalysisController.getCurrentCondition();
                if (currentCondition != null) {
                    // replot everything
                    plotCorrectedDataReplicates(currentCondition);
                    // recompute time interval
                    cellCoveredAreaPreProcessor.setTimeInterval(preProcessingMap.get(currentCondition));
                    // refresh info label
                    correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(preProcessingMap.get(currentCondition).getTimeInterval().getLastTimeFrame()).toString());
                }
            }
        });

        /**
         * If the user decides to overwrite decision about replicates selection, pop up a JDialog with Distance matrix table In this table the user is able to select or deselect conditions replicates
         */
        correctedAreaPanel.getSelectReplicatesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = dataAnalysisController.getCurrentCondition();
                if (currentCondition != null) {
                    showDistanceMatrix(currentCondition);
                    // pack the dialog
                    replicatesSelectionDialog.pack();
                    // center the dialog on main frame
                    GuiUtils.centerDialogOnFrame(dataAnalysisController.getCellMissyFrame(), replicatesSelectionDialog);
                    // show the dialog
                    replicatesSelectionDialog.setVisible(true);
                }
            }
        });

        /**
         * If the user decides to overwrite cut off value- pop up a JDialog with some options Select a different cut off time frame from a combo box and update plot according to value
         */
        correctedAreaPanel.getChooseTimeFramesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = dataAnalysisController.getCurrentCondition();
                if (currentCondition != null) {
                    // (re)set to invisible the warning message
                    timeFramesSelectionDialog.getWarningLabel().setVisible(false);
                    timeFramesSelectionDialog.getDefaultCutOffTextField().setText(timeFramesBindingList.get(preProcessingMap.get(currentCondition).getTimeInterval().getProposedCutOff()).toString());
                    // pack the dialog
                    timeFramesSelectionDialog.pack();
                    // center dialog on the main frame
                    GuiUtils.centerDialogOnFrame(dataAnalysisController.getCellMissyFrame(), replicatesSelectionDialog);
                    // show the dialog
                    timeFramesSelectionDialog.setVisible(true);
                }
            }
        });
    }
}
