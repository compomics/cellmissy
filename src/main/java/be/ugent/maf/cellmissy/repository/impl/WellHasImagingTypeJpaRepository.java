/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */

@Repository ("wellHasImagingTypeRepository")
public class WellHasImagingTypeJpaRepository extends GenericJpaRepository<WellHasImagingType, Long> implements WellHasImagingTypeRepository{
    
}
