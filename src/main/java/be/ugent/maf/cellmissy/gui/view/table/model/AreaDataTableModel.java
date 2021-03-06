/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This class extends AbstractTableModel. Table Model to show data for area
 * analysis.
 *
 * @author Paola Masuzzo
 */
public class AreaDataTableModel extends AbstractTableModel {

    private final PlateCondition plateCondition;
    private String columnNames[];
    private Double[][] data;

    /**
     * constructor
     *
     * @param plateCondition
     * @param dataToShow
     * @param firstColumn
     */
    public AreaDataTableModel(PlateCondition plateCondition, Double[][] dataToShow, double[] firstColumn) {
        this.plateCondition = plateCondition;
        initTable(firstColumn, dataToShow);
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
     * define structure for table
     */
    private void initTable(double[] firstColumn, Double[][] dataToShow) {
        //2D array of double (dimension: time frames * wellList +1)
        data = new Double[dataToShow.length][dataToShow[0].length + 1];
        List<Well> areaAnalyzedWells = plateCondition.getAreaAnalyzedWells();
        int numberOfSamplesPerCondition = AnalysisUtils.getNumberOfAreaAnalyzedSamples(plateCondition);
        //the table needs one column for the time frames + one column for each replicate (each well imaged)
        columnNames = new String[numberOfSamplesPerCondition + 1];
        //first column name: Time Frames
        columnNames[0] = "time frame";
        int counter = 1;
        for (Well areaAnalyzedWell : areaAnalyzedWells) {
            int numberOfAreaAnalyzedSamplesPerWell = AnalysisUtils.getNumberOfAreaAnalyzedSamplesPerWell(areaAnalyzedWell);
            for (int i = counter; i < numberOfAreaAnalyzedSamplesPerWell + counter; i++) {
                columnNames[i] = "" + areaAnalyzedWell;
            }
            counter += numberOfAreaAnalyzedSamplesPerWell;
        }
        for (int i = 0; i < data.length; i++) {
            //fill in first column
            data[i][0] = firstColumn[i];
            System.arraycopy(dataToShow[i], 0, data[i], 1, data[i].length - 1);
        }
    }
}
