/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 *
 * @author Paola Masuzzo
 */
public class MultipleComparisonsCorrectionFactory {

    public enum correctionMethod {
        BONFERRONI, BENJAMINI, NONE;
    }

    /**
     * 
     * @return 
     */
    public static correctionMethod getBonferroniCorrection() {
        return correctionMethod.BONFERRONI;
    }

    /**
     * 
     * @return 
     */
    public static correctionMethod getBenjaminiCorrection() {
        return correctionMethod.BENJAMINI;
    }
}
