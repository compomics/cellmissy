/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.repository.CellLineRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("cellLineRepository")
public class CellLineJpaRepository extends GenericJpaRepository<CellLine, Long> implements CellLineRepository {

    @Override
    public List<String> findAllGrowthMedia() {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("CellLine.findAllGrowthMedia");
        List<String> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public List<String> findAllSera() {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("CellLine.findAllSera");
        List<String> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
