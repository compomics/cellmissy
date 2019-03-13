/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Paola
 */
public class SingleCellConditionDataTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private final List<SingleCellConditionDataHolder> conditions;

    public SingleCellConditionDataTableModel(List<SingleCellConditionDataHolder> conditions) {
        this.conditions = conditions;
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
     * define structure for table
     */
    private void initTable() {
        columnNames = new String[]{"condition", "mean speed (µm/min)", "median speed (µm/min)", "mean direct", "median direct",
            "mean turn angle (deg)", "median turn angle (deg)"};

        data = new Object[conditions.size()][columnNames.length];

        for (int row = 0; row < data.length; row++) {

            data[row][0] = conditions.get(row).getPlateCondition();
            data[row][1] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditions.get(row).getTrackSpeedsVector()))));
            data[row][2] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditions.get(row).getTrackSpeedsVector()))));
            data[row][3] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(AnalysisUtils.excludeNaNvalues(
                            conditions.get(row).getEndPointDirectionalityRatios())))));
            data[row][4] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditions.get(row).getEndPointDirectionalityRatios()))));
            data[row][5] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMean(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditions.get(row).getTurningAnglesVector()))));
            data[row][6] = AnalysisUtils.roundThreeDecimals(
                    AnalysisUtils.computeMedian(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(conditions.get(row).getTurningAnglesVector()))));
        }
    }
}
