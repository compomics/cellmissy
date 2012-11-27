/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache.DataCategory;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeInterval;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.view.ComputedDataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.analysis.BulkCellAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.CorrectedAreaPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DistanceMatrixPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.ModifyCutOffPanel;
import be.ugent.maf.cellmissy.gui.view.AreaPlotRenderer;
import be.ugent.maf.cellmissy.gui.view.CheckBoxOutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.OutliersRenderer;
import be.ugent.maf.cellmissy.gui.view.DistanceMatrixTableModel;
import be.ugent.maf.cellmissy.gui.view.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.TableHeaderRenderer;
import be.ugent.maf.cellmissy.gui.view.VelocityBarRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
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
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingbinding.JComboBoxBinding;

/**
 * Bulk Cell Analysis Controller: Collective Cell Migration Data Analysis
 * Parent Controller: Data Analysis Controller 
 * @author Paola Masuzzo
 */
@Controller("bulkCellAnalysisController")
public class BulkCellAnalysisController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepsBindingList;
    private ObservableList<Double> timeFramesBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable dataTable;
    private Map<PlateCondition, AreaPreProcessingResultsHolder> map;
    //view
    private BulkCellAnalysisPanel bulkCellAnalysisPanel;
    private CorrectedAreaPanel correctedAreaPanel;
    private JDialog dialog;
    private ModifyCutOffPanel modifyCutOffPanel;
    private DistanceMatrixPanel distanceMatrixPanel;
    private ChartPanel rawDataChartPanel;
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    private ChartPanel correctedAreaChartPanel;
    private ChartPanel globalAreaChartPanel;
    private ChartPanel velocityChartPanel;
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
    @Autowired
    private AreaAnalyzer areaAnalyzer;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //init views
        initBulkCellAnalysisPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public ObservableList<TimeStep> getTimeStepsBindingList() {
        return timeStepsBindingList;
    }

    public BulkCellAnalysisPanel getBulkCellAnalysisPanel() {
        return bulkCellAnalysisPanel;
    }

    /**
     * public methods and classes
     */
    /**
     * Initialize map with plate conditions as keys and null objects as values
     */
    public void initMap() {
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            map.put(plateCondition, null);
        }
    }

    /**
     * When a condition is selected pre processing results are computed and condition is put into the map together with its results holder object
     * @param plateCondition 
     */
    public void updateMapWithCondition(PlateCondition plateCondition) {
        if (map.get(plateCondition) == null) {
            AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = new AreaPreProcessingResultsHolder();
            // based on area raw data, do computations for pre-processig step
            areaPreProcessingResultsHolder.setAreaRawData(getAreaRawData(plateCondition));
            areaPreProcessor.computeNormalizedArea(areaPreProcessingResultsHolder);
            areaPreProcessor.computeDeltaArea(areaPreProcessingResultsHolder);
            areaPreProcessor.computeAreaIncrease(areaPreProcessingResultsHolder);
            areaPreProcessor.normalizeCorrectedArea(areaPreProcessingResultsHolder);
            // compute distance matrix
            areaPreProcessor.computeDistanceMatrix(areaPreProcessingResultsHolder);
            // exclude replicates
            areaPreProcessor.excludeReplicates(areaPreProcessingResultsHolder, plateCondition);
            // set time interval for analysis
            areaPreProcessor.setTimeInterval(areaPreProcessingResultsHolder);
            // fill in map
            map.put(plateCondition, areaPreProcessingResultsHolder);
        }
    }

    /**
     * Clear Density Function Cache 
     * This method is needed if another algorithm or another imaging type are selected
     */
    public void emptyDensityFunctionCache() {
        densityFunctionHolderCache.clearCache();
    }

    /**
     * show table with TimeSteps results from CellMIA analysis (timeSteps fetched from DB)
     * this is populating the JTable in the ResultsImporter Panel
     */
    public void showTimeStepsInTable() {
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, bulkCellAnalysisPanel.getTimeStepsTable());
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
     * @param plateCondition 
     */
    public void showDeltaAreaInTable(PlateCondition plateCondition) {
        Double[][] deltaArea = map.get(plateCondition).getDeltaArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, deltaArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        bulkCellAnalysisPanel.getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
        bulkCellAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void showAreaIncreaseInTable(PlateCondition plateCondition) {
        Double[][] percentageAreaIncrease = map.get(plateCondition).getPercentageAreaIncrease();
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
        bulkCellAnalysisPanel.getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     * @param plateCondition 
     */
    public void showNormalizedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedArea = map.get(plateCondition).getNormalizedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        bulkCellAnalysisPanel.getTableInfoLabel().setText("Area @time frame zero is set to 0.00");
        bulkCellAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected (for outliers) area values, close to time frames
     * @param plateCondition 
     */
    public void showCorrectedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedCorrectedArea = map.get(plateCondition).getNormalizedCorrectedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedCorrectedArea, dataAnalysisController.getTimeFrames()));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(dataAnalysisController.getFormat()));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        bulkCellAnalysisPanel.getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
    }

    /**
     * Show Table with Euclidean Distances between all replicates for a certain selected condition
     * @param plateCondition 
     */
    public void showDistanceMatrix(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] distanceMatrix = areaPreProcessingResultsHolder.getDistanceMatrix();
        boolean[][] outliersMatrix = areaPreProcessor.detectOutliers(distanceMatrix);
        boolean[][] transposedOutliersMatrix = AnalysisUtils.transposeBooleanMatrix(outliersMatrix);
        DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(distanceMatrix, outliersMatrix, plateCondition);
        // if user already had interaction through check boxes overwrite distance matrix table behavior 
        if (areaPreProcessingResultsHolder.isUserHadInteraction()) {
            distanceMatrixTableModel.setCheckboxOutliers(areaPreProcessingResultsHolder.getExcludeReplicates());
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
     * Plot area raw data (before preprocessing data) for a certain condition
     * @param plateCondition 
     */
    public void plotRawDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        // get raw data, not corrected yet but only normalized
        Double[][] normalizedArea = areaPreProcessingResultsHolder.getNormalizedArea();
        // Transpose Normalized Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedArea);
        List wellList = new ArrayList(plateCondition.getWellCollection());
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = dataAnalysisController.getTimeFrames();
        for (int i = 0; i < transposedArea.length; i++) {
            double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
            XYSeries xySeries = generateXYSeries(xValues, yValues);
            xySeries.setKey("" + (wellList.get(i)));
            xySeriesCollection.addSeries(xySeries);
        }
        // Plot Logic
        String chartTitle = "Raw Data Condition " + conditionIndex + " (replicates)";
        JFreeChart rawDataAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        rawDataAreaChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        rawDataAreaChart.getLegend().setPosition(RectangleEdge.RIGHT);
        rawDataAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        rawDataAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        XYItemRenderer renderer = rawDataAreaChart.getXYPlot().getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            // plot lines with colors according to well (replicate) index
            String wellCoordinates = xySeriesCollection.getSeriesKey(i).toString();
            int wellIndex = getWellIndex(wellCoordinates, wellList);
            renderer.setSeriesStroke(i, wideLine);
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[wellIndex + 1]);
        }
        rawDataChartPanel.setChart(rawDataAreaChart);
        bulkCellAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
        bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
        bulkCellAnalysisPanel.getGraphicsParentPanel().revalidate();
        bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
        bulkCellAnalysisPanel.getGraphicsParentPanel().add(rawDataChartPanel, gridBagConstraints);
    }

    /**
     * Show Area Replicates for a certain selected condition
     * @param plateCondition 
     */
    public void plotCorrectedDataReplicates(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        // Transpose Normalized Corrected Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        List wellList = new ArrayList(plateCondition.getWellCollection());
        // check if some replicates need to be hidden from plot (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();
        List excludedWells = new ArrayList();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = dataAnalysisController.getTimeFrames();
        for (int i = 0; i < excludeReplicates.length; i++) {
            // if boolean is false, replicate has to be considered in the plot
            if (!excludeReplicates[i]) {
                // array for y axis
                double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                XYSeries xySeries = generateXYSeries(xValues, yValues);
                xySeries.setKey("" + (wellList.get(i)));
                xySeriesCollection.addSeries(xySeries);
            } else {
                // replicates excluded
                excludedWells.add(wellList.get(i));
            }
        }
        // Plot Logic
        String chartTitle = "Corrected Data Condition " + conditionIndex + " (replicates)";
        JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        correctedAreaChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        correctedAreaChart.getLegend().setPosition(RectangleEdge.RIGHT);
        correctedAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        correctedAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        XYItemRenderer renderer = correctedAreaChart.getXYPlot().getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            // plot lines with colors according to well (replicate) index
            String wellCoordinates = xySeriesCollection.getSeriesKey(i).toString();
            int wellIndex = getWellIndex(wellCoordinates, wellList);
            renderer.setSeriesStroke(i, wideLine);
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[wellIndex + 1]);
        }
        correctedAreaChartPanel.setChart(correctedAreaChart);
        correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
        correctedAreaPanel.getCutOffCheckBox().setSelected(false);
        // time frame info
        int lastTimeFrame = areaPreProcessingResultsHolder.getTimeInterval().getLastTimeFrame();
        correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        correctedAreaPanel.getExcludedReplicatesTextField().setText(excludedWells.toString());
        bulkCellAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
    }

    /**
     * for the first and the last time this is initializing the time frames binding list with an empty list of double 
     */
    public void initTimeFramesList() {
        timeFramesBindingList = ObservableCollections.observableList(new ArrayList<Double>());
    }

    /**
     * Plot Corrected data Area for selected condition, taking into account both time selection and eventual replicate exclusion
     * @param plateCondition 
     */
    private void plotCorrectedDataInTimeInterval(PlateCondition plateCondition) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        // Transpose Normalized Corrected Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        List wellList = new ArrayList(plateCondition.getWellCollection());
        // check if some replicates need to be hidden from plot (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();
        // check for time frames interval
        TimeInterval timeInterval = areaPreProcessingResultsHolder.getTimeInterval();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // array for x axis: sub selection of time frames
        double[] xValues = new double[timeInterval.getLastTimeFrame() - timeInterval.getFirstTimeFrame() + 1];
        int index = timeInterval.getFirstTimeFrame();
        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = timeFramesBindingList.get(index);
            index++;
        }
        for (int i = 0; i < excludeReplicates.length; i++) {
            index = timeInterval.getFirstTimeFrame();
            // if boolean is false, replicate has to be considered in the plot
            if (!excludeReplicates[i]) {
                // array for y axis (no need to exclude null values)
                double[] yValues = new double[xValues.length];
                for (int j = 0; j < yValues.length; j++) {
                    yValues[j] = transposedArea[i][index];
                    index++;
                }
                XYSeries xySeries = generateXYSeries(xValues, yValues);
                xySeries.setKey("" + (wellList.get(i)));
                xySeriesCollection.addSeries(xySeries);
            }
        }
        // Plot Logic
        String chartTitle = "Corrected Data Condition " + conditionIndex + " (replicates)";
        JFreeChart correctedAreaChart = ChartFactory.createXYLineChart(chartTitle, "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        correctedAreaChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        correctedAreaChart.getLegend().setPosition(RectangleEdge.RIGHT);
        correctedAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        correctedAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        XYItemRenderer renderer = correctedAreaChart.getXYPlot().getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            // plot lines with colors according to well (replicate) index
            String wellCoordinates = xySeriesCollection.getSeriesKey(i).toString();
            int wellIndex = getWellIndex(wellCoordinates, wellList);
            renderer.setSeriesStroke(i, wideLine);
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[wellIndex + 1]);
        }
        correctedAreaChartPanel.setChart(correctedAreaChart);
        correctedAreaPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
        // time frame info
        int lastTimeFrame = areaPreProcessingResultsHolder.getTimeInterval().getLastTimeFrame();
        correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        bulkCellAnalysisPanel.getGraphicsParentPanel().add(correctedAreaPanel, gridBagConstraints);
    }

    /**
     * Plot Density Functions for both raw and corrected area data.
     * A Swing Worker is used, and a cache to hold density functions values.
     * @param plateCondition 
     */
    public void plotDensityFunctions(PlateCondition plateCondition) {
        PlotDensityFunctionSwingWorker plotDensityFunctionSwingWorker = new PlotDensityFunctionSwingWorker(plateCondition);
        plotDensityFunctionSwingWorker.execute();
    }

    /**
     * private methods and classes
     */
    /**
     * Show all conditions in one plot (median is computed per time point across all replicates)
     */
    private void plotGlobalArea() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (PlateCondition plateCondition : map.keySet()) {
            double[] xValues = dataAnalysisController.getTimeFrames();
            double[] yValues = new double[dataAnalysisController.getTimeFrames().length];
            //double[] mads = new double[timeFrames.length];
            AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
            Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
            // Boolean (?Exclude Replicates from dataset)
            boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();

            //time frames direction
            for (int columnIndex = 0; columnIndex < normalizedCorrectedArea.length; columnIndex++) {
                double[] allReplicateValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(normalizedCorrectedArea[columnIndex]));
                List<Double> replicatesToIncludeList = new ArrayList();

                for (int i = 0; i < allReplicateValues.length; i++) {
                    // check if replicate has to be excluded from dataset
                    if (!excludeReplicates[i]) {
                        replicatesToIncludeList.add(allReplicateValues[i]);
                    }
                }
                Double[] replicatesArray = replicatesToIncludeList.toArray(new Double[replicatesToIncludeList.size()]);
                double median = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(replicatesArray));
                //mads[columnIndex] = AnalysisUtils.scaleMAD(replicateValues);
                yValues[columnIndex] = median;
            }
            XYSeries xySeries = generateXYSeries(xValues, yValues);
            xySeries.setKey("Cond " + (dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1));
            xySeriesCollection.addSeries(xySeries);
        }

        //@todo give an estimate on the median value (SEM?)
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area (all conditions)", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        globalAreaChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        globalAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        globalAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        AreaPlotRenderer areaPlotRenderer = new AreaPlotRenderer();
        globalAreaChart.getXYPlot().setRenderer(areaPlotRenderer);
        globalAreaChartPanel.setChart(globalAreaChart);
        bulkCellAnalysisPanel.getGlobalViewPanel().add(globalAreaChartPanel, gridBagConstraints);
        bulkCellAnalysisPanel.getGlobalViewPanel().repaint();
    }

    /**
     * show Bar charts for area velocity
     */
    private void showVelocityBars() {
        TableModel model = bulkCellAnalysisPanel.getSlopesTable().getModel();
        int[] selectedRows = bulkCellAnalysisPanel.getSlopesTable().getSelectedRows();
        int columnCount = model.getColumnCount();
        double[][] tableData = new double[columnCount - 1][];
        for (int i = 1; i < columnCount; i++) {
            List<Double> tempList = new ArrayList<>();
            for (int j : selectedRows) {
                if (model.getValueAt(j, i) != null) {
                    tempList.add((double) model.getValueAt(j, i));
                }
            }
            tableData[i - 1] = ArrayUtils.toPrimitive(tempList.toArray(new Double[tempList.size()]));
        }
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        double[] meanVelocities = tableData[6];
        double[] standardDeviations = tableData[7];
        for (int i = 0; i < meanVelocities.length; i++) {
            dataset.add(meanVelocities[i], standardDeviations[i], "Conditions", "Condition " + (selectedRows[i] + 1));
        }

        JFreeChart velocityChart = ChartFactory.createLineChart("Median Velocity", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
        velocityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        VelocityBarRenderer velocityBarRenderer = new VelocityBarRenderer();
        velocityBarRenderer.setErrorIndicatorPaint(Color.black);
        velocityBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        velocityBarRenderer.setBaseItemLabelsVisible(true);
        velocityBarRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
        velocityPlot.setRenderer(velocityBarRenderer);

        velocityPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        GuiUtils.setShadowVisible(velocityChart, false);
        velocityChartPanel.setChart(velocityChart);
        bulkCellAnalysisPanel.getVelocityChartPanel().add(velocityChartPanel, gridBagConstraints);
        bulkCellAnalysisPanel.getVelocityChartPanel().repaint();
    }

    /**
     * Populate Table with Slopes from Linear Model Estimation
     */
    private void showLinearModelInTable() {
        List<Double[]> slopes = new ArrayList<>();
        for (PlateCondition plateCondition : map.keySet()) {
            Double[] slopesPerCondition = computeSlopesPerCondition(plateCondition);
            slopes.add(slopesPerCondition);
        }
        Object[][] data = new Object[map.keySet().size()][slopes.get(0).length + 3];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 1; columnIndex < data[0].length - 2; columnIndex++) {
                data[rowIndex][columnIndex] = slopes.get(rowIndex)[columnIndex - 1];
            }
            data[rowIndex][0] = dataAnalysisController.getPlateConditionList().get(rowIndex).toString();
            data[rowIndex][data[0].length - 2] = AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopes.get(rowIndex))));
            data[rowIndex][data[0].length - 1] = AnalysisUtils.scaleMAD(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(slopes.get(rowIndex))));
        }
        String[] columnNames = new String[data[0].length];
        columnNames[0] = "Condition";
        for (int i = 1; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + i;
        }
        columnNames[columnNames.length - 2] = "Median";
        columnNames[columnNames.length - 1] = "MAD";

        bulkCellAnalysisPanel.getSlopesTable().setModel(new DefaultTableModel(data, columnNames));
        //first column needs to be bigger than others
        bulkCellAnalysisPanel.getSlopesTable().getColumnModel().getColumn(0).setMinWidth(250);
        bulkCellAnalysisPanel.getSlopesTable().getColumnModel().getColumn(0).setMaxWidth(250);
        bulkCellAnalysisPanel.getSlopesTable().getColumnModel().getColumn(0).setPreferredWidth(250);

        //set format renderer only from second column on
        for (int columnIndex = 1; columnIndex < bulkCellAnalysisPanel.getSlopesTable().getColumnCount(); columnIndex++) {
            bulkCellAnalysisPanel.getSlopesTable().getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(dataAnalysisController.getFormat()));
        }
        bulkCellAnalysisPanel.getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
    }

    /**
     * Given a certain condition estimate Linear Model and give back slopes
     * @param plateCondition
     * @return 
     */
    private Double[] computeSlopesPerCondition(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        Double[][] transposedNormalizedCorrectedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();
        return areaAnalyzer.computeSlopes(transposedNormalizedCorrectedArea, dataAnalysisController.getTimeFrames(), excludeReplicates).get(0);
    }

    /**
     * Given a certain condition estimate Linear Model and give back R2 coefficients (goodness of fitness)
     * @param plateCondition
     * @return 
     */
    //@todo R2 Coefficients to be included in view
    private Double[] computeRCoefficientsPerCondition(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        Double[][] transposedNormalizedCorrectedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();
        return areaAnalyzer.computeSlopes(transposedNormalizedCorrectedArea, dataAnalysisController.getTimeFrames(), excludeReplicates).get(1);
    }

    /**
     * from time steps List to 2D array of Double
     * @param plateCondition
     * @return 2D array with area raw data
     */
    private Double[][] getAreaRawData(PlateCondition plateCondition) {
        Double[][] areaRawData = new Double[dataAnalysisController.getTimeFrames().length][plateCondition.getWellCollection().size()];
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
     * @param densityChart 
     */
    private void plotRawDataDensityFunctions(JFreeChart densityChart) {
        densityChartPanel.setChart(densityChart);
        bulkCellAnalysisPanel.getGraphicsParentPanel().remove(distanceMatrixScrollPane);
        bulkCellAnalysisPanel.getGraphicsParentPanel().revalidate();
        bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        bulkCellAnalysisPanel.getGraphicsParentPanel().add(densityChartPanel, gridBagConstraints);
    }

    /**
     * Given a chart for the corrected data density function, show it
     * @param densityChart 
     */
    private void plotCorrectedDataDensityFunctions(JFreeChart densityChart) {
        correctedDensityChartPanel.setChart(densityChart);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        bulkCellAnalysisPanel.getGraphicsParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
    }

    /**
     * Given a xySeriesCollection and a string as a title, create a chart to show density function in a plot
     * @param plateCondition
     * @param dataCategory
     * @param chartTitle
     * @return 
     */
    private JFreeChart generateDensityChart(PlateCondition plateCondition, XYSeriesCollection xYSeriesCollection, String chartTitle) {
        int conditionIndex = dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1;
        String specificChartTitle = chartTitle + " Condition " + conditionIndex + " (replicates)";
        JFreeChart densityChart = ChartFactory.createXYLineChart(specificChartTitle, "% increase (Area)", "Density", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        densityChart.getTitle().setFont(new Font("Arial", Font.BOLD, 13));
        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xYSeriesCollection.getDomainLowerBound(true) - 0.05, xYSeriesCollection.getDomainUpperBound(true) + 0.05);
        xYPlot.getRangeAxis().setUpperBound(computeMaxY(xYSeriesCollection) + 0.05);
        xYPlot.setBackgroundPaint(Color.white);
        //renderer for wide line
        XYItemRenderer renderer = xYPlot.getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xYSeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
            renderer.setSeriesPaint(i, GuiUtils.getAvailableColors()[i + 1]);
        }
        return densityChart;
    }

    /**
     * Given a map with density functions inside, create xySeriesCollection
     * @param plateCondition
     * @param dataCategory
     * @param densityFunctionsMap
     * @return 
     */
    private XYSeriesCollection generateDensityFunction(PlateCondition plateCondition, Map<DataCategory, List<List<double[]>>> densityFunctionsMap, DataCategory dataCategory) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<Well> wellList = new ArrayList<>(plateCondition.getWellCollection());
        // get density functions only for raw data
        List<List<double[]>> densityFunctions = densityFunctionsMap.get(dataCategory);
        for (int i = 0; i < densityFunctions.size(); i++) {
            // x values
            double[] xValues = densityFunctions.get(i).get(0);
            // y values
            double[] yValues = densityFunctions.get(i).get(1);
            //XYSeries is by default ordered in ascending values, set second parameter of costructor to false
            XYSeries series = new XYSeries("" + wellList.get(i), false);
            for (int j = 0; j < xValues.length; j++) {
                double x = xValues[j];
                double y = yValues[j];
                series.add(x, y);
            }
            xySeriesCollection.addSeries(series);
        }
        return xySeriesCollection;
    }

    /**
     * This is the only method that makes use of the kernel density estimator interface.
     * Given a condition, this is estimating the density functions for both raw and corrected data.
     * @param plateCondition
     * @return a map of DataCategory (enum of type: raw data or corrected data) and a list of list of double arrays:
     * each list of array of double has two components: x values and y values.
     */
    private Map<DataCategory, List<List<double[]>>> estimateDensityFunctions(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Map<DensityFunctionHolderCache.DataCategory, List<List<double[]>>> densityFunctions = new HashMap<>();
        List<List<double[]>> rawDataDensityFunctions = new ArrayList<>();
        List<List<double[]>> correctedDataDensityFunctions = new ArrayList<>();
        // raw data
        Double[][] percentageAreaIncrease = areaPreProcessingResultsHolder.getPercentageAreaIncrease();
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
    private void initBulkCellAnalysisPanel() {
        // init main view and add it to parent panel
        bulkCellAnalysisPanel = new BulkCellAnalysisPanel();

        // time steps table is not focusable, nor the user can select rows
        bulkCellAnalysisPanel.getTimeStepsTable().setFocusable(false);
        bulkCellAnalysisPanel.getTimeStepsTable().setRowSelectionAllowed(false);
        // set background to white 
        bulkCellAnalysisPanel.getTimeStepsTableScrollPane().getViewport().setBackground(Color.white);

        //init Tables
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        bulkCellAnalysisPanel.getDataTablePanel().add(scrollPane);
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
        velocityChartPanel = new ChartPanel(null);
        velocityChartPanel.setOpaque(false);
        distanceMatrixScrollPane = new JScrollPane();
        dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.getContentPane().setBackground(Color.white);
        dialog.getContentPane().setLayout(new GridBagLayout());
        //center the dialog on the main screen
        dialog.setLocationRelativeTo(null);
        dialog.setSize(new Dimension(350, 250));
        modifyCutOffPanel = new ModifyCutOffPanel();
        distanceMatrixPanel = new DistanceMatrixPanel();

        map = new LinkedHashMap<>();

        // add action listeners

        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup buttonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        buttonGroup.add(bulkCellAnalysisPanel.getNormalizeAreaButton());
        buttonGroup.add(bulkCellAnalysisPanel.getDeltaAreaButton());
        buttonGroup.add(bulkCellAnalysisPanel.getPercentageAreaIncreaseButton());
        buttonGroup.add(bulkCellAnalysisPanel.getCorrectedAreaButton());
        //select as default first button (Delta Area values Computation)
        bulkCellAnalysisPanel.getNormalizeAreaButton().setSelected(true);
        // linear model results can not been shown before going to step: global view  plot
        bulkCellAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, false);
        // control opaque property of table
        bulkCellAnalysisPanel.getSlopesTableScrollPane().getViewport().setBackground(Color.white);

        /**
         * Calculate Normalized Area (with corrected values for Jumps)
         */
        bulkCellAnalysisPanel.getNormalizeAreaButton().addActionListener(new ActionListener() {

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
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
                    // show raw data plot (replicates)
                    plotRawDataReplicates(dataAnalysisController.getSelectedCondition());
                }
            }
        });

        /**
         * Show Delta Area Values
         */
        bulkCellAnalysisPanel.getDeltaAreaButton().addActionListener(new ActionListener() {

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
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
                }
            }
        });

        /**
         * Show %Area increase values
         */
        bulkCellAnalysisPanel.getPercentageAreaIncreaseButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    showAreaIncreaseInTable(dataAnalysisController.getSelectedCondition());
                    // remove other panels
                    rawDataChartPanel.setChart(null);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    plotDensityFunctions(dataAnalysisController.getSelectedCondition());
                }
            }
        });

        /**
         * show Corrected values for Area (corrected for outliers intra replicate)
         * show table with Euclidean distances between all replicates
         * plot area replicates according to distance matrix
         */
        bulkCellAnalysisPanel.getCorrectedAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataAnalysisController.getDataAnalysisPanel().getConditionsList().getSelectedIndex() != - 1) {
                    // show values in table
                    showCorrectedAreaInTable(dataAnalysisController.getSelectedCondition());
                    // remove other panels
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(rawDataChartPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(densityChartPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedDensityChartPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().remove(correctedAreaChartPanel);
                    bulkCellAnalysisPanel.getGraphicsParentPanel().revalidate();
                    bulkCellAnalysisPanel.getGraphicsParentPanel().repaint();
                    // this method can be called only one time, when timeframes binding list is still empty
                    if (timeFramesBindingList.isEmpty()) {
                        initCorrectedAreaPanel();
                    }
                    // show distance matrix
                    showDistanceMatrix(dataAnalysisController.getSelectedCondition());
                    // plot corrected area (all replicates for selected condition)
                    plotCorrectedDataReplicates(dataAnalysisController.getSelectedCondition());
                }
            }
        });

        /**
         * Add a Change Listener to Bulk Tabbed Pane: Actions are triggered when a tab is being clicked
         */
        bulkCellAnalysisPanel.getBulkTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                //click on Global View Panel and show Global Area increase among ALL conditions.
                //If some conditions still need to be analyzed, a swing worker is needed.
                if (bulkCellAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 2) {
                    // check if a swing worker with a progress bar is actually needed: only if the number of fetched conditions is not equal to the number of all conditions of experiment
                    if (getNumberOfFetchedCondition() != dataAnalysisController.getPlateConditionList().size()) {
                        // create and execute a swinger
                        FetchAllConditionsTimeStepsSwingWorker fetchAllConditionsSwingWorker = new FetchAllConditionsTimeStepsSwingWorker();
                        fetchAllConditionsSwingWorker.execute();
                    } else {
                        // swinger is no needed: plot Global Area
                        plotGlobalArea();
                    }
                }
                // click on "Analysis" tab, show Linear Model Results
                if (bulkCellAnalysisPanel.getBulkTabbedPane().getSelectedIndex() == 3) {
                    showLinearModelInTable();
                }
            }
        });

        /**
         * List selection Listener for linear model results Table
         * show bar charts according to user selection in model
         */
        bulkCellAnalysisPanel.getSlopesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                showVelocityBars();
            }
        });

        // add view to parent panel
        dataAnalysisController.getDataAnalysisPanel().getBulkCellAnalysisParentPanel().add(bulkCellAnalysisPanel, gridBagConstraints);
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
            plotRawDataDensityFunctions(generateDensityChart(plateCondition, rawDataXYSeriesCollection, "KDE raw data"));
            plotCorrectedDataDensityFunctions(generateDensityChart(plateCondition, correctedDataXYSeriesCollection, "KDE corrected data"));
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    /**
     * Generate Series for (x,y) Area plotting
     * @param xValues
     * @param yValues
     * @return 
     */
    private XYSeries generateXYSeries(double[] xValues, double[] yValues) {
        // autosort False
        XYSeries series = new XYSeries("", false);
        for (int i = 0; i < yValues.length; i++) {
            double x = xValues[i];
            double y = yValues[i];
            series.add(x, y);
        }
        return series;
    }

    /**
     * compute Max value of Y for density plot
     * @param xYSeriesCollection
     * @return 
     */
    private double computeMaxY(XYSeriesCollection xYSeriesCollection) {
        double maxY = 0;
        List<XYSeries> seriesList = xYSeriesCollection.getSeries();
        for (int i = 0; i < seriesList.size(); i++) {
            if (seriesList.get(i).getMaxY() > maxY) {
                maxY = seriesList.get(i).getMaxY();
            }
        }
        return maxY;
    }

    /**
     * Given a list of wells and one well's coordinate, get the index of the well in the List
     * @param wellCoordinates
     * @param wellList
     * @return 
     */
    private int getWellIndex(String wellCoordinates, List<Well> wellList) {
        int wellIndex = 0;
        for (Well well : wellList) {
            if (well.toString().equals(wellCoordinates)) {
                wellIndex = wellList.indexOf(well);
            }
        }
        return wellIndex;
    }

    /**
     * Get the number of conditions that have already been analyzed
     * The user has clicked on them and pre-process results were already computed
     * @return 
     */
    private int getNumberOfFetchedCondition() {
        int progress = 0;
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            if (map.get(plateCondition) != null) {
                progress++;
            }
        }
        return progress;
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
            updateAreaImage();
        }

        // Determine with replicates need to be shown and update Area image on the right panel
        private void updateAreaImage() {
            AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
            // Get boolean from table model and pass it to the results holder
            areaPreProcessingResultsHolder.setExcludeReplicates(distanceMatrixTableModel.getCheckboxOutliers());
            // update area image excluding selected technical replicates
            plotCorrectedDataReplicates(plateCondition);
            // keep note of the fact that the user had interaction with check boxes
            map.get(plateCondition).setUserHadInteraction(true);
            // recompute time interval for selected condition
            areaPreProcessor.setTimeInterval(areaPreProcessingResultsHolder);
            int lastTimeFrame = areaPreProcessingResultsHolder.getTimeInterval().getLastTimeFrame();
            correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(lastTimeFrame).toString());
        }
    }

    /**
     * Swing Worker for Global Area Plot: 
     * we check how many conditions were already fetched, and we update the map of bulk cell analysis controller
     * in background, all the computations needed for the global area view plot are performed.
     */
    private class FetchAllConditionsTimeStepsSwingWorker extends SwingWorker<Void, Void> {

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

            dataAnalysisController.setCursor(Cursor.WAIT_CURSOR);
            for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
                // if for current condition computations were not performed yet
                if (map.get(plateCondition) == null) {
                    // update status of progress bar with the current number of fetched conditions
                    dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setValue(getNumberOfFetchedCondition());
                    dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setString("Condition " + getNumberOfFetchedCondition() + "/" + dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().getMaximum());
                    // fetch current condition
                    dataAnalysisController.fetchConditionTimeSteps(plateCondition);
                    // uodate map (this is actually doing all the computations)
                    updateMapWithCondition(plateCondition);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            // when the thread is done, hide progress bar again
            dataAnalysisController.getDataAnalysisPanel().getFetchAllConditionsProgressBar().setVisible(false);
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
            // show all conditions in one plot (Global Area View)
            plotGlobalArea();
            // enable now tab for analysis
            bulkCellAnalysisPanel.getBulkTabbedPane().setEnabledAt(3, true);
        }
    }

    /**
     * initialize Matrix Panel with action listeners and everything
     */
    private void initCorrectedAreaPanel() {
        // set Border to empty one for text fields
        correctedAreaPanel.getCutOffTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        correctedAreaPanel.getExcludedReplicatesTextField().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        // initialize Binding List for time frames (2 combo boxes binded)
        timeFramesBindingList = ObservableCollections.observableList(Arrays.asList(ArrayUtils.toObject(dataAnalysisController.getTimeFrames())));
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, modifyCutOffPanel.getTimeFrameComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
//        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, distanceMatrixPanel.getLastTimeFrameComboBox());
//        bindingGroup.addBinding(jComboBoxBinding);
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
         * If the user decides to modify the current cut off value
         * I need to check if the chosen value is greater or lesser than the computed cut off. 
         */
        modifyCutOffPanel.getTimeFrameComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // results holder for currently selected condition
                AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(dataAnalysisController.getSelectedCondition());
                // get last time frame selected
                int selectedCutOff = timeFramesBindingList.indexOf(modifyCutOffPanel.getTimeFrameComboBox().getSelectedItem());

                // if last time frame provided by the user is equal or smaller than cut off time frame: No problem
                if (selectedCutOff == areaPreProcessingResultsHolder.getTimeInterval().getLastTimeFrame() || selectedCutOff < areaPreProcessingResultsHolder.getTimeInterval().getLastTimeFrame()) {
                    areaPreProcessingResultsHolder.getTimeInterval().setLastTimeFrame(selectedCutOff);
                    // refresh plot
                    plotCorrectedDataInTimeInterval(dataAnalysisController.getSelectedCondition());
                } else {
                    // if last time frame provided by the user is greater than cut off time frame: Warn the user!
                }
            }
        });

        /**
         * If the user is not happy with new selection, reset cut off value back to preciously computed one.
         */
        modifyCutOffPanel.getResetCutOffButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateCondition selectedCondition = dataAnalysisController.getSelectedCondition();
                // replot everything
                plotCorrectedDataReplicates(selectedCondition);
                // recompute time interval
                areaPreProcessor.setTimeInterval(map.get(selectedCondition));
                // refresh info label
                correctedAreaPanel.getCutOffTextField().setText(timeFramesBindingList.get(map.get(selectedCondition).getTimeInterval().getLastTimeFrame()).toString());
            }
        });

        /**
         * If the user decides to overwrite cut off value- pop up a JDialog with some options
         * Select a different cut off time frame from a combo box
         */
        correctedAreaPanel.getModifyCutOffButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // remove previous panel
                dialog.getContentPane().remove(distanceMatrixPanel);
                // add new panel
                dialog.getContentPane().add(modifyCutOffPanel, gridBagConstraints);
                // show the dialog
                dialog.setVisible(true);
            }
        });

        /**
         * If the user decides to overwrite decision about replicates selection, pop up a JDialog with Distance matrix table
         * In this table the user is able to select or deselect conditions replicates
         */
        correctedAreaPanel.getSelectReplicatesButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // remove previous panel
                dialog.getContentPane().remove(modifyCutOffPanel);
                // add distance matrix on top of panel
                showDistanceMatrix(dataAnalysisController.getSelectedCondition());
                // add new panel 
                dialog.getContentPane().add(distanceMatrixPanel, gridBagConstraints);
                // show the dialog
                dialog.setVisible(true);
            }
        });
    }
}
