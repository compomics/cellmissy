/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo
 */
public class PValuesTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private Double[][] pValues;

    /**
     * Constructor
     * @param pValues 
     */
    public PValuesTableModel(Double[][] pValues) {
        this.pValues = pValues;
        initTable();
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
     * Initialize matrix
     */
    private void initTable() {
        columnNames = new String[pValues.length + 1];
        columnNames[0] = "";
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "Cond" + i;
        }

        data = new Object[pValues.length][columnNames.length];
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Cond " + (rowIndex + 1);
            for (int columnIndex = 1; columnIndex < columnNames.length; columnIndex++) {
                data[rowIndex][columnIndex] = pValues[rowIndex][columnIndex - 1];
            }
        }
    }
}
