/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *This renderer is only used in the setup step: 
 * Conditions still need to be designed, so only condition indexes are shown.
 * @author Paola Masuzzo
 */
public class ConditionsSetupListRenderer extends DefaultListCellRenderer {
    
    /*
     *constructor
     */
    public ConditionsSetupListRenderer() {
        setOpaque(true);
        setIconTextGap(10);
    }

    //Overrides method from the DefaultListCellRenderer
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        PlateCondition newCondition = (PlateCondition) value;
        setText(newCondition.getName());
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[((PlateCondition) value).getConditionIndex()]));
        if (isSelected) {
            setBackground(Color.lightGray);
        }
        return this;
    }
}
