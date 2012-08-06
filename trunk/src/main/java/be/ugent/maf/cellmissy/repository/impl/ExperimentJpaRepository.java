/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.repository.ExperimentRepository;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("experimentRepository")
public class ExperimentJpaRepository extends GenericJpaRepository<Experiment, Long> implements ExperimentRepository {

    @Override
    public List<Integer> findExperimentNumbersByProjectId(Integer projectId) {

        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Experiment.findExperimentNumbersByProjectId");
        byNameQuery.setParameter("projectid", projectId);
        List<Integer> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public List<Experiment> findExperimentsByProjectIdAndStatus(Integer projectId, ExperimentStatus experimentStatus) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Experiment.findExperimentsByProjectIdAndStatus");
        byNameQuery.setParameter("projectid", projectId);
        byNameQuery.setParameter("experimentStatus", experimentStatus);
        List<Experiment> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public List<Experiment> findExperimentsByProjectId(Integer projectId) {
        //annotated query
        Query byNameQuery = getEntityManager().createNamedQuery("Experiment.findExperimentsByProjectId");
        byNameQuery.setParameter("projectid", projectId);
        List<Experiment> resultList = byNameQuery.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
