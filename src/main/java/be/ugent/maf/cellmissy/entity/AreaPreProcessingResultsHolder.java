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

    private Double[][] areaRawData;
    private Double[][] normalizedArea;
    private Double[][] deltaArea;
    private Double[][] percentageAreaIncrease;
    private Double[][] normalizedCorrectedArea;
    private Double[][] distanceMatrix;
    //TODO create array of booleans initialise on null ////this is meant to know if the user changed normal distance matrix behavior and what columns are relevant

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
}
