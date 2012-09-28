/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.impl.AreaCalculatorImpl;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.analysis.DataTableModel;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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
    //parent controller
    @Autowired
    private DataAnalysisController dataAnalysisController;
    //child controllers
    //services
    @Autowired
    private KernelDensityEstimator kernelDensityEstimator;
    @Autowired
    private OutliersHandler outliersHandler;
    @Autowired
    private AreaCalculatorImpl areaCalculator;
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
     * initialize Area Calculator
     */
    public void initAreaCalculator() {
        areaCalculator.init(dataAnalysisController.getExperiment().getTimeFrames(), timeStepBindingList);
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
        //NOTE that each time a new condition is selected, new data is passed to the model
        dataTable.setModel(new DeltaAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void setAreaIncreaseTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new data is passed to the model
        dataTable.setModel(new AreaIncreaseTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
        //starting from second column set Renderer for cells
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            //show OUTLIERS in red
            dataTable.getColumnModel().getColumn(i).setCellRenderer(new AreaIncreaseRenderer(outliersHandler.handleOutliers(getDataFromTableModel()[i - 1]).get(0)));
        }
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized area values, close to time frames
     * @param plateCondition 
     */
    public void setNormalizedAreaTableData(PlateCondition plateCondition) {
        //set Model for the Normalized AreaTable
        //NOTE that each time a new condition is selected, new data is passed to the model
        dataTable.setModel(new NormalizedAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
    }

    /**
     * for each replicate (well) of a certain selected condition, show normalized corrected area values, close to time frames
     * @param plateCondition 
     */
    public void setCorrectedAreaTableData(PlateCondition plateCondition) {
        //set the model for the Correcte AreaTable
        dataTable.setModel(new CorrectedAreaTableModel(plateCondition, dataAnalysisController.getExperiment().getTimeFrames()));
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
    }

    /**
     * For each set of data points estimate density function and return a XY series for the plot
     * This is doing the job for one replicate!
     * @param data
     * @return a XYSeries
     */
    private XYSeries generateDensityFunction(double[] data) {
        //use KDE to estimate density function for dataset data
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
     * for a condition selected this method shows in one plot the estimated density functions for each replicate (=well)
     * This is doing the job for one condition (all replicates)
     */
    public void showRawDataDensityFunction() {
        JFreeChart densityChart = showDensityFunction(getDataFromTableModel(), "Kernel Density Estimator");
        densityChartPanel.setChart(densityChart);
        dataAnalysisController.getDataAnalysisPanel().getDensityChartParentPanel().add(densityChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
    }

    /**
     * for a condition selected this method shows density values for corrected distributions
     */
    public void showCorrectedDataDensityFunction() {
        //compute first corrected data (no outliers)
        double[][] correctedData = new double[getDataFromTableModel().length][];
        for (int i = 0; i < getDataFromTableModel().length; i++) {
            double[] correctedValues = outliersHandler.handleOutliers(getDataFromTableModel()[i]).get(1);
            correctedData[i] = correctedValues;
        }
        JFreeChart correctedDensityChart = showDensityFunction(correctedData, "Outliers Correction");
        correctedDensityChartPanel.setChart(correctedDensityChart);
        dataAnalysisController.getDataAnalysisPanel().getCorrectedDensityChartParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
    }

    /**
     * show Density function for each 2D array of double
     * @param data - 2D array of double
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
     * Show Area for a certain condition selected
     */
    public void showArea() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        double[][] dataFromTableModel = getDataFromTableModel();
        double[] xValues = areaCalculator.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval());
        for (int columnIndex = 0; columnIndex < dataFromTableModel.length; columnIndex++) {
            double[] yValues = dataFromTableModel[columnIndex];
            XYSeries xySeries = generateXYSeriesArea(xValues, yValues);
            xySeries.setKey("Rep " + (columnIndex + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart areaChart = ChartFactory.createXYLineChart("Area", "Time Frame", "Area", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        areaChart.getXYPlot().setBackgroundPaint(Color.white);
        XYItemRenderer renderer = areaChart.getXYPlot().getRenderer();
        BasicStroke wideLine = new BasicStroke(1.3f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
        }
        areaChartPanel.setChart(areaChart);
        dataAnalysisController.getDataAnalysisPanel().getAreaChartPanelParentPanel().add(areaChartPanel, gridBagConstraints);
        dataAnalysisController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
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
     * get primitive data from the table model 
     * @return 
     */
    private double[][] getDataFromTableModel() {
        TableModel model = dataTable.getModel();
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
     * TableModel for the NormalizedArea data
     */
    private class NormalizedAreaTableModel extends DataTableModel {

        public NormalizedAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeNormalizedArea array into data array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = areaCalculator.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval())[i];
                //fill in all the other columns
                //arraycopy(Object src,  int  srcPos, Object dest, int destPos, int length)
                System.arraycopy(areaCalculator.computeNormalizedArea(data)[i], 1, data[i], 1, data[i].length - 1);
            }
        }
    }

    /**
     * Table Model for DeltaArea data
     */
    private class DeltaAreaTableModel extends DataTableModel {

        public DeltaAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeNormalizedArea array into data array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = areaCalculator.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval())[i];
                System.arraycopy(areaCalculator.computeDeltaArea(data)[i], 1, data[i], 1, data[i].length - 1);
            }
        }
    }

    /**
     * Table Model for AreaIncrease data
     */
    private class AreaIncreaseTableModel extends DataTableModel {

        public AreaIncreaseTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeAreaIncrease array into data array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = areaCalculator.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval())[i];
                System.arraycopy(areaCalculator.computeAreaIncrease(data, dataAnalysisController.getSelectedCondition())[i], 1, data[i], 1, data[i].length - 1);

            }
        }
    }

    /**
     * Table Model for CorrectedArea data
     */
    private class CorrectedAreaTableModel extends DataTableModel {

        public CorrectedAreaTableModel(PlateCondition plateCondition, int numberOfRows) {
            super(plateCondition, numberOfRows);
            insertRawData();
        }

        @Override
        protected final void insertRawData() {
            //copy the content of computeAreaIncrease array into data array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = areaCalculator.computeTimeFrames(dataAnalysisController.getExperiment().getExperimentInterval())[i];
                System.arraycopy(areaCalculator.normalizeCorrectedArea(data, dataAnalysisController.getSelectedCondition())[i], 1, data[i], 1, data[i].length - 1);
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
}
