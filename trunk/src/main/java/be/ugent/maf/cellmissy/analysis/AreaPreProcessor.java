/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AreaPreProcessingResultsHolder;

/**
 * Interface for area data pre-processing: Normalize Area, Identify and Correct for Outliers
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
     * Using the Outlier Interface, compute OUTLIERS values for a certain dataset
     * @param data
     * @return 
     */
    public double[] computeOutliers(double[] data);

    /**
     * Using the outlier interface, compute Corrected Values (Outliers are being deleted from dataset)
     * @param data
     * @return 
     */
    public double[] correctForOutliers(double[] data);
}