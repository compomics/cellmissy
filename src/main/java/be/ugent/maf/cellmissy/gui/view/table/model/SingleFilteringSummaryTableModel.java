/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola
 */
public class SingleFilteringSummaryTableModel extends AbstractTableModel {

    private final Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap;
    private String columnNames[];
    private Object[][] data;

    /**
     * Constructor.
     *
     * @param filteringMap
     */
    public SingleFilteringSummaryTableModel(Map<SingleCellConditionDataHolder, List<TrackDataHolder>> filteringMap) {
        this.filteringMap = filteringMap;
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

    private void initTable() {

        columnNames = new String[5];
        columnNames[0] = "condition";
        columnNames[1] = "nr tracks";
        columnNames[2] = "nr retained tracks";
        columnNames[3] = "% retained tracks";

        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>(filteringMap.keySet());
        data = new Object[conditionDataHolders.size()][columnNames.length];

        for (int i = 0; i < conditionDataHolders.size(); i++) {
            SingleCellConditionDataHolder conditionDataHolder = conditionDataHolders.get(i);
            data[i][0] = conditionDataHolder.getPlateCondition();
            data[i][1] = conditionDataHolder.getTrackDataHolders().size();
            data[i][2] = filteringMap.get(conditionDataHolder).size();
            data[i][3] = filteringMap.get(conditionDataHolder).size() * 100 / conditionDataHolder.getTrackDataHolders().size();
        }
    }
}
