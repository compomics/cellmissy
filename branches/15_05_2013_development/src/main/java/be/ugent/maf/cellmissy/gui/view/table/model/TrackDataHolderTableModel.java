/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This class extends AbstractTableModel. Table model to show data associated
 * with track holder objects.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataHolderTableModel extends AbstractTableModel {

    private String columnNames[];
    private Object[][] data;
    private TrackDataHolder trackDataHolder;

    /**
     * Constructor: takes a track data holder object needed to populate the
     * table model.
     *
     * @param trackDataHolder
     */
    public TrackDataHolderTableModel(TrackDataHolder trackDataHolder) {
        this.trackDataHolder = trackDataHolder;
        initTable();
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * Define structure for the table
     */
    private void initTable() {
        // column names
        columnNames = new String[2];
        columnNames[0] = "feature";
        columnNames[1] = "value";
        // first column: features names
        List<String> featuresNames = getFeaturesNames();
        data = new Object[featuresNames.size()][2];
        for (int i = 0; i < featuresNames.size(); i++) {
            data[i][0] = featuresNames.get(i);
        }
        // second column: track data
        Track track = trackDataHolder.getTrack();
        data[0][1] = track.getWellHasImagingType().getWell();
        data[1][1] = track.getTrackNumber();
        data[2][1] = track.getTrackLength();
        data[3][1] = trackDataHolder.getTimeIndexes().length;
        data[4][1] = trackDataHolder.getDuration();
        data[5][1] = trackDataHolder.getxMin();
        data[6][1] = trackDataHolder.getxMax();
        data[7][1] = trackDataHolder.getyMin();
        data[8][1] = trackDataHolder.getyMax();
        data[9][1] = trackDataHolder.getxMax() - trackDataHolder.getxMin();
        data[10][1] = trackDataHolder.getyMax() - trackDataHolder.getyMin();
        data[11][1] = trackDataHolder.getMedianDisplacement();
        data[12][1] = trackDataHolder.getMedianSpeed();
        data[13][1] = trackDataHolder.getCumulativeDistance();
        data[14][1] = trackDataHolder.getEuclideanDistance();
        data[15][1] = trackDataHolder.getDirectionality();
        data[16][1] = trackDataHolder.getMedianTurningAngle();
    }

    /**
     * Get a list with strings for feature.
     *
     * @return
     */
    private List<String> getFeaturesNames() {
        List<String> featuresNames = new ArrayList<>();
        featuresNames.add("well");
        featuresNames.add("track number");
        featuresNames.add("length");
        featuresNames.add("points number");
        featuresNames.add("duration (min)");
        featuresNames.add("x min (µm)");
        featuresNames.add("x max (µm)");
        featuresNames.add("y min (µm)");
        featuresNames.add("y max (µm)");
        featuresNames.add("x ND (µm)");
        featuresNames.add("y ND (µm)");
        featuresNames.add("MD (µm)");
        featuresNames.add("MS (µm/min)");
        featuresNames.add("CD (µm)");
        featuresNames.add("ED (µm)");
        featuresNames.add("directionality");
        featuresNames.add("MTA (deg)");
        return featuresNames;
    }
}
