/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.singlecell;

/**
 * A bounding box is a box defined by minimum and maximum coordinates per
 * dimension for all positions in a track. It also contains net displacement in
 * the x and y directions.
 *
 * @author Paola
 */
public class BoundingBox {

    // minimum value for the x coordinate
    private double xMin;
    // maximum value for the x coordinate
    private double xMax;
    // minimum value for the y coordinate
    private double yMin;
    // maximum value for the y coordinate
    private double yMax;
    // the net displacement in the x direction
    private double xNetDisplacement;
    // the net displacement in the y direction
    private double yNetDisplacement;

    /**
     * Empty constructor
     */
    public BoundingBox() {
    }

    /**
     * Constructor.
     *
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     */
    public BoundingBox(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public double getxNetDisplacement() {
        return xNetDisplacement;
    }

    public void setxNetDisplacement(double xNetDisplacement) {
        this.xNetDisplacement = xNetDisplacement;
    }

    public double getyNetDisplacement() {
        return yNetDisplacement;
    }

    public void setyNetDisplacement(double yNetDisplacement) {
        this.yNetDisplacement = yNetDisplacement;
    }

}
