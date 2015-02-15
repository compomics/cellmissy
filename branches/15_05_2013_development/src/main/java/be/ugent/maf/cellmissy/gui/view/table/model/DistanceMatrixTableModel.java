/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo
 */
public class DistanceMatrixTableModel extends AbstractTableModel {

    private Object[][] data;
    private boolean[] checkboxOutliers;
    private String columnNames[];
    private final boolean[][] outliers;
    private final PlateCondition plateCondition;

    /**
     * Constructor (data to show in the table and boolean matrix for outliers detection)
     *
     * @param dataToShow
     * @param outliers
     * @param plateCondition
     */
    public DistanceMatrixTableModel(Double[][] dataToShow, boolean[][] outliers, PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
        this.outliers = outliers;
        initTable(dataToShow);
    }

    public boolean[] getCheckboxOutliers() {
        return checkboxOutliers;
    }

    public void setCheckboxOutliers(boolean[] checkboxOutliers) {
        this.checkboxOutliers = checkboxOutliers;
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
        Object result = null;
        // If we have the last row (one beyond last real data)
        // return checkbox
        if (rowIndex == data.length - 1 && columnIndex != 0) {
            result = checkboxOutliers[columnIndex - 1];
        } else {
            result = data[rowIndex][columnIndex];
        }
        return result;

    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // isCellEditable and setValuesAt need BOTH to be overriden in order to keep track of changes that occour in the table model
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rowIndex == data.length - 1 && columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        checkboxOutliers[columnIndex - 1] = (boolean) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Fill in Table with Data
     *
     * @param dataToShow
     */
    private void initTable(Double[][] dataToShow) {
        double outliersDetectionRatio = PropertiesConfigurationHolder.getInstance().getDouble("outliersDetectionRatio");
        // List of imaged wells
        List<Well> processedWells = plateCondition.getAreaAnalyzedWells();
        int numberOfSamplesPerCondition = AnalysisUtils.getNumberOfAreaAnalyzedSamples(plateCondition);
        //the table needs one column for the time frames + one column for each replicate (each well imaged)
        columnNames = new String[numberOfSamplesPerCondition + 1];
     
        int counter = 1;
        for (Well processedWell1 : processedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfAreaAnalyzedSamplesPerWell(processedWell1);
            for (int i = counter; i < numberOfSamplesPerWell + counter; i++) {
                columnNames[i] = "" + processedWell1;
            }
            counter += numberOfSamplesPerWell;
        }

        checkboxOutliers = new boolean[dataToShow.length];
        data = new Object[dataToShow.length + 1][columnNames.length];
        counter = 0;
        for (Well processedWell : processedWells) {
            int numberOfSamplesPerWell = AnalysisUtils.getNumberOfAreaAnalyzedSamplesPerWell(processedWell);

            for (int columnIndex = 1; columnIndex < data.length; columnIndex++) {
                for (int rowIndex = counter; rowIndex < numberOfSamplesPerWell + counter; rowIndex++) {
                    data[rowIndex][0] = "" + processedWell;
                    data[rowIndex][columnIndex] = dataToShow[rowIndex][columnIndex - 1];
                }
                // if the outliers ratio is bigger than RATIO, chechBox is selected (true)
                if (getOutlierRatio(columnIndex - 1) >= outliersDetectionRatio) {
                    checkboxOutliers[columnIndex - 1] = true;
                }
            }
            counter += numberOfSamplesPerWell;
        }
    }

    /**
     * Get the outlier Ratio per column: number of outliers divided by number of replicates
     *
     * @param columnIndex
     * @return a double value for the ratio
     */
    private double getOutlierRatio(int columnIndex) {
        boolean[] outliersPerColumn = outliers[columnIndex];
        int count = 0;
        for (boolean anOutliersPerColumn : outliersPerColumn) {
            if (anOutliersPerColumn) {
                count++;
            }
        }
        return (double) count / outliersPerColumn.length;
    }
}
