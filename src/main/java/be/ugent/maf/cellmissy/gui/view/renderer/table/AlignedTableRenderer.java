/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer with alignment specified.
 *
 * @author Paola Masuzzo
 */
public class AlignedTableRenderer extends DefaultTableCellRenderer {

    private int alignment;

    /**
     * Constructor
     *
     * @param alignment
     */
    public AlignedTableRenderer(int alignment) {
        this.alignment = alignment;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setValue(value);
        setHorizontalAlignment(alignment);
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}
