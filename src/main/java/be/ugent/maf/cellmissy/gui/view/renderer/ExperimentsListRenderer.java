/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.Experiment;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo
 */
public class ExperimentsListRenderer extends DefaultListCellRenderer {

    private boolean selectable;
    /*
     * Constructor: takes a boolean to set the focusable of the cell
     */

    public ExperimentsListRenderer(boolean selectable) {
        this.selectable = selectable;
        setOpaque(true);
    }

    //Overrides method from the DefaultListCellRenderer
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (selectable) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else {
            super.getListCellRendererComponent(list, value, index, false, false);
        }
        Experiment experiment = (Experiment) value;
        setText(experiment.toString() + ", " + experiment.getExperimentStatus());
        return this;
    }
}
