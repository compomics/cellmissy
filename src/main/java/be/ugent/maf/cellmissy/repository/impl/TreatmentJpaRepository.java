/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
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
    public List<TreatmentType> findByCategory(Integer treatmentCategory) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("TreatmentType.findByTreatmentCategory");
        byNameQuery.setParameter("treatmentCategory", treatmentCategory);
        List<TreatmentType> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public TreatmentType findByName(String name) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("TreatmentType.findByName");
        byNameQuery.setParameter("name", name);
        List<TreatmentType> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void saveTreatmentType(TreatmentType treatmentType) {
        getEntityManager().persist(treatmentType);
    }

    @Override
    public List<String> findAllDrugSolvents() {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Treatment.findAllDrugSolvents");
        List<String> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
