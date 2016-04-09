/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellWellDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * A table model to capture filtered tracks.
 *
 * @author Paola
 */
public class FilterTrackTableModel extends AbstractTableModel {

    private final Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> map;
    private final List<Double> motileSteps;
    private String columnNames[];
    private Object[][] data;

    /**
     * Constructor.
     *
     * @param filterMap
     * @param motileSteps
     */
    public FilterTrackTableModel(Map<SingleCellWellDataHolder, Map<TrackDataHolder, boolean[]>> filterMap, List<Double> motileSteps) {
        this.map = filterMap;
        this.motileSteps = motileSteps;
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
     * Initialize the table
     */
    private void initTable() {
        // how many motile steps + 2 for the "well" and the "track" headers
        columnNames = new String[motileSteps.size() + 2];
        columnNames[0] = "well";
        columnNames[1] = "track";

        for (int i = 2; i < columnNames.length; i++) {
            columnNames[i] = "step: " + AnalysisUtils.roundTwoDecimals(motileSteps.get(i - 2));
        }

        // now the data
        List<SingleCellWellDataHolder> holders = new ArrayList<>(map.keySet());

        int size = 0;
        size = holders.stream().map((holder) -> new ArrayList<>(map.get(holder).keySet())).map((trackHolders) -> trackHolders.size()).reduce(size, Integer::sum);

        data = new Object[size][columnNames.length];

        Map<TrackDataHolder, boolean[]> totalMap = new LinkedHashMap<>();
        holders.stream().forEach((holder) -> {
            totalMap.putAll(map.get(holder));
        });

        List<TrackDataHolder> trackDataHolders = new ArrayList<>(totalMap.keySet());

        for (int i = 0; i < trackDataHolders.size(); i++) {

            TrackDataHolder trackHolder = trackDataHolders.get(i);
            boolean[] values = totalMap.get(trackHolder);

            // first column is always the track
            data[i][0] = "" + trackHolder.getTrack().getWellHasImagingType().getWell();
            data[i][1] = "" + trackHolder.getTrack();
            for (int j = 0; j < values.length; j++) {
                data[i][j + 2] = values[j];
            }
        }

    }

}
