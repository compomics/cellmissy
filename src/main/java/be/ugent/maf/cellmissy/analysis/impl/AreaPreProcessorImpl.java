/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.view.DistanceMatrixTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements the Area PreProcessor interface.
 * It takes care of all the pre-processing operations required before handling area values and proceed with real analysis
 * @author Paola Masuzzo
 */
@Component("areaPreProcessor")
public class AreaPreProcessorImpl implements AreaPreProcessor {

    @Autowired
    private OutliersHandler outliersHandler;

    @Override
    public void computeNormalizedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] normalizedArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null && areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex] >= 0) {
                    normalizedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex];
                }
            }
        }
        areaPreProcessingResultsHolder.setNormalizedArea(normalizedArea);
    }

    @Override
    public void computeDeltaArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] deltaArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null) {
                    deltaArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] - areaRawData[rowIndex - 1][columnIndex];
                }
            }
        }
        areaPreProcessingResultsHolder.setDeltaArea(deltaArea);
    }

    @Override
    public void computeAreaIncrease(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] deltaArea = areaPreProcessingResultsHolder.getDeltaArea();
        Double[][] percentageAreaIncrease = new Double[deltaArea.length][deltaArea[0].length];
        for (int columnIndex = 0; columnIndex < deltaArea[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < deltaArea.length; rowIndex++) {
                if (deltaArea[rowIndex][columnIndex] != null && areaRawData[rowIndex - 1][columnIndex] != null) {
                    percentageAreaIncrease[rowIndex][columnIndex] = (deltaArea[rowIndex][columnIndex] / areaRawData[rowIndex - 1][columnIndex]) * 100;
                }
            }
        }
        areaPreProcessingResultsHolder.setPercentageAreaIncrease(percentageAreaIncrease);
    }

    @Override
    public void normalizeCorrectedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] correctedArea = computeCorrectedArea(areaPreProcessingResultsHolder);
        Double[][] normalizedCorrectedArea = new Double[correctedArea.length][correctedArea[0].length];
        for (int columnIndex = 0; columnIndex < correctedArea[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < correctedArea.length; rowIndex++) {
                if (correctedArea[rowIndex][columnIndex] != null) {
                    normalizedCorrectedArea[rowIndex][columnIndex] = correctedArea[rowIndex][columnIndex] - correctedArea[0][columnIndex];
                }
            }
        }
        areaPreProcessingResultsHolder.setNormalizedCorrectedArea(normalizedCorrectedArea);
    }

    @Override
    public void computeDistanceMatrix(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
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
        areaPreProcessingResultsHolder.setDistanceMatrix(distanceMatrix);
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
     * @param areaPreProcessingResultsHolder
     * @return 2D array with corrected area values (still need to be normalized)
     */
    private Double[][] computeCorrectedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] percentageAreaIncrease = areaPreProcessingResultsHolder.getPercentageAreaIncrease();
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] deltaArea = areaPreProcessingResultsHolder.getDeltaArea();
        Double[][] correctedArea = new Double[percentageAreaIncrease.length][percentageAreaIncrease[0].length];
        boolean[][] outliers = detectOutliers(percentageAreaIncrease);
        for (int rowIndex = 0; rowIndex < outliers.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < outliers[0].length; columnIndex++) {
                if (rowIndex == 0) {
                    correctedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex];
                    continue;
                }
                if (outliers[rowIndex][columnIndex]) {
                    correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex];
                } else {
                    if (deltaArea[rowIndex][columnIndex] != null) {
                        correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex] + deltaArea[rowIndex][columnIndex];
                    }
                }
            }
        }
        return correctedArea;
    }

    /**
     * Exclude Technical replicates (wells) from analysis
     * @param areaPreProcessingResultsHolder
     * @param plateCondition
     */
    @Override
    public void excludeReplicates(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder, PlateCondition plateCondition) {
        // detect outliers for distance matrix
        Double[][] distanceMatrix = areaPreProcessingResultsHolder.getDistanceMatrix();
        boolean[][] outliersMatrix = detectOutliers(distanceMatrix);
        // create a new distance matrix table model and set boolean for results holder from the model
        DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(areaPreProcessingResultsHolder.getDistanceMatrix(), outliersMatrix, plateCondition);
        areaPreProcessingResultsHolder.setExcludeReplicates(distanceMatrixTableModel.getCheckboxOutliers());
    }
}
