/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola
 */
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
public enum TreatmentCategory {

    DRUG(1), GENERAL_TREATMENT(2);

    private final int databaseValue;

    TreatmentCategory(int databaseValue){
        this.databaseValue = databaseValue;
    }

    public int getDatabaseValue() {
        return databaseValue;
    }
}
