/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola
 */
public class SingleCellPValuesTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private final SingleCellAnalysisGroup singleCellAnalysisGroup;

    public SingleCellPValuesTableModel(SingleCellAnalysisGroup singleCellAnalysisGroup, boolean isAdjusted) {
        this.singleCellAnalysisGroup = singleCellAnalysisGroup;
        initTable(isAdjusted);
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
     * Initialize table
     */
    private void initTable(boolean adjusted) {
        Double[][] pValuesMatrix;
        //take data according to boolean: are p -values adjusted or not?
        if (!adjusted) {
            // p-values matrix of analysis group
            pValuesMatrix = singleCellAnalysisGroup.getpValuesMatrix();
        } else {
            pValuesMatrix = singleCellAnalysisGroup.getAdjustedPValuesMatrix();
        }

        // columns
        columnNames = new String[pValuesMatrix.length + 1];
        columnNames[0] = "";
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "Cond " + (singleCellAnalysisGroup.getConditionDataHolders().get(i - 1).getPlateCondition());
        }

        // data
        data = new Object[pValuesMatrix.length][columnNames.length];
        // fill in data
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Cond " + (singleCellAnalysisGroup.getConditionDataHolders().get(rowIndex).getPlateCondition());
            System.arraycopy(pValuesMatrix[rowIndex], 0, data[rowIndex], 1, columnNames.length - 1);
        }
    }
}
