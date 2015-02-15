/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class CheckBoxesConditionsTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private boolean[] checkBoxes;
    private final Map<PlateCondition, Boolean> map;

    /**
     * Constructor.
     *
     * @param map
     */
    public CheckBoxesConditionsTableModel(Map<PlateCondition, Boolean> map) {
        this.map = map;
        initTable();
    }


    public boolean[] getCheckBoxes() {
        return checkBoxes;
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
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        // If we have the second column
        // return checkbox
        if (columnIndex == 1) {
            result = checkBoxes[rowIndex];
        } else {
            result = data[rowIndex][columnIndex];
        }
        return result;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        checkBoxes[rowIndex] = (boolean) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Initialize table with data.
     */
    private void initTable() {
        checkBoxes = new boolean[map.size()];
        columnNames = new String[2];
        columnNames[0] = " ";
        columnNames[1] = " ";
        data = new Object[map.size()][2];
        List<Boolean> list = new ArrayList<>();

        Set<PlateCondition> keySet = map.keySet();
        for (PlateCondition plateCondition : keySet) {
            Boolean checkBox = map.get(plateCondition);
            list.add(checkBox);
        }

        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Condition " + (rowIndex + 1);
            checkBoxes[rowIndex] = list.get(rowIndex);
        }
    }
}
