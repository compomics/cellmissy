/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;
import be.ugent.maf.cellmissy.gui.view.icon.RectIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class AnalysisGroupListRenderer extends DefaultListCellRenderer {

    private List<PlateCondition> plateConditionList;

    public AnalysisGroupListRenderer(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
        setOpaque(true);
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
        AnalysisGroup analysisGroup = (AnalysisGroup) value;
        List<PlateCondition> plateConditions = analysisGroup.getPlateConditions();
        String conditionsName = "";
        for (PlateCondition plateCondition : plateConditions) {
            int conditionIndex = plateConditionList.indexOf(plateCondition);
            conditionsName = conditionsName.concat(", " + "C " + (conditionIndex + 1));
        }
        setText(analysisGroup.getGroupName() + "(" + conditionsName + ")");
        return this;
    }
}
