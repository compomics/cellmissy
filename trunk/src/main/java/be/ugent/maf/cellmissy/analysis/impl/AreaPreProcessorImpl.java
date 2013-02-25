/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.TimeInterval;
import be.ugent.maf.cellmissy.gui.view.table.models.DistanceMatrixTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the Area PreProcessor interface. It takes care of all the pre-processing operations required before handling area values and proceed with real analysis.
 *
 * @author Paola Masuzzo
 */
@Component("areaPreProcessor")
public class AreaPreProcessorImpl implements AreaPreProcessor {

    @Autowired
    private OutliersHandler outliersHandler;

    @Override
    public void computeNormalizedArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] normalizedArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                // check if area raw data at first time frame is already zero
                if (areaRawData[0][columnIndex] != 0) {
                    if (areaRawData[rowIndex][columnIndex] != null && areaRawData[0][columnIndex] != null && areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex] >= 0) {
                        normalizedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex];
                    }
                } else {
                    normalizedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex];
                }
            }
        }
        areaPreProcessingResults.setNormalizedArea(normalizedArea);
    }

    @Override
    public void computeDeltaArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] deltaArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null && areaRawData[rowIndex - 1][columnIndex] != null) {
                    deltaArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] - areaRawData[rowIndex - 1][columnIndex];
                }
            }
        }
        areaPreProcessingResults.setDeltaArea(deltaArea);
    }

    @Override
    public void computeAreaIncrease(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
        Double[][] percentageAreaIncrease = new Double[deltaArea.length][deltaArea[0].length];
        for (int columnIndex = 0; columnIndex < deltaArea[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < deltaArea.length; rowIndex++) {
                if (deltaArea[rowIndex][columnIndex] != null && deltaArea[rowIndex][columnIndex] != 0 && areaRawData[rowIndex - 1][columnIndex] != null && areaRawData[rowIndex - 1][columnIndex] != 0) {
                    percentageAreaIncrease[rowIndex][columnIndex] = (deltaArea[rowIndex][columnIndex] / areaRawData[rowIndex - 1][columnIndex]) * 100;
                } else if (deltaArea[rowIndex][columnIndex] == 0 || areaRawData[rowIndex - 1][columnIndex] == 0) {
                    percentageAreaIncrease[rowIndex][columnIndex] = 0.0;
                }
            }
        }
        areaPreProcessingResults.setPercentageAreaIncrease(percentageAreaIncrease);
    }

    @Override
    public void normalizeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] correctedArea = computeCorrectedArea(areaPreProcessingResults);
        Double[][] normalizedCorrectedArea = new Double[correctedArea.length][correctedArea[0].length];
        for (int columnIndex = 0; columnIndex < correctedArea[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < correctedArea.length; rowIndex++) {
                if (correctedArea[rowIndex][columnIndex] != null && correctedArea[0][columnIndex] != null) {
                    normalizedCorrectedArea[rowIndex][columnIndex] = correctedArea[rowIndex][columnIndex] - correctedArea[0][columnIndex];
                }
            }
        }
        areaPreProcessingResults.setNormalizedCorrectedArea(normalizedCorrectedArea);
    }

    @Override
    public void computeDistanceMatrix(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        Double[][] distanceMatrix = new Double[transposedArea.length][transposedArea.length];
        for (int rowIndex = 0; rowIndex < transposedArea.length; rowIndex++) {
            Double[] firstVector = transposedArea[rowIndex];
            for (int seq = 0; seq < transposedArea.length; seq++) {
                if (seq != rowIndex) {
                    Double[] secondVector = transposedArea[seq];
                    double computeEuclideanDistance = AnalysisUtils.computeEuclideanDistance(firstVector, secondVector);
                    distanceMatrix[rowIndex][seq] = computeEuclideanDistance / 10000; //rescale all distances
                }
            }
        }
        areaPreProcessingResults.setDistanceMatrix(distanceMatrix);
    }

    @Override
    public boolean[][] detectOutliers(Double[][] data) {
        return outliersHandler.detectOutliers(data);
    }

    @Override
    public Double[][] correctForOutliers(Double[][] data) {
        return outliersHandler.correctForOutliers(data);
    }

    /**
     * Correct Area
     *
     * @param areaPreProcessingResults
     * @return 2D array with corrected area values (still need to be normalized)
     */
    private Double[][] computeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] percentageAreaIncrease = areaPreProcessingResults.getPercentageAreaIncrease();
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
        Double[][] correctedArea = new Double[percentageAreaIncrease.length][percentageAreaIncrease[0].length];
        boolean[][] outliers = detectOutliers(percentageAreaIncrease);
        for (int rowIndex = 0; rowIndex < outliers.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < outliers[0].length; columnIndex++) {
                if (rowIndex == 0 && areaRawData[rowIndex][columnIndex] != null) {
                    correctedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex];
                    continue;
                }
                if (outliers[rowIndex][columnIndex] && correctedArea[rowIndex - 1][columnIndex] != null) {
                    correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex];
                } else {
                    if (deltaArea[rowIndex][columnIndex] != null && correctedArea[rowIndex - 1][columnIndex] != null) {
                        correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex] + deltaArea[rowIndex][columnIndex];
                    }
                }
            }
        }
        return correctedArea;
    }

    /**
     * Exclude Technical replicates (wells) from analysis
     *
     * @param areaPreProcessingResults
     * @param plateCondition
     */
    @Override
    public void excludeReplicates(AreaPreProcessingResults areaPreProcessingResults, PlateCondition plateCondition) {
        // detect outliers for distance matrix
        Double[][] distanceMatrix = areaPreProcessingResults.getDistanceMatrix();
        boolean[][] outliersMatrix = detectOutliers(distanceMatrix);
        // create a new distance matrix table model and set boolean for results holder from the model
        DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(areaPreProcessingResults.getDistanceMatrix(), outliersMatrix, plateCondition);
        areaPreProcessingResults.setExcludeReplicates(distanceMatrixTableModel.getCheckboxOutliers());
    }

    /**
     *
     * @param areaPreProcessingResults
     */
    @Override
    public void setTimeInterval(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        // check if some replicates need to be excluded from computation (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();
        // first time point for interval is set to zero by default
        // this is changed if user decides to analyse only a subset of entire time frames
        int firstTimeFrame = 0;
        // last time point for interval is set to cutoff time point: by default this is the entire time frame of experiment
        // cutoff time point is intended to be the time point from which starting every replicates in the condition has only no null values. 
        int lastTimeFrame = normalizedCorrectedArea.length - 1;
        // for each replicate
        for (int columnIndex = 0; columnIndex < transposedArea.length; columnIndex++) {
            if (!excludeReplicates[columnIndex]) {
                // temporary last time point
                int temporaryLastTimeFrame = lastTimeFrame;
                for (int rowIndex = 0; rowIndex < transposedArea[0].length; rowIndex++) {
                    if (transposedArea[columnIndex][rowIndex] == null) {
                        temporaryLastTimeFrame = rowIndex - 1;
                        break;
                    }
                }
                if (temporaryLastTimeFrame < lastTimeFrame) {
                    lastTimeFrame = temporaryLastTimeFrame;
                }
            }
        }
        TimeInterval timeInterval = new TimeInterval(firstTimeFrame, lastTimeFrame);
        timeInterval.setProposedCutOff(lastTimeFrame);
        areaPreProcessingResults.setTimeInterval(timeInterval);
    }
}
