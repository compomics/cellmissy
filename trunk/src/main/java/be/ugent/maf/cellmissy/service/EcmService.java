/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.MatrixDimension;

/**
 *
 * @author Paola
 */
public interface EcmService extends GenericService<MatrixDimension, Long> {
    
    MatrixDimension findByDimension(String dimension);
}
