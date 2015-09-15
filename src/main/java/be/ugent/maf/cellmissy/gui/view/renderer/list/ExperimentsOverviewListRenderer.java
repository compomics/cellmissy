/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.list;

import be.ugent.maf.cellmissy.entity.Experiment;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renderer for the experiments list: takes a boolean in the constructor to set
 * the selectable option for the cell.
 *
 * @author Paola Masuzzo
 */
public class ExperimentsOverviewListRenderer extends DefaultListCellRenderer {

    private final boolean selectable;

    /**
     * Constructor
     *
     * @param selectable
     */
    public ExperimentsOverviewListRenderer(boolean selectable) {
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
        if (experiment != null) {
            // what we'll see in the list is the number of the experiment, followed by its status
            setText(experiment.toString() + ", " + experiment.getExperimentStatus());
        } else {
            setText("");
        }
        return this;
    }
}
