/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 *
 * @author Paola Masuzzo
 */
public interface StatisticsCalculator {
    
    /**
     * Execute a Pairwise Wilcoxon Rank Sum Test between two arrays od double
     * @param x
     * @param y
     * @return p-value associated with the test 
     */
    public double executeWilcoxonTest(double[] x, double[] y);
    
}
