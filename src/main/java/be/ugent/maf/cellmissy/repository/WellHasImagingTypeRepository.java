/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface WellHasImagingTypeRepository extends GenericRepository<WellHasImagingType, Long> {

    List<WellHasImagingType> findByWellIdAlgoIdAndImagingTypeId(Long wellId, Long algorithmId, Long imagingTypeId);
}
