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
import static be.ugent.maf.cellmissy.analysis.MeasuredAreaType.CELL_COVERED_AREA;
import static be.ugent.maf.cellmissy.analysis.MeasuredAreaType.OPEN_AREA;
import be.ugent.maf.cellmissy.analysis.impl.CellCoveredAreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.impl.OpenAreaPreProcessor;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.CorrectedAreaPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.RawAreaPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DistanceMatrixDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.TimeFramesSelectionDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.TransformedAreaPanel;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
 *
 * Controller for area pre-processing.
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
    private RawAreaPanel rawAreaPanel;
    private CorrectedAreaPanel correctedAreaPanel;
    private TransformedAreaPanel transformedAreaPanel;
    private DistanceMatrixDialog distanceMatrixDialog;
    private TimeFramesSelectionDialog timeFramesSelectionDialog;
    private ChartPanel rawAreaChartPanel;
    private ChartPanel transformedAreaChartPanel;
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    private ChartPanel correctedAreaChartPanel;
    private ChartPanel globalAreaChartPanel;
    //parent controller
    @Autowired
    private AreaController areaController;
    //child controllers
    //services
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
        initRawAreaPanel();
        initCorrectedAreaPanel();
        initTransformedAreaPanel();
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

    public RawAreaPanel getRawAreaPanel() {
        return rawAreaPanel;
    }

    public TransformedAreaPanel getTransformedAreaPanel() {
        return transformedAreaPanel;
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

    public CorrectedAreaPanel getCorrectedAreaPanel() {
        return correctedAreaPanel;
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
        for (PlateCondition plateCondition : areaController.getPlateConditionList()) {
            // each condition is not loaded at the beginning
            plateCondition.setLoaded(false);
            preProcessingMap.put(plateCondition, null);
        }
    }

    /**
     * When a condition is selected pre processing results are computed and
     * condition is put into the map together with its results holder object
     *
     * @param plateCondition
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        if (preProcessingMap.get(plateCondition) == null) {
            AreaPreProcessingResults areaPreProcessingResults = new AreaPreProcessingResults();
            // set processed time frames for current condition
            areaPreProcessingResults.setProcessedTimeFrames(getProcessedTimeFrames(plateCondition));
            // based on area raw data, do computations for pre-processig step
            areaPreProcessingResults.setAreaRawData(getAreaRawData(plateCondition));
            // normalization depends on type of measured area
            MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
            // call the pre-processors according to measured area type
            // check for the outliers algorithm to apply - we get it from the parent controller
            String outliersHandlerBeanName = areaController.getOutliersHandlerBeanName();
            // check for the distance metric to use
            String distanceMetricBeanName = areaController.getDistanceMetricBeanName();
            switch (measuredAreaType) {
                case CELL_COVERED_AREA:
                    // normalize area
                    cellCoveredAreaPreProcessor.computeNormalizedArea(areaPreProcessingResults);
                    // delta area
                    cellCoveredAreaPreProcessor.computeDeltaArea(areaPreProcessingResults);
                    // % area increase
                    cellCoveredAreaPreProcessor.computeAreaIncrease(areaPreProcessingResults);
                    // correct and normalize area
                    cellCoveredAreaPreProcessor.normalizeCorrectedArea(areaPreProcessingResults, outliersHandlerBeanName);
                    // compute distance matrix
                    cellCoveredAreaPreProcessor.computeDistanceMatrix(areaPreProcessingResults, distanceMetricBeanName);
                    // exclude replicates
                    cellCoveredAreaPreProcessor.excludeReplicates(areaPreProcessingResults, plateCondition, outliersHandlerBeanName);
                    // set time interval for analysis
                    cellCoveredAreaPreProcessor.setTimeInterval(areaPreProcessingResults);
                    break;
                case OPEN_AREA:
                    areaController.getAreaAnalysisHolder().setAreaUnitOfMeasurement(AreaUnitOfMeasurement.PERCENTAGE);
                    // normalize area
                    openAreaPreProcessor.computeNormalizedArea(areaPreProcessingResults);
                    // transform data to cell covered area
                    openAreaPreProcessor.transformAreaData(areaPreProcessingResults);
                    // delta area
                    openAreaPreProcessor.computeDeltaArea(areaPreProcessingResults);
                    // % area increase
                    openAreaPreProcessor.computeAreaIncrease(areaPreProcessingResults);
                    // correct and normalize area
                    openAreaPreProcessor.normalizeCorrectedArea(areaPreProcessingResults, outliersHandlerBeanName);
                    // compute distance matrix
                    openAreaPreProcessor.computeDistanceMatrix(areaPreProcessingResults, distanceMetricBeanName);
                    // exclude replicates
                    openAreaPreProcessor.excludeReplicates(areaPreProcessingResults, plateCondition, outliersHandlerBeanName);
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
        rawAreaChartPanel.setChart(null);
        transformedAreaChartPanel.setChart(null);
        densityChartPanel.setChart(null);
        correctedDensityChartPanel.setChart(null);
        correctedAreaChartPanel.setChart(null);
        globalAreaChartPanel.setChart(null);
    }

    /**
     * Called from parent controller if a cancel is called while analysis is
     * performed
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
        areaAnalysisPanel.getGraphicsParentPanel().remove(rawAreaPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
        areaAnalysisPanel.getGraphicsParentPanel().revalidate();
        areaAnalysisPanel.getGraphicsParentPanel().repaint();
    }

    /**
     * show table with TimeSteps results from CellMIA analysis (timeSteps
     * fetched from DB) this is populating the JTable in the ResultsImporter
     * Panel
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
        columnBinding.setRenderer(new FormatRenderer(areaController.getFormat()));

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * for each replicate (well) of a certain selected condition, show delta
     * area values, close to time frames
     *
     * @param plateCondition
     */
    public void showDeltaAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, deltaArea, processedTimeFrames));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(areaController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase
     * in Area (in %), close to time frames
     *
     * @param plateCondition
     */
    public void showAreaIncreaseInTable(PlateCondition plateCondition) {
        String outliersHandlerBeanName = areaController.getOutliersHandlerBeanName();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] percentageAreaIncrease = preProcessingMap.get(plateCondition).getPercentageAreaIncrease();
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, percentageAreaIncrease, processedTimeFrames));
            //format first column
            dataTable.getColumnModel().getColumn(0).setCellRenderer(new FormatRenderer(areaController.getFormat()));
            boolean[][] outliers = cellCoveredAreaPreProcessor.detectOutliers(percentageAreaIncrease, outliersHandlerBeanName);
            //show outliers in red from second column on
            OutliersRenderer outliersRenderer = new OutliersRenderer(outliers, areaController.getFormat());
            for (int i = 1; i < dataTable.getColumnCount(); i++) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(outliersRenderer);
            }
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        areaAnalysisPanel.getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show
     * normalised area values, close to time frames
     *
     * @param plateCondition
     */
    public void showNormalizedAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedArea, processedTimeFrames));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(areaController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Area is normalized in terms of starting location at time zero");
    }

    /**
     * Show how many time frames were actually processed
     *
     * @param plateCondition
     */
    public void showProcessedTimeFrames(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        int processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames().length;
        rawAreaPanel.getProcessedTimeFramesTextField().setText("" + processedTimeFrames);
    }

    /**
     * Show information on time interval
     *
     * @param plateCondition
     */
    public void showTimeIntervalInfo(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
        correctedAreaPanel.getLastTimeFrameTextField().setText("" + timeFramesBindingList.get(lastTimeFrame));
        int firstTimeFrame = areaPreProcessingResults.getTimeInterval().getFirstTimeFrame();
        correctedAreaPanel.getFirstTimeFrameTextField().setText("" + timeFramesBindingList.get(firstTimeFrame));
    }

    /**
     * for each replicate (well) of a certain selected condition, show
     * transformed data from open area to cell covered area
     *
     * @param plateCondition
     */
    public void showTransformedDataInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] transformedData = areaPreProcessingResults.getTransformedData();
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, transformedData, processedTimeFrames));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(areaController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Open area is converted to (complementary) cell covered area");
    }

    /**
     * for each replicate (well) of a certain selected condition, show
     * normalised corrected (for outliers) area values, close to time frames
     *
     * @param plateCondition
     */
    public void showCorrectedAreaInTable(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedCorrectedArea, processedTimeFrames));
            dataTable.setDefaultRenderer(Object.class, new FormatRenderer(areaController.getFormat()));
            dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
        }
        areaAnalysisPanel.getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
    }

    /**
     *
     * @param plateCondition
     * @param plotLines
     * @param plotPoints
     */
    public void plotRawAreaReplicates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints) {
        String measuredAreaTypeString = getMeasuredAreaTypeString();
        int conditionIndex = areaController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            // get raw data, not corrected yet but only normalized
            Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
            // Transpose Normalized Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedArea);
            List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = processedTimeFrames;
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
            JFreeChart rawAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(rawAreaChart, processedWells, plotLines, plotPoints);
            rawAreaChartPanel.setChart(rawAreaChart);
            rawAreaPanel.getReplicatesAreaChartParentPanel().add(rawAreaChartPanel, gridBagConstraints);
            areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
            areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
            areaAnalysisPanel.getGraphicsParentPanel().revalidate();
            areaAnalysisPanel.getGraphicsParentPanel().repaint();
            areaAnalysisPanel.getGraphicsParentPanel().add(rawAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Plot corrected data replicates
     *
     * @param showTimeInterval - use time interval chosen for current condition
     * or use the entire experiment time frames range?
     */
    public void plotCorrectedArea(PlateCondition plateCondition, boolean plotLines, boolean plotPoints, boolean showTimeInterval) {
        if (showTimeInterval) {
            plotCorrectedAreaInTimeInterval(plateCondition, plotLines, plotPoints);
        } else {
            plotCorrectedAreaReplicates(plateCondition, plotLines, plotPoints);
        }
    }

    /**
     * Plot cell covered area form open area for a certain selected condition
     *
     * @param plateCondition
     */
    public void plotTransformedAreaReplicates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints) {
        int conditionIndex = areaController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
            // get raw data, not corrected yet but only normalized
            Double[][] transformedData = areaPreProcessingResults.getTransformedData();
            // Transpose Normalized Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(transformedData);
            List<Well> processedWells = plateCondition.getAreaAnalyzedWells();

            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = processedTimeFrames;
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
            String chartTitle = "Cell-covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart transformedAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(transformedAreaChart, processedWells, plotLines, plotPoints);
            transformedAreaChartPanel.setChart(transformedAreaChart);
            transformedAreaPanel.getReplicatesAreaChartParentPanel().add(transformedAreaChartPanel, gridBagConstraints);
            areaAnalysisPanel.getGraphicsParentPanel().add(transformedAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Show Area Replicates for a certain selected condition
     *
     * @param plateCondition
     */
    private void plotCorrectedAreaReplicates(PlateCondition plateCondition, boolean plotLines, boolean plotPoints) {
        int conditionIndex = areaController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            // Transpose Normalized Corrected Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
            List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
            // check if some replicates need to be hidden from plot (this means these replicates are outliers)
            boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
            List excludedWells = new ArrayList();
            XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
            // array for x axis
            double[] xValues = areaPreProcessingResults.getProcessedTimeFrames();
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
            String chartTitle = "Cell-covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, processedWells, plotLines, plotPoints);
            correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
            correctedAreaChartPanel.setChart(correctedAreaChart);
            correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
            // time interval info
            showTimeIntervalInfo(plateCondition);
            correctedAreaPanel.getExcludedReplicatesTextArea().setText(excludedWells.toString());
            areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Plot Corrected data Area for selected condition, taking into account both
     * time selection and eventual replicate exclusion
     *
     * @param plateCondition
     */
    private void plotCorrectedAreaInTimeInterval(PlateCondition plateCondition, boolean plotLines, boolean plotPoints) {
        int conditionIndex = areaController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
            // Transpose Normalized Corrected Area
            Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
            List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
            // check if some replicates need to be hidden from plot (this means these replicates are outliers)
            boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
            List excludedWells = new ArrayList();
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
                        } else {
                            // replicates excluded
                            excludedWells.add(well);
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
                        } else {
                            // replicates excluded
                            excludedWells.add(well);
                        }
                    }
                    counter += numberOfSamplesPerWell;
                }
            }
            // Plot Logic
            String chartTitle = "Cell-covered area (wound closure) - Condition " + conditionIndex + " (replicates)";
            String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
            JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
            JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, processedWells, plotLines, plotPoints);
            correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
            correctedAreaChartPanel.setChart(correctedAreaChart);
            correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
            // time frame info
            showTimeIntervalInfo(plateCondition);
            correctedAreaPanel.getExcludedReplicatesTextArea().setText(excludedWells.toString());
            areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
        }
    }

    /**
     * Plot Density Functions for both raw and corrected area data. A Swing
     * Worker is used, and a cache to hold density functions values.
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
     *
     * @param plateConditionList
     * @param useCorrectedData
     * @param plotErrorBars
     * @param plotLines
     * @param plotPoints
     * @param measuredAreaType
     * @return
     */
    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, MeasuredAreaType measuredAreaType) {
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
                        double mad = AnalysisUtils.scaleMAD(ArrayUtils.toPrimitive(replicatesToIncludeArray));
                        yErrors[i] = mad;
                        index++;
                    }
                }
                yErrorsList.add(yErrors);
                XYSeries values = JFreeChartUtils.generateXYSeries(xValues, yValues);
                values.setKey("Condition " + (areaController.getPlateConditionList().indexOf(plateCondition) + 1));
                xySeriesCollection.addSeries(values);
            }
        }
        String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        if (plotErrorBars) {
            JFreeChartUtils.plotVerticalErrorBars(globalAreaChart, xySeriesCollection, yErrorsList);
            globalAreaChart.getXYPlot().getRangeAxis().setUpperBound(JFreeChartUtils.computeMaxY(xySeriesCollection) + AnalysisUtils.getMaxOfAList(yErrorsList));
        }
        JFreeChartUtils.setupGlobalAreaChart(globalAreaChart, plotLines, plotPoints);
        return globalAreaChart;
    }

    /**
     *
     * @param plateConditionList
     * @param useCorrectedData
     * @param plotErrorBars
     * @param plotLines
     * @param plotPoints
     * @param measuredAreaType
     * @return
     */
    public JFreeChart createGlobalAreaChartInTimeInterval(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, MeasuredAreaType measuredAreaType) {
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
                // time interval to use is experiment analysis frames
                double[] analysisTimeFrames = areaController.getAnalysisTimeFrames();
                // array for x axis: sub selection of time frames
                double[] xValues = new double[analysisTimeFrames.length];
                // array for y axis: same length of x axis
                double[] yValues = new double[xValues.length];
                Double[] yErrors = new Double[xValues.length];

                int index = timeFramesBindingList.indexOf(analysisTimeFrames[0]);
                for (int i = 0; i < xValues.length; i++) {
                    xValues[i] = timeFramesBindingList.get(index);
                    index++;
                }
                //time frames direction
                index = timeFramesBindingList.indexOf(analysisTimeFrames[0]);
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
                values.setKey("Condition " + (areaController.getPlateConditionList().indexOf(plateCondition) + 1));
                xySeriesCollection.addSeries(values);
            }
        }
        String areaUnitOfMeasurement = getAreaUnitOfMeasurementString();
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "time (min)", "Area " + "(" + areaUnitOfMeasurement + ")", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        if (plotErrorBars) {
            JFreeChartUtils.plotVerticalErrorBars(globalAreaChart, xySeriesCollection, yErrorsList);
            globalAreaChart.getXYPlot().getRangeAxis().setUpperBound(JFreeChartUtils.computeMaxY(xySeriesCollection) + AnalysisUtils.getMaxOfAList(yErrorsList));
        }
        JFreeChartUtils.setupGlobalAreaChart(globalAreaChart, plotLines, plotPoints);
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
                        areaController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setValue(progress);
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
            areaController.computeAnalysisTimeFrames();
            // set value for analysis time frames
            double[] analysisTimeFrames = areaController.getAnalysisTimeFrames();
            double first = analysisTimeFrames[0];
            double last = analysisTimeFrames[analysisTimeFrames.length - 1];
            areaAnalysisPanel.getFirstTimeFrameTextField().setText("" + first);
            areaAnalysisPanel.getLastTimeFrameTextField().setText("" + last);
            boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
            boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
            boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
            boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
            boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
            MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
            if (selectedConditions.isEmpty()) {
                plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
            } else {
                plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
            }
        }
    }

    /**
     * Called by parent controller, show linear regression model results
     */
    public void onLinearRegressionModel() {
        // show Linear Model Results from the other child controller
        areaController.showLinearModelInTable(useCorrectedData());
        // update information on time frames and corrected data in analysis
        areaController.updateAnalysisInfo();
    }

    /**
     * Use corrected data or retain the raw data?
     *
     * @return boolean
     */
    public boolean useCorrectedData() {
        return areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
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
        return areaController.getAreaUnitOfMeasurement();
    }

    private String getMeasuredAreaTypeString() {
        return areaController.getMeasuredAreaType();
    }

    /**
     * Show Table with Euclidean Distances between all replicates for a certain
     * selected condition
     *
     * @param plateCondition
     */
    private void showDistanceMatrix(PlateCondition plateCondition) {
        String outliersHandlerBeanName = areaController.getOutliersHandlerBeanName();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        if (areaPreProcessingResults != null) {
            Double[][] distanceMatrix = areaPreProcessingResults.getDistanceMatrix();
            boolean[][] outliersMatrix = cellCoveredAreaPreProcessor.detectOutliers(distanceMatrix, outliersHandlerBeanName);
            boolean[][] transposedOutliersMatrix = AnalysisUtils.transposeBooleanMatrix(outliersMatrix);
            DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(distanceMatrix, outliersMatrix, plateCondition);
            // if user already had interaction through check boxes overwrite distance matrix table behavior 
            if (areaPreProcessingResults.isUserSelectedReplicates()) {
                distanceMatrixTableModel.setCheckboxOutliers(areaPreProcessingResults.getExcludeReplicates());
            }
            JTable distanceMatrixTable = distanceMatrixDialog.getDistanceMatrixTable();
            distanceMatrixTable.setModel(distanceMatrixTableModel);
            // Renderer
            CheckBoxOutliersRenderer checkBoxOutliersRenderer = new CheckBoxOutliersRenderer(transposedOutliersMatrix, areaController.getFormat());
            // Cell Editor
            CheckBoxCellEditor checkBoxCellEditor = new CheckBoxCellEditor(distanceMatrixTableModel, plateCondition);
            // set cell editor starting from column 1 and pack all columns
            for (int i = 1; i < distanceMatrixTable.getColumnCount(); i++) {
                distanceMatrixTable.getColumnModel().getColumn(i).setCellEditor(checkBoxCellEditor);
                distanceMatrixTable.getColumnModel().getColumn(i).setCellRenderer(checkBoxOutliersRenderer);
            }
            distanceMatrixTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));
            distanceMatrixTable.getTableHeader().setReorderingAllowed(false);
            // disable row selection
            distanceMatrixTable.setRowSelectionAllowed(false);
        }
    }

    /**
     * Plot global area for a list of certain biological conditions, using or
     * not corrected data and plotting or not error bars on top
     *
     * @param plateConditionList
     * @param useCorrectedData
     * @param plotErrorBars
     */
    private void plotGlobalArea(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, boolean showTimeInterval, MeasuredAreaType measuredAreaType) {
        JFreeChart globalAreaChart;
        if (showTimeInterval) {
            globalAreaChart = createGlobalAreaChartInTimeInterval(plateConditionList, useCorrectedData, plotErrorBars, plotLines, plotPoints, measuredAreaType);
        } else {
            globalAreaChart = createGlobalAreaChart(plateConditionList, useCorrectedData, plotErrorBars, plotLines, plotPoints, measuredAreaType);
        }
        globalAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
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
        // get processed time frames
        double[] processedTimeFrames = getProcessedTimeFrames(plateCondition);
        // get number of samples 
        int numberOfSamplesPerCondition = AnalysisUtils.getNumberOfSamplesPerCondition(plateCondition);
        boolean firstAreaIsZero = false;
        Double[][] areaRawData = new Double[processedTimeFrames.length][numberOfSamplesPerCondition];
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
     * Compute time frames for current condition
     *
     * @param plateCondition
     */
    private double[] getProcessedTimeFrames(PlateCondition plateCondition) {
        // how many time steps for current condition??
        int minTimeStepsNumber = getNumberOfTimeFrames(plateCondition);
        Double experimentInterval = areaController.getExperiment().getExperimentInterval();
        double[] frames = new double[minTimeStepsNumber];
        for (int i = 0; i < frames.length; i++) {
            double timeFrame = i * experimentInterval;
            frames[i] = timeFrame;
        }
        return frames;
    }

    /**
     *
     * @param plateCondition
     * @return
     */
    private int getNumberOfTimeFrames(PlateCondition plateCondition) {
        int numberOfTimeFrames = areaController.getExperiment().getTimeFrames();
        // look into the analyzed wells
        for (Well well : plateCondition.getAreaAnalyzedWells()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                int numberOfTimeSteps = wellHasImagingType.getTimeStepList().size();
                if (numberOfTimeSteps != 0 && numberOfTimeSteps < numberOfTimeFrames) {
                    numberOfTimeFrames = numberOfTimeSteps;
                }
            }
        }
        return numberOfTimeFrames;
    }

    /**
     * Update time frames list for current condition
     */
    public void updateTimeFramesList(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        double[] processedTimeFrames = areaPreProcessingResults.getProcessedTimeFrames();
        if (!timeFramesBindingList.isEmpty()) {
            timeFramesBindingList.clear();
        }
        Double[] toObject = ArrayUtils.toObject(processedTimeFrames);
        timeFramesBindingList.addAll(Arrays.asList(toObject));
    }

    /**
     * Given a chart for the raw data density function, show it
     *
     * @param densityChart
     */
    private void plotRawDataDensityFunctions(JFreeChart densityChart) {
        densityChartPanel.setChart(densityChart);
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
     * This is the only method that makes use of the kernel density estimator
     * interface. Given a condition, this is estimating the density functions
     * for both raw and corrected data.
     *
     * @param plateCondition
     * @return a map of DataCategory (enum of type: raw data or corrected data)
     * and a list of list of double arrays: each list of array of double has two
     * components: x values and y values.
     */
    private Map<DataCategory, List<List<double[]>>> estimateDensityFunctions(PlateCondition plateCondition) {
        String outliersHandlerBeanName = areaController.getOutliersHandlerBeanName();
        String kernelDensityEstimatorBeanName = areaController.getKernelDensityEstimatorBeanName();
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>> densityFunctions = new HashMap<>();
        List<List<double[]>> rawDataDensityFunctions = new ArrayList<>();
        List<List<double[]>> correctedDataDensityFunctions = new ArrayList<>();
        // raw data
        Double[][] percentageAreaIncrease = areaPreProcessingResults.getPercentageAreaIncrease();
        Double[][] transposedRawData = AnalysisUtils.transpose2DArray(percentageAreaIncrease);
        // corrected data (after outliers detection)
        Double[][] correctedArea = cellCoveredAreaPreProcessor.correctForOutliers(percentageAreaIncrease, outliersHandlerBeanName);
        Double[][] transposedCorrectedData = AnalysisUtils.transpose2DArray(correctedArea);
        for (int i = 0; i < transposedRawData.length; i++) {
            // compute density function for each replicate of the raw data
            List<double[]> oneReplicateRawDataDensityFunction = cellCoveredAreaPreProcessor.estimateDensityFunction(transposedRawData[i], kernelDensityEstimatorBeanName);
            // compute density function for each replicate of the corrected data
            List<double[]> oneReplicateCorrectedDataDensityFunction = cellCoveredAreaPreProcessor.estimateDensityFunction(transposedCorrectedData[i], kernelDensityEstimatorBeanName);
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
     * Initialize main panel
     */
    private void initAreaAnalysisPanel() {
        // init main view and add it to parent panel
        areaAnalysisPanel = new AreaAnalysisPanel();
        // show both lines and points
        areaAnalysisPanel.getPlotLinesCheckBox().setSelected(true);
        areaAnalysisPanel.getPlotPointsCheckBox().setSelected(true);
        // time steps table can not be edit, but it can be selected through columns
        areaAnalysisPanel.getTimeStepsTable().setColumnSelectionAllowed(true);
        areaAnalysisPanel.getTimeStepsTable().setRowSelectionAllowed(false);
        areaAnalysisPanel.getTimeStepsTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer(SwingConstants.RIGHT));

        // set background to white 
        areaAnalysisPanel.getTimeStepsTableScrollPane().getViewport().setBackground(Color.white);
        //init dataTable
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        dataTable.getTableHeader().setReorderingAllowed(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        areaAnalysisPanel.getDataTablePanel().add(scrollPane);
        //init timeStepsBindingList 
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
        // set booleans
        globalPlotForFirstTime = true;
        proceedToAnalysis = false;
        //init subview
        rawAreaPanel = new RawAreaPanel();
        transformedAreaPanel = new TransformedAreaPanel();
        correctedAreaPanel = new CorrectedAreaPanel();
        //init chart panels
        rawAreaChartPanel = new ChartPanel(null);
        rawAreaChartPanel.setOpaque(false);
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
        // init other views
        distanceMatrixDialog = new DistanceMatrixDialog(areaController.getCellMissyFrame(), true);
        timeFramesSelectionDialog = new TimeFramesSelectionDialog(areaController.getCellMissyFrame(), true);
        // do nothing on close the dialog
        timeFramesSelectionDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // justify text info 
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = timeFramesSelectionDialog.getInfoTextPane().getStyledDocument();
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
                if (areaController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show normalized values in the table
                    showNormalizedAreaInTable(areaController.getCurrentCondition());
                    showProcessedTimeFrames(areaController.getCurrentCondition());
                    //set charts panel to null
                    densityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    rawAreaChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show raw data plot (replicates)
                    boolean plotLines = rawAreaPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = rawAreaPanel.getPlotPointsCheckBox().isSelected();
                    plotRawAreaReplicates(areaController.getCurrentCondition(), plotLines, plotPoints);
                }
            }
        });

        /**
         * If open area, cell covered area is computed and shown
         */
        areaAnalysisPanel.getCellCoveredAreaRadioButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (areaController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    showTransformedDataInTable(areaController.getCurrentCondition());
                    //set charts panel to null
                    densityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    rawAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().revalidate();
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show transformed data plot (replicates)
                    boolean plotLines = transformedAreaPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = transformedAreaPanel.getPlotPointsCheckBox().isSelected();
                    plotTransformedAreaReplicates(areaController.getCurrentCondition(), plotLines, plotPoints);
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
                if (areaController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show delta area values in the table            
                    showDeltaAreaInTable(areaController.getCurrentCondition());
                    // remove other panels
                    rawAreaChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    densityChartPanel.setChart(null);
                    correctedDensityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaPanel);
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
                if (areaController.getCurrentCondition() != null) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    showAreaIncreaseInTable(areaController.getCurrentCondition());
                    // remove other panels
                    rawAreaChartPanel.setChart(null);
                    transformedAreaChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    plotDensityFunctions(areaController.getCurrentCondition());
                }
            }
        });

        /**
         * show Corrected values for Area (corrected for outliers intra
         * replicate)
         */
        areaAnalysisPanel.getCorrectedAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (areaController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    // show values in table
                    showCorrectedAreaInTable(areaController.getCurrentCondition());
                    boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
                    boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
                    boolean showTimeInterval = correctedAreaPanel.getShowTimeIntervalCheckBox().isSelected();
                    // update time frames list for current condition
                    updateTimeFramesList(areaController.getCurrentCondition());
                    // remove other panels
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(transformedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().revalidate();
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // plot corrected area (all replicates for selected condition)
                    plotCorrectedArea(areaController.getCurrentCondition(), plotLines, plotPoints, showTimeInterval);
                    // enable global view and analysis
                    proceedToAnalysis = true;
                    areaController.getAnalysisExperimentPanel().getNextButton().setEnabled(proceedToAnalysis);
                }
            }
        });

        /**
         * Add Item Listener to error bars Check Box: plot area increases with
         * or without error bars on top
         */
        areaAnalysisPanel.getPlotErrorBarsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // check if conditions are selected, if not plot entire dataset
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, true, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, true, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    }
                } else {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, false, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, false, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    }
                }
            }
        });

        // show lines?
        areaAnalysisPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // check if conditions are selected, if not plot entire dataset
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, true, plotPoints, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, true, plotPoints, showTimeInterval, measuredAreaType);
                    }
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        areaAnalysisPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, false, true, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, false, true, showTimeInterval, measuredAreaType);
                    }
                }
            }
        });

        // show points?
        areaAnalysisPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // check if conditions are selected, if not plot entire dataset
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, plotLines, true, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, plotLines, true, showTimeInterval, measuredAreaType);
                    }
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotLines) {
                        areaAnalysisPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, true, false, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, true, false, showTimeInterval, measuredAreaType);
                    }
                }
            }
        });


        /**
         * Add item listener to use corrected data check box: use or not
         * corrected data for global plot and linear model?
         */
        areaAnalysisPanel.getUseCorrectedDataCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, true, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, true, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    }
                } else {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, false, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, false, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
                    }
                }
            }
        });

        /**
         * Show the effect that the cut off time frame has on the plot
         */
        areaAnalysisPanel.getShowTimeIntervalCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                List<PlateCondition> selectedConditions = getSelectedConditions();
                List<PlateCondition> processedConditions = getProcessedConditions();
                boolean useCorrectedData = areaAnalysisPanel.getUseCorrectedDataCheckBox().isSelected();
                boolean plotErrorBars = areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected();
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, true, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, true, measuredAreaType);
                    }
                } else {
                    if (selectedConditions.isEmpty()) {
                        plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, false, measuredAreaType);
                    } else {
                        plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, false, measuredAreaType);
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
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                plotGlobalArea(selectedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
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
                boolean plotLines = areaAnalysisPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = areaAnalysisPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = areaAnalysisPanel.getShowTimeIntervalCheckBox().isSelected();
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                plotGlobalArea(processedConditions, useCorrectedData, plotErrorBars, plotLines, plotPoints, showTimeInterval, measuredAreaType);
            }
        });

        // add view to parent panel
        areaController.getDataAnalysisPanel().getAreaAnalysisParentPanel().add(areaAnalysisPanel, gridBagConstraints);
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
            areaController.setCursor(Cursor.WAIT_CURSOR);
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
                int conditionIndex = areaController.getPlateConditionList().indexOf(plateCondition) + 1;
                JFreeChart rawDensityChart = JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, rawDataXYSeriesCollection, "KDE raw data");
                plotRawDataDensityFunctions(rawDensityChart);
                JFreeChart correctedDensityChart = JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, correctedDataXYSeriesCollection, "KDE corrected data");
                plotCorrectedDataDensityFunctions(correctedDensityChart);
                areaController.setCursor(Cursor.DEFAULT_CURSOR);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                areaController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Get the number of conditions that have already been analyzed. The user
     * has clicked on them and pre-process results were already computed
     *
     * @return
     */
    private int getNumberOfFetchedCondition() {
        int progress = 0;
        for (PlateCondition plateCondition : areaController.getPlateConditionList()) {
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
            PlateCondition selectedCondition = areaController.getPlateConditionList().get(selectedIndex);
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
     * I am keeping this Editor in this controller since it has to update area
     * image
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
            boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
            boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
            boolean showTimeInterval = correctedAreaPanel.getShowTimeIntervalCheckBox().isSelected();
            // keep note of the fact that the user had interaction with check boxes
            preProcessingMap.get(plateCondition).setUserSelectedReplicates(true);
            // recompute time interval 
            cellCoveredAreaPreProcessor.recomputeTimeInterval(areaPreProcessingResults);
            plotCorrectedArea(plateCondition, plotLines, plotPoints, showTimeInterval);
            // time interval info
            showTimeIntervalInfo(plateCondition);
        }
    }

    /**
     * Swing Worker for Global Area Plot: we check how many conditions were
     * already fetched, and we update the map of bulk cell analysis controller
     * in background, all the computations needed for the global area view plot
     * are performed.
     */
    private class FetchAllConditionsSwingWorker extends SwingWorker<Void, Void> {

        List<PlateCondition> plateConditionList = areaController.getPlateConditionList();

        @Override
        protected Void doInBackground() throws Exception {
            // disable buttons
            areaController.onButtonsState(false);
            int progress = getNumberOfFetchedCondition();
            // initialize progress property.
            setProgress(progress);
            // show progress bar
            areaController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setVisible(true);
            // set max value of progress bar to size of conditions' list
            areaController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setMaximum(plateConditionList.size());
            // show waiting cursor
            areaController.setCursor(Cursor.WAIT_CURSOR);
            for (PlateCondition plateCondition : plateConditionList) {
                // if for current condition computations were not performed yet
                if (preProcessingMap.get(plateCondition) == null) {
                    // fetch current condition
                    areaController.fetchConditionTimeSteps(plateCondition);
                    if (!timeStepsBindingList.isEmpty()) {
                        // update map (this is actually doing all the computations)
                        updateMapWithCondition(plateCondition);
                    }
                    // the condition is loaded, and plate view is refreshed
                    areaController.showNotImagedWells(plateCondition);
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
                areaController.onButtonsState(true);
                globalPlotForFirstTime = false;
                List<PlateCondition> processedConditions = getProcessedConditions();
                // when the thread is done, hide progress bar again
                areaController.getAnalysisExperimentPanel().getFetchAllConditionsProgressBar().setVisible(false);
                areaController.setCursor(Cursor.DEFAULT_CURSOR);
                // show all conditions in one plot (Global Area View)
                MeasuredAreaType measuredAreaType = areaController.getAreaAnalysisHolder().getMeasuredAreaType();
                // corrected data, no sem, lines AND points, NO time interval: default plot
                plotGlobalArea(processedConditions, true, false, true, true, false, measuredAreaType);
                areaController.computeAnalysisTimeFrames();
                // set value for analysis time frames
                double[] analysisTimeFrames = areaController.getAnalysisTimeFrames();
                double first = analysisTimeFrames[0];
                double last = analysisTimeFrames[analysisTimeFrames.length - 1];
                areaAnalysisPanel.getFirstTimeFrameTextField().setText("" + first);
                areaAnalysisPanel.getLastTimeFrameTextField().setText("" + last);
                // enable check box to show error bars
                areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
                ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(processedConditions);
                JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, areaAnalysisPanel.getConditionsList());
                bindingGroup.addBinding(jListBinding);
                bindingGroup.bind();
                areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(processedConditions, getNumberOfReplicates()));
                if (processedConditions.size() != plateConditionList.size()) {
                    // inform the user that not all conditions were imaged
                    areaController.showMessage("Note that not every condition was imaged!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                areaController.handleUnexpectedError(ex);
            }
        }
    }

    /**
     * Initialize raw area panel
     */
    private void initRawAreaPanel() {
        // show both lines and points
        rawAreaPanel.getPlotLinesCheckBox().setSelected(true);
        rawAreaPanel.getPlotPointsCheckBox().setSelected(true);

        // plot lines or not?
        rawAreaPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotPoints = rawAreaPanel.getPlotPointsCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotRawAreaReplicates(areaController.getCurrentCondition(), true, plotPoints);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        rawAreaPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    plotRawAreaReplicates(areaController.getCurrentCondition(), false, true);
                }
            }
        });

        // plot points or not?
        rawAreaPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotLines = rawAreaPanel.getPlotLinesCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotRawAreaReplicates(areaController.getCurrentCondition(), plotLines, true);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotLines) {
                        rawAreaPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    plotRawAreaReplicates(areaController.getCurrentCondition(), true, false);
                }
            }
        });
    }

    /**
     * Initialize transformed area panel
     */
    private void initTransformedAreaPanel() {
        // show both lines and points
        transformedAreaPanel.getPlotLinesCheckBox().setSelected(true);
        transformedAreaPanel.getPlotPointsCheckBox().setSelected(true);

        // plot lines or not?
        transformedAreaPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotPoints = transformedAreaPanel.getPlotPointsCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotTransformedAreaReplicates(areaController.getCurrentCondition(), true, plotPoints);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        transformedAreaPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    plotTransformedAreaReplicates(areaController.getCurrentCondition(), false, true);
                }
            }
        });

        // plot points or not?
        transformedAreaPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean plotLines = transformedAreaPanel.getPlotLinesCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotTransformedAreaReplicates(areaController.getCurrentCondition(), plotLines, true);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotLines) {
                        rawAreaPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    plotTransformedAreaReplicates(areaController.getCurrentCondition(), true, false);
                }
            }
        });
    }

    /**
     * Initialize corrected area panel
     */
    private void initCorrectedAreaPanel() {
        // set to true both points and lines
        correctedAreaPanel.getPlotLinesCheckBox().setSelected(true);
        correctedAreaPanel.getPlotPointsCheckBox().setSelected(true);
        correctedAreaPanel.getShowTimeIntervalCheckBox().setSelected(true);
        correctedAreaPanel.getExcludedReplicatesTextArea().setLineWrap(true);
        correctedAreaPanel.getExcludedReplicatesTextArea().setWrapStyleWord(true);
        // initialize Binding List for time frames (2 combo boxes binded)
        timeFramesBindingList = ObservableCollections.observableList(new ArrayList<Double>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionDialog.getCutOffTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionDialog.getFirstTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        /**
         * Show the effect that the time interval has on the plot
         */
        correctedAreaPanel.getShowTimeIntervalCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // update plot when cut off has to be shown
                    plotCorrectedArea(currentCondition, plotLines, plotPoints, true);
                } else {
                    // if check box is delesected show entire dataset
                    plotCorrectedArea(currentCondition, plotLines, plotPoints, false);
                }
            }
        });

        /**
         * Validate the selections on the combo boxes. If everything is OK, set
         * the new time interval and refresh the plot
         */
        timeFramesSelectionDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                // results holder for currently selected condition
                AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(currentCondition);
                TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = correctedAreaPanel.getShowTimeIntervalCheckBox().isSelected();
                // get first time frame selected
                int selectedFirstTimeFrame = timeFramesSelectionDialog.getFirstTimeFrameComboBox().getSelectedIndex();
                // get last time frame selected
                int selectedLastTimeFrame = timeFramesSelectionDialog.getCutOffTimeFrameComboBox().getSelectedIndex();
                // get proposed cut off
                int proposedCutOff = timeInterval.getProposedCutOff();
                if (selectedFirstTimeFrame != -1 && selectedLastTimeFrame != -1) {
                    if (selectedFirstTimeFrame <= selectedLastTimeFrame && selectedLastTimeFrame <= proposedCutOff) {
                        // set first and last time frames of current condition
                        timeInterval.setFirstTimeFrame(selectedFirstTimeFrame);
                        timeInterval.setLastTimeFrame(selectedLastTimeFrame);
                        // update plot
                        plotCorrectedArea(currentCondition, plotLines, plotPoints, showTimeInterval);
                        timeFramesSelectionDialog.setVisible(false);
                    } else if (selectedLastTimeFrame > proposedCutOff) {
                        // if last time frame provided by the user is greater than cut off time frame: Warn the user!
                        timeFramesSelectionDialog.getWarningLabel().setVisible(true);
                        // set the index back to the proposed cut off: selection is being ignored !!
                        timeFramesSelectionDialog.getCutOffTimeFrameComboBox().setSelectedIndex(proposedCutOff);
                    } else if (selectedLastTimeFrame < selectedFirstTimeFrame) {
                        // last time frame can not be smaller than first one: warn the user and ignore selection
                        String message = "Last time frame cannot be smaller than first one!";
                        String title = "Error in chosing time frames";
                        JOptionPane.showMessageDialog(timeFramesSelectionDialog, message, title, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        /**
         * If the user is not happy with new selection, reset cut off value back
         * to previously computed one.
         */
        timeFramesSelectionDialog.getResetTimeIntervalButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(currentCondition);
                boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
                boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
                if (currentCondition != null) {
                    // recompute time interval
                    cellCoveredAreaPreProcessor.setTimeInterval(areaPreProcessingResults);
                    // replot everything
                    plotCorrectedArea(currentCondition, plotLines, plotPoints, true);
                    TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                    int firstTimeFrame = areaPreProcessingResults.getTimeInterval().getFirstTimeFrame();
                    correctedAreaPanel.getFirstTimeFrameTextField().setText("" + timeFramesBindingList.get(firstTimeFrame));
                    int lastTimeFrame = timeInterval.getLastTimeFrame();
                    correctedAreaPanel.getLastTimeFrameTextField().setText("" + timeFramesBindingList.get(lastTimeFrame));
                    // set time frames in combo box
                    timeFramesSelectionDialog.getFirstTimeFrameComboBox().setSelectedIndex(firstTimeFrame);
                    timeFramesSelectionDialog.getCutOffTimeFrameComboBox().setSelectedIndex(lastTimeFrame);
                    timeFramesSelectionDialog.getWarningLabel().setVisible(false);
                }
            }
        });

        /**
         * If the user decides to overwrite decision about replicates selection,
         * pop up a JDialog with Distance matrix table. In this table the user
         * is able to select or deselect conditions replicates
         */
        correctedAreaPanel.getSelectReplicatesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                if (currentCondition != null) {
                    showDistanceMatrix(currentCondition);
                    // pack the dialog
                    distanceMatrixDialog.pack();
                    // center the dialog on main frame
                    GuiUtils.centerDialogOnFrame(areaController.getCellMissyFrame(), distanceMatrixDialog);
                    // show the dialog
                    distanceMatrixDialog.setVisible(true);
                }
            }
        });

        /**
         * If the user decides to overwrite cut off value- pop up a JDialog with
         * some options Select a different cut off time frame from a combo box
         * and update plot according to value
         */
        correctedAreaPanel.getChooseTimeFramesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                if (currentCondition != null) {
                    // (re)set to invisible the warning message
                    timeFramesSelectionDialog.getWarningLabel().setVisible(false);
                    TimeInterval timeInterval = preProcessingMap.get(currentCondition).getTimeInterval();
                    timeFramesSelectionDialog.getDefaultCutOffTextField().setText("" + timeFramesBindingList.get(timeInterval.getProposedCutOff()));
                    timeFramesSelectionDialog.getFirstTimeFrameComboBox().setSelectedIndex(timeInterval.getFirstTimeFrame());
                    timeFramesSelectionDialog.getCutOffTimeFrameComboBox().setSelectedIndex(timeInterval.getLastTimeFrame());
                    // pack the dialog
                    timeFramesSelectionDialog.pack();
                    // center dialog on the main frame
                    GuiUtils.centerDialogOnFrame(areaController.getCellMissyFrame(), timeFramesSelectionDialog);
                    // show the dialog
                    timeFramesSelectionDialog.setVisible(true);
                }
            }
        });

        // plot lines or not?
        correctedAreaPanel.getPlotLinesCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                // do you want to plot points or not? check for the value of the checkbox
                boolean plotPoints = correctedAreaPanel.getPlotPointsCheckBox().isSelected();
                boolean showTimeInterval = correctedAreaPanel.getShowTimeIntervalCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotCorrectedArea(currentCondition, plotPoints, plotPoints, showTimeInterval);
                } else {
                    // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                    if (!plotPoints) {
                        correctedAreaPanel.getPlotPointsCheckBox().setSelected(true);
                    }
                    plotCorrectedArea(currentCondition, false, true, showTimeInterval);
                }
            }
        });

        // plot points or not?
        correctedAreaPanel.getPlotPointsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PlateCondition currentCondition = areaController.getCurrentCondition();
                boolean plotLines = correctedAreaPanel.getPlotLinesCheckBox().isSelected();
                boolean showTimeInterval = correctedAreaPanel.getShowTimeIntervalCheckBox().isSelected();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    plotCorrectedArea(currentCondition, plotLines, true, showTimeInterval);
                } else {
                    // if the checkbox is being deselected, check for the lines checkbox, if it's deselected, select it
                    if (!plotLines) {
                        correctedAreaPanel.getPlotLinesCheckBox().setSelected(true);
                    }
                    plotCorrectedArea(currentCondition, true, false, showTimeInterval);
                }
            }
        });
    }
}
