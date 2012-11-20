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
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.view.ComputedDataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.cache.impl.DensityFunctionHolderCache;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.experiment.DistanceMatrixPanel;
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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
    //array with time frames
    private double[] timeFrames;
    private Format format;

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

    public ChartPanel getDensityChartPanel() {
        return densityChartPanel;
    }

    public ChartPanel getCorrectedDensityChartPanel() {
        return correctedDensityChartPanel;
    }

    public ChartPanel getRawDataChartPanel() {
        return rawDataChartPanel;
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public Map<PlateCondition, AreaPreProcessingResultsHolder> getMap() {
        return map;
    }

    public DistanceMatrixPanel getDistanceMatrixPanel() {
        return distanceMatrixPanel;
    }

    /**
     * public methods and classes
     */
    /**
     * show table with TimeSteps results from CellMIA analysis (timeSteps fetched from DB)
     * this is populating the JTable in the ResultsImporter Panel
     */
    public void showTimeStepsInTable() {
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepsBindingList, dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable());
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

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidX}"));
        columnBinding.setColumnName("Centroid_x (pixels)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidY}"));
        columnBinding.setColumnName("Centroid_y (pixels)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${eccentricity}"));
        columnBinding.setColumnName("Eccentricity");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${majorAxis}"));
        columnBinding.setColumnName("Major Axis " + "(\u00B5" + "m)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${minorAxis}"));
        columnBinding.setColumnName("Minor Axis " + "(\u00B5" + "m)");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * for each replicate (well) of a certain selected condition, show delta area values, close to time frames
     * @param plateCondition 
     */
    public void showDeltaAreaInTable(PlateCondition plateCondition) {
        Double[][] deltaArea = map.get(plateCondition).getDeltaArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, deltaArea, timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(format));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void showAreaIncreaseInTable(PlateCondition plateCondition) {
        Double[][] percentageAreaIncrease = map.get(plateCondition).getPercentageAreaIncrease();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, percentageAreaIncrease, timeFrames));
        //format first column
        dataTable.getColumnModel().getColumn(0).setCellRenderer(new FormatRenderer(format));
        boolean[][] outliers = areaPreProcessor.detectOutliers(percentageAreaIncrease);
        //show outliers in red from second column on
        OutliersRenderer outliersRenderer = new OutliersRenderer(outliers, format);
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setCellRenderer(outliersRenderer);
        }
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     * @param plateCondition 
     */
    public void showNormalizedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedArea = map.get(plateCondition).getNormalizedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedArea, timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(format));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Area @time frame zero is set to 0.00");
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(distanceMatrixScrollPane);
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected (for outliers) area values, close to time frames
     * @param plateCondition 
     */
    public void showCorrectedAreaInTable(PlateCondition plateCondition) {
        Double[][] normalizedCorrectedArea = map.get(plateCondition).getNormalizedCorrectedArea();
        dataTable.setModel(new ComputedDataTableModel(plateCondition, normalizedCorrectedArea, timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(format));
        dataTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
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
        CheckBoxOutliersRenderer checkBoxOutliersRenderer = new CheckBoxOutliersRenderer(transposedOutliersMatrix, format);
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
     * Show Area Replicates for a certain selected condition
     * @param plateCondition 
     */
    public void plotCorrectedAreaReplicates(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        // Transpose Normalized Corrected Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        List wellList = new ArrayList(plateCondition.getWellCollection());
        // check if some replicates need to be hidden from plot (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResultsHolder.getExcludeReplicates();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = timeFrames;
        for (int i = 0; i < excludeReplicates.length; i++) {
            // if boolean is false, replicate has to be considered in the plot
            if (!excludeReplicates[i]) {
                // array for y axis
                double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
                XYSeries xySeries = generateXYSeries(xValues, yValues);
                xySeries.setKey("" + (wellList.get(i)));
                xySeriesCollection.addSeries(xySeries);
            }
        }
        // Plot Logic
        JFreeChart correctedAreaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
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
        distanceMatrixPanel.getReplicatesAreaChartParentPanel().add(correctedAreaChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(distanceMatrixPanel, gridBagConstraints);
    }

    /**
     * 
     * @param plateCondition 
     */
    public void plotRawDataReplicates(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        // get raw data, not corrected yet but only normalized
        Double[][] normalizedArea = areaPreProcessingResultsHolder.getNormalizedArea();
        // Transpose Normalized Area
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedArea);
        List wellList = new ArrayList(plateCondition.getWellCollection());
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        // array for x axis
        double[] xValues = timeFrames;
        for (int i = 0; i < transposedArea.length; i++) {
            double[] yValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposedArea[i]));
            XYSeries xySeries = generateXYSeries(xValues, yValues);
            xySeries.setKey("" + (wellList.get(i)));
            xySeriesCollection.addSeries(xySeries);
        }
        // Plot Logic
        JFreeChart rawDataAreaChart = ChartFactory.createXYLineChart("Raw Data Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
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
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(densityChartPanel);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(correctedDensityChartPanel);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().revalidate();
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(rawDataChartPanel, gridBagConstraints);
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
     * Show all conditions in one plot (median is computed per time point across all replicates)
     */
    public void plotGlobalArea() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (PlateCondition plateCondition : map.keySet()) {
            double[] xValues = timeFrames;
            double[] yValues = new double[timeFrames.length];
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
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        globalAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        globalAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        AreaPlotRenderer areaPlotRenderer = new AreaPlotRenderer();
        globalAreaChart.getXYPlot().setRenderer(areaPlotRenderer);
        globalAreaChartPanel.setChart(globalAreaChart);
        dataAnalysisController.getDataAnalysisPanel().getGlobalViewPanel().add(globalAreaChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGlobalViewPanel().repaint();
    }

    /**
     * show Bar charts for area velocity
     */
    public void showVelocityBars() {
        TableModel model = dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getModel();
        int[] selectedRows = dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getSelectedRows();
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
        velocityChart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
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
        dataAnalysisController.getDataAnalysisPanel().getVelocityChartPanel().add(velocityChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getVelocityChartPanel().repaint();
    }

    /**
     * Populate Table with Slopes from Linear Model Estimation
     */
    public void showLinearModelInTable() {
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

        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().setModel(new DefaultTableModel(data, columnNames));
        //first column needs to be bigger than others
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnModel().getColumn(0).setMinWidth(250);
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnModel().getColumn(0).setMaxWidth(250);
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnModel().getColumn(0).setPreferredWidth(250);

        //set format renderer only from second column on
        for (int columnIndex = 1; columnIndex < dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnCount(); columnIndex++) {
            dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(format));
        }
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
    }

    /**
     * Compute time frames from time steps list
     */
    public void computeTimeFrames() {
        double[] timeFrames = new double[dataAnalysisController.getExperiment().getTimeFrames()];
        for (int i = 0; i < timeFrames.length; i++) {
            Double timeFrame = timeStepsBindingList.get(i).getTimeStepSequence() * dataAnalysisController.getExperiment().getExperimentInterval();
            int intValue = timeFrame.intValue();
            timeFrames[i] = intValue;
        }
        this.timeFrames = timeFrames;
    }

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
     * private methods and classes
     */
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
        return areaAnalyzer.computeSlopes(transposedNormalizedCorrectedArea, timeFrames, excludeReplicates).get(0);
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
        return areaAnalyzer.computeSlopes(transposedNormalizedCorrectedArea, timeFrames, excludeReplicates).get(1);
    }

    /**
     * from time steps List to 2D array of Double
     * @param plateCondition
     * @return 2D array with area raw data
     */
    private Double[][] getAreaRawData(PlateCondition plateCondition) {
        Double[][] areaRawData = new Double[timeFrames.length][plateCondition.getWellCollection().size()];
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
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(distanceMatrixScrollPane);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().revalidate();
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(densityChartPanel, gridBagConstraints);
    }

    /**
     * Given a chart for the corrected data density function, show it
     * @param densityChart 
     */
    private void plotCorrectedDataDensityFunctions(JFreeChart densityChart) {
        correctedDensityChartPanel.setChart(densityChart);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
    }

    /**
     * Given a xySeriesCollection and a string as a title, create a chart to show density function in a plot
     * @param plateCondition
     * @param dataCategory
     * @param chartTitle
     * @return 
     */
    private JFreeChart generateDensityChart(XYSeriesCollection xYSeriesCollection, String chartTitle) {
        JFreeChart densityChart = ChartFactory.createXYLineChart(chartTitle, "% increase (Area)", "Density", xYSeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        densityChart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 12));
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
        //init Tables
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setBackground(Color.white);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        dataAnalysisController.getDataAnalysisPanel().getDataTablePanel().add(scrollPane);
        //init timeStepsBindingList
        timeStepsBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());

        //init subview
        initDistanceMatrixPanel();
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
        map = new LinkedHashMap<>();
        format = new DecimalFormat("0.00");
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
            dataAnalysisController.setCursor(Cursor.DEFAULT_CURSOR);
            // once xySeriesCollections are generated, generate also Charts and show results
            plotRawDataDensityFunctions(generateDensityChart(rawDataXYSeriesCollection, "KDE raw data"));
            plotCorrectedDataDensityFunctions(generateDensityChart(correctedDataXYSeriesCollection, "KDE corrected data"));
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
            plotCorrectedAreaReplicates(plateCondition);
            // keep note of the fact that the user had interaction with check boxes
            map.get(plateCondition).setUserHadInteraction(true);
        }
    }

    /**
     * initialize Matrix Panel with action listeners and everything
     */
    private void initDistanceMatrixPanel() {
        distanceMatrixPanel = new DistanceMatrixPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(distanceMatrixPanel.getFullTimeFramesRadioButton());
        buttonGroup.add(distanceMatrixPanel.getSubsetTimeFramesRadioButton());
        // select by default option for entire time frame set
        distanceMatrixPanel.getFullTimeFramesRadioButton().setSelected(true);
        
        // this has to be init after the time frames are computed or a null pointer exception will be thrown
        timeFramesBindingList = ObservableCollections.observableList(Arrays.asList(ArrayUtils.toObject(timeFrames))); 
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, timeFramesBindingList, distanceMatrixPanel.getTimeFramesComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();
        
        // ?user chooses full time
        distanceMatrixPanel.getFullTimeFramesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        // ?or user chooses only a subset of time frames
        distanceMatrixPanel.getSubsetTimeFramesRadioButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        distanceMatrixPanel.getTimeFramesComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }
}
