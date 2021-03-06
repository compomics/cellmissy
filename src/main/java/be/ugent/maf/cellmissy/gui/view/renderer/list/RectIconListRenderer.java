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
 * This is rendering a list of rectangular icons with number of replicates for
 * each condition.
 *
 * @author Paola Masuzzo
 */
public class RectIconListRenderer extends DefaultListCellRenderer {

    private final List<PlateCondition> plateConditionList;
    private final List<Integer> numberOfReplicates;

    /**
     * Constructor, needs a list of plate conditions, together with number of
     * replicates for each condition.
     *
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
        int lenght = GuiUtils.getAvailableColors().length;
        int conditionIndex = plateConditionList.indexOf(value);
        int indexOfColor = conditionIndex % lenght;
        setIcon(new RectIcon(GuiUtils.getAvailableColors()[indexOfColor]));
        setText("N = " + numberOfReplicates.get(conditionIndex));
        return this;
    }
}
