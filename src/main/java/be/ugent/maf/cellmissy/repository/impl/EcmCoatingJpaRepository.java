/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.EcmCoating;
import be.ugent.maf.cellmissy.repository.EcmCoatingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository ("ecmCoatingRepository")
public class EcmCoatingJpaRepository extends GenericJpaRepository<EcmCoating, Long> implements EcmCoatingRepository{
    
}
