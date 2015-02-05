/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 * Level for statistical significance
 * @author Paola Masuzzo
 */
public enum SignificanceLevel {
    
    LOWER(0.01), STANDARD(0.05), HIGHER(0.1);
    
    private final Double value;

    private SignificanceLevel(Double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }    
}
