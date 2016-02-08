/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EnclosingBallsCalculator {

    /**
     * For a specific radius, get the minimum number of enclosing balls of a
     * track (in the given step centric data holder), containing the track.
     *
     * @param stepCentricDataHolder
     * @param radius
     * @return
     */
    List<Ellipse2D> computeEnclosingBalls(StepCentricDataHolder stepCentricDataHolder, double radius);

}
