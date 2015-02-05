/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class CheckBoxesGlobalViewsTableModel extends AbstractTableModel {

    private Object[][] data;
    private String columnNames[];
    private Boolean[][] checkBoxes;
    private final Map<String, Boolean[]> globalViewsMap;

    /**
     * Constructor.
     *
     * @param globalViews
     */
    public CheckBoxesGlobalViewsTableModel(Map<String, Boolean[]> globalViewsMap) {
        this.globalViewsMap = globalViewsMap;
        initTable();
    }

    public Boolean[][] getCheckBoxes() {
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
        // If we do not have the first column
        // return checkbox
        if (columnIndex != 0) {
            result = checkBoxes[rowIndex][columnIndex -1];
        } else {
            result = data[rowIndex][columnIndex];
        }
        return result;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        checkBoxes[rowIndex][columnIndex-1] = (boolean) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Initialize table with data.
     */
    private void initTable() {
        checkBoxes = new Boolean[globalViewsMap.size()][3];
        columnNames = new String[4];
        columnNames[0] = " ";
        columnNames[1] = "Points";
        columnNames[2] = "SEM";
        columnNames[3] = "Time Interval";
        data = new Object[globalViewsMap.size()][4];

        List<Boolean[]> list = new ArrayList<>();
        Set<String> keySet = globalViewsMap.keySet();
        for (String globalView : keySet) {
            Boolean[] get = globalViewsMap.get(globalView);
            list.add(get);
        }

        for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
            data[rowIndex][0] = "Global View " + (rowIndex + 1);
            checkBoxes[rowIndex] = list.get(rowIndex);
        }
    }
}
