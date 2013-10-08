/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Algorithm;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface AlgorithmRepository extends GenericRepository<Algorithm, Long> {
    
    List<Algorithm> findAlgosByWellId(Long wellId);
}
