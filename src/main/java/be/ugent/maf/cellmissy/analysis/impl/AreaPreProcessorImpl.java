/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.AnalysisUtils;
import be.ugent.maf.cellmissy.analysis.AreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
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

    @Autowired
    private OutliersHandler outliersHandler;

    /**
     * Compute normalized area values: Area value at time frame zero is set to zero
     * @param areaPreProcessingResultsHolder 
     */
    @Override
    public void computeNormalizedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] normalizedArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null && areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex] >= 0) {
                    normalizedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex];
                } else {
                    normalizedArea[rowIndex][columnIndex] = null;
                }
            }
        }
        areaPreProcessingResultsHolder.setNormalizedArea(normalizedArea);
    }

    /**
     * Compute Delta area values from time frame n to time frame n+1
     * @param areaPreProcessingResultsHolder 
     */
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

    /**
     * Compute % Area increase between two time frames
     * @param areaPreProcessingResultsHolder 
     */
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

    /**
     * Normalize Corrected Area (Area value time frame zero is set to zero)
     * @param areaPreProcessingResultsHolder 
     */
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

    /**
     * using outliers handler, compute outliers for each set of data (each replicate)
     * @param data
     * @return a double array
     */
    @Override
    public Double[] computeOutliers(Double[] data) {
        return outliersHandler.handleOutliers(data).get(0);
    }

    /**
     * using outliers handler, compute corrected area (outliers are being kicked out from distribution)
     * @param data
     * @return corrected area
     */
    @Override
    public Double[] correctForOutliers(Double[] data) {
        return outliersHandler.handleOutliers(data).get(1);
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

        //transpose percentage area increase 2D array
        Double[][] transposed = new Double[percentageAreaIncrease[0].length][percentageAreaIncrease.length];
        for (int i = 0; i < percentageAreaIncrease.length; i++) {
            for (int j = 0; j < percentageAreaIncrease[0].length; j++) {
                transposed[j][i] = percentageAreaIncrease[i][j];
            }
        }

        for (int columnIndex = 0; columnIndex < transposed.length; columnIndex++) {
            Double[] outliers = computeOutliers(AnalysisUtils.excludeNullValues(transposed[columnIndex]));

            for (int rowIndex = 0; rowIndex < transposed[0].length; rowIndex++) {
                if (outliers.length != 0) {
                    //check first row (area increase is always null)
                    if (rowIndex == 0) {
                        correctedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex];
                        continue;
                    }

                    for (double outlier : outliers) {
                        if (transposed[columnIndex][rowIndex] != null && transposed[columnIndex][rowIndex].doubleValue() == outlier) {
                            //set area value back to previous one
                            correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex];
                            break;
                        } else if (transposed[columnIndex][rowIndex] != null && transposed[columnIndex][rowIndex].doubleValue() != outlier) {
                            if (deltaArea[rowIndex][columnIndex] != null) {
                                correctedArea[rowIndex][columnIndex] = correctedArea[rowIndex - 1][columnIndex] + deltaArea[rowIndex][columnIndex];
                            }
                        }
                    }
                } else {
                    correctedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex];
                }
            }
        }
        return correctedArea;
    }

    @Override
    public void computeEuclideanDistances(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResultsHolder.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);

        Double[][] distances = new Double[transposedArea.length][transposedArea.length];

        for (int columnIndex = 0; columnIndex < transposedArea.length; columnIndex++) {
            Double[] firstVector = transposedArea[columnIndex];
            for (int seq = 0; seq < transposedArea.length; seq++) {
                if (seq != columnIndex) {
                    Double[] secondVector = transposedArea[seq];
                    double computeEuclideanDistance = AnalysisUtils.computeEuclideanDistance(ArrayUtils.toPrimitive(firstVector), ArrayUtils.toPrimitive(secondVector));
                    distances[columnIndex][seq] = computeEuclideanDistance / 10000; //rescale all distances
                }
            }
        }
        areaPreProcessingResultsHolder.setEuclideanDistances(distances);
    }
}
