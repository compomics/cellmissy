/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 *
 * @author Paola
 */
public class SingleCellStatSummaryTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private final SingleCellAnalysisGroup singleCellAnalysisGroup;

    /**
     * Constructor
     *
     * @param singleCellAnalysisGroup
     */
    public SingleCellStatSummaryTableModel(SingleCellAnalysisGroup singleCellAnalysisGroup) {
        this.singleCellAnalysisGroup = singleCellAnalysisGroup;
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
     * Initialize table
     */
    private void initTable() {
        // list of summaries from the analysis group: number of rows
        List<StatisticalSummary> statisticalSummaries = singleCellAnalysisGroup.getStatisticalSummaries();
        int size = statisticalSummaries.size();
        // columns: 1 + 6 for statistical numbers
        columnNames = new String[7];
        columnNames[0] = "";
        columnNames[1] = "Max";
        columnNames[2] = "Min";
        columnNames[3] = "Mean";
        columnNames[4] = "N";
        columnNames[5] = "SD";
        columnNames[6] = "Variance";
        singleCellAnalysisGroup.getConditionDataHolders();
        data = new Object[size][columnNames.length];
        // fill in data
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Cond " + (singleCellAnalysisGroup.getConditionDataHolders().get(rowIndex).getPlateCondition());
            // summary for a row
            StatisticalSummary statisticalSummary = statisticalSummaries.get(rowIndex);
            // distribute statistical objects per columns
            data[rowIndex][1] = statisticalSummary.getMax();
            data[rowIndex][2] = statisticalSummary.getMin();
            data[rowIndex][3] = statisticalSummary.getMean();
            data[rowIndex][4] = statisticalSummary.getN();
            data[rowIndex][5] = statisticalSummary.getStandardDeviation();
            data[rowIndex][6] = statisticalSummary.getVariance();

        }
    }
}
