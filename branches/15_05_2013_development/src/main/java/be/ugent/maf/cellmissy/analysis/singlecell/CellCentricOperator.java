/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;

/**
 * An interface to operate on cell-centric data. As such, works basically with a
 * cell centric data holder entity. However, a step centric data holder entity
 * is also necessary to get back the instantaneous measurements.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface CellCentricOperator {

    /**
     * Compute the duration of a track.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeTrackDuration(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute the min and max x and y coordinates, along with the net
     * displacements in the two directions.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeCoordinatesRange(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute track median displacement.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeMedianDisplacement(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute cumulative distance between start and end point of track. This is
     * usually bigger the real displacement, and it's equal to the total path
     * length.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeCumulativeDistance(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute Euclidean distance between start and end point. This is the real
     * displacement of a cell along the track.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeEuclideanDistance(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute track median speed.
     *
     * @param cellCentricDataHolder
     */
    public void computeMedianSpeed(CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute directionality of a certain track as the ratio between the
     * Euclidean and the cumulative distance.
     *
     * @param cellCentricDataHolder
     */
    public void computeEndPointDirectionalityRatio(CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute the median directionality ratio: the directionality ratio is
     * computed in time and then the median value is derived.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeMedianDirectionalityRatio(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute convex hull and relative measurements for a certain track.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeConvexHull(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute displacement ratio of a certain track: the net displacement
     * (Euclidean distance) travelled by the cell, divided by the maximum span
     * (the length of the segment connecting the two most distant points on the
     * cell track).
     *
     * @param cellCentricDataHolder
     */
    public void computeDisplacementRatio(CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute outreach ratio of a certain track: the max span (the length of
     * the segment connecting the two most distant points on the cell track)
     * divided by the cumulative distance travelled by the cell.
     *
     * @param cellCentricDataHolder
     */
    public void computeOutreachRatio(CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute track angle: median across all angles for the track.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeMedianTurningAngle(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);

    /**
     * Compute median direction autocorrelations at time interval of size (step)
     * 1.
     *
     * @param stepCentricDataHolder
     * @param cellCentricDataHolder
     */
    public void computeMedianDirectionAutocorrelation(StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder);
}
