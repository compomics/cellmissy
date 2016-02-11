/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.processing.impl;

import be.ugent.maf.cellmissy.analysis.kdtree.KDTree;
import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeySizeException;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.EnclosingBallsCalculator;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    public List<EnclosingBall> computeEnclosingBalls(double[] firstDimension, double[] secondDimension, KDTree<Point2D> tree, double eps) {
        List<EnclosingBall> enclosingBalls = new ArrayList<>();

        // first ball: always add it to the list
        Point2D m_0 = new Point2D.Double(firstDimension[0], secondDimension[0]);

        Ellipse2D ball = new Ellipse2D.Double();
        ball.setFrameFromCenter(m_0.getX(), m_0.getY(), m_0.getX() + eps, m_0.getY() + eps);

        EnclosingBall enclosingBall = new EnclosingBall(ball, eps);
        enclosingBall.getPoints().add(m_0);
        enclosingBalls.add(enclosingBall);

        // now start counting from 1
        for (int i = 1; i < firstDimension.length; i++) {
            Point2D m_i = new Point2D.Double(firstDimension[i], secondDimension[i]);
            // try to get the points close to the current point:
            // i.e. points m_i such that ||m_i - m_t|| 2 <= radius 
            try {
                List<Point2D> nearestPoints = tree.nearestEuclidean(new double[]{m_i.getX(), m_i.getY()}, eps);
                for (Point2D nearest : nearestPoints) {
                    boolean ballsContainPoint = ballsContainPoint(enclosingBalls, nearest);
                    if (!ballsContainPoint) {
                        ball = new Ellipse2D.Double();
                        ball.setFrameFromCenter(nearest.getX(), nearest.getY(), nearest.getX() + eps, nearest.getY() + eps);
                        enclosingBall = new EnclosingBall(ball, eps);
                        enclosingBall.getPoints().add(nearest);
                        if (!enclosingBalls.contains(enclosingBall)) {
                            enclosingBalls.add(enclosingBall);
                        }
                    } else {
                        EnclosingBall whichBallContainsPoint = whichBallContainsPoint(enclosingBalls, nearest);
                        if (!whichBallContainsPoint.getPoints().contains(m_i)) {
                            whichBallContainsPoint.getPoints().add(m_i);
                        }
                    }
                }
            } catch (KeySizeException ex) {
                Logger.getLogger(EnclosingBallsCalculatorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return enclosingBalls;
    }

    private boolean ballsContainPoint(List<EnclosingBall> enclosingBalls, Point2D point) {
        return enclosingBalls.stream().map((enclosingBall) -> enclosingBall.getBall()).anyMatch((ball) -> (ball.contains(point)));
    }

    private EnclosingBall whichBallContainsPoint(List<EnclosingBall> enclosingBalls, Point2D point) {
        EnclosingBall found = null;
        for (Iterator<EnclosingBall> it = enclosingBalls.iterator(); it.hasNext();) {
            EnclosingBall ball = it.next();
            if (ball.getBall().contains(point)) {
                found = ball;
            }
        }
        return found;
    }
}
