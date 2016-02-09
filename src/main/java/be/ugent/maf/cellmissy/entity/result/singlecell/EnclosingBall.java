/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An open ball enclosing a geometric point of a track. Contains a radius, the
 * correspondent ellipse and the number of points inside it.
 *
 * @author Paola
 */
public class EnclosingBall {

    // the ball 
    private Ellipse2D ball;
    // the radius of the ball
    private double radius;
    // the points inside the ball
    private List<GeometricPoint> points;

    /**
     * Empty constructor.
     */
    public EnclosingBall() {
    }

    public EnclosingBall(Ellipse2D ball, double radius, List<GeometricPoint> points) {
        this.ball = ball;
        this.radius = radius;
        this.points = points;
    }

    // the list with points is updated while computing the enclosing balls
    public EnclosingBall(Ellipse2D ball, double radius) {
        this.ball = ball;
        this.radius = radius;
        this.points = new ArrayList<>();
    }

    public Ellipse2D getBall() {
        return ball;
    }

    public void setBall(Ellipse2D ball) {
        this.ball = ball;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<GeometricPoint> getPoints() {
        return points;
    }

    public void setPoints(List<GeometricPoint> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "ball radius: " + radius + "; centerX: " + ball.getCenterX() + ", centerY: " + ball.getCenterY() + "; N: " + points.size();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnclosingBall other = (EnclosingBall) obj;
        if (!Objects.equals(this.ball, other.ball)) {
            return false;
        }
        return true;
    }
}
