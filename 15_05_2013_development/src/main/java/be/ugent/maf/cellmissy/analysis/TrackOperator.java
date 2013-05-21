/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis;

import be.ugent.maf.cellmissy.entity.TrackDataHolder;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface TrackOperator {

    /**
     * Generate Track Point Matrix for a track.
     *
     * @param track
     * @param trackDataHolder
     */
    public void generateTrackCoordinatesMatrix(TrackDataHolder trackDataHolder);

    /**
     * Compute normalised track coordinates starting from the track point
     * matrix.
     *
     * @param trackDataHolder
     */
    public void computeNormalizedTrackCoordinates(TrackDataHolder trackDataHolder);

    /**
     * Compute delta movements matrix.
     *
     * @param trackDataHolder
     */
    public void computeDeltaMovements(TrackDataHolder trackDataHolder);

    /**
     * Compute velocities.
     *
     * @param trackDataHolder
     */
    public void computeVelocities(TrackDataHolder trackDataHolder);

    /**
     * Compute angles.
     *
     * @param trackDataHolder
     */
    public void computeAngles(TrackDataHolder trackDataHolder);
}
