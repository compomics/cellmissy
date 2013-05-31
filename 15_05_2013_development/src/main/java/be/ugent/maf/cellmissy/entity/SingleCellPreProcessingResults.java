/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.util.List;

/**
 * This class is holding the results from the pre processing of single cell
 * analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class SingleCellPreProcessingResults {

    // list of track data holders
    private List<TrackDataHolder> trackDataHolders;
    // data structure containing wells, tracks numbers and time indexes 
    private Object[][] dataStructure;
    // raw data track coordinates
    private Double[][] rawTrackCoordinatesMatrix;
    // track coordinates normalized to position (0, 0)
    private Double[][] normalizedTrackCoordinatesMatrix;
    // array for velocities
    private Double[] instantaneousVelocitiesVector;
    // boolean for the motile steps computation
    private boolean[] outliersVector;
    // array for velocities filtered for non motile steps
    private Double[] trackVelocitiesVector;

    public List<TrackDataHolder> getTrackDataHolders() {
        return trackDataHolders;
    }

    public void setTrackDataHolders(List<TrackDataHolder> trackDataHolders) {
        this.trackDataHolders = trackDataHolders;
    }

    public Object[][] getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(Object[][] dataStructure) {
        this.dataStructure = dataStructure;
    }

    public Double[][] getRawTrackCoordinatesMatrix() {
        return rawTrackCoordinatesMatrix;
    }

    public void setRawTrackCoordinatesMatrix(Double[][] rawTrackCoordinatesMatrix) {
        this.rawTrackCoordinatesMatrix = rawTrackCoordinatesMatrix;
    }

    public Double[][] getNormalizedTrackCoordinatesMatrix() {
        return normalizedTrackCoordinatesMatrix;
    }

    public void setNormalizedTrackCoordinatesMatrix(Double[][] normalizedTrackCoordinatesMatrix) {
        this.normalizedTrackCoordinatesMatrix = normalizedTrackCoordinatesMatrix;
    }

    public Double[] getInstantaneousVelocitiesVector() {
        return instantaneousVelocitiesVector;
    }

    public void setInstantaneousVelocitiesVector(Double[] instantaneousVelocitiesVector) {
        this.instantaneousVelocitiesVector = instantaneousVelocitiesVector;
    }

    public boolean[] getOutliersVector() {
        return outliersVector;
    }

    public void setOutliersVector(boolean[] outliersVector) {
        this.outliersVector = outliersVector;
    }

    public Double[] getTrackVelocitiesVector() {
        return trackVelocitiesVector;
    }

    public void setTrackVelocitiesVector(Double[] trackVelocitiesVector) {
        this.trackVelocitiesVector = trackVelocitiesVector;
    }
}
