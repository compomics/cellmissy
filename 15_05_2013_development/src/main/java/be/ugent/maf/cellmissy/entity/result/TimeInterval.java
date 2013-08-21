/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result;

/**
 * Time Frames interval used for Analysis: integer values form first and last
 * time frame selected (or computed by default)
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class TimeInterval {

    private int firstTimeFrame;// first value of interval
    private int lastTimeFrame;// last value of interval
    private int proposedCutOff;// default cutoff

    /**
     * Constructor
     */
    public TimeInterval() {
    }

    /**
     * Constructor
     *
     * @param firstTimeFrame
     * @param lastTimeFrame
     */
    public TimeInterval(int firstTimeFrame, int lastTimeFrame) {
        this.firstTimeFrame = firstTimeFrame;
        this.lastTimeFrame = lastTimeFrame;
    }

    /**
     * Getters and Setters
     *
     * @return
     */
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

    public int getProposedCutOff() {
        return proposedCutOff;
    }

    public void setProposedCutOff(int proposedCutOff) {
        this.proposedCutOff = proposedCutOff;
    }

    @Override
    public String toString() {
        return "(" + firstTimeFrame + ", " + lastTimeFrame + ")";
    }
}
