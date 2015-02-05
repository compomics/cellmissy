/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class AnalysisGroupListRenderer extends DefaultListCellRenderer {

    private final List<PlateCondition> plateConditionList;

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
        AreaAnalysisGroup areaAnalysisGroup = (AreaAnalysisGroup) value;
        List<PlateCondition> plateConditions = areaAnalysisGroup.getPlateConditions();
        List<String> conditionsName = new ArrayList<>();
        for (PlateCondition plateCondition : plateConditions) {
            int conditionIndex = plateConditionList.indexOf(plateCondition);
            conditionsName.add("" + (conditionIndex + 1));
        }
        String join = StringUtils.join(conditionsName, ", ");
        setText(areaAnalysisGroup.getGroupName() + " (" + join + ")");
        return this;
    }
}
