package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing;

import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;

/**
 * This interface pre-processes on a track level, operating on 2 levels:
 * step-centric, and cell-centric level. The pre-processing takes care uniquely
 * of computing coordinates for quick global inspection.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface TrackPreProcessor {

    /**
     * Pre-process steps: uses the step-centric operator interface and generates time indexes, the coordinates matrix
     * (using the conversion factor information), and compute the shifted-to-zero coordinates matrix as well.
     *
     * @param trackDataHolder
     * @param conversionFactor
     */
    void preProcessSteps(TrackDataHolder trackDataHolder, double conversionFactor);

    /**
     * Pre-process cells (i.e. entire cell trajectories): uses a cell-centric operator interface and computes track
     * durations (using the time lapse information) and coordinates ranges (both x and y direction), along with net
     * displacements (both x and y direction).
     *
     * @param trackDataHolder
     * @param timeLapse
     */
    void preProcessCells(TrackDataHolder trackDataHolder, double timeLapse);
}
