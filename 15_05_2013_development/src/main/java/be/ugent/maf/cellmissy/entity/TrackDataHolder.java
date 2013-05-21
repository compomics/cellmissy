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
    private Double[][] normalizedTrackCoordinates;
    // matrix for delta movements in (x, y) direction
    private Double[][] deltaMovements;
    // array for velocitie
    private Double[] velocities;
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

    public Double[][] getNormalizedTrackCoordinates() {
        return normalizedTrackCoordinates;
    }

    public void setNormalizedTrackCoordinates(Double[][] normalizedTrackCoordinates) {
        this.normalizedTrackCoordinates = normalizedTrackCoordinates;
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

    public Double[] getAngles() {
        return angles;
    }

    public void setAngles(Double[] angles) {
        this.angles = angles;
    }
}
