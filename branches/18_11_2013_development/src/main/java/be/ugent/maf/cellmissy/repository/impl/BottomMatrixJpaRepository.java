/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.repository.BottomMatrixRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("bottomMatrixRepository")
public class BottomMatrixJpaRepository extends GenericJpaRepository<BottomMatrix, Long> implements BottomMatrixRepository {

    @Override
    public BottomMatrix findBottomMatrixByType(String bottomMatrixType) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("BottomMatrix.findByType");
        byNameQuery.setParameter("type", bottomMatrixType);
        List<BottomMatrix> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
