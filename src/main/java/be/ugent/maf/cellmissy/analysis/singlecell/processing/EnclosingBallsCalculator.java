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
 * An interface to compute enclosing balls for a 2D series.
 *
 * @author Paola
 */
public interface EnclosingBallsCalculator {

    /**
     * Given a 2D series, compute the enclosing balls necessary to cover it.
     *
     * @param firstDimension: the x dimension
     * @param secondDimension: the y dimension
     * @param tree: the 2D tree containing the series points (we need to pass
     * this as an argument to the method, to avoid to initialize the tree every
     * time we change the value of epsilon).
     * @param eps: the epsilon (radius) of the enclosing balls
     * @return: a list of enclosing ball objects
     */
    List<EnclosingBall> computeEnclosingBalls(double[] firstDimension, double[] secondDimension, KDTree<Point2D> tree, double eps);

}
