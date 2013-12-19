/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;

/**
 * This class keeps the pre-processing results on the track level.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataHolder {

    // track
    // the track from which the data are being computed-kept in memory
    private Track track;
    // time indexes
    // the time interval in which the track has been detected and followed in the tracking step
    private double[] timeIndexes;
    // track duration, in minutes
    private double duration;
    // matrix for track coordinates (x, y)
    // each row is a track point and contains couples of coordinates (x, y)
    private Double[][] coordinatesMatrix;
    // minimum value for the x coordinate
    private double xMin;
    // maximum value for the x coordinate
    private double xMax;
    // minimum value for the y coordinate
    private double yMin;
    // maximum value for the y coordinate
    private double yMax;
    // matrix for shifted track coordinates
    // same as the coordinatesMatrix, but the origins of migration are superimposed at (0, 0)
    private Double[][] shiftedCooordinatesMatrix;
    // matrix for delta movements in (x, y) direction
    // differences between location (x[n], y[n]) and location (x[n-1], y[n-1])
    private Double[][] deltaMovements;
    // array for  the instantaneous cell displacements
    // for a track, the minimal instantaneous speeds are derived from the displacement of the cell centroid between adjacent time points
    private Double[] instantaneousDisplacements;
    // track median displacement
    // this is the median displacement computed from all time intervals throughout a track
    private double medianDisplacement;
    // track median speed
    // this is the track median displacement divided by the track duration (time interval in which the cell has been tracked)
    private double medianSpeed;
    // double for cumulative distance (between first and last time point of the track)
    // this is the total path length travelled by the cell in its displacement
    private double cumulativeDistance;
    // double for euclidean distance (between first and last time point of the track)
    // this is the real displacement of the cell in its motion (the net distance traveled), tipically smaller than the cumulative distance
    private double euclideanDistance;
    // directionality: this is  the ratio between the euclidean and the cumulative distance
    // this parameter is also known in literature as confinement ratio or meandering index
    // since the path length is at least equal to the displacement, this coefficient can vary between 0 and 1
    private double directionality;
    // maximal displacement: the length of the longest vector among all possible pair combinations of displacements
    private double maximalDisplacement;
    // displacement ratio: displacement/maximal displacement
    private double displacementRatio;
    // outreach ratio: maximal displacement/path lenght
    private double outreachRatio;
    // array for turning angles
    // a turning angle is the observed turning angle of a cell between sequential time points (it is an instantaneous angle)
    private Double[] turningAngles;
    // track angle: the median turning angle computed from all time intervals throughout a track
    private double medianTurningAngle;

    /**
     * Constructor, takes a track as argument.
     *
     * @param track
     */
    public TrackDataHolder(Track track) {
        this.track = track;
    }

    /**
     * Getters and setters
     *
     */
    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public double[] getTimeIndexes() {
        return timeIndexes;
    }

    public void setTimeIndexes(double[] timeIndexes) {
        this.timeIndexes = timeIndexes;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Double[][] getCoordinatesMatrix() {
        return coordinatesMatrix;
    }

    public void setCoordinatesMatrix(Double[][] coordinatesMatrix) {
        this.coordinatesMatrix = coordinatesMatrix;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public Double[][] getShiftedCooordinatesMatrix() {
        return shiftedCooordinatesMatrix;
    }

    public void setShiftedCooordinatesMatrix(Double[][] shiftedCooordinatesMatrix) {
        this.shiftedCooordinatesMatrix = shiftedCooordinatesMatrix;
    }

    public Double[][] getDeltaMovements() {
        return deltaMovements;
    }

    public void setDeltaMovements(Double[][] deltaMovements) {
        this.deltaMovements = deltaMovements;
    }

    public Double[] getInstantaneousDisplacements() {
        return instantaneousDisplacements;
    }

    public void setInstantaneousDisplacements(Double[] instantaneousDisplacements) {
        this.instantaneousDisplacements = instantaneousDisplacements;
    }

    public double getMedianDisplacement() {
        return medianDisplacement;
    }

    public void setMedianDisplacement(double medianDisplacement) {
        this.medianDisplacement = medianDisplacement;
    }

    public double getMedianSpeed() {
        return medianSpeed;
    }

    public void setMedianSpeed(double medianSpeed) {
        this.medianSpeed = medianSpeed;
    }

    public double getCumulativeDistance() {
        return cumulativeDistance;
    }

    public void setCumulativeDistance(double cumulativeDistance) {
        this.cumulativeDistance = cumulativeDistance;
    }

    public double getEuclideanDistance() {
        return euclideanDistance;
    }

    public void setEuclideanDistance(double euclideanDistance) {
        this.euclideanDistance = euclideanDistance;
    }

    public double getDirectionality() {
        return directionality;
    }

    public void setDirectionality(double directionality) {
        this.directionality = directionality;
    }

    public double getMaximalDisplacement() {
        return maximalDisplacement;
    }

    public void setMaximalDisplacement(double maximalDisplacement) {
        this.maximalDisplacement = maximalDisplacement;
    }

    public double getDisplacementRatio() {
        return displacementRatio;
    }

    public void setDisplacementRatio(double displacementRatio) {
        this.displacementRatio = displacementRatio;
    }

    public double getOutreachRatio() {
        return outreachRatio;
    }

    public void setOutreachRatio(double outreachRatio) {
        this.outreachRatio = outreachRatio;
    }

    public Double[] getTurningAngles() {
        return turningAngles;
    }

    public void setTurningAngles(Double[] turningAngles) {
        this.turningAngles = turningAngles;
    }

    public double getMedianTurningAngle() {
        return medianTurningAngle;
    }

    public void setMedianTurningAngle(double medianTurningAngle) {
        this.medianTurningAngle = medianTurningAngle;
    }

    @Override
    public String toString() {
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        return "track " + trackNumber + ", well " + well;
    }
}
