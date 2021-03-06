/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.list;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * This renderer is used in the analysis step Conditions were already set up; we
 * click on a condition and fetch data to analyse.
 *
 * @author Paola Masuzzo
 */
public class ConditionsAnalysisListRenderer extends DefaultListCellRenderer {

    // Plate Conditions List
    private final List<PlateCondition> plateConditionList;

    /**
     * Constructor, takes the list of plate conditions
     *
     * @param plateConditionList
     */
    public ConditionsAnalysisListRenderer(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
        setOpaque(true);
        // set the gap between the icon and the text on the list
        setIconTextGap(10);
    }

    /**
     * Override Component Method
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return this class
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int conditionIndex = plateConditionList.indexOf(value);
        int lenght = GuiUtils.getAvailableColors().length;
        int indexOfColor = conditionIndex % lenght;
        // set a new rectangular icon
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[indexOfColor]));
        return this;
    }
}
