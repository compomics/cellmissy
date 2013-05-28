/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import javax.swing.table.AbstractTableModel;

/**
 * Table Model for track coordinates data
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackCoordinatesTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];

    /**
     * Constructor
     *
     * @param dataStructure
     * @param coordinatesMatrix
     */
    public TrackCoordinatesTableModel(Object[][] dataStructure, Object[][] coordinatesMatrix) {
        initTable(dataStructure, coordinatesMatrix);
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
    private void initTable(Object[][] dataStructure, Object[][] coordinatesMatrix) {
        columnNames = new String[]{"well", "track", "time index", "x (µm)", "y (µm)"};
        int firstLength = dataStructure[0].length;
        int secondLength = coordinatesMatrix[0].length;
        int totalLength = firstLength + secondLength;

        data = new Object[dataStructure.length][totalLength];

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (col < firstLength) {
                    data[row][col] = dataStructure[row][col];
                } else {
                    data[row][col] = coordinatesMatrix[row][col - firstLength];
                }
            }
        }
    }
}
