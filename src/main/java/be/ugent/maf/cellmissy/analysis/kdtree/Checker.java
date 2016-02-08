/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.kdtree;

/**
 *
 * @author Paola
 * @param <T>
 */
public interface Checker<T> {
    public boolean usable(T v);
}