/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.repository.CellLineTypeRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("cellLineTypeRepository")
public class CellLineTypeJpaRepository extends GenericJpaRepository<CellLineType, Long> implements CellLineTypeRepository {

    @Override
    public CellLineType findByName(String cellLineName) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("CellLineType.findByName");
        byNameQuery.setParameter("name", cellLineName);
        List<CellLineType> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
