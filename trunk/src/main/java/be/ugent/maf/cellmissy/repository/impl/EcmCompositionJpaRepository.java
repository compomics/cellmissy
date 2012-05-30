/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.repository.EcmCompositionRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("ecmCompositionRepository")
public class EcmCompositionJpaRepository extends GenericJpaRepository<EcmComposition, Long> implements EcmCompositionRepository {

    @Override
    public List<EcmComposition> findByMatrixDimensionName(String matrixDimensionName) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("EcmComposition.findByMatrixDimensionName");
        byNameQuery.setParameter("matrixDimension", matrixDimensionName);
        List<EcmComposition> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public void saveEcmComposition(EcmComposition ecmComposition) {
        getEntityManager().persist(ecmComposition);
    }
}
