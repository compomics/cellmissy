/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeStep;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the Area PreProcessor interface.
 * It takes care of all the pre-processing operations required before handling area values and proceed with real analysis
 * @author Paola Masuzzo
 */
@Component("areaPreProcessor")
public class AreaPreProcessorImpl implements AreaPreProcessor {

    private List<TimeStep> timeStepsList;
    private int timeFramesNumber;
    @Autowired
    private OutliersHandler outliersHandler;

    /**
     * set time frames number
     * @param timeFramesNumber 
     */
    @Override
    public void setTimeFramesNumber(int timeFramesNumber) {
        this.timeFramesNumber = timeFramesNumber;
    }

    /**
     * set time steps list
     * @param timeStepsList 
     */
    @Override
    public void setTimeStepsList(List<TimeStep> timeStepsList) {
        this.timeStepsList = timeStepsList;
    }

    /**
     * compute time frames from step sequence
     * @return an array of integers
     */
    @Override
    public double[] computeTimeFrames(Double experimentInterval) {

        double[] timeFrames;
        timeFrames = new double[timeFramesNumber];
        for (int i = 0; i < timeFramesNumber; i++) {
            Double timeFrame = timeStepsList.get(i).getTimeStepSequence() * experimentInterval;
            int intValue = timeFrame.intValue();
            timeFrames[i] = intValue;
        }
        return timeFrames;
    }

    /**
     * compute Normalized Area
     * @param data
     * @return a 2D array of double values
     */
    @Override
    public Double[][] computeNormalizedArea(Double[][] data) {

        int counter = 0;
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            if (timeStepsList.get(columnIndex).getArea() != 0) {
                for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                    int index = (counter / timeFramesNumber) * timeFramesNumber;
                    if (timeStepsList.get(counter).getArea() - timeStepsList.get(index).getArea() >= 0) {
                        data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(timeStepsList.get(counter).getArea() - timeStepsList.get(index).getArea());
                    } else {
                        data[rowIndex][columnIndex] = null;
                    }
                    counter++;
                }
            }
        }
        return data;
    }

    /*
     * compute Delta Area values (increments from one time frame to the following one)
     * @param data
     * @return a 2D array of double values
     */
    @Override
    public Double[][] computeDeltaArea(Double[][] data) {
        int counter = 0;
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (timeStepsList.get(counter).getTimeStepSequence() != 0 && timeStepsList.get(counter).getArea() != 0) {
                    data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(timeStepsList.get(counter).getArea() - timeStepsList.get(counter - 1).getArea());
                }
                counter++;
            }
        }
        return data;
    }

    /**
     * Compute %Area increase (these values are used for density plots and area correction for outliers)
     * @param data
     * @return a 2D array of double arrays
     */
    @Override
    public Double[][] computeAreaIncrease(Double[][] data, PlateCondition plateCondition) {
        int counter = 0;
        Double[][] newArray = new Double[timeFramesNumber][plateCondition.getWellCollection().size() + 1];
        Double[][] computeDeltaArea = computeDeltaArea(newArray);
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (timeStepsList.get(counter).getTimeStepSequence() != 0) {
                    Double deltaArea = computeDeltaArea[rowIndex][columnIndex];
                    if (deltaArea != null) {
                        data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(deltaArea / timeStepsList.get(counter - 1).getArea() * 100);
                    }
                }
                counter++;
            }
        }
        return data;
    }

    /**
     * given corrected area values, normalize (area time frame 0 = 0)
     * @param data
     * @return a 2D array of corrected and normalized double values
     */
    @Override
    public Double[][] normalizeCorrectedArea(Double[][] data, PlateCondition plateCondition) {
        Double[][] newArray = new Double[timeFramesNumber][plateCondition.getWellCollection().size() + 1];
        Double[][] computeCorrectedArea = computeCorrectedArea(newArray, plateCondition);
        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (computeCorrectedArea[rowIndex][columnIndex] != null) {
                    data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(computeCorrectedArea[rowIndex][columnIndex] - computeCorrectedArea[0][columnIndex]);
                }
            }
        }
        return data;
    }

    /**
     * using outliers handler, compute outliers for each set of data
     * @param data
     * @return 
     */
    public double[] computeOutliers(double[] data) {
        return outliersHandler.handleOutliers(data).get(0);
    }

    /**
     * using outliers handler, compute corrected area (outliers are being kicked out from distribution)
     * @param data
     * @return 
     */
    public double[] computeCorrectedArea(double[] data) {
        return outliersHandler.handleOutliers(data).get(1);
    }

    /**
     * Correct area values after outlier detection
     * @param data
     * @return a 2D array to populate data table
     */
    private Double[][] computeCorrectedArea(Double[][] data, PlateCondition plateCondition) {

        int counter = 0;
        Double[][] newArray = new Double[timeFramesNumber][plateCondition.getWellCollection().size() + 1];
        Double[][] computeAreaIncrease = computeAreaIncrease(newArray, plateCondition);
        Double[][] transposed = new Double[computeAreaIncrease[0].length][computeAreaIncrease.length];
        for (int i = 0; i < computeAreaIncrease.length; i++) {
            for (int j = 0; j < computeAreaIncrease[0].length; j++) {
                transposed[j][i] = computeAreaIncrease[i][j];
            }
        }
        Double[][] array = new Double[timeFramesNumber][plateCondition.getWellCollection().size() + 1];
        Double[][] computeDeltaArea = computeDeltaArea(array);

        for (int columnIndex = 1; columnIndex < data[0].length; columnIndex++) {
            double[] outliers = computeOutliers(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[columnIndex])));

            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                if (outliers.length != 0) {
                    //check first row (area increase is always null)
                    if (rowIndex == 0) {
                        data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(timeStepsList.get(counter).getArea());
                        counter++;
                        continue;
                    }

                    Double areaIncrease = transposed[columnIndex][rowIndex];
                    for (double outlier : outliers) {
                        if (areaIncrease != null && areaIncrease.doubleValue() == outlier) {
                            //set area value back to previous one
                            data[rowIndex][columnIndex] = data[rowIndex - 1][columnIndex];
                            break;
                        } else if (areaIncrease != null && areaIncrease.doubleValue() != outlier) {
                            if (computeDeltaArea[rowIndex][columnIndex] != null) {
                                data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(data[rowIndex - 1][columnIndex] + computeDeltaArea[rowIndex][columnIndex]);
                            }
                        }
                    }
                } else {
                    data[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(timeStepsList.get(counter).getArea());
                }
                counter++;
            }
        }
        return data;
    }
}
