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
     * Generate Track Point Matrix for a track. This is already scaling
     * according to the conversion factor, going from pixels to micrometers. If
     * the conversion factor is equal to one, data was already in micrometers
     * and no actual conversion is taken up.
     *
     * @param trackDataHolder
     * @param conversionFactor
     */
    public void generateTrackCoordinatesMatrix(TrackDataHolder trackDataHolder, double conversionFactor);

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
     * Compute motile steps: if the cell has been moved more than a certain
     * amount of pixels, described by the motile criterium.
     *
     * @param trackDataHolder
     * @param motileCriterium
     */
    public void filterNonMotileSteps(TrackDataHolder trackDataHolder);

    /**
     * Generate the velocities filtered through the motile step criterium.
     *
     * @param trackDataHolder
     */
    public void generateMeanVelocities(TrackDataHolder trackDataHolder);

    /**
     * Compute angles.
     *
     * @param trackDataHolder
     */
    public void computeAngles(TrackDataHolder trackDataHolder);
}
