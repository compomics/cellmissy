/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * Position List Mismatch Exception, thrown in case of mismatching between names of position list in setup folder and names of those in obsep file.
 * @author Paola Masuzzo
 */
public class PositionListMismatchException extends Exception {

    public PositionListMismatchException() {
    }

    public PositionListMismatchException(String message) {
        super(message);
    }

    public PositionListMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositionListMismatchException(Throwable cause) {
        super(cause);
    }

    public PositionListMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
