/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * This class is holding the results from the area pre-processing step.
 * Normalized Area, Corrected Normalized Area, Delta Area, Percentage of Area Increase
 * @author Paola Masuzzo
 */
public class AreaPreProcessingResultsHolder {

    // Area Raw Data (from DB)
    private Double[][] areaRawData;
    // Normalized Area - Area at Time Frame 0 is set to 0
    private Double[][] normalizedArea;
    // Delta Area between two Time Frames
    private Double[][] deltaArea;
    // % Area Increase between two Time Frames
    private Double[][] percentageAreaIncrease;
    // Area Corrected for Otliers (intra-replicate) and again Normalized
    private Double[][] normalizedCorrectedArea;
    // 2D Array containing the euclidean distances, pairwise, of a set of replicates 
    private Double[][] distanceMatrix;
    // Array of boolean: exclude (or not) a technical replicate (i.e. a well) from analysis
    private boolean[] excludeReplicates;

    /**
     * Getters and setters
     * @return 
     */
    public Double[][] getAreaRawData() {
        return areaRawData;
    }

    public void setAreaRawData(Double[][] areaRawData) {
        this.areaRawData = areaRawData;
    }

    public Double[][] getDeltaArea() {
        return deltaArea;
    }

    public void setDeltaArea(Double[][] deltaArea) {
        this.deltaArea = deltaArea;
    }

    public Double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(Double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public Double[][] getNormalizedArea() {
        return normalizedArea;
    }

    public void setNormalizedArea(Double[][] normalizedArea) {
        this.normalizedArea = normalizedArea;
    }

    public Double[][] getNormalizedCorrectedArea() {
        return normalizedCorrectedArea;
    }

    public void setNormalizedCorrectedArea(Double[][] normalizedCorrectedArea) {
        this.normalizedCorrectedArea = normalizedCorrectedArea;
    }

    public Double[][] getPercentageAreaIncrease() {
        return percentageAreaIncrease;
    }

    public void setPercentageAreaIncrease(Double[][] percentageAreaIncrease) {
        this.percentageAreaIncrease = percentageAreaIncrease;
    }

    public boolean[] getExcludeReplicates() {
        return excludeReplicates;
    }

    public void setExcludeReplicates(boolean[] excludeReplicates) {
        this.excludeReplicates = excludeReplicates;
    }
}
