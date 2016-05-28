/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;

/**
 *
 * @author Paola Masuzzo
 */
public interface MultipleComparisonsCorrector {

    /**
     * Correct for multiple Comparisons.
     *
     * @param analysisGroup
     */
    void correctForMultipleComparisons(AreaAnalysisGroup analysisGroup);

    void correctForMultipleComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup);
}
