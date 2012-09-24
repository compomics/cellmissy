/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.analysis.DataTableModel;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.analysis.impl.NormalKernelDensityEstimator;
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
 *
 * @author Paola Masuzzo
 */
@Controller("bulkCellAnalysisPanelController")
public class BulkCellAnalysisPanelController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable dataTable;
    //view
    private ChartPanel densityChartPanel;
    private ChartPanel correctedDensityChartPanel;
    //parent controller
    @Autowired
    private DataAnalysisPanelController dataAnalysisPanelController;
    //child controllers
    //services
    @Autowired
    private KernelDensityEstimator normalKernelDensityEstimator;
    @Autowired
    private OutliersHandler outliersHandler;
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

    /**
     * public methods and classes
     */
    /**
     * show table with TimeSteps results from CellMia analysis
     * this is populating the JTable in the ResultsImporter Panel
     */
    public void showTimeSteps() {
        //make the TimeStepsTable non selectable
        dataAnalysisPanelController.getDataAnalysisPanel().getTimeStepsTable().setFocusable(false);
        dataAnalysisPanelController.getDataAnalysisPanel().getTimeStepsTable().setRowSelectionAllowed(false);
        //table binding
        timeStepsTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, timeStepBindingList, dataAnalysisPanelController.getDataAnalysisPanel().getTimeStepsTable());
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
        dataTable.setModel(new DeltaAreaTableModel(plateCondition, dataAnalysisPanelController.getExperiment().getTimeFrames()));
    }

    /**
     * for each replicate (well) of a certain selected condition, show increase in Area (in %), close to time frames
     * @param plateCondition 
     */
    public void setAreaIncreaseTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new data is passed to the model
        dataTable.setModel(new AreaIncreaseTableModel(plateCondition, dataAnalysisPanelController.getExperiment().getTimeFrames()));
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
        dataTable.setModel(new NormalizedAreaTableModel(plateCondition, dataAnalysisPanelController.getExperiment().getTimeFrames()));
    }

    /**
     * private methods and classes
     */
    /**
     * compute Normalized Area
     * @param data
     * @return a 2D array of double values
     */
    private Double[][] computeNormalizedArea(Double[][] data) {
        int timeFrames = dataAnalysisPanelController.getExperiment().getTimeFrames();
        int counter = 0;
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            if (timeStepBindingList.get(columnIndex).getArea() != 0) {
                for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                    int index = (counter / timeFrames) * timeFrames;
                    if (timeStepBindingList.get(counter).getArea() - timeStepBindingList.get(index).getArea() >= 0) {
                        data[rowIndex][columnIndex] = roundTwoDecimals(timeStepBindingList.get(counter).getArea() - timeStepBindingList.get(index).getArea());
                    } else {
                        data[rowIndex][columnIndex] = null;
                    }
                    counter++;
                }
            }
        }
        return data;
    }

    /**
     * compute Delta Area values (increments from one time frame to the following one)
     * @param data
     * @return a 2D array of double values
     */
    private Double[][] computeDeltaArea(Double[][] data) {
        int counter = 0;
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (timeStepBindingList.get(counter).getTimeStepSequence() != 0 && timeStepBindingList.get(counter).getArea() != 0) {
                    data[rowIndex][columnIndex] = roundTwoDecimals(timeStepBindingList.get(counter).getArea() - timeStepBindingList.get(counter - 1).getArea());
                }
                counter++;
            }
        }
        return data;
    }

    /**
     * Compute %Area increase (these values are used for density plots and area correction for outliers)
     * @param data
     * @return a 2D array of double arrays
     */
    private Double[][] computeAreaIncrease(Double[][] data) {
        int counter = 0;
        Double[][] newArray = new Double[dataAnalysisPanelController.getExperiment().getTimeFrames()][dataAnalysisPanelController.getSelectedCondition().getWellCollection().size() + 1];
        Double[][] computeDeltaArea = computeDeltaArea(newArray);
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (timeStepBindingList.get(counter).getTimeStepSequence() != 0) {
                    Double deltaArea = computeDeltaArea[rowIndex][columnIndex];
                    if (deltaArea != null) {
                        data[rowIndex][columnIndex] = roundTwoDecimals(deltaArea / timeStepBindingList.get(counter - 1).getArea() * 100);
                    }
                }
                counter++;
            }
        }
        return data;
    }

    /**
     * compute time frames from step sequence
     * @return an array of integers
     */
    private double[] computeTimeFrames() {

        double[] timeFrames;
        timeFrames = new double[dataAnalysisPanelController.getExperiment().getTimeFrames()];
        for (int i = 0; i < dataAnalysisPanelController.getExperiment().getTimeFrames(); i++) {
            Double timeFrame = timeStepBindingList.get(i).getTimeStepSequence() * dataAnalysisPanelController.getExperiment().getExperimentInterval();
            int intValue = timeFrame.intValue();
            timeFrames[i] = intValue;
        }
        return timeFrames;
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
        //row selection must be false && column selection true to be able to select through columns
        dataTable.setColumnSelectionAllowed(true);
        dataTable.setRowSelectionAllowed(false);
        dataAnalysisPanelController.getDataAnalysisPanel().getDataTablePanel().add(scrollPane);
        //init timeStepsBindingList
        timeStepBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
        //init chart panels
        densityChartPanel = new ChartPanel(null);
        densityChartPanel.setOpaque(false);
        correctedDensityChartPanel = new ChartPanel(null);
        correctedDensityChartPanel.setOpaque(false);
    }

    /**
     * For each set of data points estimate density function and return a XY series for the plot
     * This is doing the job for one replicate!
     * @param data
     * @return a XYSeries
     */
    private XYSeries generateDensityFunction(double[] data) {
        //use KDE to estimate density function for dataset data
        List<double[]> estimateDensityFunction = normalKernelDensityEstimator.estimateDensityFunction(data);
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
    public void showDensityFunction() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        //generate density function for each replicate and add the series to the seriescollection    
        for (int i = 0; i < getDataFromTableModel().length; i++) {
            XYSeries xySeries = generateDensityFunction(getDataFromTableModel()[i]);
            xySeries.setKey("Rep " + (i + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        //create only one densityChart and use it to set the densityChart of the densityChart panel
        JFreeChart densityChart = ChartFactory.createXYLineChart("Kernel Density Estimation", "% increase (Area)", "Density", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
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
        BasicStroke wideLine = new BasicStroke(1.5f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
        }
        densityChartPanel.setChart(densityChart);
        dataAnalysisPanelController.getDataAnalysisPanel().getDensityChartParentPanel().add(densityChartPanel, gridBagConstraints);
        dataAnalysisPanelController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
    }

    public void showCorrectedDensityFunction() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int i = 0; i < getDataFromTableModel().length; i++) {
            double[] correctedValues = outliersHandler.handleOutliers(getDataFromTableModel()[i]).get(1);
            XYSeries xySeries = generateDensityFunction(correctedValues);
            xySeries.setKey("Rep " + (i + 1));
            xySeriesCollection.addSeries(xySeries);
        }

        //create only one densityChart and use it to set the densityChart of the densityChart panel
        JFreeChart densityChart = ChartFactory.createXYLineChart("Outliers Correction", "% increase (Area)", "Density", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
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
        BasicStroke wideLine = new BasicStroke(1.5f);
        for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, wideLine);
        }
        correctedDensityChartPanel.setChart(densityChart);
        dataAnalysisPanelController.getDataAnalysisPanel().getCorrectedDensityChartParentPanel().add(correctedDensityChartPanel, gridBagConstraints);
        dataAnalysisPanelController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
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

        double[][] tableData = new double[columnCount - 1][rowCount - 1];
        for (int i = 1; i < columnCount; i++) {
            for (int j = 1; j < rowCount; j++) {
                if (model.getValueAt(j, i) != null) {
                    tableData[i - 1][j - 1] = (double) model.getValueAt(j, i);
                }
            }
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
                data[i][0] = computeTimeFrames()[i];
                //fill in all the other columns
                //arraycopy(Object src,  int  srcPos, Object dest, int destPos, int length)
                System.arraycopy(computeNormalizedArea(data)[i], 1, data[i], 1, data[i].length - 1);
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
                data[i][0] = computeTimeFrames()[i];
                System.arraycopy(computeDeltaArea(data)[i], 1, data[i], 1, data[i].length - 1);
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
                data[i][0] = computeTimeFrames()[i];
                System.arraycopy(computeAreaIncrease(data)[i], 1, data[i], 1, data[i].length - 1);
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

    /**
     * compute Area Regression: 
     * @return a SimpleRegression Class : Compute summary statistics for a list of double values
     */
    //round double to 2 decimals
    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
