/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.repository.impl;

import be.ugent.maf.limsdesktop.entity.WellHasImagingType;
import be.ugent.maf.limsdesktop.repository.WellHasImagingTypeRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */

@Repository ("wellHasImagingTypeRepository")
public class WellHasImagingTypeJpaRepository extends GenericJpaRepository<WellHasImagingType, Long> implements WellHasImagingTypeRepository{
    
}
