/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;
import be.ugent.maf.cellmissy.entity.PlateCondition;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct for Outliers, compute Distances between replicates
 * @author Paola Masuzzo
 */
public interface AreaPreProcessor {

    /**
     * Normalize Area values (Area @ Time Frame zero is zero)
     * @param areaPreProcessingResultsHolder 
     */
    public void computeNormalizedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder);

    /**
     * Compute increments of Area from one time frame to the following one (Delta Area Values)
     * @param areaPreProcessingResultsHolder 
     */
    public void computeDeltaArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder);

    /**
     * Compute %area increase for a certain condition
     * @param areaPreProcessingResultsHolder 
     */
    public void computeAreaIncrease(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder);

    /**
     * Compute Normalized Corrected Area values for a certain Condition
     * @param areaPreProcessingResultsHolder 
     */
    public void normalizeCorrectedArea(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder);
    
    /**
     * For Normalized and Corrected Area, compute Euclidean Distances between one replicate and all the others
     * @param areaPreProcessingResultsHolder
     */
    public void computeDistanceMatrix(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder);
    
    /**
     * Check if a replicate can be considered as an Outlier
     * @param areaPreProcessingResultsHolder
     * @param plateCondition
     */
    public void excludeReplicates(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder, PlateCondition plateCondition);
    
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
     * 
     * @param areaPreProcessingResultsHolder
     * @param noiseThreshold
     */
    public void filterBackgroundNoise(AreaPreProcessingResultsHolder areaPreProcessingResultsHolder, double noiseThreshold);
}
