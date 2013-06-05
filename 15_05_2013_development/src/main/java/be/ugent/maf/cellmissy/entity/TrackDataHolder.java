/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * This object keeps pre processing results on the track level.
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
    // matrix for track coordinates (x, y)
    // each row is a track point and contains couples (x, y)
    private Double[][] trackCoordinatesMatrix;
    // minimum value for the x coordinate
    private double xMin;
    // maximum value for the x coordinate
    private double xMax;
    // minimum value for the y coordinate
    private double yMin;
    // maximum value for the y coordinate
    private double yMax;
    // matrix for shifted track coordinates
    // same as the trackCoordinatesMatrix, but the origins of migration are superimposed at (0, 0) 
    private Double[][] shiftedTrackCoordinates;
    // matrix for delta movements in (x, y) direction
    // differences between location (x[n], y[n]) and location (x[n-1], y[n-1])
    private Double[][] deltaMovements;
    // boolean for outliers: TRUE is data point is an outlier -----**** needs to be revisited
    private boolean[] outliers;
    // array for  the instantaneous velocities
    // for a track, the instantaneous velocities are derived from the displacement of the cell centroid between adjacent time points
    private Double[] instantaneousVelocities;
    // track velocity
    // this is the median velocity computed from all time intervals throughout a track
    private double trackVelocity;
    // double for cumulative distance (between first and last time point of the track)
    // this is the total path length travelled by the cell in its displacement
    private double cumulativeDistance;
    // double for euclidean distance (between first and last time point of the track)
    // this is the real displacement of the cell in its motion, tipically smaller than the Euclidean distance
    private double euclideanDistance;
    // directionality: this is the ratio between the euclidean and the cumulative distance
    // this parameter is also known in literature as confinement ratio or meandering index
    // since the path length is at least equal to the displacement, this coefficient can vary between zero and one
    private double directionality;
    // array for turning angles
    // a turning angle is the observed turning angle of a cell between sequential time points
    private Double[] turningAngles;
    // track angle: the median turning angle computed from all time intervals throughout a track
    private double trackAngle;

    /**
     * Constructor
     *
     * @param track
     */
    public TrackDataHolder(Track track) {
        this.track = track;
    }

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

    public Double[][] getTrackCoordinatesMatrix() {
        return trackCoordinatesMatrix;
    }

    public void setTrackCoordinatesMatrix(Double[][] trackCoordinatesMatrix) {
        this.trackCoordinatesMatrix = trackCoordinatesMatrix;
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

    public Double[][] getShiftedTrackCoordinates() {
        return shiftedTrackCoordinates;
    }

    public void setShiftedTrackCoordinates(Double[][] shiftedTrackCoordinates) {
        this.shiftedTrackCoordinates = shiftedTrackCoordinates;
    }

    public Double[][] getDeltaMovements() {
        return deltaMovements;
    }

    public void setDeltaMovements(Double[][] deltaMovements) {
        this.deltaMovements = deltaMovements;
    }

    public boolean[] getOutliers() {
        return outliers;
    }

    public void setOutliers(boolean[] outliers) {
        this.outliers = outliers;
    }

    public Double[] getInstantaneousVelocities() {
        return instantaneousVelocities;
    }

    public void setInstantaneousVelocities(Double[] instantaneousVelocities) {
        this.instantaneousVelocities = instantaneousVelocities;
    }

    public double getTrackVelocity() {
        return trackVelocity;
    }

    public void setTrackVelocity(double trackVelocity) {
        this.trackVelocity = trackVelocity;
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

    public Double[] getTurningAngles() {
        return turningAngles;
    }

    public void setTurningAngles(Double[] turningAngles) {
        this.turningAngles = turningAngles;
    }

    public double getTrackAngle() {
        return trackAngle;
    }

    public void setTrackAngle(double trackAngle) {
        this.trackAngle = trackAngle;
    }

    @Override
    public String toString() {
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        return "track " + trackNumber + ", well " + well;
    }
}
