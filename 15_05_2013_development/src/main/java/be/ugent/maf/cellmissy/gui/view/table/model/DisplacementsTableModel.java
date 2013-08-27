/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import javax.swing.table.AbstractTableModel;

/**
 * Table model for the displacements data --- this could be extended to a more
 * general table model to keep data from track points.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class DisplacementsTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];

    /**
     * Constructor
     *
     * @param dataStructure: the fixed track point data information
     * @param velocitiesVector: the extra data vector we add to the table as
     * last column
     */
    public DisplacementsTableModel(Object[][] dataStructure, Object[] velocitiesVector) {
        initTable(dataStructure, velocitiesVector);
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
    private void initTable(Object[][] dataStructure, Object[] velocitiesVector) {
        columnNames = new String[]{"well", "track", "time index", "displacement (µm)"};
        int firstLength = dataStructure[0].length;
        int secondLength = 1;
        int totalLength = firstLength + secondLength;

        data = new Object[dataStructure.length][totalLength];

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (col < firstLength) {
                    data[row][col] = dataStructure[row][col];
                } else {
                    data[row][col] = velocitiesVector[row];
                }
            }
        }
    }
}
