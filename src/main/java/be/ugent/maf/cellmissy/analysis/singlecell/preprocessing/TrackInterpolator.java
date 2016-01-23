/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.preprocessing;

import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;

/**
 *
 * @author Paola
 */
public interface TrackInterpolator {

    /**
     * 
     * @param cellCentricDataHolder
     * @param stepCentricDataHolder
     * @param interpolationPoints 
     */
    void interpolateTrack(CellCentricDataHolder cellCentricDataHolder, StepCentricDataHolder stepCentricDataHolder, int interpolationPoints);
}
