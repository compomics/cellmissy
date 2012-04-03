/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
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
    public List<Assay> findByMatrixDimension(MatrixDimension matrixDimension) {

        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Assay.findByMatrixDimension");
        byNameQuery.setParameter("matrixDimension", matrixDimension);
        List<Assay> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
