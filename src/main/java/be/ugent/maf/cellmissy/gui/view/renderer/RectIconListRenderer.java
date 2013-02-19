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
 * This is rendering a list of rectangular icons with number of replicates for each condition
 * @author Paola Masuzzo
 */
public class RectIconListRenderer extends DefaultListCellRenderer {

    private List<PlateCondition> plateConditionList;
    private List<Integer> numberOfReplicates;

    /**
     * Constructor
     * @param plateConditionList 
     * @param numberOfReplicates 
     */
    public RectIconListRenderer(List<PlateCondition> plateConditionList, List<Integer> numberOfReplicates) {
        this.plateConditionList = plateConditionList;
        this.numberOfReplicates = numberOfReplicates;
        setOpaque(true);
        setIconTextGap(10);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        int conditionIndex = plateConditionList.indexOf((PlateCondition) value);
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[conditionIndex + 1]));
        setText("N = " + numberOfReplicates.get(conditionIndex));
        return this;
    }
}
