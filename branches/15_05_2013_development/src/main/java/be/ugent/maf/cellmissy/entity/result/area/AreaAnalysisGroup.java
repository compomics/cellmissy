/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.List;
import java.util.Objects;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * This class contains list of conditions that were compared, together with
 * results from statistical analysis. This is only used for the area analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class AreaAnalysisGroup {
    // name for the group

    private String groupName;
    // list of conditions
    private List<PlateCondition> plateConditions;
    // list of area analysis results to be used in statistical comparison
    private List<AreaAnalysisResults> areaAnalysisResults;
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

    /**
     * Constructor
     */
    public AreaAnalysisGroup() {
    }

    /**
     * Constructor
     *
     * @param plateConditions
     * @param areaAnalysisResults
     */
    public AreaAnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> areaAnalysisResults) {
        this.plateConditions = plateConditions;
        this.areaAnalysisResults = areaAnalysisResults;
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

    public List<AreaAnalysisResults> getAreaAnalysisResults() {
        return areaAnalysisResults;
    }

    public void setAreaAnalysisResults(List<AreaAnalysisResults> areaAnalysisResults) {
        this.areaAnalysisResults = areaAnalysisResults;
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
        final AreaAnalysisGroup other = (AreaAnalysisGroup) obj;
        if (!Objects.equals(this.plateConditions, other.plateConditions)) {
            return false;
        }
        if (!Objects.equals(this.areaAnalysisResults, other.areaAnalysisResults)) {
            return false;
        }
        return Objects.equals(this.correctionMethodName, other.correctionMethodName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.plateConditions);
        hash = 89 * hash + Objects.hashCode(this.areaAnalysisResults);
        hash = 89 * hash + (this.correctionMethodName != null ? this.correctionMethodName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return groupName;
    }
}
