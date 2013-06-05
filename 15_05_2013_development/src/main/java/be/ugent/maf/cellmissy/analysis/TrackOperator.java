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
     * Generate Double vector with time indexes of track.
     *
     * @param trackDataHolder
     */
    public void generateTimeIndexes(TrackDataHolder trackDataHolder);

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
     * Compute the min and max x + min and max y
     *
     * @param trackDataHolder
     */
    public void computeCoordinatesRange(TrackDataHolder trackDataHolder);

    /**
     * Compute normalised track coordinates starting from the track point
     * matrix.
     *
     * @param trackDataHolder
     */
    public void computeShiftedTrackCoordinates(TrackDataHolder trackDataHolder);

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
    public void computeInstantaneousVelocities(TrackDataHolder trackDataHolder);

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
    public void computeTrackVelocity(TrackDataHolder trackDataHolder);

    /**
     * Compute cumulative distance between start and end point of track. This is
     * usually bigger the real displacement, and it's equal to the total path
     * length.
     *
     * @param trackDataHolder
     */
    public void computeCumulativeDistance(TrackDataHolder trackDataHolder);

    /**
     * Compute Euclidean distance between start and end point. This is the real
     * displacement of a cell along the track.
     *
     * @param trackDataHolder
     */
    public void computeEuclideanDistance(TrackDataHolder trackDataHolder);

    /**
     * Compute directionality of a certain track as the ratio between the
     * Euclidean and the cumulative distance.
     *
     * @param trackDataHolder
     */
    public void computeDirectionality(TrackDataHolder trackDataHolder);

    /**
     * Compute angles.
     *
     * @param trackDataHolder
     */
    public void computeTurningAngles(TrackDataHolder trackDataHolder);

    /**
     * Compute track angle: median across all angles for the track.
     *
     * @param trackDataHolder
     */
    public void computeTrackAngle(TrackDataHolder trackDataHolder);
}
