/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

/**
 * A renderer does not handle events. If you want to detect a event from a cell that is being edited, you need to implement a TableCellEditor as well.
 * @author Paola Masuzzo
 */
public class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    protected JCheckBox checkBox;

    /**
     * Constructor; create a new check box for editing the cell and set alignment to right
     */
    public CheckBoxCellEditor() {
        checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Object getCellEditorValue() {
        return Boolean.valueOf(checkBox.isSelected());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // if value is true, select checkbox, else do nothing
        checkBox.setSelected((boolean) value);
        return checkBox;
    }

    //when a check box is selected or deselected, the correspondent technical replicate needs to be flagged as part or not of the dataset
    @Override
    public void actionPerformed(ActionEvent e) {
        
        /**
         * on click: 4 things 
         * update array(List) of booleans in distanceMatrix so it is not null anymore
         * put tabbed pane button Global View bold (tabbedpane.getButton(2).setFont(BOLD)) and inform it has to reload on global view click
         * 
         * 
         * getArrayList --> returns null --> if it does do standard behavior otherwise show image of not selected columns
         * 
         * for creating corrected area Distance Matrix and Area image --> first calculate distance matrix and afterwards getArrayList --> if returns null use all values (so people see outliers) otherwise only use the false boolean columns (because we had user interaction)
         * 
         * 
         * ArrayList booleanistrueornot? = null (in DistanceMatrix?)
         * when the user select a checkbox put booleanistrueornot as a filled arraylist --> then it is not null  anymore
         * 
         * 
         */
  
    }
}
