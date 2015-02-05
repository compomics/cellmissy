/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.MostDistantPointsPair;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This class extends AbstractTableModel. Table model to show data associated
 * with track data holder objects.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataHolderTableModel extends AbstractTableModel {

    private String columnNames[];
    private Object[][] data;
    private final TrackDataHolder trackDataHolder;

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
        StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
        CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
        data[3][1] = stepCentricDataHolder.getTimeIndexes().length;
        data[4][1] = cellCentricDataHolder.getTrackDuration();
        data[5][1] = cellCentricDataHolder.getxMin();
        data[6][1] = cellCentricDataHolder.getxMax();
        data[7][1] = cellCentricDataHolder.getyMin();
        data[8][1] = cellCentricDataHolder.getyMax();
        data[9][1] = cellCentricDataHolder.getxNetDisplacement();
        data[10][1] = cellCentricDataHolder.getyNetDisplacement();
        data[11][1] = cellCentricDataHolder.getMedianDisplacement();
        data[12][1] = cellCentricDataHolder.getMedianSpeed();
        data[13][1] = cellCentricDataHolder.getCumulativeDistance();
        data[14][1] = cellCentricDataHolder.getEuclideanDistance();
        data[15][1] = cellCentricDataHolder.getEndPointDirectionalityRatio();
        data[16][1] = cellCentricDataHolder.getMedianDirectionalityRatio();
        data[17][1] = cellCentricDataHolder.getMedianTurningAngle();
        ConvexHull convexHull = cellCentricDataHolder.getConvexHull();
        MostDistantPointsPair mostDistantPointsPair = convexHull.getMostDistantPointsPair();
        data[18][1] = mostDistantPointsPair.getMaxSpan();
        data[19][1] = cellCentricDataHolder.getDisplacementRatio();
        data[20][1] = cellCentricDataHolder.getOutreachRatio();
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
        featuresNames.add("number of steps");
        featuresNames.add("duration (min)");
        featuresNames.add("x min (µm)");
        featuresNames.add("x max (µm)");
        featuresNames.add("y min (µm)");
        featuresNames.add("y max (µm)");
        featuresNames.add("x ND (µm)");
        featuresNames.add("y ND (µm)");
        featuresNames.add("median displ. (µm)");
        featuresNames.add("median speed (µm/min)");
        featuresNames.add("cumulative dist. (µm)");
        featuresNames.add("euclidean dist. (µm)");
        featuresNames.add("end-point direct. ratio");
        featuresNames.add("median direct. ratio");
        featuresNames.add("median turning angle (deg)");
        featuresNames.add("max displ. (µm)");
        featuresNames.add("displ. ratio");
        featuresNames.add("outreach ratio");
        return featuresNames;
    }
}
