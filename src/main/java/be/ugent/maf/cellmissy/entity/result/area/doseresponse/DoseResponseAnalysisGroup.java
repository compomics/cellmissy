/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import java.util.List;

/**
 * This class contains the list of conditions selected by the user for dose-
 * response analysis, together with the results of this analysis.
 *
 * @author Gwendolien
 */
public class DoseResponseAnalysisGroup {
    
    // list of conditions
    private List<PlateCondition> plateConditions;
    
    // linear model results, slopes are needed for dose-response analysis
    private List<AreaAnalysisResults> areaAnalysisResults;
    
    // list of dose response analysis results to be shown in table
    private List<DoseResponseAnalysisResults> doseResponseAnalysisResults;
    
    /**
     * Constructor
     */
    public DoseResponseAnalysisGroup() {
    }
    
    /**
     * Constructor
     *
     * @param plateConditions
     * @param areaAnalysisResults
     */
    public DoseResponseAnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> areaAnalysisResults) {
        this.plateConditions = plateConditions;
        this.areaAnalysisResults = areaAnalysisResults;
    }
    
    
    /**
     * Getters and setters
     * 
     * @return 
     */
    public List<PlateCondition> getPlateConditions() {
        return plateConditions;
    }

    public void setPlateConditions(List<PlateCondition> plateConditions) {
        this.plateConditions = plateConditions;
    }

    public List<AreaAnalysisResults> getAreaAnalysisResults() {
        return areaAnalysisResults;
    }

    public void setAreaAnalysisResults(List<AreaAnalysisResults> areaAnalysisResults) {
        this.areaAnalysisResults = areaAnalysisResults;
    }

    public List<DoseResponseAnalysisResults> getDoseResponseAnalysisResults() {
        return doseResponseAnalysisResults;
    }

    public void setDoseResponseAnalysisResults(List<DoseResponseAnalysisResults> doseResponseAnalysisResults) {
        this.doseResponseAnalysisResults = doseResponseAnalysisResults;
    }
    
}
