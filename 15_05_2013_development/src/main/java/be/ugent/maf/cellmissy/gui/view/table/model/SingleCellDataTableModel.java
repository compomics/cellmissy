/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class SingleCellDataTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];

    /**
     * Constructor
     *
     * @param dataToAdd
     */
    public SingleCellDataTableModel(Object[][] fixedData, Object[][] dataToAdd, String columnNames[]) {
        this.columnNames = columnNames;
        initTable(fixedData, dataToAdd);
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
    private void initTable(Object[][] fixedData, Object[][] dataToAdd) {
        int firstLength = fixedData[0].length;
        int secondLength = dataToAdd[0].length;
        int totalLength = firstLength + secondLength;

        data = new Object[fixedData.length][totalLength];

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (col < firstLength) {
                    data[row][col] = fixedData[row][col];
                } else {
                    data[row][col] = dataToAdd[row][col - firstLength];
                }
            }
        }
    }
}
