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
     * compute normalized area values
     * @param preProcessingResultsHolder 
     */
    @Override
    public void computeNormalizedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] normalizedArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null && areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex] >= 0) {
                    normalizedArea[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(areaRawData[rowIndex][columnIndex] - areaRawData[0][columnIndex]);
                } else {
                    normalizedArea[rowIndex][columnIndex] = null;
                }
            }
        }
        areaPreProcessingResultsHolder.setNormalizedArea(normalizedArea);
    }

    /**
     * 
     * @param areaPreProcessingResultsHolder 
     */
    @Override
    public void computeDeltaArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] areaRawData = areaPreProcessingResultsHolder.getAreaRawData();
        Double[][] deltaArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null) {
                    deltaArea[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(areaRawData[rowIndex][columnIndex] - areaRawData[rowIndex - 1][columnIndex]);
                }
            }
        }
        areaPreProcessingResultsHolder.setDeltaArea(deltaArea);
    }

    /**
     * 
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
                    percentageAreaIncrease[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals((deltaArea[rowIndex][columnIndex] / areaRawData[rowIndex - 1][columnIndex]) * 100);
                }
            }
        }
        areaPreProcessingResultsHolder.setPercentageAreaIncrease(percentageAreaIncrease);
    }

    /**
     * 
     * @param areaPreProcessingResultsHolder 
     */
    @Override
    public void normalizeCorrectedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder) {
        Double[][] correctedArea = computeCorrectedArea(areaPreProcessingResultsHolder);
        Double[][] normalizedCorrectedArea = new Double[correctedArea.length][correctedArea[0].length];
        for (int columnIndex = 0; columnIndex < correctedArea[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < correctedArea.length; rowIndex++) {
                if (correctedArea[rowIndex][columnIndex] != null) {
                    normalizedCorrectedArea[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(correctedArea[rowIndex][columnIndex] - correctedArea[0][columnIndex]);
                }
            }
        }
        areaPreProcessingResultsHolder.setNormalizedCorrectedArea(normalizedCorrectedArea);
    }

    /**
     * using outliers handler, compute outliers for each set of data
     * @param data
     * @return 
     */
    @Override
    public double[] computeOutliers(double[] data) {
        return outliersHandler.handleOutliers(data).get(0);
    }

    /**
     * using outliers handler, compute corrected area (outliers are being kicked out from distribution)
     * @param data
     * @return 
     */
    @Override
    public double[] correctForOutliers(double[] data) {
        return outliersHandler.handleOutliers(data).get(1);
    }

    /**
     * 
     * @param areaPreProcessingResultsHolder
     * @return 
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
            double[] outliers = computeOutliers(ArrayUtils.toPrimitive(AnalysisUtils.excludeNullValues(transposed[columnIndex])));

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
                                correctedArea[rowIndex][columnIndex] = AnalysisUtils.roundTwoDecimals(correctedArea[rowIndex - 1][columnIndex] + deltaArea[rowIndex][columnIndex]);
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
}
