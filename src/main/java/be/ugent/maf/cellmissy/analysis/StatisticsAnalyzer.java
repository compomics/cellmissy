/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.AnalysisGroup;

/**
 *
 * @author Paola Masuzzo
 */
public interface StatisticsAnalyzer {

    /**
     * Compute a p values matrix for a certain analysis group
     * @param analysisGroup 
     */
    public void computePValues(AnalysisGroup analysisGroup);
    
    /**
     * Correct p values for multiple comparisons
     * @param analysisGroup 
     */
    public void adjustPValues(AnalysisGroup analysisGroup);
}
