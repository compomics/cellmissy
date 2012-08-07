/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.WellHasImagingType;

/**
 *
 * @author Paola Masuzzo
 */
public interface WellHasImagingTypeRepository extends GenericRepository<WellHasImagingType, Long> {

    WellHasImagingType findByWellIdAlgoIdAndImagingTypeId(Integer wellId, Integer algorithmId, Integer imagingTypeId);
}
