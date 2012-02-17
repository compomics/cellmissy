/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.repository.ExperimentRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("experimentRepository")
public class ExperimentJpaRepository extends GenericJpaRepository<Experiment, Long> implements ExperimentRepository {
    
}
