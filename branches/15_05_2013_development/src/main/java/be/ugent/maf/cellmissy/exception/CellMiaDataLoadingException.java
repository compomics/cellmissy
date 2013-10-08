/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * CellMia data loading exception, thrown when no data to be imported are found, or number of samples expected is different from number of folders to process.
 *
 * @author Paola Masuzzo
 */
public class CellMiaDataLoadingException extends Exception {

    public CellMiaDataLoadingException() {
    }

    public CellMiaDataLoadingException(String message) {
        super(message);
    }

    public CellMiaDataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellMiaDataLoadingException(Throwable cause) {
        super(cause);
    }

    public CellMiaDataLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
