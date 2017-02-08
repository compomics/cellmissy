/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import java.util.List;

/**
 *
 * @author Gwendolien Sergeant
 */
public class GenericDoseResponseAnalysisGroup implements DoseResponseAnalysisGroup {

//    The dose and response data, most likely as LinkedHashMap<Double, List<Double>>, removing the need for prepareFittingData method 
//    in the "initial" controller depending on whether the replicates are in the rows or columns
    private List<DoseResponsePair> doseResponseData;

    //The results of the analysis
    private DoseResponseAnalysisResults doseResponseAnalysisResults;

    /**
     * Constructor. 
     * Sets the doseResponseData when creating analysis group 
     * @param dataToAnalyze
     */
    public GenericDoseResponseAnalysisGroup(List<DoseResponsePair> dataToAnalyze) {
        this.doseResponseData = dataToAnalyze;
        this.doseResponseAnalysisResults = new DoseResponseAnalysisResults();
    }

    /**
     * Getters
     * @return 
     */
    @Override
    public DoseResponseAnalysisResults getDoseResponseAnalysisResults() {
        return doseResponseAnalysisResults;
    }

    public List<DoseResponsePair> getDoseResponseData() {
        return doseResponseData;
    }

}
