/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.list;

import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.gui.view.icon.LineIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Class to render the tracks in a list for the plotting.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class PlottedTracksListRenderer extends DefaultListCellRenderer {

    private final List<TrackDataHolder> trackDataHolders;

    /**
     * Constructor: takes a list of track data holders
     *
     * @param trackDataHolders: the tracks to plot (and thus to be rendered on
     * the list)
     */
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
        Color color = GuiUtils.getAvailableColors()[indexOfColor];
        // set a new line icon
        setIcon(new LineIcon(10, 15, color));
        return this;
    }
}
