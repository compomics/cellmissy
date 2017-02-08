/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

/**
 * An interface for generic/area delegation purposes.
 * Contains method signatures all implementations should have.
 * @author Gwendolien Sergeant
 */
public interface DoseResponseAnalysisGroup {
    
    //analysis group is always linked with the results from the analyis
    public DoseResponseAnalysisResults getDoseResponseAnalysisResults();
   
}
