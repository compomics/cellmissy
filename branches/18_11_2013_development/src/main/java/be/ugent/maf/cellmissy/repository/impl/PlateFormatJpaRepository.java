/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.repository.PlateFormatRepository;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("plateFormatRepository")
public class PlateFormatJpaRepository extends GenericJpaRepository<PlateFormat, Long> implements PlateFormatRepository {

    @Override
    public PlateFormat findByFormat(int format) {

        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("PlateFormat.findByFormat");
        byNameQuery.setParameter("format", format);
        List<PlateFormat> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
