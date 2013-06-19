/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct
 * for Outliers, compute Distances between replicates
 *
 * @author Paola Masuzzo
 */
public interface AreaPreProcessor {

    /**
     * Normalize Area values, this method is different according to the type of
     * measured area: is it cell covered or open area?
     *
     * @param areaPreProcessingResults
     */
    public void computeNormalizedArea(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute increments of Area from one time frame to the following one
     * (Delta Area Values)
     *
     * @param areaPreProcessingResults
     */
    public void computeDeltaArea(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute %area increase for a certain condition
     *
     * @param areaPreProcessingResults
     */
    public void computeAreaIncrease(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute normalised Corrected Area values for a certain Condition
     *
     * @param areaPreProcessingResults
     */
    public void normalizeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults, String outlierHandlerBeanName);

    /**
     * For normalised and Corrected Area, compute distance matrix containing all
     * the distances between one replicate and all the others.
     *
     * @param areaPreProcessingResults
     */
    public void computeDistanceMatrix(AreaPreProcessingResults areaPreProcessingResults, String distanceMetricBeanName);

    /**
     * Check if a replicate can be considered as an Outlier.
     *
     * @param areaPreProcessingResults
     * @param plateCondition
     */
    public void excludeReplicates(AreaPreProcessingResults areaPreProcessingResults, PlateCondition plateCondition, String outliersHandlerBeanName);

    /**
     * Detect outliers for a 2D array of double (one condition)
     *
     * @param data
     * @param outliersHandlerBeanName
     * @return a matrix of Boolean (true if the data point is detected as an
     * outlier, false if not)
     */
    public boolean[][] detectOutliers(Double[][] data, String outliersHandlerBeanName);

    /**
     * Making use of the detect outliers method, correct data set for outliers
     *
     * @param data
     * @return a matrix with corrected value
     */
    public Double[][] correctForOutliers(Double[][] data, String outliersHandlerBeanName);

    /**
     *
     * @param data
     * @return
     */
    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName);

    /**
     * Set time frame interval for a condition
     *
     * @param areaPreProcessingResults
     */
    public void setTimeInterval(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Recompute time interval for a condition: this is called if first a time
     * interval has already been set
     *
     * @param areaPreProcessingResults
     */
    public void recomputeTimeInterval(AreaPreProcessingResults areaPreProcessingResults);
}
