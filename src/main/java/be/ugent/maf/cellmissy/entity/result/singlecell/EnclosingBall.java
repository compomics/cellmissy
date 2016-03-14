/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An enclosing ball entity: has a shape (2D ellipse), a radius, and a list of
 * enclosing points.
 *
 * @author Paola
 */
public class EnclosingBall {

    // the shape 
    private Ellipse2D shape;
    // the radius of the shape
    private double radius;
    // the enclosingPoints inside the shape
    private List<Point2D> enclosingPoints;

    /**
     * Empty constructor.
     */
    public EnclosingBall() {
    }

    public EnclosingBall(Ellipse2D ball, double radius, List<Point2D> points) {
        this.shape = ball;
        this.radius = radius;
        this.enclosingPoints = points;
    }

    // the list with enclosingPoints is updated while computing the enclosing balls
    public EnclosingBall(Ellipse2D ball, double radius) {
        this.shape = ball;
        this.radius = radius;
        this.enclosingPoints = new ArrayList<>();
    }

    public Ellipse2D getShape() {
        return shape;
    }

    public void setShape(Ellipse2D shape) {
        this.shape = shape;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Point2D> getEnclosingPoints() {
        return enclosingPoints;
    }

    public void setEnclosingPoints(List<Point2D> enclosingPoints) {
        this.enclosingPoints = enclosingPoints;
    }

    @Override
    public String toString() {
        return "centerX: " + AnalysisUtils.roundTwoDecimals(shape.getCenterX()) + ", centerY: "
                + AnalysisUtils.roundTwoDecimals(shape.getCenterY()) + "; N: " + enclosingPoints.size();
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
        return Objects.equals(this.shape, other.shape);
    }
}
