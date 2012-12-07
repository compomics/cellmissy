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
public interface MultipleComparisonsCorrector {

    /**
     * Correct for multiple Comparisons
     * @param analysisGroup 
     */
    public void correctForMultipleComparisons(AnalysisGroup analysisGroup);
}
