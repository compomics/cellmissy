/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.repository.MatrixDimensionRepository;
import javax.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("matrixDimensionRepository")
public class MatrixDimensionJpaRepository extends GenericJpaRepository<MatrixDimension, Long> implements MatrixDimensionRepository {

    @Override
    public MatrixDimension findByDimension(String dimension) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("MatrixDimension.findByDimension");
        byNameQuery.setParameter("matrixDimension", dimension);
        List<MatrixDimension> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
