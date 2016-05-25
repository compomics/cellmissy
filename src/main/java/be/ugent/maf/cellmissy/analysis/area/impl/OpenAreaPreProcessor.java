/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl;

import be.ugent.maf.cellmissy.analysis.area.AreaPreProcessor;
import be.ugent.maf.cellmissy.analysis.factory.DistanceMetricFactory;
import be.ugent.maf.cellmissy.analysis.DistanceMetricOperator;
import be.ugent.maf.cellmissy.analysis.KernelDensityEstimator;
import be.ugent.maf.cellmissy.analysis.factory.KernelDensityEstimatorFactory;
import be.ugent.maf.cellmissy.analysis.OutliersHandler;
import be.ugent.maf.cellmissy.analysis.factory.OutliersHandlerFactory;
import be.ugent.maf.cellmissy.entity.result.area.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.TimeInterval;
import be.ugent.maf.cellmissy.exception.TwoOrMoreObservationsException;
import be.ugent.maf.cellmissy.gui.view.table.model.DistanceMatrixTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * Implementation of the area pre-processor for the open area values.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("openAreaPreProcessor")
public class OpenAreaPreProcessor implements AreaPreProcessor {

    @Override
    public void computeNormalizedArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] normalizedArea = new Double[areaRawData.length][areaRawData[0].length];
        for (int columnIndex = 0; columnIndex < areaRawData[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < areaRawData.length; rowIndex++) {
                if (areaRawData[rowIndex][columnIndex] != null) {
                    normalizedArea[rowIndex][columnIndex] = areaRawData[rowIndex][columnIndex] / areaRawData[0][columnIndex] * 100;
                }
            }
        }
        areaPreProcessingResults.setNormalizedArea(normalizedArea);
    }

    @Override
    public void computeDeltaArea(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] transformedData = areaPreProcessingResults.getTransformedData();
        Double[][] deltaArea = new Double[transformedData.length][transformedData[0].length];
        for (int columnIndex = 0; columnIndex < transformedData[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < transformedData.length; rowIndex++) {
                if (transformedData[rowIndex][columnIndex] != null && transformedData[rowIndex - 1][columnIndex] != null) {
                    deltaArea[rowIndex][columnIndex] = transformedData[rowIndex][columnIndex] - transformedData[rowIndex - 1][columnIndex];
                }
            }
        }
        areaPreProcessingResults.setDeltaArea(deltaArea);
    }

    @Override
    public void computeAreaIncrease(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] transformedData = areaPreProcessingResults.getTransformedData();
        Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
        Double[][] percentageAreaIncrease = new Double[deltaArea.length][deltaArea[0].length];
        for (int columnIndex = 0; columnIndex < deltaArea[0].length; columnIndex++) {
            for (int rowIndex = 1; rowIndex < deltaArea.length; rowIndex++) {
                if (deltaArea[rowIndex][columnIndex] != null && deltaArea[rowIndex][columnIndex] != 0 && transformedData[rowIndex - 1][columnIndex] != null && transformedData[rowIndex - 1][columnIndex] != 0) {
                    percentageAreaIncrease[rowIndex][columnIndex] = (deltaArea[rowIndex][columnIndex] / transformedData[rowIndex - 1][columnIndex]);
                } else if (deltaArea[rowIndex][columnIndex] == null || transformedData[rowIndex - 1][columnIndex] == null) {
                    percentageAreaIncrease[rowIndex][columnIndex] = 0.0;
                } else if (deltaArea[rowIndex][columnIndex] == 0 || transformedData[rowIndex - 1][columnIndex] == 0) {
                    percentageAreaIncrease[rowIndex][columnIndex] = 0.0;
                }
            }
        }
        areaPreProcessingResults.setPercentageAreaIncrease(percentageAreaIncrease);
    }

    @Override
    public void normalizeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults, String outlierHandlerBeanName) {
        Double[][] correctedArea = computeCorrectedArea(areaPreProcessingResults, outlierHandlerBeanName);
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
    public void computeDistanceMatrix(AreaPreProcessingResults areaPreProcessingResults, String distanceMetricBeanName) {
        DistanceMetricOperator distanceMetricOperator = DistanceMetricFactory.getInstance().getDistanceMetricOperator(distanceMetricBeanName);
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        Double[][] distanceMatrix = new Double[transposedArea.length][transposedArea.length];
        for (int rowIndex = 0; rowIndex < transposedArea.length; rowIndex++) {
            Double[] firstVector = transposedArea[rowIndex];
            for (int seq = 0; seq < transposedArea.length; seq++) {
                if (seq != rowIndex) {
                    Double[] secondVector = transposedArea[seq];
                    double computeEuclideanDistance = distanceMetricOperator.computeDistanceMetric(firstVector, secondVector);
                    distanceMatrix[rowIndex][seq] = computeEuclideanDistance / 10000; //rescale all distances
                }
            }
        }
        areaPreProcessingResults.setDistanceMatrix(distanceMatrix);
    }

    @Override
    public void excludeReplicates(AreaPreProcessingResults areaPreProcessingResults, PlateCondition plateCondition, String outliersHandlerBeanName) {
        // detect outliers for distance matrix
        Double[][] distanceMatrix = areaPreProcessingResults.getDistanceMatrix();
        boolean[][] outliersMatrix = detectOutliers(distanceMatrix, outliersHandlerBeanName);
        // create a new distance matrix table model and set boolean for results holder from the model
        DistanceMatrixTableModel distanceMatrixTableModel = new DistanceMatrixTableModel(areaPreProcessingResults.getDistanceMatrix(), outliersMatrix, plateCondition);
        areaPreProcessingResults.setExcludeReplicates(distanceMatrixTableModel.getCheckboxOutliers());
    }

    @Override
    public boolean[][] detectOutliers(Double[][] data, String outliersHandlerBeanName) {
        OutliersHandler outliersHandler = OutliersHandlerFactory.getInstance().getOutliersHandler(outliersHandlerBeanName);
        return outliersHandler.detectOutliers(data);
    }

    @Override
    public Double[][] correctForOutliers(Double[][] data, String outliersHandlerBeanName) {
        OutliersHandler outliersHandler = OutliersHandlerFactory.getInstance().getOutliersHandler(outliersHandlerBeanName);
        return outliersHandler.correctForOutliers(data);
    }

    @Override
    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName) {
        KernelDensityEstimator kernelDensityEstimator = KernelDensityEstimatorFactory.getInstance().getKernelDensityEstimator(kernelDensityEstimatorBeanName);
        try {
            return kernelDensityEstimator.estimateDensityFunction(data);
        } catch (TwoOrMoreObservationsException ex) {
            Logger.getLogger(OpenAreaPreProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void setTimeInterval(AreaPreProcessingResults areaPreProcessingResults) {
        // first time point for interval is set to zero by default
        // this is changed if user decides to analyse only a subset of entire time frames
        int firstTimeFrame = 0;
        // last time point for interval is set to cutoff time point: by default this is the entire time frame of experiment
        // cutoff time point is intended to be the time point from which starting every replicates in the condition has only no null values.
        int cutOff = computeCutOff(areaPreProcessingResults);
        TimeInterval timeInterval = new TimeInterval(firstTimeFrame, cutOff);
        timeInterval.setProposedCutOff(cutOff);
        areaPreProcessingResults.setTimeInterval(timeInterval);
    }

    /**
     * Transform area values from open area to cell covered area
     *
     * @param areaPreProcessingResults
     */
    public void transformAreaData(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] normalizedArea = areaPreProcessingResults.getNormalizedArea();
        Double[][] transformedData = new Double[normalizedArea.length][normalizedArea[0].length];
        for (int columnIndex = 0; columnIndex < normalizedArea[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < normalizedArea.length; rowIndex++) {
                if (normalizedArea[rowIndex][columnIndex] != null) {
                    transformedData[rowIndex][columnIndex] = 100.0 - normalizedArea[rowIndex][columnIndex];
                }
            }
        }
        areaPreProcessingResults.setTransformedData(transformedData);
    }

    /**
     * Correct Area
     *
     * @param areaPreProcessingResults
     * @return 2D array with corrected area values (still need to be normalized)
     */
    private Double[][] computeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults, String outlierHandlerBeanName) {
        Double[][] percentageAreaIncrease = areaPreProcessingResults.getPercentageAreaIncrease();
        Double[][] areaRawData = areaPreProcessingResults.getAreaRawData();
        Double[][] deltaArea = areaPreProcessingResults.getDeltaArea();
        Double[][] correctedArea = new Double[percentageAreaIncrease.length][percentageAreaIncrease[0].length];
        boolean[][] outliers = detectOutliers(percentageAreaIncrease, outlierHandlerBeanName);
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

    @Override
    public void recomputeTimeInterval(AreaPreProcessingResults areaPreProcessingResults) {
        //get existing time interval and retain the chosen first time frame
        TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
        int firstTimeFrame = timeInterval.getFirstTimeFrame();
        // recompute the cut off
        int cutOff = computeCutOff(areaPreProcessingResults);
        TimeInterval newTimeInterval = new TimeInterval(firstTimeFrame, cutOff);
        newTimeInterval.setProposedCutOff(cutOff);
        areaPreProcessingResults.setTimeInterval(timeInterval);
    }

    /**
     * Compute cut off
     *
     * @param areaPreProcessingResults
     * @return
     */
    private int computeCutOff(AreaPreProcessingResults areaPreProcessingResults) {
        Double[][] normalizedCorrectedArea = areaPreProcessingResults.getNormalizedCorrectedArea();
        Double[][] transposedArea = AnalysisUtils.transpose2DArray(normalizedCorrectedArea);
        // check if some replicates need to be excluded from computation (this means these replicates are outliers)
        boolean[] excludeReplicates = areaPreProcessingResults.getExcludeReplicates();// last time point for interval is set to cutoff time point: by default this is the entire time frame of experiment
        // cutoff time point is intended to be the time point from which starting every replicates in the condition has only no null values.
        int cutOff = normalizedCorrectedArea.length - 1;
        // for each replicate
        for (int columnIndex = 0; columnIndex < transposedArea.length; columnIndex++) {
            if (!excludeReplicates[columnIndex]) {
                // temporary last time point
                int temporaryLastTimeFrame = cutOff;
                for (int rowIndex = 0; rowIndex < transposedArea[0].length; rowIndex++) {
                    if (transposedArea[columnIndex][rowIndex] == null) {
                        temporaryLastTimeFrame = rowIndex - 1;
                        break;
                    }
                }
                if (temporaryLastTimeFrame < cutOff) {
                    cutOff = temporaryLastTimeFrame;
                }
            }
        }
        return cutOff;
    }
}
