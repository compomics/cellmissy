/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;

/**
 *
 * @author Paola
 */
public interface SingleCellStatisticsAnalyzer {

    /**
     * Generate SUMMARY STATISTICS for a certain analysis group.
     *
     * @param singleCellAnalysisGroup
     * @param statisticalTestName
     * @param parameter
     */
    void generateSummaryStatistics(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, String parameter);

    /**
     * Compute a p values matrix for a certain analysis group.
     *
     * @param singleCellAnalysisGroup
     * @param statisticalTestName
     * @param parameter
     */
    void executePairwiseComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, String parameter);

    /**
     * For a certain analysis group and an alpha level, get significance for non
     * adjusted (if boolean is false) or adjusted (boolean is true) p-values
     * matrix.
     *
     * @param singleCellAnalysisGroup
     * @param statisticalTestName
     * @param alpha
     * @param isAdjusted
     */
    void detectSignificance(SingleCellAnalysisGroup singleCellAnalysisGroup, String statisticalTestName, double alpha, boolean isAdjusted);

    /**
     * Correct p values for multiple comparisons. This is performed differently
     * according to the correction method given as as argument.
     *
     * @param singleCellAnalysisGroup
     * @param correctionMethodName
     */
    void correctForMultipleComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup, String correctionMethodName);
}
