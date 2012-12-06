/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo
 */
public class GroupsListRenderer extends DefaultListCellRenderer {

    private List<AnalysisGroup> groupsList;

    /**
     * Constructor
     * @param groupsList 
     */
    public GroupsListRenderer(List<AnalysisGroup> groupsList) {
        this.groupsList = groupsList;
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        int listIndex = groupsList.indexOf((AnalysisGroup) value);
        setText("Group " + (listIndex + 1));
        return this;
    }
}
