/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.repository.TreatmentRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("treatmentRepository")
public class TreatmentJpaRepository extends GenericJpaRepository<Treatment, Long> implements TreatmentRepository {

    @Override
    public List<Treatment> findByType(Integer treatmentType) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Treatment.findByType");
        byNameQuery.setParameter("type", treatmentType);
        List<Treatment> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
