/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.BottomMatrix;

/**
 *
 * @author Paola
 */
public interface BottomMatrixRepository extends GenericRepository<BottomMatrix, Long> {

    BottomMatrix findBottomMatrixByType(String bottomMatrixType);
}
