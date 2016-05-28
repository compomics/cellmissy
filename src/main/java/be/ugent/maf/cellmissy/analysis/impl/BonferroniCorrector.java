/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellAnalysisGroup;

/**
 * This is performing a multiple comparisons correction with the Bonferroni
 * algorithm.
 *
 * @author Paola Masuzzo
 */
public class BonferroniCorrector implements MultipleComparisonsCorrector {

    @Override
    public void correctForMultipleComparisons(AreaAnalysisGroup analysisGroup) {
        Double[][] pValuesMatrix = analysisGroup.getpValuesMatrix();
        Double[][] adjustedPValues = new Double[pValuesMatrix.length][pValuesMatrix.length];
        int numberOfComparisons = (pValuesMatrix.length) * (pValuesMatrix.length - 1) / 2;
        for (int i = 0; i < pValuesMatrix.length; i++) {
            for (int j = 0; j < pValuesMatrix[0].length; j++) {
                if (pValuesMatrix[i][j] != null) {
                    adjustedPValues[i][j] = pValuesMatrix[i][j] * numberOfComparisons;
                }
            }
        }
        analysisGroup.setAdjustedPValuesMatrix(adjustedPValues);
    }

    @Override
    public void correctForMultipleComparisons(SingleCellAnalysisGroup singleCellAnalysisGroup) {
        Double[][] pValuesMatrix = singleCellAnalysisGroup.getpValuesMatrix();
        Double[][] adjustedPValues = new Double[pValuesMatrix.length][pValuesMatrix.length];
        int numberOfComparisons = (pValuesMatrix.length) * (pValuesMatrix.length - 1) / 2;
        for (int i = 0; i < pValuesMatrix.length; i++) {
            for (int j = 0; j < pValuesMatrix[0].length; j++) {
                if (pValuesMatrix[i][j] != null) {
                    adjustedPValues[i][j] = pValuesMatrix[i][j] * numberOfComparisons;
                }
            }
        }
        singleCellAnalysisGroup.setAdjustedPValuesMatrix(adjustedPValues);
    }
}
