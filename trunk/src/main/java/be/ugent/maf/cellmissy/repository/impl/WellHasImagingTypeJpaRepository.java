/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("wellHasImagingTypeRepository")
public class WellHasImagingTypeJpaRepository extends GenericJpaRepository<WellHasImagingType, Long> implements WellHasImagingTypeRepository {

    @Override
    public List<WellHasImagingType> findByWellIdAlgoIdAndImagingTypeId(Long wellId, Long algorithmId, Long imagingTypeId) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("WellHasImagingType.findByWellIdAlgoIdAndImagingTypeId");
        byNameQuery.setParameter("wellid", wellId);
        byNameQuery.setParameter("algorithmid", algorithmId);
        byNameQuery.setParameter("imagingTypeid", imagingTypeId);
        List<WellHasImagingType> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
