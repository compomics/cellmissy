/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo
 */
public abstract class DataTableModel extends AbstractTableModel {

    protected PlateCondition plateCondition;
    protected int numberOfRows;
    protected String columnNames[];
    protected Double[][] data;

    public DataTableModel(PlateCondition plateCondition, int numberOfRows) {
        this.plateCondition = plateCondition;
        this.numberOfRows = numberOfRows;
        defineTableStructure();
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

    private void defineTableStructure() {
        //2D array of double (dimension: time frames * wellList +1)
        data = new Double[numberOfRows][plateCondition.getWellCollection().size() + 1];
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
    }

    protected abstract void insertRawData();
}
