/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.util.List;

/**
 * This class is holding the results from the pre processing of single cell
 * analysis. This object stays associated to a certain biological condition and
 * keeps all the track-related data. The data are computed for a single track
 * data holder and then brought all together in this class.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class SingleCellPreProcessingResults {

    // list of track data holders
    private List<TrackDataHolder> trackDataHolders;
    // data structure containing wells, tracks numbers and time indexes 
    // this data structure is on the track point level 
    private Object[][] dataStructure;
    // raw data track coordinates
    private Double[][] rawTrackCoordinatesMatrix;
    // track coordinates shifted to position (0, 0)
    private Double[][] shiftedTrackCoordinatesMatrix;
    // boolean for the motile steps computation --- ******************* to be revisited
    private boolean[] outliersVector;
    // array for instantaneous velocities
    private Double[] instantaneousVelocitiesVector;
    // array for track velocities
    private Double[] trackVelocitiesVector;
    // array for track cumulative distancse
    private Double[] cumulativeDistancesVector;
    // array for track Euclidean distancse
    private Double[] euclideanDistancesVector;
    //array for directionalities 
    private Double[] directionalitiesVector;
    // array for turning angles
    private Double[] turningAnglesVector;
    // array for track angles
    private Double[] trackAnglesVector;

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

    public Double[][] getShiftedTrackCoordinatesMatrix() {
        return shiftedTrackCoordinatesMatrix;
    }

    public void setShiftedTrackCoordinatesMatrix(Double[][] normalizedTrackCoordinatesMatrix) {
        this.shiftedTrackCoordinatesMatrix = normalizedTrackCoordinatesMatrix;
    }

    public boolean[] getOutliersVector() {
        return outliersVector;
    }

    public void setOutliersVector(boolean[] outliersVector) {
        this.outliersVector = outliersVector;
    }

    public Double[] getInstantaneousVelocitiesVector() {
        return instantaneousVelocitiesVector;
    }

    public void setInstantaneousVelocitiesVector(Double[] instantaneousVelocitiesVector) {
        this.instantaneousVelocitiesVector = instantaneousVelocitiesVector;
    }

    public Double[] getTrackVelocitiesVector() {
        return trackVelocitiesVector;
    }

    public void setTrackVelocitiesVector(Double[] trackVelocitiesVector) {
        this.trackVelocitiesVector = trackVelocitiesVector;
    }

    public Double[] getCumulativeDistancesVector() {
        return cumulativeDistancesVector;
    }

    public void setCumulativeDistancesVector(Double[] cumulativeDistancesVector) {
        this.cumulativeDistancesVector = cumulativeDistancesVector;
    }

    public Double[] getEuclideanDistancesVector() {
        return euclideanDistancesVector;
    }

    public void setEuclideanDistancesVector(Double[] euclideanDistancesVector) {
        this.euclideanDistancesVector = euclideanDistancesVector;
    }

    public Double[] getDirectionalitiesVector() {
        return directionalitiesVector;
    }

    public void setDirectionalitiesVector(Double[] directionalitiesVector) {
        this.directionalitiesVector = directionalitiesVector;
    }

    public Double[] getTurningAnglesVector() {
        return turningAnglesVector;
    }

    public void setTurningAnglesVector(Double[] turningAnglesVector) {
        this.turningAnglesVector = turningAnglesVector;
    }

    public Double[] getTrackAnglesVector() {
        return trackAnglesVector;
    }

    public void setTrackAnglesVector(Double[] trackAnglesVector) {
        this.trackAnglesVector = trackAnglesVector;
    }
}
