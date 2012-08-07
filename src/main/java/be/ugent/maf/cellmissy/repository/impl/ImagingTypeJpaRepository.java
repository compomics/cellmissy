/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.repository.ImagingTypeRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("imagingTypeRepository")
public class ImagingTypeJpaRepository extends GenericJpaRepository<ImagingType, Long> implements ImagingTypeRepository {

    @Override
    public List<ImagingType> findImagingTypesByWellId(Integer wellId) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("WellHasImagingType.findImagingTypesByWellId");
        byNameQuery.setParameter("wellid", wellId);
        List<ImagingType> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
