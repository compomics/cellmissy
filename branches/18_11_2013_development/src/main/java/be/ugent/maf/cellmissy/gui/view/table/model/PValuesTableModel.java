/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table Model to show p - values in a pairwise test
 * @author Paola Masuzzo
 */
public class PValuesTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private AnalysisGroup analysisGroup;
    private List<PlateCondition> plateConditionList;

    /**
     * Constructor
     * @param analysisGroup
     * @param plateConditionList
     * @param isAdjusted  
     */
    public PValuesTableModel(AnalysisGroup analysisGroup, List<PlateCondition> plateConditionList, boolean isAdjusted) {
        this.analysisGroup = analysisGroup;
        this.plateConditionList = plateConditionList;
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
        Double[][] pValuesMatrix = null;
        //take data according to boolean: are p -values adjusted or not?
        if (!adjusted) {
            // p-values matrix of analysis group
            pValuesMatrix = analysisGroup.getpValuesMatrix();
        } else {
            pValuesMatrix = analysisGroup.getAdjustedPValuesMatrix();
        }

        // columns
        List<PlateCondition> plateConditions = analysisGroup.getPlateConditions();
        columnNames = new String[pValuesMatrix.length + 1];
        columnNames[0] = "";
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "Cond " + (plateConditionList.indexOf(plateConditions.get(i - 1)) + 1);
        }

        // data
        data = new Object[pValuesMatrix.length][columnNames.length];
        // fill in data
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Cond " + (plateConditionList.indexOf(plateConditions.get(rowIndex)) + 1);
            for (int columnIndex = 1; columnIndex < columnNames.length; columnIndex++) {
                data[rowIndex][columnIndex] = pValuesMatrix[rowIndex][columnIndex - 1];
            }
        }
    }
}
