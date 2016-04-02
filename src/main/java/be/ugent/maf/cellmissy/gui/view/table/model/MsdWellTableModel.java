/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import javax.swing.table.AbstractTableModel;

/**
 * A table model to show MSD values across wells.
 *
 * @author Paola
 */
public class MsdWellTableModel extends AbstractTableModel {

    private Object[][] data;
    private final String[] columnNames = {"well", "time-lag", "MSD"};

    /**
     * Constructor
     *
     * @param singleCellConditionDataHolder: the results holder from where to
     * get the track information
     */
    public MsdWellTableModel(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        initTable(singleCellConditionDataHolder);
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
     * Define the structure for the table.
     */
    private void initTable(SingleCellConditionDataHolder singleCellConditionDataHolder) {
        int dimension = 0;

        dimension = singleCellConditionDataHolder.getSingleCellWellDataHolders().stream().map((_item) -> singleCellConditionDataHolder.getMsdArray().length).reduce(dimension, Integer::sum);

        // 'dimension' rows and 3 columns
        data = new Object[dimension][3];

        int counter = 0;
        for (SingleCellWellDataHolder singleCellWellDataHolder : singleCellConditionDataHolder.getSingleCellWellDataHolders()) {
            double[][] temp = singleCellWellDataHolder.getMsdArray();
            for (int row = 0; row < temp.length; row++) {
                data[counter][0] = singleCellWellDataHolder.getWell().toString();
                data[counter][1] = temp[row][0];
                data[counter][2] = temp[row][1];
                counter++;
            }
        }
    }
}
