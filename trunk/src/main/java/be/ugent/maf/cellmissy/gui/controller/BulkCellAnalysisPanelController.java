/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola Masuzzo
 */
public class BulkCellAnalysisPanelController {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<TimeStep> timeStepBindingList;
    private JTableBinding timeStepsTableBinding;
    private JTable normalizedAreaTable;
    private JTable deltaAreaTable;
    //view
    //parent controller
    private DataAnalysisPanelController dataAnalysisPanelController;
    //child controllers
    //services

    /**
     * constructor (parent controller)
     * @param dataAnalysisPanelController 
     */
    public BulkCellAnalysisPanelController(DataAnalysisPanelController dataAnalysisPanelController) {
        this.dataAnalysisPanelController = dataAnalysisPanelController;

        //init services
        bindingGroup = new BindingGroup();
        initBulkCellAnalysisPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public ObservableList<TimeStep> getTimeStepBindingList() {
        return timeStepBindingList;
    }
    
    public JTable getDeltaAreaTable() {
        return deltaAreaTable;
    }

    public JTable getNormalizedAreaTable() {
        return normalizedAreaTable;
    }

    /**
     * public methods and classes
     */
    /**
     * show table with TimeSteps results from CellMia analysis
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
     * 
     */
    public void setDeltaAreaTableData(PlateCondition plateCondition) {
        //set model for the delta area Table
        //NOTE that each time a new condition is selected, new data is passed to the model
        deltaAreaTable.setModel(new DeltaAreaTableModel(plateCondition));
    }

    /**
     * for each replicate (well) of a certain condition, show normalized area values in a table, close to time frames
     */
    public void setNormalizedAreaTableData(PlateCondition plateCondition) {
        //set Model for the Normalized AreaTable
        //NOTE that each time a new condition is selected, new data is passed to the model
        normalizedAreaTable.setModel(new NormalizedAreaTableModel(plateCondition));
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
                if (timeStepBindingList.get(counter).getTimeStepSequence() != 0) {
                    data[rowIndex][columnIndex] = roundTwoDecimals(timeStepBindingList.get(counter).getArea() - timeStepBindingList.get(counter - 1).getArea());
                }
                counter++;
            }
        }
        return data;
    }

    /**
     * init main panel
     */
    private void initBulkCellAnalysisPanel() {
        //init Tables
        normalizedAreaTable = new JTable();
        deltaAreaTable = new JTable();
        //init timeStepsBindingList
        timeStepBindingList = ObservableCollections.observableList(new ArrayList<TimeStep>());
    }

    /**
     * TableModel for the NormalizedAreaTable
     */
    private class NormalizedAreaTableModel extends AbstractTableModel {

        private PlateCondition plateCondition;
        private String columnNames[];
        private Double[][] data;

        public NormalizedAreaTableModel(PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
            populateTable();
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        //fill in table with data
        private void populateTable() {
            List<Well> wellList = new ArrayList<>();
            wellList.addAll(plateCondition.getWellCollection());
            //the table needs one column for the time frames + one column for each replicate (each well)
            columnNames = new String[wellList.size() + 1];
            //first column name: Time Frames
            columnNames[0] = "time frame";
            //other columns names: coordinates of the well
            for (int i = 1; i < columnNames.length; i++) {
                columnNames[i] = "" + wellList.get(i - 1);
            }
            //2D array of double (dimension: time frames * wellList +1)
            data = new Double[dataAnalysisPanelController.getExperiment().getTimeFrames()][plateCondition.getWellCollection().size() + 1];

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
     * Table Model for the DeltaArea JTable
     */
    private class DeltaAreaTableModel extends AbstractTableModel {

        private PlateCondition plateCondition;
        private String columnNames[];
        private Double[][] data;

        public DeltaAreaTableModel(PlateCondition plateCondition) {
            this.plateCondition = plateCondition;
            populateTable();
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        //fill in table with data

        private void populateTable() {
            List<Well> wellList = new ArrayList<>();
            wellList.addAll(plateCondition.getWellCollection());
            //the table needs one column for the time frames + one column for each replicate (each well)
            columnNames = new String[wellList.size() + 1];
            //first column name: Time Frames
            columnNames[0] = "time frame";
            //other columns names: coordinates of the well
            for (int i = 1; i < columnNames.length; i++) {
                columnNames[i] = "" + wellList.get(i - 1);
            }
            //2D array of double (dimension: time frames * wellList +1)
            data = new Double[dataAnalysisPanelController.getExperiment().getTimeFrames()][plateCondition.getWellCollection().size() + 1];

            //copy the content of computeNormalizedArea array into data array
            for (int i = 0; i < data.length; i++) {
                //fill in first column
                data[i][0] = computeTimeFrames()[i];
                System.arraycopy(computeDeltaArea(data)[i], 1, data[i], 1, data[i].length - 1);
            }
        }
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
     * compute Area Regression: 
     * @return a SimpleRegression Class : Compute summary statistics for a list of double values
     */
    private SimpleRegression computeAreaRegression() {

        SimpleRegression areaRegression = new SimpleRegression();

        double[][] data = new double[dataAnalysisPanelController.getExperiment().getTimeFrames()][];
        for (int i = 0; i < data.length; i++) {
            //data[columnIndex] = computeNormalizedArea();
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
