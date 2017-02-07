/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Gwendolien Sergeant
 */
public class GenericDoseResponseAnalysisGroup implements DoseResponseAnalysisGroup {
    
//    The dose and response data, most likely as LinkedHashMap<Double, List<Double>>, removing the need for prepareFittingData method 
//    in the "initial" controller depending on whether the replicates are in the rows or columns
    private LinkedHashMap<Double, List<Double>> doseResponseData;
    
    //The name of the treatment to be analyzed, only for display purposes
    private String treatmentToAnalyse;

    //The results of the analysis
    private DoseResponseAnalysisResults doseResponseAnalysisResults;

    /**
     * Constructor
     * Sets the doseResponseData when creating analysis group (after data loading)
     */
    public GenericDoseResponseAnalysisGroup(ImportedDRDataHolder importedData) {
        this.doseResponseData = importedData.getDoseResponseData();
        setTreatmentToAnalyse(importedData.getTreatmentName());
    }
    
    @Override
    public DoseResponseAnalysisResults getDoseResponseAnalysisResults() {
        return doseResponseAnalysisResults;
    }

    @Override
    public String getTreatmentToAnalyse() {
        return treatmentToAnalyse;
    }

    /**
     * Exclusively used in constructor but implementation was necessary because of interface.
     * @param treatmentToAnalyse 
     */
    @Override
    public void setTreatmentToAnalyse(String treatmentToAnalyse) {
        this.treatmentToAnalyse = treatmentToAnalyse;
    }

    public LinkedHashMap<Double, List<Double>> getDoseResponseData() {
        return doseResponseData;
    }
    
    
}
