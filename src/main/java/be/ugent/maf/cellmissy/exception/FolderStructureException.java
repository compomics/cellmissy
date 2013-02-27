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
public class FolderStructureException extends Exception {

    public FolderStructureException() {
    }

    public FolderStructureException(String message) {
        super(message);
    }

    public FolderStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderStructureException(Throwable cause) {
        super(cause);
    }

    public FolderStructureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
