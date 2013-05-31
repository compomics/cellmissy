/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.entity.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.view.icon.LineIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class PlottedTracksListRenderer extends DefaultListCellRenderer {

    private List<TrackDataHolder> trackDataHolders;

    public PlottedTracksListRenderer(List<TrackDataHolder> trackDataHolders) {
        this.trackDataHolders = trackDataHolders;
        setOpaque(true);
        // set the gap between the icon and the text on the list
        setIconTextGap(10);
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
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int trackIndex = trackDataHolders.indexOf((TrackDataHolder) value);
        int lenght = GuiUtils.getAvailableColors().length;
        int indexOfColor = trackIndex % lenght;
        // set a new rectangular icon
        setIcon(new LineIcon(GuiUtils.getAvailableColors()[indexOfColor]));
        return this;
    }
}
