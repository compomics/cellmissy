/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EnclosingBallsCalculator {

//    /**
//     * For a specific radius, get the minimum number of enclosing balls of a
//     * track (in the given step centric data holder), containing the track.
//     *
//     * @param stepCentricDataHolder
//     * @param radius
//     * @return
//     */
//    List<EnclosingBall> computeEnclosingBalls(StepCentricDataHolder stepCentricDataHolder, double radius);
    /**
     * 
     * @param firstDimension
     * @param secondDimension
     * @param tree
     * @param eps
     * @return 
     */
    List<EnclosingBall> computeEnclosingBalls(double[] firstDimension, double[] secondDimension, KDTree<Point2D> tree, double eps);

}
