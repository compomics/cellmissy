/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area;

import be.ugent.maf.cellmissy.entity.result.area.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct
 * for Outliers, compute Distances between replicates, estimate the density
 * function, compute time interval for the analysis. This interface is
 * implemented separately for open area or cell-covered area.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface AreaPreProcessor {

    /**
     * Normalize Area values relatively to area at time 0
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
     * @param outlierHandlerBeanName
     */
    public void normalizeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults, String outlierHandlerBeanName);

    /**
     * For normalised and Corrected Area, compute distance matrix containing all
     * the distances between one replicate and all the others. This depends on
     * the ban of the distance matrix: could be Euclidean or anything else.
     *
     * @param areaPreProcessingResults
     * @param distanceMetricBeanName
     */
    public void computeDistanceMatrix(AreaPreProcessingResults areaPreProcessingResults, String distanceMetricBeanName);

    /**
     * Check if a replicate can be considered as an Outlier. This will depend on
     * the bean of the outliers handler, could use R or Excel algorithms for
     * example.
     *
     * @param areaPreProcessingResults
     * @param plateCondition
     * @param outliersHandlerBeanName
     */
    public void excludeReplicates(AreaPreProcessingResults areaPreProcessingResults, PlateCondition plateCondition, String outliersHandlerBeanName);

    /**
     * Detect outliers for a 2D array of double (one condition). This will
     * depend on the bean of the outliers handler, could use R or Excel
     * algorithms for example.
     *
     * @param data
     * @param outliersHandlerBeanName
     * @return a matrix of Boolean (true if the data point is detected as an
     * outlier, false if not)
     */
    public boolean[][] detectOutliers(Double[][] data, String outliersHandlerBeanName);

    /**
     * Making use of the detect outliers method, correct data set for outliers.
     * This will depend on the bean of the outliers handler, could use R or
     * Excel algorithms for example.
     *
     * @param data
     * @param outliersHandlerBeanName
     * @return a matrix with corrected value
     */
    public Double[][] correctForOutliers(Double[][] data, String outliersHandlerBeanName);

    /**
     * Estimate the probability density function, according to name of the bean
     * for the kernel density estimator; could use for example a Gaussian kernel
     * or something else.
     *
     * @param data
     * @param kernelDensityEstimatorBeanName
     * @return a list of doubles[] containing x and y values for the function.
     */
    public List<double[]> estimateDensityFunction(Double[] data, String kernelDensityEstimatorBeanName);

    /**
     * Set time frame interval for a condition
     *
     * @param areaPreProcessingResults
     */
    public void setTimeInterval(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Recompute time interval for a condition; this will recompute the interval
     * according to the minimum first and last time points.
     *
     * @param areaPreProcessingResults
     */
    public void recomputeTimeInterval(AreaPreProcessingResults areaPreProcessingResults);
}
