/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.analysis.impl.BenjaminiCorrector;
import be.ugent.maf.cellmissy.analysis.impl.BonferroniCorrector;

/**
 * Factory for multiple test correction 
 * @author Paola Masuzzo
 */
public class MultipleComparisonsCorrectionFactory {

    //implementations of Corrector
    private static BonferroniCorrector bonferroniCorrector = new BonferroniCorrector();
    private static BenjaminiCorrector benjaminiCorrector = new BenjaminiCorrector();

    /**
     * Correction method
     */
    public enum CorrectionMethod {

        BONFERRONI, BENJAMINI, NONE;
    }

    /**
     * Get the corrector according to correction method
     * @param correctionMethod
     * @return 
     */
    public static MultipleComparisonsCorrector getCorrector(CorrectionMethod correctionMethod) {
        MultipleComparisonsCorrector multipleComparisonsCorrector = null;
        if (correctionMethod.equals(CorrectionMethod.BONFERRONI)) {
            multipleComparisonsCorrector = bonferroniCorrector;
        } else if (correctionMethod.equals(CorrectionMethod.BENJAMINI)) {
            multipleComparisonsCorrector = benjaminiCorrector;
        }
        return multipleComparisonsCorrector;
    }
}
