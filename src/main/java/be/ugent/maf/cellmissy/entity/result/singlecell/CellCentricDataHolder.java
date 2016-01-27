/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

/**
 * This class keeps the measurements about a certain track, on a cell-centric
 * level, i.e. averaged across all the time points.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class CellCentricDataHolder {

    // track duration, in minutes
    private double trackDuration;
    // minimum value for the x coordinate
    private double xMin;
    // maximum value for the x coordinate
    private double xMax;
    // minimum value for the y coordinate
    private double yMin;
    // maximum value for the y coordinate
    private double yMax;
    // net displacement in the x direction
    private double xNetDisplacement;
    // net displacement in the y direction
    private double yNetDisplacement;
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
    private double endPointDirectionalityRatio;
    // median directionality ratio
    // when computed in time (not end-point measurement!), the median directionality ratio is derived
    private double medianDirectionalityRatio;
    // the convex hull of the track: the convex polygon containing all the points of the track
    private ConvexHull convexHull;
    // displacement ratio: displacement/maximal displacement
    private double displacementRatio;
    // outreach ratio: maximal displacement/path length
    private double outreachRatio;
    // track angle: the median turning angle computed from all time intervals throughout a track
    private double medianTurningAngle;
    //@todo: to be deleted afterwards, for testing only
    private int label;
    // the root mean squared deviation from average displacement
    private double RMSDeviation;
    // the median direction autocorrelation, computed for time interval of size 1
    private double medianDirectionAutocorrelation;

    /**
     * Empty Constructor
     */
    public CellCentricDataHolder() {
    }

    /**
     * Getters and setters.
     *
     * @return
     */
    public double getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(double trackDuration) {
        this.trackDuration = trackDuration;
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

    public double getxNetDisplacement() {
        return xNetDisplacement;
    }

    public void setxNetDisplacement(double xNetDisplacement) {
        this.xNetDisplacement = xNetDisplacement;
    }

    public double getyNetDisplacement() {
        return yNetDisplacement;
    }

    public void setyNetDisplacement(double yNetDisplacement) {
        this.yNetDisplacement = yNetDisplacement;
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

    public double getEndPointDirectionalityRatio() {
        return endPointDirectionalityRatio;
    }

    public void setEndPointDirectionalityRatio(double endPointDirectionalityRatio) {
        this.endPointDirectionalityRatio = endPointDirectionalityRatio;
    }

    public double getMedianDirectionalityRatio() {
        return medianDirectionalityRatio;
    }

    public void setMedianDirectionalityRatio(double medianDirectionalityRatio) {
        this.medianDirectionalityRatio = medianDirectionalityRatio;
    }

    public ConvexHull getConvexHull() {
        return convexHull;
    }

    public void setConvexHull(ConvexHull convexHull) {
        this.convexHull = convexHull;
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

    public double getMedianTurningAngle() {
        return medianTurningAngle;
    }

    public void setMedianTurningAngle(double medianTurningAngle) {
        this.medianTurningAngle = medianTurningAngle;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double getRMSDeviation() {
        return RMSDeviation;
    }

    public void setRMSDeviation(double RMSDeviation) {
        this.RMSDeviation = RMSDeviation;
    }

    public double getMedianDirectionAutocorrelation() {
        return medianDirectionAutocorrelation;
    }

    public void setMedianDirectionAutocorrelation(double medianDirectionAutocorrelation) {
        this.medianDirectionAutocorrelation = medianDirectionAutocorrelation;
    }
}
