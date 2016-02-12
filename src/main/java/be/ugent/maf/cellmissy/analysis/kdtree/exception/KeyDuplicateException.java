/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree.exception;

/**
 * An exception thrown if the key is already in the tree.
 *
 * @author Paola
 */
public class KeyDuplicateException extends KDException {

    public KeyDuplicateException() {
        super("Key already in tree");
    }
}
