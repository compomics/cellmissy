/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Experiment;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface ExperimentRepository extends GenericRepository<Experiment, Long> {

    List<Integer> findExperimentNumbersByProjectId(Integer projectId);
}
