/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.repository.EcmDensityRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("ecmDensityRepository")
public class EcmDensityJpaRepository extends GenericJpaRepository<EcmDensity, Long> implements EcmDensityRepository {

    @Override
    public EcmDensity findByEcmDensity(Double ecmDensity) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("EcmDensity.findByEcmDensity");
        byNameQuery.setParameter("ecmDensity", ecmDensity);
        List<EcmDensity> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
