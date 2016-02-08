/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree;

import be.ugent.maf.cellmissy.analysis.kdtree.exception.KeyDuplicateException;

/**
 * An interface to hold the types of editing can be performed on a KD tree.
 *
 * @author Paola
 * @param <T>
 */
public interface Editor<T> {

    public T edit(T current) throws KeyDuplicateException;

    // the basic editor
    public static abstract class BaseEditor<T> implements Editor<T> {

        final T val;

        public BaseEditor(T val) {
            this.val = val;
        }

        @Override
        public abstract T edit(T current) throws KeyDuplicateException;
    }

    // an editor to do insertions
    public static class Inserter<T> extends BaseEditor<T> {

        public Inserter(T val) {
            super(val);
        }

        @Override
        public T edit(T current) throws KeyDuplicateException {
            if (current == null) {
                return this.val;
            }
            throw new KeyDuplicateException();
        }
    }

    // an editor to do optional insertions
    public static class OptionalInserter<T> extends BaseEditor<T> {

        public OptionalInserter(T val) {
            super(val);
        }

        @Override
        public T edit(T current) {
            return (current == null) ? this.val : current;
        }
    }

    // an editor to do a replacement
    public static class Replacer<T> extends BaseEditor<T> {

        public Replacer(T val) {
            super(val);
        }

        @Override
        public T edit(T current) {
            return this.val;
        }
    }
}
