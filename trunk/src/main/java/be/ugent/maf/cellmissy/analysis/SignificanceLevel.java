/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

/**
 *
 * @author Paola Masuzzo
 */
public enum SignificanceLevel {
    
    CRUCIAL(0.005), HIGH(0.05), LOW(0.01);
    
    private Double value;

    private SignificanceLevel(Double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }    
}
