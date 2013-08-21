/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
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
    private String columnNames[];

    /**
     * Constructor
     *
     * @param columnNames: the names to give to the columns of the table
     * @param singleCellPreProcessingResults: the results holder from where to
     * get the track information
     * @param dataVector: the extra vector we want to add to the table as last
     * column
     */
    public TrackDataTableModel(String[] columnNames, SingleCellPreProcessingResults singleCellPreProcessingResults, Object[] dataVector) {
        this.columnNames = columnNames;
        initTable(singleCellPreProcessingResults, dataVector);
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
    private void initTable(SingleCellPreProcessingResults singleCellPreProcessingResults, Object[] dataVector) {
        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
        data = new Object[trackDataHolders.size()][3];
        for (int row = 0; row < data.length; row++) {
            TrackDataHolder trackDataHolder = trackDataHolders.get(row);
            Track track = trackDataHolder.getTrack();
            Well well = track.getWellHasImagingType().getWell();
            data[row][0] = well;
            data[row][1] = track.getTrackNumber();
            data[row][2] = dataVector[row];
        }
    }
}
