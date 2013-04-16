/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * This renderer is used in the loading step.
 * Conditions were set up, but we only need to display them, so JList is not focusable, either selectable.
 * @author Paola Masuzzo
 */
public class ConditionsLoadListRenderer extends DefaultListCellRenderer {
    // Plate Conditions List

    private List<PlateCondition> plateConditionList;

    /**
     * Constructor
     * @param plateConditionList 
     */
    public ConditionsLoadListRenderer(List<PlateCondition> plateConditionList) {
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
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, false, false);
        int conditionIndex = plateConditionList.indexOf((PlateCondition) value);
        int lenght = GuiUtils.getAvailableColors().length;
        int indexOfColor = conditionIndex % lenght;
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[indexOfColor]));
        return this;
    }
}
