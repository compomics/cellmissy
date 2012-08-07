/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.ImagingType;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface ImagingTypeRepository extends GenericRepository<ImagingType, Long> {
    
    List<ImagingType> findImagingTypesByWellId(Integer wellId);
    
}
