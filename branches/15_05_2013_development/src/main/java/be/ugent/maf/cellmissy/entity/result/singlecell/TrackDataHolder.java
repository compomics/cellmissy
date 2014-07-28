/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;

/**
 * This class keeps the pre-processing results about a cell path. These results
 * are specified on two possible levels: one is step-centric (these are the
 * instantaneous measurements at time t), and the other is cell-centric (these
 * are the measurements for an entire cell trajectory).
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TrackDataHolder {

    // track
    // the track from which the data are being computed-kept in memory
    private Track track;
    // the step-centric measurements
    private StepCentricDataHolder stepCentricDataHolder;
    // the cell-centric measurements
    private CellCentricDataHolder cellCentricDataHolder;

    /**
     * Constructor, takes a track as argument.
     *
     * @param track
     */
    public TrackDataHolder(Track track, StepCentricDataHolder stepCentricDataHolder, CellCentricDataHolder cellCentricDataHolder) {
        this.track = track;
        this.stepCentricDataHolder = stepCentricDataHolder;
        this.cellCentricDataHolder = cellCentricDataHolder;
    }

    /**
     * Getters and setters
     *
     */
    public Track getTrack() {
        return track;
    }

    public StepCentricDataHolder getStepCentricDataHolder() {
        return stepCentricDataHolder;
    }

    public void setStepCentricDataHolder(StepCentricDataHolder stepCentricDataHolder) {
        this.stepCentricDataHolder = stepCentricDataHolder;
    }

    public CellCentricDataHolder getCellCentricDataHolder() {
        return cellCentricDataHolder;
    }

    public void setCellCentricDataHolder(CellCentricDataHolder cellCentricDataHolder) {
        this.cellCentricDataHolder = cellCentricDataHolder;
    }

    @Override
    public String toString() {
        int trackNumber = track.getTrackNumber();
        Well well = track.getWellHasImagingType().getWell();
        return "track " + trackNumber + ", well " + well;
    }
}
