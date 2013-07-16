/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ConditionsCheckBoxesTableModel extends AbstractTableModel {

    private Object[][] data;
    private boolean[] checkboxes;
    private List<PlateCondition> plateConditions;

    /**
     *
     * @param dataToShow
     * @param plateConditions
     */
    public ConditionsCheckBoxesTableModel(List<PlateCondition> plateConditions) {
        this.plateConditions = plateConditions;
        initTable();
    }

    public void setCheckboxes(boolean[] checkboxes) {
        this.checkboxes = checkboxes;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        // If we have the second column
        // return checkbox
        if (columnIndex == 1) {
            result = checkboxes[rowIndex];
        } else {
            result = data[rowIndex][columnIndex];
        }
        return result;
    }

    /**
     *
     */
    private void initTable() {
        data = new Object[plateConditions.size()][2];
        for (int rowIndex = 0; rowIndex < data[0].length; rowIndex++) {
            data[rowIndex][0] = "Condition " + (rowIndex + 1);
        }
    }
}
