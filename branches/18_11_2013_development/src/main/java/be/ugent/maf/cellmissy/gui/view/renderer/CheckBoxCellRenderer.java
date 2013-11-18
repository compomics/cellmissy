/*
 * To change this template, choose Tools | Templates
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
     */
    public CheckBoxCellRenderer(boolean isSelected, int alignment) {
        setSelected(isSelected);
        setHorizontalAlignment(alignment);
    }
}
