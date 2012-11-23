/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * Time Frames interval used for Analysis
 * @author Paola Masuzzo
 */
public class TimeInterval {

    // first value of interval
    private int firstTimePoint;
    // second values of interval
    private int lastTimePoint;

    /**
     * Constructor
     * @param firstTimePoint
     * @param lastTimePoint
     */
    public TimeInterval(int firstTimePoint, int lastTimePoint) {
        this.firstTimePoint = firstTimePoint;
        this.lastTimePoint = lastTimePoint;
    }

    public double getFirstTimePoint() {
        return firstTimePoint;
    }

    public void setFirstTimePoint(int firstTimePoint) {
        this.firstTimePoint = firstTimePoint;
    }

    public double getLastTimePoint() {
        return lastTimePoint;
    }

    public void setLastTimePoint(int lastTimePoint) {
        this.lastTimePoint = lastTimePoint;
    }
}
