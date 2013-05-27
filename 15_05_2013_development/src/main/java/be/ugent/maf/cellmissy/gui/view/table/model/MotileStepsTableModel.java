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
public class MotileStepsTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];

    /**
     * Constructor
     *
     * @param dataStructure
     * @param velocitiesVector
     * @param motileSteps
     */
    public MotileStepsTableModel(Object[][] dataStructure, Object[] velocitiesVector, Object[] motileSteps) {
        initTable(dataStructure, velocitiesVector, motileSteps);
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
    private void initTable(Object[][] dataStructure, Object[] velocitiesVector, Object[] motileSteps) {
        columnNames = new String[]{"well", "track", "time index", "velocity (pixels)", "motile step?"};
        int firstLength = dataStructure[0].length;
        int secondLength = 1;
        int thirdLength = 1;
        int totalLength = firstLength + secondLength + thirdLength;

        data = new Object[dataStructure.length][totalLength];

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                if (col < firstLength) {
                    data[row][col] = dataStructure[row][col];
                } else if (col == firstLength) {
                    data[row][col] = velocitiesVector[row];
                } else {
                    data[row][col] = motileSteps[row];
                }
            }
        }
    }
}
