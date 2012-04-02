/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.repository.MatrixDimensionRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("matrixDimensionRepository")
public class MatrixDimensionJpaRepository extends GenericJpaRepository<MatrixDimension, Long> implements MatrixDimensionRepository{
    
}
