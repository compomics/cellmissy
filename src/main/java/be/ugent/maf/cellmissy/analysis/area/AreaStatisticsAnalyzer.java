/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area;

import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;

/**
 * Interface for the statistical analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface AreaStatisticsAnalyzer {

    /**
     * Generate SUMMARY STATISTICS for a certain analysis group.
     *
     * @param areaAnalysisGroup
     * @param statisticalTestName
     */
    void generateSummaryStatistics(AreaAnalysisGroup areaAnalysisGroup, String statisticalTestName);

    /**
     * Compute a p values matrix for a certain analysis group.
     *
     * @param areaAnalysisGroup
     * @param statisticalTestName
     */
    void executePairwiseComparisons(AreaAnalysisGroup areaAnalysisGroup, String statisticalTestName);

    /**
     * For a certain analysis group and an alpha level, get significance for non
     * adjusted (if boolean is false) or adjusted (boolean is true) p-values
     * matrix.
     *
     * @param areaAnalysisGroup
     * @param statisticalTestName
     * @param alpha
     * @param isAdjusted
     */
    void detectSignificance(AreaAnalysisGroup areaAnalysisGroup, String statisticalTestName, double alpha, boolean isAdjusted);

    /**
     * Correct p values for multiple comparisons. This is performed differently
     * according to the correction method given as as argument.
     *
     * @param areaAnalysisGroup
     * @param correctionMethodName
     */
    void correctForMultipleComparisons(AreaAnalysisGroup areaAnalysisGroup, String correctionMethodName);
}
