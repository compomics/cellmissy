/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;
import java.util.Objects;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * This class contains list of conditions that were compared, together with
 * results from statistical analysis.
 *
 * @author Paola Masuzzo
 */
public class AnalysisGroup {

    // name for the group
    private String groupName;
    // list of conditions
    private List<PlateCondition> plateConditions;
    // list of area analysis results to be used in statistical comparison
    private List<AreaAnalysisResults> analysisResults;
    // matrix with p-values
    private Double[][] pValuesMatrix;
    // matrix with adjusted p-values
    private Double[][] adjustedPValuesMatrix;
    // Summary Statistics for each Condition of the group
    private List<StatisticalSummary> statisticalSummaries;
    // correction method chosen for multiple comparisons correction
    private String correctionMethodName;
    // boolean to keep significances
    private boolean[][] significances;

    public AnalysisGroup() {
    }    
    
    /**
     * Constructor
     *
     * @param plateConditions
     * @param analysisResults
     */
    public AnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> analysisResults) {
        this.plateConditions = plateConditions;
        this.analysisResults = analysisResults;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public List<StatisticalSummary> getStatisticalSummaries() {
        return statisticalSummaries;
    }

    public void setStatisticalSummaries(List<StatisticalSummary> statisticalSummaries) {
        this.statisticalSummaries = statisticalSummaries;
    }

    public Double[][] getAdjustedPValuesMatrix() {
        return adjustedPValuesMatrix;
    }

    public void setAdjustedPValuesMatrix(Double[][] adjustedPValuesMatrix) {
        this.adjustedPValuesMatrix = adjustedPValuesMatrix;
    }

    public String getCorrectionMethodName() {
        return correctionMethodName;
    }

    public void setCorrectionMethodName(String correctionMethodName) {
        this.correctionMethodName = correctionMethodName;
    }

    public boolean[][] getSignificances() {
        return significances;
    }

    public void setSignificances(boolean[][] significances) {
        this.significances = significances;
    }

    @Override
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
        if (!Objects.equals(this.correctionMethodName, other.correctionMethodName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.plateConditions);
        hash = 89 * hash + Objects.hashCode(this.analysisResults);
        hash = 89 * hash + (this.correctionMethodName != null ? this.correctionMethodName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return groupName;
    }
}
