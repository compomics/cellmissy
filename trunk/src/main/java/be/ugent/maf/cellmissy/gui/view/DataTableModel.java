/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This class extends AbstractTableModel
 * @author Paola Masuzzo
 */
public class DataTableModel extends AbstractTableModel {

    protected PlateCondition plateCondition;
    protected String columnNames[];
    protected Double[][] data;

    /**
     * constructor
     * @param plateCondition
     * @param dataToShow 
     * @param firstColumn 
     */
    public DataTableModel(PlateCondition plateCondition, Double[][] dataToShow, double[] firstColumn) {
        this.plateCondition = plateCondition;
        initTable(firstColumn, dataToShow);
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

    /**
     * define structure for table
     */
    private void initTable(double[] firstColumn, Double[][] dataToShow) {
        //2D array of double (dimension: time frames * wellList +1)
        data = new Double[dataToShow.length][dataToShow[0].length + 1];
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
        for (int i = 0; i < data.length; i++) {
            //fill in first column
            data[i][0] = firstColumn[i];
            System.arraycopy(dataToShow[i], 0, data[i], 1, data[i].length - 1);
        }

    }
}
