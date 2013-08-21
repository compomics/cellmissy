/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area;

import be.ugent.maf.cellmissy.entity.result.TimeInterval;

/**
 * This class is holding the results from the area-data pre-processing step.
 * Normalized Area, Corrected Normalized Area, Delta Area, Percentage of Area
 * Increase, Area corrected for outliers, Euclidean distances between all
 * replicates, Boolean to exclude replicates, information about the eventual
 * interaction of the user for replicates selection and time time frames
 * interval used for analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class AreaPreProcessingResults {
    // time frames

    private double[] processedTimeFrames;
    // Area Raw Data (from DB)
    private Double[][] areaRawData;
    // Normalized Area - Area at Time Frame 0 is set to the same value
    private Double[][] normalizedArea;
    // Transformed Data - from Open Area to Cell Covered Area
    private Double[][] transformedData;
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
    // user has changed standard behaviour of outliers detection?
    private boolean userSelectedReplicates;
    // time interval
    private TimeInterval timeInterval;

    /**
     * Getters and setters
     *
     * @return
     */
    public double[] getProcessedTimeFrames() {
        return processedTimeFrames;
    }

    public void setProcessedTimeFrames(double[] processedTimeFrames) {
        this.processedTimeFrames = processedTimeFrames;
    }

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

    public Double[][] getTransformedData() {
        return transformedData;
    }

    public void setTransformedData(Double[][] transformedData) {
        this.transformedData = transformedData;
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

    public boolean isUserSelectedReplicates() {
        return userSelectedReplicates;
    }

    public void setUserSelectedReplicates(boolean userSelectedReplicates) {
        this.userSelectedReplicates = userSelectedReplicates;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }
}
