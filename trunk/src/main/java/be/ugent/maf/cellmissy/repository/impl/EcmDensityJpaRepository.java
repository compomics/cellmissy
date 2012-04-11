/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.repository.EcmDensityRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("ecmDensityRepository")
public class EcmDensityJpaRepository extends GenericJpaRepository<EcmDensity, Long> implements EcmDensityRepository{
    
}
