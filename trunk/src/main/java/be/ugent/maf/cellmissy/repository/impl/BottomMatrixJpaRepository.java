/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.repository.BottomMatrixRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository ("bottomMatrixRepository")
public class BottomMatrixJpaRepository extends GenericJpaRepository<BottomMatrix, Long> implements BottomMatrixRepository{
    
}
