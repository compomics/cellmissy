/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.repository.AssayRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;

/**
 *
 * @author Paola
 */
@Repository("assayRepository")
public class AssayJpaRepository extends GenericJpaRepository<Assay, Long> implements AssayRepository {

    @Override
    public List<Assay> findByMatrixDimensionName(String matrixDimensionName) {

        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Assay.findByMatrixDimensionName");
        byNameQuery.setParameter("matrixDimension", matrixDimensionName);
        List<Assay> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public Assay findByAssayType(String assayType) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Assay.findByAssayType");
        byNameQuery.setParameter("assayType", assayType);
        List<Assay> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
