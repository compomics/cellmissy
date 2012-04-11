/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 *
 * @author Paola
 */
public enum TreatmentType {
    
    DRUG(1), GENERAL_TREATMENT(2);
    
    private int databaseValue;
    
    private TreatmentType(int databaseValue){
        this.databaseValue = databaseValue;
    }

    public int getDatabaseValue() {
        return databaseValue;
    }    
}
