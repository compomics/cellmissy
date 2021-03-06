/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import java.util.List;

/**
 * This class represents the dose-response(s) relationship
 *
 * @author Gwendolien Sergeant
 */
public class DoseResponsePair {

    private Double dose;

    private List<Double> responses;

    /**
     * Getters
     * @return 
     */
    public Double getDose() {
        return dose;
    }

    public List<Double> getResponses() {
        return responses;
    }

    /**
     * Constructor
     * @param dose
     * @param responses
     */
    public DoseResponsePair(Double dose, List<Double> responses) {
        this.dose = dose;
        this.responses = responses;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DoseResponsePair) {
            DoseResponsePair object = (DoseResponsePair) obj; 
            return (object.getDose().equals(dose) && object.getResponses().equals(responses));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Double hash = dose;
        for (Double response : responses) {
            hash += response;
        }
        return hash.intValue();
    }
    
    
    

}
