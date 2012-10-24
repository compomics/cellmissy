/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo
 */
public class EuclideanDistancesTableModel extends AbstractTableModel {

    public EuclideanDistancesTableModel(Double[][] dataToShow) {
        initTable(dataToShow);
    }
    private Object[][] data;
    private String columnNames[];

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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == columnNames.length - 1) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == columnNames.length - 1) {
            return true;
        }
        return false;
    }

    //initialize the table with data
    private void initTable(Double[][] dataToShow) {
        columnNames = new String[dataToShow.length + 2];
        columnNames[0] = "";
        columnNames[columnNames.length - 1] = "";
        for (int i = 1; i < columnNames.length - 1; i++) {
            columnNames[i] = "" + i;
        }

        data = new Object[dataToShow.length][columnNames.length];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "" + (rowIndex + 1);
            //////////////////////////////////////////////////////
            data[rowIndex][columnNames.length - 1] = Boolean.TRUE;
            for (int columnIndex = 1; columnIndex < data.length + 1; columnIndex++) {
                data[rowIndex][columnIndex] = dataToShow[rowIndex][columnIndex - 1];
            }
        }
    }
}
