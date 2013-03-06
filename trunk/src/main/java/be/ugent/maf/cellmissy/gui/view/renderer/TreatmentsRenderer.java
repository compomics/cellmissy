/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.Treatment;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Renderer for treatments list
 * @author Paola Masuzzo
 */
public class TreatmentsRenderer extends DefaultListCellRenderer {

    public TreatmentsRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Treatment treatment = (Treatment) value;
        setText(treatment.getTreatmentType().getName());
        return this;
    }
}
