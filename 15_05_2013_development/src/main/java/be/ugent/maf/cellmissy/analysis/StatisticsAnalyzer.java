/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.result.AnalysisGroup;

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
    public void generateSummaryStatistics(AnalysisGroup analysisGroup, String statisticalTestName);

    /**
     * Compute a p values matrix for a certain analysis group
     *
     * @param analysisGroup
     */
    public void executePairwiseComparisons(AnalysisGroup analysisGroup, String statisticalTestName);

    /**
     * For a certain analysis group and an alpha level, get significance for non
     * adjusted (if boolean is false) or adjusted (boolean is true) p-values
     * matrix
     *
     * @param analysisGroup
     * @param alpha
     * @param isAdjusted
     */
    public void detectSignificance(AnalysisGroup analysisGroup, String statisticalTestName, double alpha, boolean isAdjusted);

    /**
     * Correct p values for multiple comparisons. This is performed differently
     * according to the correction method given as as argument.
     *
     * @param analysisGroup
     * @param correctionMethodName
     */
    public void correctForMultipleComparisons(AnalysisGroup analysisGroup, String correctionMethodName);
}
