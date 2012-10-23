/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaAnalyzer;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.view.DataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
import be.ugent.maf.cellmissy.gui.view.AreaIncreaseRenderer;
import be.ugent.maf.cellmissy.gui.view.FormatRenderer;
import be.ugent.maf.cellmissy.gui.view.HeaderRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
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
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Bulk Cell Analysis Controller: Collective Cell Migration Data Analysis
 * Parent Controller: Data Analysis Controller 
 * @author Paola Masuzzo
 */
@Controller("bulkCellAnalysisController")
public class BulkCellAnalysisController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable dataTable;
    private Map<PlateCondition, AreaPreProcessingResultsHolder> map;
    //view
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    private ChartPanel areaChartPanel;
    private ChartPanel globalAreaChartPanel;
    private ChartPanel velocityChartPanel;
    //parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    //child controllers
    //services
    @Autowired
    private KernelDensityEstimator kernelDensityEstimator;
    @Autowired
    private AreaPreProcessor areaPreProcessor;
    @Autowired
    private AreaAnalyzer areaAnalyzer;
    private GridBagConstraints gridBagConstraints;
    //array with time frames
    private double[] timeFrames;

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
    public ObservableList<TimeStep> getTimeStepBindingList() {
        return timeStepBindingList;
    }

    public ChartPanel getDensityChartPanel() {
        return densityChartPanel;
    }

    public ChartPanel getCorrectedDensityChartPanel() {
        return correctedDensityChartPanel;
    }

    public ChartPanel getAreaChartPanel() {
        return areaChartPanel;
    }

    public JTable getDataTable() {
        return dataTable;
    }

    /**
     * public methods and classes
     */
    /**
     * show table with TimeSteps results from CellMia analysis
     * this is populating the JTable in the ResultsImporter Panel
     */
    public void showTimeSteps() {
        //make the TimeStepsTable non selectable
        dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable().setFocusable(false);
        dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable().setRowSelectionAllowed(false);
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepBindingList, dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable());
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

        dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable().setDefaultRenderer(Object.class, new FormatRenderer(new DecimalFormat()));
        dataAnalysisController.getDataAnalysisPanel().getTimeStepsTable().setFillsViewportHeight(true);
        dataAnalysisController.getDataAnalysisPanel().getTimeStepsTableScrollPane().getViewport().setOpaque(false);
    }

    /**
     * for each replicate (well) of a certain selected condition, show delta area values, close to time frames
     * @param plateCondition 
     */
    public void setDeltaAreaTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        dataTable.setModel(new DataTableModel(plateCondition, map.get(plateCondition).getDeltaArea(), timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(new DecimalFormat("0.00")));
        dataTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Area increments between time frame t(n) and t(n+1)");
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void setAreaIncreaseTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        Double[][] percentageAreaIncrease = map.get(plateCondition).getPercentageAreaIncrease();
        Double[][] percentageAreaTransposed = AnalysisUtils.transpose2DArray(percentageAreaIncrease);
        dataTable.setModel(new DataTableModel(plateCondition, percentageAreaIncrease, timeFrames));
        dataTable.getColumnModel().getColumn(0).setCellRenderer(new FormatRenderer(new DecimalFormat("0.00")));
        //starting from second column set Renderer for cells
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            Double[] outliers = areaPreProcessor.computeOutliers(percentageAreaTransposed[i - 1]);
            //show OUTLIERS in red
            dataTable.getColumnModel().getColumn(i).setCellRenderer(new AreaIncreaseRenderer(outliers, new DecimalFormat("0.00")));
        }
        dataTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("% area increases, distributions' outliers are shown in red");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     * @param plateCondition 
     */
    public void setNormalizedAreaTableData(PlateCondition plateCondition) {
        //set Model for the Normalized AreaTable
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        dataTable.setModel(new DataTableModel(plateCondition, map.get(plateCondition).getNormalizedArea(), timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(new DecimalFormat("0.00")));
        dataTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Area @time frame zero is set to 0.00");
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected area values, close to time frames
     * @param plateCondition 
     */
    public void setCorrectedAreaTableData(PlateCondition plateCondition) {
        //set the model for the Correcte AreaTable
        dataTable.setModel(new DataTableModel(plateCondition, map.get(plateCondition).getNormalizedCorrectedArea(), timeFrames));
        dataTable.setDefaultRenderer(Object.class, new FormatRenderer(new DecimalFormat("0.00")));
        dataTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        dataAnalysisController.getDataAnalysisPanel().getTableInfoLabel().setText("Outliers are removed from distributions and new area values are shown");
    }

    /**
     * Show Area for a certain condition selected
     * @param plateCondition 
     */
    public void showArea(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        double[] xValues = timeFrames;
        for (int columnIndex = 0; columnIndex < transposedArea.length; columnIndex++) {
            double[] yValues = ArrayUtils.toPrimitive(transposedArea[columnIndex]);
            XYSeries xySeries = generateXYSeries(xValues, yValues);
            xySeries.setKey("Rep " + (columnIndex + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart areaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        areaChart.getXYPlot().setBackgroundPaint(Color.white);
        areaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        XYItemRenderer renderer = areaChart.getXYPlot().getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
        }
        areaChartPanel.setChart(areaChart);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(densityChartPanel);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().remove(correctedDensityChartPanel);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().revalidate();
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(areaChartPanel, gridBagConstraints);
    }

    /**
     * 
     * @param plateCondition 
     */
    public void showEuclideanDistances(PlateCondition plateCondition) {
        Double[][] euclideanDistances = map.get(plateCondition).getEuclideanDistances();
        Object[][] data = new Object[euclideanDistances.length][euclideanDistances.length + 1];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "" + (rowIndex + 1);
            for (int columnIndex = 1; columnIndex < data.length + 1; columnIndex++) {
                data[rowIndex][columnIndex] = euclideanDistances[rowIndex][columnIndex - 1];
            }
        }
        String[] columnNames = new String[euclideanDistances.length + 1];
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "" + i;
        }
        dataAnalysisController.getDataAnalysisPanel().getDistancesTable().setModel(new DefaultTableModel(data, columnNames));
        //set format renderer only from second column on
        for (int i = 1; i < dataAnalysisController.getDataAnalysisPanel().getDistancesTable().getColumnCount(); i++) {
            Double[] outliers = areaPreProcessor.computeOutliers(euclideanDistances[i - 1]);
            dataAnalysisController.getDataAnalysisPanel().getDistancesTable().getColumnModel().getColumn(i).setCellRenderer(new AreaIncreaseRenderer(outliers, new DecimalFormat("0.00")));
        }
    }

    /**
     * Show all conditions in one plot (median is computed per time point across all replicates)
     */
    public void showGlobalArea() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (PlateCondition plateCondition : map.keySet()) {
            double[] xValues = timeFrames;
            double[] yValues = new double[timeFrames.length];
            double[] mads = new double[timeFrames.length];
            Double[][] normalizedCorrectedArea = map.get(plateCondition).getNormalizedCorrectedArea();
            for (int columnIndex = 0; columnIndex < normalizedCorrectedArea.length; columnIndex++) {
                double[] replicateValues = ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(normalizedCorrectedArea[columnIndex]));
                double median = AnalysisUtils.computeMedian(replicateValues);
                mads[columnIndex] = AnalysisUtils.scaleMAD(replicateValues);
                yValues[columnIndex] = median;
            }
            double medianMadPerCondition = AnalysisUtils.computeMedian(mads);
            System.out.println("" + medianMadPerCondition);
            XYSeries xySeries = generateXYSeries(xValues, yValues);
            xySeries.setKey("Cond " + (dataAnalysisController.getPlateConditionList().indexOf(plateCondition) + 1));
            xySeriesCollection.addSeries(xySeries);
        }

        //@todo give an estimate on the median value (SEM?)
        JFreeChart globalAreaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        globalAreaChart.getXYPlot().setBackgroundPaint(Color.white);
        globalAreaChart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        AreaRenderer areaRenderer = new AreaRenderer();
        globalAreaChart.getXYPlot().setRenderer(areaRenderer);
        globalAreaChartPanel.setChart(globalAreaChart);
        dataAnalysisController.getDataAnalysisPanel().getGlobalViewPanel().add(globalAreaChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGlobalViewPanel().repaint();
    }

    /**
     * for a condition selected this method shows in one plot the estimated density functions for each replicate (=well)
     * This is doing the job for one condition (all replicates)
     * @param plateCondition 
     */
    public void showRawDataDensityFunction(PlateCondition plateCondition) {
        Double[][] percentageAreaIncrease = map.get(plateCondition).getPercentageAreaIncrease();
        Double[][] percentageAreaTransposed = AnalysisUtils.transpose2DArray(percentageAreaIncrease);
        JFreeChart densityChart = showDensityFunction(percentageAreaTransposed, "Kernel Density Estimator");
        densityChartPanel.setChart(densityChart);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(densityChartPanel, gridBagConstraints);
    }

    /**
     * for a condition selected this method shows density values for corrected distributions
     * @param plateCondition 
     */
    public void showCorrectedDataDensityFunction(PlateCondition plateCondition) {
        Double[][] percentageAreaIncrease = map.get(plateCondition).getPercentageAreaIncrease();
        Double[][] percentageAreaTransposed = AnalysisUtils.transpose2DArray(percentageAreaIncrease);
        //compute first corrected slopes (no outliers)
        Double[][] correctedData = new Double[percentageAreaTransposed.length][];
        for (int i = 0; i < percentageAreaTransposed.length; i++) {
            Double[] correctedValues = areaPreProcessor.correctForOutliers(percentageAreaTransposed[i]);
            correctedData[i] = correctedValues;
        }
        JFreeChart correctedDensityChart = showDensityFunction(correctedData, "KDE (Outliers Correction)");
        correctedDensityChartPanel.setChart(correctedDensityChart);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
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

        JFreeChart velocityChart = ChartFactory.createLineChart("", "", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
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
        dataAnalysisController.getDataAnalysisPanel().getjPanel1().add(velocityChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getjPanel1().repaint();
    }

    /**
     * Populate Table with Slopes from Linear Model Estimation
     */
    public void showLinearModelResults() {
        List<double[]> slopes = new ArrayList<>();
        for (PlateCondition plateCondition : map.keySet()) {
            double[] slopesPerCondition = computeSlopesPerCondition(plateCondition);
            slopes.add(slopesPerCondition);
        }
        Object[][] data = new Object[map.keySet().size()][slopes.get(0).length + 3];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            for (int columnIndex = 1; columnIndex < data[0].length - 2; columnIndex++) {
                data[rowIndex][columnIndex] = slopes.get(rowIndex)[columnIndex - 1];
            }
            data[rowIndex][0] = dataAnalysisController.getPlateConditionList().get(rowIndex).toString();
            data[rowIndex][data[0].length - 2] = AnalysisUtils.computeMedian(slopes.get(rowIndex));
            data[rowIndex][data[0].length - 1] = AnalysisUtils.scaleMAD(slopes.get(rowIndex));
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
            dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumnModel().getColumn(columnIndex).setCellRenderer(new FormatRenderer(new DecimalFormat("0.00")));
        }
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getTableHeader().setDefaultRenderer(new HeaderRenderer());
    }

    /**
     * Given a certain condition estimate Linear Model and give back slopes
     * @param plateCondition
     * @return 
     */
    private double[] computeSlopesPerCondition(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        return areaAnalyzer.computeSlopes(AnalysisUtils.transpose2DArray(normalizedCorrectedArea), timeFrames).get(0);
    }

    /**
     * Given a certain condition estimate Linear Model and give back R2 coefficients (goodness of fitness)
     * @param plateCondition
     * @return 
     */
    //@todo R2 Coefficients to be included in view
    private double[] computeRCoefficientsPerCondition(PlateCondition plateCondition) {
        AreaPreProcessingResultsHolder areaPreProcessingResultsHolder = map.get(plateCondition);
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        return areaAnalyzer.computeSlopes(AnalysisUtils.transpose2DArray(normalizedCorrectedArea), timeFrames).get(1);
    }

    //compute time frames
    public void computeTimeFrames() {
        double[] timeFrames = new double[dataAnalysisController.getExperiment().getTimeFrames()];
        for (int i = 0; i < dataAnalysisController.getExperiment().getTimeFrames(); i++) {
            Double timeFrame = timeStepBindingList.get(i).getTimeStepSequence() * dataAnalysisController.getExperiment().getExperimentInterval();
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
            AreaPreProcessingResultsHolder preProcessingResultsHolder = new AreaPreProcessingResultsHolder();
            preProcessingResultsHolder.setAreaRawData(getAreaRawData(plateCondition));
            areaPreProcessor.computeNormalizedArea(preProcessingResultsHolder);
            areaPreProcessor.computeDeltaArea(preProcessingResultsHolder);
            areaPreProcessor.computeAreaIncrease(preProcessingResultsHolder);
            areaPreProcessor.normalizeCorrectedArea(preProcessingResultsHolder);
            areaPreProcessor.computeEuclideanDistances(preProcessingResultsHolder);
            map.put(plateCondition, preProcessingResultsHolder);
        }
    }

    /**
     * update map for every condition (even the ones that have not been selected by the user)
     */
    public void updateMap() {
        for (PlateCondition plateCondition : dataAnalysisController.getPlateConditionList()) {
            if (map.get(plateCondition) == null) {
                dataAnalysisController.fetchCondition(plateCondition);
                updateMapWithCondition(plateCondition);
            }

        }
    }

    /**
     * from time steps List to 2D array of Double
     * @param plateCondition
     * @return 
     */
    private Double[][] getAreaRawData(PlateCondition plateCondition) {
        Double[][] areaRawData = new Double[dataAnalysisController.getExperiment().getTimeFrames()][plateCondition.getWellCollection().size()];
        int counter = 0;
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (timeStepBindingList.get(counter).getArea() != 0) {
                    areaRawData[rowIndex][columnIndex] = timeStepBindingList.get(counter).getArea();
                } else {
                    areaRawData[rowIndex][columnIndex] = null;
                }
                counter++;
            }
        }
        return areaRawData;
    }

    /**
     * private methods and classes
     */
    /**
     * initialize main panel
     */
    private void initBulkCellAnalysisPanel() {
        //init Tables
        dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        //the table will take all the viewport height available
        dataTable.setFillsViewportHeight(true);
        scrollPane.getViewport().setOpaque(false);
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        dataAnalysisController.getDataAnalysisPanel().getDataTablePanel().add(scrollPane);
        //init timeStepsBindingList
        timeStepBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
        //init chart panels
        densityChartPanel = new ChartPanel(null);
        densityChartPanel.setOpaque(false);
        correctedDensityChartPanel = new ChartPanel(null);
        correctedDensityChartPanel.setOpaque(false);
        areaChartPanel = new ChartPanel(null);
        areaChartPanel.setOpaque(false);
        globalAreaChartPanel = new ChartPanel(null);
        globalAreaChartPanel.setOpaque(false);
        velocityChartPanel = new ChartPanel(null);
        velocityChartPanel.setOpaque(false);
        map = new LinkedHashMap<>();
    }

    /**
     * For each set of slopes points estimate density function and return a XY series for the plot
     * This is doing the job for one replicate!
     * @param slopes
     * @return a XYSeries
     */
    private XYSeries estimateDensityFunctionPerReplicate(Double[] data) {
        //use KDE to estimate density function for dataset slopes
        List<double[]> estimateDensityFunction = kernelDensityEstimator.estimateDensityFunction(data);
        double[] xValues = estimateDensityFunction.get(0);
        double[] yValues = estimateDensityFunction.get(1);
        //XYSeries is by default ordered in ascending values, set second parameter of costructor to false
        XYSeries series = new XYSeries("", false);
        for (int i = 0; i < xValues.length; i++) {
            double x = xValues[i];
            double y = yValues[i];
            series.add(x, y);
        }
        return series;
    }

    /**
     * show Density function for each 2D array of double
     * @param slopes - 2D array of double
     * @param chartTitle - string for chart title
     * @return a JFreeChart
     */
    private JFreeChart showDensityFunction(Double[][] data, String chartTitle) {
        XYSeriesCollection xySeriesCollection = estimateDensityFunctionPerCondition(data);
        JFreeChart densityChart = ChartFactory.createXYLineChart(chartTitle, "% increase (Area)", "Density", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);

        //XYplot
        XYPlot xYPlot = densityChart.getXYPlot();
        //disable autorange for the axes
        xYPlot.getDomainAxis().setAutoRange(false);
        xYPlot.getRangeAxis().setAutoRange(false);
        //set ranges for x and y axes
        xYPlot.getDomainAxis().setRange(xySeriesCollection.getDomainLowerBound(true) - 0.05, xySeriesCollection.getDomainUpperBound(true) + 0.05);
        xYPlot.getRangeAxis().setUpperBound(computeMaxY(xySeriesCollection) + 0.05);
        xYPlot.setBackgroundPaint(Color.white);
        //renderer for wide line
        XYItemRenderer renderer = xYPlot.getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
        }
        return densityChart;
    }

    /**
     * Estimate Density Function per Condition
     * @param data
     * @return 
     */
    public XYSeriesCollection estimateDensityFunctionPerCondition(Double[][] data) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int i = 0; i < data.length; i++) {
            XYSeries xySeries = estimateDensityFunctionPerReplicate(data[i]);
            xySeries.setKey("Rep " + (i + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        return xySeriesCollection;
    }

    /**
     * Generate Series for (x,y) Area plotting
     * @param xValues
     * @param yValues
     * @return 
     */
    private XYSeries generateXYSeries(double[] xValues, double[] yValues) {
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
     * get primitive slopes from the table model 
     * @return 
     */
    private double[][] getDataFromTableModel(JTable table) {
        TableModel model = table.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        double[][] tableData = new double[columnCount - 1][];
        for (int i = 1; i < columnCount; i++) {
            List<Double> tempList = new ArrayList<>();
            for (int j = 0; j < rowCount; j++) {
                if (model.getValueAt(j, i) != null) {
                    tempList.add((double) model.getValueAt(j, i));
                }
            }
            tableData[i - 1] = ArrayUtils.toPrimitive(tempList.toArray(new Double[tempList.size()]));
        }
        return tableData;
    }

    /**
     * Statistical Bar Renderer for Velocity Bar Charts
     */
    private class VelocityBarRenderer extends StatisticalBarRenderer {

        @Override
        public Paint getItemPaint(final int row, final int column) {
            String conditionName = (String) getPlot().getDataset().getColumnKey(column);
            int length = conditionName.length();
            CharSequence subSequence = conditionName.subSequence(10, length);
            int conditionIndex = Integer.parseInt(subSequence.toString());
            return GuiUtils.getAvailableColors()[conditionIndex];
        }
    }

    /**
     * XY Line and Shape Renderer for Area Plot
     */
    private class AreaRenderer extends XYLineAndShapeRenderer {

        @Override
        public Paint getItemPaint(int series, int item) {
            return GuiUtils.getAvailableColors()[series + 1];
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            BasicStroke wideLine = new BasicStroke(1.3f);
            return wideLine;
        }

        @Override
        public boolean getItemShapeVisible(int series, int item) {
            return false;
        }
    }
}
