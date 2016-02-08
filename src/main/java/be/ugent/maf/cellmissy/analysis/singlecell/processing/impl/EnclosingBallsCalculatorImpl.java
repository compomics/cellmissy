/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyDuplicateException;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeySizeException;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallsCalculator;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * An implementation for the "enclosing balls calculator" interface.
 *
 * @author Paola
 */
@Component("enclosingBallsCalculator")
public class EnclosingBallsCalculatorImpl implements EnclosingBallsCalculator {

    // a KD tree of geometric points
    private final KDTree<GeometricPoint> tree = new KDTree(2);

    @Override
    public List<Ellipse2D> computeEnclosingBalls(StepCentricDataHolder stepCentricDataHolder, double radius) {

        initKDTree(stepCentricDataHolder);
        List<Ellipse2D> enclosingBalls = new ArrayList<>();

        for (TrackPoint trackPoint : stepCentricDataHolder.getTrack().getTrackPointList()) {
            GeometricPoint geometricPoint = trackPoint.getGeometricPoint();
            Ellipse2D currentBall = new Ellipse2D.Double(geometricPoint.getX() - radius, geometricPoint.getY() + radius, radius, radius);

            double[] key = new double[]{geometricPoint.getX(), geometricPoint.getY()};
            try {
                // get the nearest points inside the distance given by the radius
                List<GeometricPoint> nearestNeigh = tree.nearestEuclidean(key, radius);
                for (GeometricPoint neigh : nearestNeigh) {
                    if (!geometricPoint.equals(neigh)) {
                        if (!ballsContainPoint(enclosingBalls, neigh)) {
                            enclosingBalls.add(currentBall);
                            Ellipse2D nextEll = new Ellipse2D.Double(neigh.getX() - radius, neigh.getY() + radius, radius, radius);
                            enclosingBalls.add(nextEll);
                        }
                    }
                }
            } catch (KeySizeException ex) {
                Logger.getLogger(EnclosingBallsCalculatorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return enclosingBalls;

    }

    /**
     * Initialize the KD-tree.
     *
     * @param stepCentricDataHolder
     */
    private void initKDTree(StepCentricDataHolder stepCentricDataHolder) {
        for (TrackPoint trackPoint : stepCentricDataHolder.getTrack().getTrackPointList()) {
            GeometricPoint geometricPoint = trackPoint.getGeometricPoint();
            double[] key = new double[]{geometricPoint.getX(), geometricPoint.getY()};
            try {
                tree.insert(key, geometricPoint);
            } catch (KeySizeException | KeyDuplicateException ex) {
                Logger.getLogger(EnclosingBallsCalculatorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param balls
     * @param point
     * @return
     */
    private boolean ballsContainPoint(List<Ellipse2D> balls, GeometricPoint point) {

        for (Ellipse2D ball : balls) {
            if (ball.contains(point.getX(), point.getY())) {
                return true;
            }
        }

        return false;
    }
}
