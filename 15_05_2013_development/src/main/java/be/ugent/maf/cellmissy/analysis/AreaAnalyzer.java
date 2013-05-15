/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;

/**
 * Interface for Analysis of Area Data (after pre-processing step)
 * @author Paola Masuzzo
 */
public interface AreaAnalyzer {

    /**
     * Estimate Linear Regression Model: compute slopes and R squared coefficients
     * @param areaPreProcessingResults
     * @param areaAnalysisResults
     * @param useCorrectedData 
     * @param measuredAreaType 
     * @param timeFrames 
     */
    public void estimateLinearModel(AreaPreProcessingResults areaPreProcessingResults, AreaAnalysisResults areaAnalysisResults, boolean useCorrectedData, MeasuredAreaType measuredAreaType, double[] timeFrames);
}
