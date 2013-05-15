/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renderer for the ex
 *
 * @author paola
 */
public class ExperimentsListRenderer extends DefaultListCellRenderer {

    private User currentUser;

    public ExperimentsListRenderer(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // get the experiment form the list
        Experiment experiment = (Experiment) value;
        // if the current user has a standard role, check if he has rights to the experiment
        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            if (experiment.getUser().equals(currentUser)) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } else {
                // otherwise, set to false the selectable and the focusable
                super.getListCellRendererComponent(list, value, index, false, false);
                this.setEnabled(false);
            }
        } else if (currentUser.getRole().equals(Role.ADMIN_USER)) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        // what we'll see in the list is the number of the experiment, followed by its status
        setText(experiment.toString() + ", " + experiment.getExperimentStatus());
        return this;
    }
}
