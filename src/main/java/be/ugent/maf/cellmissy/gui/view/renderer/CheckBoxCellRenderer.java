/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import javax.swing.JCheckBox;

/**
 * This renderer for the check box extends the component CheckBox.
 *
 * @author Paola Masuzzo
 */
public class CheckBoxCellRenderer extends JCheckBox {

    /**
     * Constructor: the JCheckBox is selected according to the boolean.
     *
     * @param isSelected
     * @param alignment
     */
    public CheckBoxCellRenderer(boolean isSelected, int alignment) {
        setSelected(isSelected);
        setHorizontalAlignment(alignment);
    }
}