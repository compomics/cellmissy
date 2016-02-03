/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;

/**
 * This interface operates on a track, performing operations on 2 levels:
 * step-centric first, and cell-centric after.
 *
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface TrackOperator {

    /**
     * Do the operations on the step-centric level, i.e. compute all the
     * features related to instantaneous measurements.
     *
     * @param trackDataHolder
     */
    void operateOnSteps(TrackDataHolder trackDataHolder);

    /**
     * Do the operations on the cell-centric level, i.e. compute all the
     * features related to aggregated measurements.
     *
     * @param trackDataHolder
     */
    void operateOnCells(TrackDataHolder trackDataHolder);
}
