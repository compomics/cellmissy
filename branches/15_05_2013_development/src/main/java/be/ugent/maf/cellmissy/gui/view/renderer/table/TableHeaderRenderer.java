/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for the JTable header
 *
 * @author Paola Masuzzo
 */
public class TableHeaderRenderer extends DefaultTableCellRenderer {

    public TableHeaderRenderer(int alignment) {
        setHorizontalAlignment(alignment);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, false, false, row, column);
        setBackground(new Color(176, 196, 222));
        setBorder(BorderFactory.createEmptyBorder());

        return this;
    }
}
