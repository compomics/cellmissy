/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrectionFactory.CorrectionMethod;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;

/**
 *
 * @author Paola Masuzzo
 */
public interface StatisticsAnalyzer {

    /**
     * Generate SUMMARY STATISTICS for a certain analysis group
     *
     * @param analysisGroup
     */
    public void generateSummaryStatistics(AnalysisGroup analysisGroup);

    /**
     * Compute a p values matrix for a certain analysis group
     *
     * @param analysisGroup
     */
    public void executePairwiseComparisons(AnalysisGroup analysisGroup);

    /**
     * For a certain analysis group and an alpha level, get significance for non adjusted (if boolean is false) or adjusted (boolean is true) p-values matrix
     *
     * @param analysisGroup
     * @param alpha
     * @param isAdjusted
     */
    public void detectSignificance(AnalysisGroup analysisGroup, double alpha, boolean isAdjusted);

    /**
     * Correct p values for multiple comparisons
     *
     * @param analysisGroup
     * @param correctionMethod
     */
    public void correctForMultipleComparisons(AnalysisGroup analysisGroup, CorrectionMethod correctionMethod);
}
