/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * When estimating KDEs, two or more observations are needed.
 *
 * @author Paola
 */
public class TwoOrMoreObservationsException extends Exception {

    public TwoOrMoreObservationsException() {
    }

    public TwoOrMoreObservationsException(String message) {
        super(message);
    }

    public TwoOrMoreObservationsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoOrMoreObservationsException(Throwable cause) {
        super(cause);
    }

    public TwoOrMoreObservationsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
