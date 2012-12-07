/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.impl;

import be.ugent.maf.cellmissy.analysis.MultipleComparisonsCorrector;
import be.ugent.maf.cellmissy.entity.AnalysisGroup;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo
 */
@Component("bonferroniCorrector")
public class BonferroniCorrector implements MultipleComparisonsCorrector {

    @Override
    public void correctForMultipleComparisons(AnalysisGroup analysisGroup) {
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
        analysisGroup.setpValuesMatrix(adjustedPValues);
    }
}
