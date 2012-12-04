/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.PlateCondition;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct for Outliers, compute Distances between replicates
 * @author Paola Masuzzo
 */
public interface AreaPreProcessor {

    /**
     * Normalize Area values (Area @ Time Frame zero is zero)
     * @param areaPreProcessingResults 
     */
    public void computeNormalizedArea(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute increments of Area from one time frame to the following one (Delta Area Values)
     * @param areaPreProcessingResults 
     */
    public void computeDeltaArea(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute %area increase for a certain condition
     * @param areaPreProcessingResults 
     */
    public void computeAreaIncrease(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Compute Normalized Corrected Area values for a certain Condition
     * @param areaPreProcessingResults 
     */
    public void normalizeCorrectedArea(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * For Normalized and Corrected Area, compute Euclidean Distances between one replicate and all the others
     * @param areaPreProcessingResults
     */
    public void computeDistanceMatrix(AreaPreProcessingResults areaPreProcessingResults);

    /**
     * Check if a replicate can be considered as an Outlier
     * @param areaPreProcessingResults
     * @param plateCondition
     */
    public void excludeReplicates(AreaPreProcessingResults areaPreProcessingResults, PlateCondition plateCondition);

    /**
     * Detect outliers for a 2D array of double (one condition)
     * @param data
     * @return a matrix of Boolean (true if the data point is detected as an outlier, false if not)
     */
    public boolean[][] detectOutliers(Double[][] data);

    /**
     * Making use of the detect outliers method, correct data set for outliers 
     * @param data
     * @return a matrix with corrected value
     */
    public Double[][] correctForOutliers(Double[][] data);
    
    /**
     * Set time frame interval used for analysis
     * @param areaPreProcessingResults
     */
    public void setTimeInterval(AreaPreProcessingResults areaPreProcessingResults);
}
