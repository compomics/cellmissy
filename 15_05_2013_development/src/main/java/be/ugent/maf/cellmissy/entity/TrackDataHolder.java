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
    private Track track;
    // matrix for track coordinates (x, y)
    private Double[][] trackCoordinatesMatrix;
    // matrix for normalized track coordinates
    private Double[][] shiftedTrackCoordinates;
    // matrix for delta movements in (x, y) direction
    private Double[][] deltaMovements;
    // array for velocities
    private Double[] velocities;
    // objects for the motile steps computation: this can be false, true or null
    private Object[] motileSteps;
    // array for velocities filtered for non motile steps
    private double meanVelocity;
    // array for angles
    private Double[] angles;

    public TrackDataHolder(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Double[][] getTrackCoordinatesMatrix() {
        return trackCoordinatesMatrix;
    }

    public void setTrackCoordinatesMatrix(Double[][] trackCoordinatesMatrix) {
        this.trackCoordinatesMatrix = trackCoordinatesMatrix;
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

    public Double[] getVelocities() {
        return velocities;
    }

    public void setVelocities(Double[] velocities) {
        this.velocities = velocities;
    }

    public Object[] getMotileSteps() {
        return motileSteps;
    }

    public void setMotileSteps(Object[] motileSteps) {
        this.motileSteps = motileSteps;
    }

    public double getMeanVelocity() {
        return meanVelocity;
    }

    public void setMeanVelocity(double meanVelocity) {
        this.meanVelocity = meanVelocity;
    }

    public Double[] getAngles() {
        return angles;
    }

    public void setAngles(Double[] angles) {
        this.angles = angles;
    }

    @Override
    public String toString() {
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        return "track " + trackNumber + ", well " + well;
    }
}
