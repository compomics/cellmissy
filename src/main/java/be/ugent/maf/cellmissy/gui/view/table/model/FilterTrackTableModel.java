/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * A table model to capture filtered tracks.
 *
 * @author Paola
 */
public class FilterTrackTableModel extends AbstractTableModel {

    private final Map<TrackDataHolder, boolean[]> filterMap;
    private final double[] motileSteps;
    private String columnNames[];
    private Object[][] data;

    public FilterTrackTableModel(Map<TrackDataHolder, boolean[]> filterMap, double[] motileSteps) {
        this.filterMap = filterMap;
        this.motileSteps = motileSteps;
        initTable();
    }

    @Override
    public int getRowCount() {
        return filterMap.size();
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
     * Initialize the table
     */
    private void initTable() {
        // how many motile steps + 1 for the "track" header
        columnNames = new String[motileSteps.length + 1];
        columnNames[0] = "track";
        for (int i = 1; i < columnNames.length; i++) {
            columnNames[i] = "step: " + AnalysisUtils.roundTwoDecimals(motileSteps[i - 1]);
        }

        // now the data
        List<TrackDataHolder> holders = new ArrayList<>(filterMap.keySet());

        data = new Object[holders.size()][columnNames.length];

        for (int i = 0; i < holders.size(); i++) {

            TrackDataHolder trackHolder = holders.get(i);
            boolean[] values = filterMap.get(trackHolder);

            // first column is always the track
            data[i][0] = "" + trackHolder.getTrack().getWellHasImagingType().getWell() + "-" + trackHolder.getTrack().toString();
            for (int j = 0; j < values.length; j++) {
                data[i][j + 1] = values[j];
            }
        }
    }

}
