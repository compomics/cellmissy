/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.DataTableModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.apache.commons.math.stat.regression.SimpleRegression;
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
import umontreal.iro.lecuyer.gof.KernelDensity;
import umontreal.iro.lecuyer.probdist.EmpiricalDist;
import umontreal.iro.lecuyer.probdist.NormalDist;

/**
 *
 * @author Paola Masuzzo
 */
public class BulkCellAnalysisPanelController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable dataTable;
    //view
    private ChartPanel chartPanel;
    //parent controller
    private DataAnalysisPanelController dataAnalysisPanelController;
    //child controllers
    //services
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor (parent controller)
     * @param dataAnalysisPanelController 
     */
    public BulkCellAnalysisPanelController(DataAnalysisPanelController dataAnalysisPanelController) {
        this.dataAnalysisPanelController = dataAnalysisPanelController;

        //init services
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        
        //init views
        initBulkCellAnalysisPanel();
        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);
    }

    /**
     * getters and setters
     * 
     */
    public ObservableList<TimeStep> getTimeStepBindingList() {
        return timeStepBindingList;
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
        //show JUMP or OK strings
//        for (int i = 1; i < dataTable.getColumnCount(); i++) {
//            //set Cell Renderer for each column of the table
//            dataTable.getColumnModel().getColumn(i).setCellRenderer(new AreaIncreaseRenderer(Double.parseDouble(dataAnalysisPanelController.getDataAnalysisPanel().getJumpThresholdTextField().getText())));
//        }
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
     * compute Normalized Area ==== TO BE REWRITTEN WITH NEW ARRAYS!!!
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
    }

    /**
     * For each set of data points estimate density function and return a XY series for the plot
     * @param data
     * @return a XYSeries
     */
    private XYSeries generateDensityFunction(double[] data) {
        //empirical distribution uses all obs stored in data, assumed to have been sorted in increasing mumerical order
        //sort double values
        Arrays.sort(data);
        //empirical distribution based on data to compute density
        EmpiricalDist empiricalDist = new EmpiricalDist(data);
        //create a new KernelDensityEstimator Object with current empirical distribution
        KernelDensityEstimator kernelDensityEstimator = new KernelDensityEstimator(empiricalDist);
        //vector of values at which the density estimate needs to be computed
        double[] randomSamples = kernelDensityEstimator.drawRandomSample();
        //use normal default kernel
        NormalDist kern = new NormalDist();
        double datasetSize = (double) data.length;
        //calculate optimal bandwidth with the (ROBUST) Silverman's ‘rule of thumb’ (Scott Variation uses factor = 1.06)
        double bandWidth = 0.99 * Math.min(empiricalDist.getSampleStandardDeviation(), (empiricalDist.getInterQuartileRange() / 1.34)) / Math.pow(datasetSize, 0.2);
        //actually estimate density and store values in a vector
        double[] estimatedDensityValues = KernelDensity.computeDensity(empiricalDist, kern, bandWidth, randomSamples);

        //create dataset for plot
        //XYSeries is by default ordered in ascending values, set second parameter of costructor to false
        XYSeries series = new XYSeries("", false);
        for (int i = 0; i < estimatedDensityValues.length; i++) {
            double x = randomSamples[i];
            double y = estimatedDensityValues[i];
            series.add(x, y);
        }
        return series;
    }

    /**
     * for a condition selected this method shows in one plot the estimated density functions for each replicate (=well)
     */
    public void showDensityFunction() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        //generate density function for each replicate and add the series to the seriescollection    
        for (int i = 0; i < getDataFromTableModel().length; i++) {
            XYSeries xySeries = generateDensityFunction(getDataFromTableModel()[i]);
            xySeries.setKey("Rep " + (i + 1));
            xySeriesCollection.addSeries(xySeries);
        }
        JFreeChart chart = ChartFactory.createXYLineChart("Kernel Density Estimation", "% increase (Area)", "Density", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        //XYplot
        XYPlot xYPlot = chart.getXYPlot();
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
        chartPanel.setChart(chart);

        dataAnalysisPanelController.getDataAnalysisPanel().getGraphicsParentPanel().add(chartPanel, gridBagConstraints);
        dataAnalysisPanelController.getDataAnalysisPanel().getGraphicsParentPanel().repaint();
    }

    /**
     * compute Max value of Y for plot
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

        private double thresholdForJump;

        public AreaIncreaseRenderer(double thresholdForJump) {
            this.thresholdForJump = thresholdForJump;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(dataTable, value, false, false, row, column);

            Double areaIncrease = (Double) value;

            if (areaIncrease != null) {
                if (areaIncrease < thresholdForJump) {
                    setValue(areaIncrease + " (OK)");
                    setBackground(null);
                } else {
                    setValue(areaIncrease + " (JUMP)");
                    setBackground(Color.red);
                }
            }
            setOpaque(true);

            return this;
        }
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
     * compute Area Regression: 
     * @return a SimpleRegression Class : Compute summary statistics for a list of double values
     */
    private SimpleRegression computeAreaRegression(double[][] data) {

        SimpleRegression areaRegression = new SimpleRegression();

        data = new double[dataAnalysisPanelController.getExperiment().getTimeFrames()][];
        for (int i = 0; i < data.length; i++) {
            // data[columnIndex] = computeNormalizedArea();
        }
        areaRegression.addData(data);
        return areaRegression;
    }

    //round double to 2 decimals
    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
