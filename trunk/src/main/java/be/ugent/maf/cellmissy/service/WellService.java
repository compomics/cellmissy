/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Paola
 */
public interface WellService {

    /**
     * Initializes the service
     * 
     */
    void init();

    List<Well> positionWellsByImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell);

    List<ImagingType> getImagingTypes();
}
