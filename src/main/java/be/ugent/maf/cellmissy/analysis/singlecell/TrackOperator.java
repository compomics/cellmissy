/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;

/**
 * Interface: track operator. This performs basic operations on the track level,
 * using the track data holder.
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
     * Compute the duration of a track; this will depend on the time interval
     * between successive frames in the time-lapse experiment.
     *
     * @param timeLapse
     * @param trackDataHolder
     */
    public void computeTrackDuration(Double timeLapse, TrackDataHolder trackDataHolder);

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
     * Compute the min and max x + min and max y.
     *
     * @param trackDataHolder
     */
    public void computeCoordinatesRange(TrackDataHolder trackDataHolder);

    /**
     * Compute shifted track coordinates starting from the track coordinates
     * matrix; shifted means track coordinates are normalised to 0.
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
     * Compute minimal instantaneous cell displacements.
     *
     * @param trackDataHolder
     */
    public void computeInstantaneousDisplacements(TrackDataHolder trackDataHolder);

    /**
     * Compute track median displacement
     *
     * @param trackDataHolder
     */
    public void computeTrackMedianDisplacement(TrackDataHolder trackDataHolder);

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
     * Compute track median speed.F
     *
     * @param trackDataHolder
     */
    public void computeTrackMeanSpeed(TrackDataHolder trackDataHolder);

    /**
     * Compute directionality of a certain track as the ratio between the
     * Euclidean and the cumulative distance.
     *
     * @param trackDataHolder
     */
    public void computeDirectionality(TrackDataHolder trackDataHolder);

    /**
     * Compute turning angles.
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
