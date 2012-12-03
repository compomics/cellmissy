/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo
 */
public class ConditionsListRenderer extends DefaultListCellRenderer {

    // Plate Conditions List
    private List<PlateCondition> plateConditionList;

    /**
     * Constructor
     * @param plateConditionList 
     */
    public ConditionsListRenderer(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
        setOpaque(true);
        setIconTextGap(10);
    }

    /**
     * Override Component Method
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return this class
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int conditionIndex = plateConditionList.indexOf((PlateCondition) value);
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[conditionIndex + 1]));
        return this;
    }
}
