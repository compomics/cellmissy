/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.table;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Paola
 */
public class FilterTrackTableRenderer extends DefaultTableCellRenderer {

//    private final Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> map;

//    public FilterTrackTableRenderer(Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> map) {
//        this.map = map;
//    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, false, false, row, column);

        if (value.equals(Boolean.TRUE)) {
            setForeground(new Color(34, 139, 34));
            setFont(new Font("Tahoma", Font.BOLD, 11));
        } else {
            setForeground(new Color(255, 51, 51));
            setFont(new Font("Tahoma", Font.PLAIN, 11));
        }

//        setValue(value);
//        setHorizontalAlignment(SwingConstants.RIGHT);
        return this;
    }

}
