/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache.DataCategory;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeInterval;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.view.table.models.ComputedDataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.CorrectedAreaPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DistanceMatrixPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.TimeFramesSelectionPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.CheckBoxOutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.OutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.table.models.DistanceMatrixTableModel;
import be.ugent.maf.cellmissy.gui.view.renderer.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.RectIconListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.TableHeaderRenderer;
import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    //view
    private AreaAnalysisPanel areaAnalysisPanel;
    private CorrectedAreaPanel correctedAreaPanel;
    private JDialog dialog;
    private TimeFramesSelectionPanel timeFramesSelectionPanel;
    private DistanceMatrixPanel distanceMatrixPanel;
    private ChartPanel rawDataChartPanel;
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
    private AreaPreProcessor areaPreProcessor;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init views
        initAreaAnalysisPanel();
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
            areaPreProcessor.computeNormalizedArea(areaPreProcessingResults);
            areaPreProcessor.computeDeltaArea(areaPreProcessingResults);
            areaPreProcessor.computeAreaIncrease(areaPreProcessingResults);
            areaPreProcessor.normalizeCorrectedArea(areaPreProcessingResults);
            // compute distance matrix
            areaPreProcessor.computeDistanceMatrix(areaPreProcessingResults);
            // exclude replicates
            areaPreProcessor.excludeReplicates(areaPreProcessingResults, plateCondition);
            // set time interval for analysis
            areaPreProcessor.setTimeInterval(areaPreProcessingResults);
            // fill in map
            preProcessingMap.put(plateCondition, areaPreProcessingResults);
        }
    }

    /**
     * Clear Density Function Cache This method is needed if another algorithm or another imaging type are selected
     */
    public void emptyDensityFunctionCache() {
        densityFunctionHolderCache.clearCache();
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
        columnBinding.setColumnName("Sequence");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Integer.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${area}"));
        columnBinding.setColumnName("Area " + "(\u00B5" + "m" + "\u00B2)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidX}"));
        columnBinding.setColumnName("Centroid_x (pixels)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidY}"));
        columnBinding.setColumnName("Centroid_y (pixels)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${eccentricity}"));
        columnBinding.setColumnName("Eccentricity");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${majorAxis}"));
        columnBinding.setColumnName("Major Axis " + "(\u00B5" + "m)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);
        columnBinding.setRenderer(new FormatRenderer(dataAnalysisController.getFormat()));

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${minorAxis}"));
        columnBinding.setColumnName("Minor Axis " + "(\u00B5" + "m)");
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
        Double[][] deltaArea = preProcessingMap.get(plateCondition).getDeltaArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, deltaArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        areaAnalysisPanel.getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     *
     * @param plateCondition
     */
    public void showAreaIncreaseInTable(PlateCondition plateCondition) {
        Double[][] percentageAreaIncrease = preProcessingMap.get(plateCondition).getPercentageAreaIncrease();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, percentageAreaIncrease, dataAnalysisController.getTimeFrames()));
        //format first column
        dataTable.getColumnModel().getColumn(0).setCellRenderer(new FormatRenderer(dataAnalysisController.getFormat()));
        boolean[][] outliers = areaPreProcessor.detectOutliers(percentageAreaIncrease);
        //show outliers in red from second column on
        OutliersRenderer outliersRenderer = new OutliersRenderer(outliers, dataAnalysisController.getFormat());
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setCellRenderer(outliersRenderer);
        }
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        areaAnalysisPanel.getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     *
     * @param plateCondition
     */
    public void showNormalizedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedArea = preProcessingMap.get(plateCondition).getNormalizedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        areaAnalysisPanel.getTableInfoLabel().setText("Area @time frame zero is set to 0.00");
        areaAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected (for outliers) area values, close to time frames
     *
     * @param plateCondition
     */
    public void showCorrectedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedCorrectedArea = preProcessingMap.get(plateCondition).getNormalizedCorrectedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedCorrectedArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        areaAnalysisPanel.getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
    }

    /**
     * Plot area raw data (before preprocessing data) for a certain condition
     *
     * @param plateCondition
     */
    public void plotRawDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        // get raw data, not corrected yet but only normalized
        Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
        // Transpose Normalized Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedArea);
        List<Well> imagedWells = plateCondition.getImagedWells();

        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = dataAnalysisController.getTimeFrames();
        int counter = 0;
        for (Well well : imagedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
            for (int i = counter; i < counter + numberOfSamplesPerWell; i++) {
                double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                XYSeries xySeries = JFreeChartUtils.generateXYSeries(xValues, yValues);
                xySeries.setKey("" + (well));
                xYSeriesCollection.addSeries(xySeries);
            }
            counter += numberOfSamplesPerWell;
        }
        // Plot Logic
        String chartTitle = "Raw Data Condition " + conditionIndex + " (replicates)";
        JFreeChart rawDataAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setupReplicatesAreaChart(rawDataAreaChart, xYSeriesCollection, imagedWells);
        rawDataChartPanel.setChart(rawDataAreaChart);
        areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
        areaAnalysisPanel.getGraphicsParentPanel().revalidate();
        areaAnalysisPanel.getGraphicsParentPanel().repaint();
        areaAnalysisPanel.getGraphicsParentPanel().add(rawDataChartPanel, gridBagConstraints);
    }

    /**
     * Show Area Replicates for a certain selected condition
     *
     * @param plateCondition
     */
    public void plotCorrectedDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        // Transpose Normalized Corrected Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        List<Well> imagedWells = plateCondition.getImagedWells();
        // check if some replicates need to be hidden from plot (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
        List excludedWells = new ArrayList();
        XYSeriesCollection xYSeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = dataAnalysisController.getTimeFrames();
        int counter = 0;
        for (Well well : imagedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
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
            counter += numberOfSamplesPerWell;
        }
        // Plot Logic
        String chartTitle = "Corrected Data Condition " + conditionIndex + " (replicates)";
        JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, xYSeriesCollection, imagedWells);
        correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
        correctedAreaChartPanel.setChart(correctedAreaChart);
        correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
        correctedAreaPanel.getCutOffCheckBox().setSelected(false);
        // time frame info
        int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
        correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        correctedAreaPanel.getExcludedReplicatesTextField().setText(excludedWells.toString());
        areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
    }

    /**
     * for the first and the last time this is initializing the time frames binding list with an empty list of double
     */
    public void initTimeFramesList() {
        timeFramesBindingList = ObservableCollections.observableList(new ArrayList<Double>());
    }

    /**
     * Show Table with Euclidean Distances between all replicates for a certain selected condition
     *
     * @param plateCondition
     */
    private void showDistanceMatrix(PlateCondition plateCondition) {
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        Double[][] distanceMatrix = areaPreProcessingResults.getDistanceMatrix();
        boolean[][] outliersMatrix = areaPreProcessor.detectOutliers(distanceMatrix);
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
        distanceMatrixPanel.getDistanceMatrixTableParentPanel().add(distanceMatrixScrollPane, gridBagConstraints);
    }

    /**
     * Plot Corrected data Area for selected condition, taking into account both time selection and eventual replicate exclusion
     *
     * @param plateCondition
     */
    private void plotCorrectedDataInTimeInterval(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        // Transpose Normalized Corrected Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        List<Well> imagedWells = plateCondition.getImagedWells();
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
        for (Well well : imagedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfSamplesPerWell(well);
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
        }
        // Plot Logic
        String chartTitle = "Corrected Data Condition " + conditionIndex + " (replicates)";
        JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        JFreeChartUtils.setupReplicatesAreaChart(correctedAreaChart, xySeriesCollection, imagedWells);
        correctedAreaChart.getXYPlot().getDomainAxis().setRange(new Range(timeFramesBindingList.get(0), timeFramesBindingList.get(timeFramesBindingList.size() - 1) + 50));
        correctedAreaChartPanel.setChart(correctedAreaChart);
        correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
        // time frame info
        int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
        correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        areaAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
    }

    /**
     * Plot Density Functions for both raw and corrected area data. A Swing Worker is used, and a cache to hold density functions values.
     *
     * @param plateCondition
     */
    public void plotDensityFunctions(PlateCondition plateCondition) {
        PlotDensityFunctionSwingWorker plotDensityFunctionSwingWorker = new PlotDensityFunctionSwingWorker(plateCondition);
        plotDensityFunctionSwingWorker.execute();
    }

    /**
     *
     * @param plateConditionList
     * @param plotErrorBars
     * @return
     */
    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean plotErrorBars) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<Double[]> yErrorsList = new ArrayList<>();
        for (PlateCondition plateCondition : plateConditionList) {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
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
                double[] allReplicateValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(normalizedCorrectedArea[index]));
                List<Double> replicatesToIncludeList = new ArrayList();
                for (int j = 0; j < allReplicateValues.length; j++) {
                    // check if replicate has to be excluded from dataset
                    if (!excludeReplicates[j]) {
                        replicatesToIncludeList.add(allReplicateValues[j]);
                    }
                }
                Double[] replicatesToIncludeArray = replicatesToIncludeList.toArray(new Double[replicatesToIncludeList.size()]);
                double median = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(replicatesToIncludeArray));
                yValues[i] = median;
                double mad = AnalysisUtils.computeSEM(ArrayUtils.toPrimitive(replicatesToIncludeArray));
                yErrors[i] = mad;
                index++;
            }
            yErrorsList.add(yErrors);
            XYSeries values = JFreeChartUtils.generateXYSeries(xValues, yValues);

            values.setKey("Cond " + (dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1));
            xySeriesCollection.addSeries(values);
        }
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        if (plotErrorBars) {
            JFreeChartUtils.plotVerticalErrorBars(globalAreaChart, xySeriesCollection, yErrorsList);
            globalAreaChart.getXYPlot().getRangeAxis().setUpperBound(JFreeChartUtils.computeMaxY(xySeriesCollection) + AnalysisUtils.getMaxOfAList(yErrorsList));
        }
        JFreeChartUtils.setupGlobalAreaChart(globalAreaChart, xySeriesCollection);
        return globalAreaChart;
    }

    /**
     * private methods and classes
     */
    /**
     *
     * @param plateConditionList
     * @param plotErrorBars
     */
    private void plotGlobalArea(List<PlateCondition> plateConditionList, boolean plotErrorBars) {
        JFreeChart globalAreaChart = createGlobalAreaChart(plateConditionList, plotErrorBars);
        globalAreaChartPanel.setChart(globalAreaChart);
        areaAnalysisPanel.getGlobalViewPanel().add(globalAreaChartPanel, gridBagConstraints);
        areaAnalysisPanel.getGlobalViewPanel().repaint();
    }

    /**
     * from time steps List to 2D array of Double
     *
     * @param plateCondition
     * @return 2D array with area raw data
     */
    private Double[][] getAreaRawData(PlateCondition plateCondition) {
        int numberOfSamples = AnalysisUtils.getNumberOfSamplesPerCondition(plateCondition);
        Double[][] areaRawData = new Double[dataAnalysisController.getTimeFrames().length][numberOfSamples];
        int counter = 0;
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (timeStepsBindingList.get(counter).getArea() != 0) {
                    areaRawData[rowIndex][columnIndex] = timeStepsBindingList.get(counter).getArea();
                } else {
                    areaRawData[rowIndex][columnIndex] = null;
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
        Double[][] correctedArea = areaPreProcessor.correctForOutliers(percentageAreaIncrease);
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

        // time steps table is not focusable, nor the user can select rows
        areaAnalysisPanel.getTimeStepsTable().setFocusable(false);
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

        //init subview
        correctedAreaPanel = new CorrectedAreaPanel();
        //init chart panels
        rawDataChartPanel = new ChartPanel(null);
        rawDataChartPanel.setOpaque(false);
        densityChartPanel = new ChartPanel(null);
        densityChartPanel.setOpaque(false);
        correctedDensityChartPanel = new ChartPanel(null);
        correctedDensityChartPanel.setOpaque(false);
        correctedAreaChartPanel = new ChartPanel(null);
        correctedAreaChartPanel.setOpaque(false);
        globalAreaChartPanel = new ChartPanel(null);
        globalAreaChartPanel.setOpaque(false);

        distanceMatrixScrollPane = new JScrollPane();
        dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.getContentPane().setBackground(Color.white);
        dialog.getContentPane().setLayout(new GridBagLayout());
        //center the dialog on the main screen
        dialog.setLocationRelativeTo(null);
        distanceMatrixPanel = new DistanceMatrixPanel();
        timeFramesSelectionPanel = new TimeFramesSelectionPanel();
        timeFramesSelectionPanel.getDefaultCutOffTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // justify text info 
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = distanceMatrixPanel.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        styledDocument = timeFramesSelectionPanel.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        styledDocument = areaAnalysisPanel.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);

        // hide warning message
        timeFramesSelectionPanel.getWarningLabel().setVisible(false);
        preProcessingMap = new LinkedHashMap<>();
        areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(false);

        // add action listeners

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup buttonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        buttonGroup.add(areaAnalysisPanel.getNormalizeAreaButton());
        buttonGroup.add(areaAnalysisPanel.getDeltaAreaButton());
        buttonGroup.add(areaAnalysisPanel.getPercentageAreaIncreaseButton());
        buttonGroup.add(areaAnalysisPanel.getCorrectedAreaButton());
        //select as default first button (Delta Area values Computation)
        areaAnalysisPanel.getNormalizeAreaButton().setSelected(true);
        // global view can not be shown before the correction has been applied
        areaAnalysisPanel.getBulkTabbedPane().setEnabledAt(2, false);
        // linear model results can not been shown before going to step: global view  plot
        areaAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, false);

        /**
         * Calculate Normalized Area (with corrected values for Jumps)
         */
        areaAnalysisPanel.getNormalizeAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show normalized values in the table
                    showNormalizedAreaInTable(dataAnalysisController.getSelectedCondition());
                    //set charts panel to null
                    densityChartPanel.setChart(null);
                    correctedAreaChartPanel.setChart(null);
                    rawDataChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show raw data plot (replicates)
                    plotRawDataReplicates(dataAnalysisController.getSelectedCondition());
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
                    showDeltaAreaInTable(dataAnalysisController.getSelectedCondition());
                    // remove other panels
                    rawDataChartPanel.setChart(null);
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
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    showAreaIncreaseInTable(dataAnalysisController.getSelectedCondition());
                    // remove other panels
                    rawDataChartPanel.setChart(null);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    plotDensityFunctions(dataAnalysisController.getSelectedCondition());
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
                    showCorrectedAreaInTable(dataAnalysisController.getSelectedCondition());
                    // remove other panels
                    areaAnalysisPanel.getGraphicsParentPanel().remove(rawDataChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaChartPanel);
                    areaAnalysisPanel.getGraphicsParentPanel().revalidate();
                    areaAnalysisPanel.getGraphicsParentPanel().repaint();
                    // this method can be called only one time, when timeframes binding list is still empty
                    if (timeFramesBindingList.isEmpty()) {
                        initCorrectedAreaPanel();
                    }
                    // plot corrected area (all replicates for selected condition)
                    plotCorrectedDataReplicates(dataAnalysisController.getSelectedCondition());
                    // enable global view tab
                    areaAnalysisPanel.getBulkTabbedPane().setEnabledAt(2, true);
                }
            }
        });

        /**
         * Add a Change Listener to Bulk Tabbed Pane: Actions are triggered when a tab is being clicked
         */
        areaAnalysisPanel.getBulkTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //click on Global View Panel and show Global Area increase among ALL conditions.
                //If some conditions still need to be analyzed, a swing worker is needed.
                if (areaAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 2) {
                    // check if a swing worker with a progress bar is actually needed:
                    // only if the number of fetched conditions is not equal to the number of all conditions of experiment
                    if (getNumberOfFetchedCondition() != dataAnalysisController.getPlateConditionList().size()) {
                        // create and execute a swing worker
                        FetchAllConditionsSwingWorker fetchAllConditionsSwingWorker = new FetchAllConditionsSwingWorker();
                        fetchAllConditionsSwingWorker.execute();
                    } else {
                        // enable now tab for analysis
                        areaAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, true);
                        // enable check box to show error bars
                        areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
                        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(dataAnalysisController.getPlateConditionList());
                        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, areaAnalysisPanel.getConditionsList());
                        bindingGroup.addBinding(jListBinding);
                        bindingGroup.bind();

                        if (areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected()) {
                            areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(dataAnalysisController.getPlateConditionList(), getNumberOfReplicates()));
                            if (getSelectedConditions().isEmpty()) {
                                plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), true);
                            } else {
                                plotGlobalArea(getSelectedConditions(), true);
                            }
                        } else {
                            areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(dataAnalysisController.getPlateConditionList(), getNumberOfReplicates()));
                            areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
                            if (getSelectedConditions().isEmpty()) {
                                plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), false);
                            } else {
                                plotGlobalArea(getSelectedConditions(), false);
                            }
                        }
                    }
                }

                // click on "Analysis" tab, show Linear Model Results
                if (areaAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 3) {
                    // show Linear Model Results from the other child controller
                    dataAnalysisController.showLinearModelInTable();
                }
            }
        });

        /**
         * Add Item Listener to error bars Check Box: plot area increases with or without error bars on top
         */
        areaAnalysisPanel.getPlotErrorBarsCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // check if conditions are selected, if not plot entire dataset
                    if (getSelectedConditions().isEmpty()) {
                        plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), true);
                    } else {
                        plotGlobalArea(getSelectedConditions(), true);
                    }
                } else {
                    if (getSelectedConditions().isEmpty()) {
                        plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), false);
                    } else {
                        plotGlobalArea(getSelectedConditions(), false);
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
                // make a distinction if error bars needed to be plot or not
                if (areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected()) {
                    plotGlobalArea(getSelectedConditions(), true);
                } else {
                    plotGlobalArea(getSelectedConditions(), false);
                }
            }
        });

        /**
         * Clear selection from the list and plot all conditions together
         */
        areaAnalysisPanel.getPlotAllConditionsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // clear selection from list
                areaAnalysisPanel.getConditionsList().clearSelection();
                // plot global area for all conditions, checking if error bars need to be shown or not
                if (areaAnalysisPanel.getPlotErrorBarsCheckBox().isSelected()) {
                    plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), true);
                } else {
                    plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), false);
                }
            }
        });

        // add view to parent panel
        dataAnalysisController.getDataAnalysisPanel().getBulkCellAnalysisParentPanel().add(areaAnalysisPanel, gridBagConstraints);
    }

    /**
     * Swing Worker for Density Function(s) Plot
     */
    private class PlotDensityFunctionSwingWorker extends SwingWorker<Void, Void> {

        private PlateCondition plateCondition;
        // xySeriesCollections needed for raw data and for corrected data.
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
            // once xySeriesCollections are generated, generate also Charts and show results
            int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
            plotRawDataDensityFunctions(JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, rawDataXYSeriesCollection, "KDE raw data"));
            plotCorrectedDataDensityFunctions(JFreeChartUtils.generateDensityFunctionChart(plateCondition, conditionIndex, correctedDataXYSeriesCollection, "KDE corrected data"));
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
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
        for (PlateCondition plateCondition : preProcessingMap.keySet()) {
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
            areaPreProcessor.setTimeInterval(areaPreProcessingResults);
            int lastTimeFrame = areaPreProcessingResults.getTimeInterval().getLastTimeFrame();
            correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        }
    }

    /**
     * Swing Worker for Global Area Plot: we check how many conditions were already fetched, and we update the map of bulk cell analysis controller in background, all the computations needed for the
     * global area view plot are performed.
     */
    private class FetchAllConditionsSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show progress bar
            dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setVisible(true);
            // set max value of progress bar to size of conditions' list
            dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setMaximum(dataAnalysisController.getPlateConditionList().size());
            // add property change listener to progress bar
            dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setValue(progress);
                    }
                }
            });
            // show waiting cursor
            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
                // if for current condition computations were not performed yet
                if (preProcessingMap.get(plateCondition) == null) {
                    // update status of progress bar with the current number of fetched conditions
                    dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setValue(getNumberOfFetchedCondition());
                    dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setString("Condition " + getNumberOfFetchedCondition() + "/" + dataAnalysisController.getPlateConditionList().size());
                    // fetch current condition
                    dataAnalysisController.fetchConditionTimeSteps(plateCondition);
                    // ipdate map (this is actually doing all the computations)
                    updateMapWithCondition(plateCondition);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // when the thread is done, hide progress bar again
                dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setVisible(false);
                dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
                // show all conditions in one plot (Global Area View)
                plotGlobalArea(new ArrayList<>(preProcessingMap.keySet()), false);
                // enable now tab for analysis
                areaAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, true);
                // enable check box to show error bars
                areaAnalysisPanel.getPlotErrorBarsCheckBox().setEnabled(true);
                ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(dataAnalysisController.getPlateConditionList());
                JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, areaAnalysisPanel.getConditionsList());
                bindingGroup.addBinding(jListBinding);
                bindingGroup.bind();
                areaAnalysisPanel.getConditionsList().setCellRenderer(new RectIconListRenderer(dataAnalysisController.getPlateConditionList(), getNumberOfReplicates()));
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                dataAnalysisController.showMessage("An expected error occured: " + ex.getMessage() + ", please try to restart the application.", JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOG.info("Data fetching/computation was cancelled.");
            }
        }
    }

    /**
     * Initialize Matrix Panel
     */
    private void initCorrectedAreaPanel() {
        // set Border to empty one for text fields
        correctedAreaPanel.getCutOffTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        correctedAreaPanel.getExcludedReplicatesTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // initialize Binding List for time frames (2 combo boxes binded)
        timeFramesBindingList = ObservableCollections.observableList(Arrays.asList(ArrayUtils.toObject(dataAnalysisController.getTimeFrames())));
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionPanel.getCutOffTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, timeFramesSelectionPanel.getFirstTimeFrameComboBox());
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
                    plotCorrectedDataInTimeInterval(dataAnalysisController.getSelectedCondition());
                } else {
                    // if check box is delesected show entire dataset
                    plotCorrectedDataReplicates(dataAnalysisController.getSelectedCondition());
                }
            }
        });

        /**
         * If the user decides to modify the current cut off value I need to check if the chosen value is greater or lesser than the computed cut off.
         */
        timeFramesSelectionPanel.getCutOffTimeFrameComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // (re)set to invisible the warning message
                timeFramesSelectionPanel.getWarningLabel().setVisible(false);
                // results holder for currently selected condition
                AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(dataAnalysisController.getSelectedCondition());
                TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                // get last time frame selected
                int selectedLastTimeFrame = timeFramesBindingList.indexOf(timeFramesSelectionPanel.getCutOffTimeFrameComboBox().getSelectedItem());

                // if last time frame provided by the user is equal or smaller than cut off time frame (and greater than first time frame): No problem
                if (selectedLastTimeFrame <= timeInterval.getProposedCutOff() && selectedLastTimeFrame > timeInterval.getFirstTimeFrame()) {
                    timeInterval.setLastTimeFrame(selectedLastTimeFrame);
                    // update plot
                    plotCorrectedDataInTimeInterval(dataAnalysisController.getSelectedCondition());
                } else if (selectedLastTimeFrame > timeInterval.getProposedCutOff()) {
                    // if last time frame provided by the user is greater than cut off time frame: Warn the user!
                    timeFramesSelectionPanel.getWarningLabel().setVisible(true);
                } else if (selectedLastTimeFrame < timeInterval.getFirstTimeFrame()) {
                    // last time frame can not be smaller than first one: warn the user and ignore selection
                    dataAnalysisController.showMessage("Last time frame cannot be smaller than first one!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        /**
         * If the user decides to start analysis from a value different from zero This is updating the plot and at the same time setting the first double for the time interval of the condition
         */
        timeFramesSelectionPanel.getFirstTimeFrameComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // results holder for currently selected condition
                AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(dataAnalysisController.getSelectedCondition());
                TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
                // get first time frame selected
                int selectedFirstTimeFrame = timeFramesBindingList.indexOf(timeFramesSelectionPanel.getFirstTimeFrameComboBox().getSelectedItem());
                if (selectedFirstTimeFrame <= timeInterval.getLastTimeFrame()) {
                    timeInterval.setFirstTimeFrame(selectedFirstTimeFrame);
                    // update plot
                    plotCorrectedDataInTimeInterval(dataAnalysisController.getSelectedCondition());
                } else {
                    // first time frame can not be greater than last one: warn the user and ignore selection
                }
            }
        });

        /**
         * If the user is not happy with new selection, reset cut off value back to preciously computed one.
         */
        timeFramesSelectionPanel.getResetCutOffButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition selectedCondition = dataAnalysisController.getSelectedCondition();
                // replot everything
                plotCorrectedDataReplicates(selectedCondition);
                // recompute time interval
                areaPreProcessor.setTimeInterval(preProcessingMap.get(selectedCondition));
                // refresh info label
                correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(preProcessingMap.get(selectedCondition).getTimeInterval().getLastTimeFrame()).toString());
            }
        });

        /**
         * If the user decides to overwrite decision about replicates selection, pop up a JDialog with Distance matrix table In this table the user is able to select or deselect conditions replicates
         */
        correctedAreaPanel.getSelectReplicatesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // remove previous panel
                dialog.getContentPane().remove(timeFramesSelectionPanel);
                // set title
                dialog.setTitle("Exclude Replicates");
                // add distance matrix on top of panel
                showDistanceMatrix(dataAnalysisController.getSelectedCondition());
                // add new panel 
                dialog.getContentPane().add(distanceMatrixPanel, gridBagConstraints);
                // show the dialog
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        /**
         * If the user decides to overwrite cut off value- pop up a JDialog with some options Select a different cut off time frame from a combo box and update plot according to value
         */
        correctedAreaPanel.getChooseTimeFramesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition selectedCondition = dataAnalysisController.getSelectedCondition();
                // (re)set to invisible the warning message
                timeFramesSelectionPanel.getWarningLabel().setVisible(false);

                timeFramesSelectionPanel.getDefaultCutOffTextField().setText(timeFramesBindingList.get(preProcessingMap.get(selectedCondition).getTimeInterval().getProposedCutOff()).toString());
                // remove previous panel
                dialog.getContentPane().remove(distanceMatrixPanel);
                // set title
                dialog.setTitle("Select time frames");
                // add new panel
                dialog.getContentPane().add(timeFramesSelectionPanel, gridBagConstraints);
                // show the dialog
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }
}
