/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table Model to keep data from tracks
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataTableModel extends AbstractTableModel {

    private Object[][] data;
    private final String[] columnNames;

    /**
     * Constructor
     *
     * @param columnNames: the names to give to the columns of the table
     * @param singleCellConditionDataHolder: the results holder from where to
     * get the track information
     * @param dataVector: the extra vector we want to add to the table as last
     * column (this can be the displacements vector or the speeds vector)
     */
    public TrackDataTableModel(String[] columnNames, SingleCellConditionDataHolder singleCellConditionDataHolder, Object[] dataVector) {
        this.columnNames = columnNames;
        initTable(singleCellConditionDataHolder, dataVector);
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
     * Define the structure for the table.
     */
    private void initTable(SingleCellConditionDataHolder singleCellConditionDataHolder, Object[] dataVector) {
        List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
        data = new Object[trackDataHolders.size()][3];
        for (int row = 0; row < data.length; row++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(row);
            Track track = trackDataHolder.getTrack();
            Well well = track.getWellHasImagingType().getWell();
            // first column: the well
            data[row][0] = well;
            // second column: the track number
            data[row][1] = track.getTrackNumber();
            // third column: the data we get at the row
            data[row][2] = dataVector[row];
        }
    }
}
