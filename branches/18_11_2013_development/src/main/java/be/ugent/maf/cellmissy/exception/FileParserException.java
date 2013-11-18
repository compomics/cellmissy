/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.exception;

/**
 * File parser exception, thrown in case of wrong format for files to be parsed
 *
 * @author Paola Masuzzo
 */
public class FileParserException extends Exception {

    public FileParserException() {
        super();
    }

    public FileParserException(String message) {
        super(message);
    }

    public FileParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileParserException(Throwable cause) {
        super(cause);
    }

    public FileParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
