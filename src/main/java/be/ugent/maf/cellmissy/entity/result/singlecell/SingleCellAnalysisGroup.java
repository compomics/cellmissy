/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import java.util.List;
import java.util.Objects;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * An entity class that holds results for a single cell analysis analysis.
 *
 * @author Paola
 */
public class SingleCellAnalysisGroup {

    // the name for the grou
    private String groupName;
    // the condition data holders to perform the analysis on
    private List<SingleCellConditionDataHolder> conditionDataHolders;
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

    public SingleCellAnalysisGroup(List<SingleCellConditionDataHolder> conditionDataHolders) {
        this.conditionDataHolders = conditionDataHolders;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<SingleCellConditionDataHolder> getConditionDataHolders() {
        return conditionDataHolders;
    }

    public void setConditionDataHolders(List<SingleCellConditionDataHolder> conditionDataHolders) {
        this.conditionDataHolders = conditionDataHolders;
    }

    public Double[][] getpValuesMatrix() {
        return pValuesMatrix;
    }

    public void setpValuesMatrix(Double[][] pValuesMatrix) {
        this.pValuesMatrix = pValuesMatrix;
    }

    public Double[][] getAdjustedPValuesMatrix() {
        return adjustedPValuesMatrix;
    }

    public void setAdjustedPValuesMatrix(Double[][] adjustedPValuesMatrix) {
        this.adjustedPValuesMatrix = adjustedPValuesMatrix;
    }

    public List<StatisticalSummary> getStatisticalSummaries() {
        return statisticalSummaries;
    }

    public void setStatisticalSummaries(List<StatisticalSummary> statisticalSummaries) {
        this.statisticalSummaries = statisticalSummaries;
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
    public String toString() {
        return groupName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleCellAnalysisGroup other = (SingleCellAnalysisGroup) obj;
        if (!Objects.equals(this.groupName, other.groupName)) {
            return false;
        }
        if (!Objects.equals(this.conditionDataHolders, other.conditionDataHolders)) {
            return false;
        }
        return Objects.equals(this.correctionMethodName, other.correctionMethodName);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
