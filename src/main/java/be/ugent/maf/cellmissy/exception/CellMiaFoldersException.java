/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * Folder structure exception, thrown when folder structure is not properly loaded for an experiment. Automatically loading for CELLMIA input only.
 *
 * @author Paola Masuzzo
 */
public class CellMiaFoldersException extends Exception {

    public CellMiaFoldersException() {
    }

    public CellMiaFoldersException(String message) {
        super(message);
    }

    public CellMiaFoldersException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellMiaFoldersException(Throwable cause) {
        super(cause);
    }

    public CellMiaFoldersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
