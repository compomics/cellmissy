/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.repository.AlgorithmRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("algorithmRepository")
public class AlgorithmJpaRepository extends GenericJpaRepository<Algorithm, Long> implements AlgorithmRepository {

    @Override
    public List<Algorithm> findAlgosByWellId(Integer wellId) {
        
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("WellHasImagingType.findAlgosByWellId");
        byNameQuery.setParameter("wellid", wellId);
        List<Algorithm> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
