/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo
 */
public class DistanceMatrixTableModel extends AbstractTableModel {

    private Object[][] data;
    private boolean[] checkboxOutliers;
    private String columnNames[];
    private boolean[][] outliers;
    //@todo: put this value in the properties file
    private static final double RATIO = 0.5;

    public DistanceMatrixTableModel(Double[][] dataToShow, boolean[][] outliers) {
        this.outliers = outliers;
        initTable(dataToShow);
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
        Object result = null;
        // If we have the last row (one beyond last real data)
        // return checkbox
        if (rowIndex == data.length - 1 && columnIndex != 0) {
            result = checkboxOutliers[columnIndex - 1];
        } else {
            result = data[rowIndex][columnIndex];
        }
        return result;

    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex == data.length - 1 && columnIndex != 0) {
            return true;
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex == data.length - 1) {
            getStatus(columnIndex)[rowIndex] = aValue;
        }
    }

    /**
     * Fill in Table with Data
     * @param dataToShow 
     */
    private void initTable(Double[][] dataToShow) {
        columnNames = new String[dataToShow.length + 1];
        columnNames[0] = "";
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "" + i;
        }

        checkboxOutliers = new boolean[dataToShow.length];
        data = new Object[dataToShow.length + 1][columnNames.length];

        for (int columnIndex = 1; columnIndex < data.length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length - 1; rowIndex++) {
                data[rowIndex][0] = "" + (rowIndex + 1);
                data[rowIndex][columnIndex] = dataToShow[rowIndex][columnIndex - 1];
            }
            if (getOutlierRatio(columnIndex - 1) >= RATIO) {
                checkboxOutliers[columnIndex - 1] = true;
            }
        }
    }

    /**
     * get Status of a check box
     * @param columnIndex
     * @return 
     */
    private Object[] getStatus(int columnIndex) {
        return (Object[]) data[columnIndex];
    }

    /**
     * Get the outlier Ratio per row: number of outliers divided by number of replicates
     * @param columnIndex
     * @return a double value for the ratio
     */
    private double getOutlierRatio(int columnIndex) {
        boolean[] outliersPerColumn = outliers[columnIndex];
        int count = 0;
        for (int i = 0; i < outliersPerColumn.length; i++) {
            if (outliersPerColumn[i]) {
                count++;
            }
        }
        return (double) count / outliersPerColumn.length;
    }
}
