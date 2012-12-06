/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.util.List;
import java.util.Objects;

/**
 * This class contains list of conditions that were compared, together with results from statistical analysis
 * @author Paola Masuzzo
 */
public class AnalysisGroup {

    // list of conditions
    private List<PlateCondition> plateConditions;
    // list of area analysis results to be used in statistical comparison
    private List<AreaAnalysisResults> analysisResults;
    // matrix with p-values
    private Double[][] pValuesMatrix;
    // adjusted pvalues
    private Double[][] adjustedPValues;

    /**
     * Constructor
     * @param plateConditions
     * @param analysisResults  
     */
    public AnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> analysisResults) {
        this.plateConditions = plateConditions;
        this.analysisResults = analysisResults;
    }

    public List<PlateCondition> getPlateConditions() {
        return plateConditions;
    }

    public void setPlateConditions(List<PlateCondition> plateConditions) {
        this.plateConditions = plateConditions;
    }

    public List<AreaAnalysisResults> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(List<AreaAnalysisResults> analysisResults) {
        this.analysisResults = analysisResults;
    }

    public Double[][] getpValuesMatrix() {
        return pValuesMatrix;
    }

    public void setpValuesMatrix(Double[][] pValuesMatrix) {
        this.pValuesMatrix = pValuesMatrix;
    }

    public Double[][] getAdjustedPValues() {
        return adjustedPValues;
    }

    public void setAdjustedPValues(Double[][] adjustedPValues) {
        this.adjustedPValues = adjustedPValues;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnalysisGroup other = (AnalysisGroup) obj;
        if (!Objects.equals(this.plateConditions, other.plateConditions)) {
            return false;
        }
        if (!Objects.equals(this.analysisResults, other.analysisResults)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.plateConditions);
        hash = 19 * hash + Objects.hashCode(this.analysisResults);
        return hash;
    }
}
