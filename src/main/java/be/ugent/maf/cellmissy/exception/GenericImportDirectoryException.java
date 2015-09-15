/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * Exception class for the directory loading in the generic import module.
 *
 * @author Paola
 */
public class GenericImportDirectoryException extends Exception {

    public GenericImportDirectoryException() {
    }

    public GenericImportDirectoryException(String message) {
        super(message);
    }

    public GenericImportDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericImportDirectoryException(Throwable cause) {
        super(cause);
    }

    public GenericImportDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
