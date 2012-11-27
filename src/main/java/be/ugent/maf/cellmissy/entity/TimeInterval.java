/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

/**
 * Time Frames interval used for Analysis
 * int values form first and last time frame selected (or computed by default)
 * @author Paola Masuzzo
 */
public class TimeInterval {

    // first value of interval
    private int firstTimeFrame;
    // second value of interval
    private int lastTimeFrame;

    /**
     * Constructor
     * @param firstTimeFrame
     * @param lastTimeFrame
     */
    public TimeInterval(int firstTimeFrame, int lastTimeFrame) {
        this.firstTimeFrame = firstTimeFrame;
        this.lastTimeFrame = lastTimeFrame;
    }

    public int getFirstTimeFrame() {
        return firstTimeFrame;
    }

    public void setFirstTimeFrame(int firstTimeFrame) {
        this.firstTimeFrame = firstTimeFrame;
    }

    public int getLastTimeFrame() {
        return lastTimeFrame;
    }

    public void setLastTimeFrame(int lastTimeFrame) {
        this.lastTimeFrame = lastTimeFrame;
    }
}
