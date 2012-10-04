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
import be.ugent.maf.cellmissy.analysis.DataTableModel;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
    //view
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    private ChartPanel areaChartPanel;
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
     * 
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
     * set time steps list for the area calculator
     */
    public void setTimeStepsList() {
        areaPreProcessor.setTimeStepsList(timeStepBindingList);
    }

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
        columnBinding.setColumnName("Area");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidX}"));
        columnBinding.setColumnName("Centroid_x");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${centroidY}"));
        columnBinding.setColumnName("Centroid_y");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${eccentricity}"));
        columnBinding.setColumnName("Eccentricity");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${majorAxis}"));
        columnBinding.setColumnName("Major Axis");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        columnBinding = timeStepsTableBinding.addColumnBinding(ELProperty.create("${minorAxis}"));
        columnBinding.setColumnName("Minor Axis");
        columnBinding.setEditable(false);
        columnBinding.setColumnClass(Double.class);

        bindingGroup.addBinding(timeStepsTableBinding);
        bindingGroup.bind();
    }

    /**
     * for each replicate (well) of a certain selected condition, show delta area values, close to time frames
     * @param plateCondition 
     */
    public void setDeltaAreaTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        dataTable.setModel(new DeltaAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void setAreaIncreaseTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        dataTable.setModel(new AreaIncreaseTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
        //starting from second column set Renderer for cells
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            //show OUTLIERS in red
            dataTable.getColumnModel().getColumn(i).setCellRenderer(new AreaIncreaseRenderer(areaPreProcessor.computeOutliers(getDataFromTableModel(dataTable)[i - 1])));
        }
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     * @param plateCondition 
     */
    public void setNormalizedAreaTableData(PlateCondition plateCondition) {
        //set Model for the Normalized AreaTable
        //NOTE that each time a new condition is selected, new slopes is passed to the model
        dataTable.setModel(new NormalizedAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected area values, close to time frames
     * @param plateCondition 
     */
    public void setCorrectedAreaTableData(JTable table, PlateCondition plateCondition) {
        //set the model for the Correcte AreaTable
        table.setModel(new CorrectedAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
    }

    /**
     * Show Area for a certain condition selected
     */
    public void showArea() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        double[][] dataFromTableModel = getDataFromTableModel(dataTable);
        double[] xValues = timeFrames;
        for (int columnIndex = 0; columnIndex < dataFromTableModel.length; columnIndex++) {
            double[] yValues = dataFromTableModel[columnIndex];
            XYSeries xySeries = generateXYSeriesArea(xValues, yValues);
            xySeries.setKey("Rep " + (columnIndex + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart areaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area " + "(\u00B5" + "m" + "\u00B2)", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        areaChart.getXYPlot().setBackgroundPaint(Color.white);
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
     * for a condition selected this method shows in one plot the estimated density functions for each replicate (=well)
     * This is doing the job for one condition (all replicates)
     */
    public void showRawDataDensityFunction() {
        JFreeChart densityChart = showDensityFunction(getDataFromTableModel(dataTable), "Kernel Density Estimator");
        densityChartPanel.setChart(densityChart);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(densityChartPanel, gridBagConstraints);
    }

    /**
     * for a condition selected this method shows density values for corrected distributions
     */
    public void showCorrectedDataDensityFunction() {
        //compute first corrected slopes (no outliers)
        double[][] correctedData = new double[getDataFromTableModel(dataTable).length][];
        for (int i = 0; i < getDataFromTableModel(dataTable).length; i++) {
            double[] correctedValues = areaPreProcessor.computeCorrectedArea(getDataFromTableModel(dataTable)[i]);
            correctedData[i] = correctedValues;
        }
        JFreeChart correctedDensityChart = showDensityFunction(correctedData, "KDE (Outliers Correction)");
        correctedDensityChartPanel.setChart(correctedDensityChart);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
    }

    /**
     * 
     */
    public void showSlopesInTable(int conditionIndex) {
        //model of table with rows number = number of plate conditions and columns number equal to well collection  + 3 (Condition Name, mean and SEM)
        DefaultTableModel model = (DefaultTableModel) dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getModel();
        model.setRowCount(dataAnalysisController.getPlateConditionList().size());
        model.setColumnCount(dataAnalysisController.getSelectedCondition().getWellCollection().size() + 3);
        String[] columnNames = new String[dataAnalysisController.getSelectedCondition().getWellCollection().size() + 3];
        columnNames[0] = "Condition";
        columnNames[columnNames.length - 2] = "mean";
        columnNames[columnNames.length - 1] = "SEM";
        for (int i = 1; i < columnNames.length - 2; i++) {
            columnNames[i] = "Repl " + i;
        }
        model.setColumnIdentifiers(columnNames);
        //first column needs to be bigger than others
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumn("Condition").setMinWidth(250);
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumn("Condition").setMaxWidth(250);
        dataAnalysisController.getDataAnalysisPanel().getSlopesTable().getColumn("Condition").setPreferredWidth(250);

        Object[][] data = new Object[1][dataAnalysisController.getSelectedCondition().getWellCollection().size()];

        double[] computeSlopesPerCondition = computeSlopesPerCondition(dataAnalysisController.getSelectedCondition());
        data[0] = new Object[computeSlopesPerCondition.length + 3];
        for (int j = 1; j < data[0].length - 2; j++) {
            data[0][j] = (Object) computeSlopesPerCondition[j - 1];
        }
        data[0][0] = dataAnalysisController.getSelectedCondition().toString();
        data[0][data[0].length - 2] = AnalysisUtils.roundTwoDecimals(AnalysisUtils.computeMean(computeSlopesPerCondition));
        data[0][data[0].length - 1] = AnalysisUtils.roundTwoDecimals(AnalysisUtils.computeSEM(computeSlopesPerCondition));

        for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
            model.setValueAt(data[0][columnIndex], conditionIndex, columnIndex);
        }
    }

    public void showVelocityBars() {
        double[][] data = getDataFromTableModel(dataAnalysisController.getDataAnalysisPanel().getSlopesTable());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double[] velocities = data[6];
        for (int i = 0; i < velocities.length; i++) {
            dataset.addValue(velocities[i], "", "Condition " + (i + 1));
        }

        JFreeChart velocityChart = ChartFactory.createBarChart("", "Conditions", "Velocity " + "(\u00B5" + "m" + "\u00B2" + "\\min)", dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot velocityPlot = velocityChart.getCategoryPlot();
        velocityPlot.setBackgroundPaint(Color.white);
        velocityPlot.setRenderer(new VelocityBarRenderer());
        velocityPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        AnalysisUtils.setShadowVisible(velocityChart, false);
        velocityChartPanel.setChart(velocityChart);
        dataAnalysisController.getDataAnalysisPanel().getjPanel1().add(velocityChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getjPanel1().repaint();
    }

    /**
     * 
     * @param plateCondition
     * @return 
     */
    private double[] computeSlopesPerCondition(PlateCondition plateCondition) {

        Double[][] data = new Double[timeFrames.length][plateCondition.getWellCollection().size() + 1];
        Double[][] normalizeCorrectedArea = areaPreProcessor.normalizeCorrectedArea(data, plateCondition);
        //transpose slopes
        double[][] transposed = new double[normalizeCorrectedArea[0].length - 1][];
        for (int i = 1; i < normalizeCorrectedArea[0].length; i++) {
            List<Double> tempList = new ArrayList<>();
            for (int j = 0; j < normalizeCorrectedArea.length; j++) {
                if (normalizeCorrectedArea[j][i] != null) {
                    tempList.add((double) normalizeCorrectedArea[j][i]);
                }
            }
            transposed[i - 1] = ArrayUtils.toPrimitive(tempList.toArray(new Double[tempList.size()]));
        }

        return areaAnalyzer.computeSlopesPerCondition(transposed, plateCondition, timeFrames);
    }

    //compute time frames
    public void computeTimeFrames() {
        areaPreProcessor.setTimeFramesNumber(dataAnalysisController.getExperiment().getTimeFrames());
        this.timeFrames = areaPreProcessor.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval());
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
        velocityChartPanel = new ChartPanel(null);
        velocityChartPanel.setOpaque(false);
    }

    /**
     * For each set of slopes points estimate density function and return a XY series for the plot
     * This is doing the job for one replicate!
     * @param slopes
     * @return a XYSeries
     */
    private XYSeries generateDensityFunction(double[] data) {
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
    private JFreeChart showDensityFunction(double[][] data, String chartTitle) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int i = 0; i < data.length; i++) {
            XYSeries xySeries = generateDensityFunction(data[i]);
            xySeries.setKey("Rep " + (i + 1));
            xySeriesCollection.addSeries(xySeries);
        }
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
     * 
     * @param xValues
     * @param yValues
     * @return 
     */
    private XYSeries generateXYSeriesArea(double[] xValues, double[] yValues) {
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
     * TableModel for the NormalizedArea slopes
     */
    private class NormalizedAreaTableModel extends DataTableModel {

        public NormalizedAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeNormalizedArea array into slopes array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = timeFrames[i];
                //fill in all the other columns
                //arraycopy(Object src,  int  srcPos, Object dest, int destPos, int length)
                System.arraycopy(areaPreProcessor.computeNormalizedArea(data)[i], 1, data[i], 1, data[i].length - 1);
            }
        }
    }

    /**
     * Table Model for DeltaArea slopes
     */
    private class DeltaAreaTableModel extends DataTableModel {

        public DeltaAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeNormalizedArea array into slopes array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = timeFrames[i];
                System.arraycopy(areaPreProcessor.computeDeltaArea(data)[i], 1, data[i], 1, data[i].length - 1);
            }
        }
    }

    /**
     * Table Model for AreaIncrease slopes
     */
    private class AreaIncreaseTableModel extends DataTableModel {

        public AreaIncreaseTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeAreaIncrease array into slopes array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = timeFrames[i];
                System.arraycopy(areaPreProcessor.computeAreaIncrease(data, dataAnalysisController.getSelectedCondition())[i], 1, data[i], 1, data[i].length - 1);

            }
        }
    }

    /**
     * Table Model for CorrectedArea slopes
     */
    private class CorrectedAreaTableModel extends DataTableModel {

        public CorrectedAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeAreaIncrease array into slopes array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = timeFrames[i];
                System.arraycopy(areaPreProcessor.normalizeCorrectedArea(data, dataAnalysisController.getSelectedCondition())[i], 1, data[i], 1, data[i].length - 1);
            }
        }
    }

    /**
     * Cell renderer for Area Increase Table
     */
    private class AreaIncreaseRenderer extends DefaultTableCellRenderer {

        double[] outliers;

        public AreaIncreaseRenderer(double[] outliers) {
            this.outliers = outliers;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(dataTable, value, false, false, row, column);

            Double areaIncrease = (Double) value;
            if (areaIncrease != null) {
                for (double outlier : outliers) {
                    if (Double.valueOf(areaIncrease) == outlier) {
                        setForeground(Color.red);
                        break;
                    } else {
                        setForeground(Color.black);
                    }
                }
            }

            setOpaque(true);
            return this;
        }
    }

    private class VelocityBarRenderer extends BarRenderer {

        private Paint[] colors = GuiUtils.getAvailableColors();

        @Override
        public Paint getItemPaint(final int row, final int column) {
            return this.colors[column + 1];
        }
    }
}
