/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;

/**
 * This interface uses CellMiaDataService and 
 * @author Paola
 */
public interface WellService {

    /**
     * Initializes the service
     * this method initializes also CellMiaDataService and MicroscopeDataService
     */
    void init();

    /**
     * This method uses the plate format and the first well selected by the user to position the imaged wells on the plate
     * @param imagingType
     * @param plateFormat
     * @param firstWell
     * @return a List of Well entities
     */
    List<Well> positionWellsByImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell);

    /**
     * This method gets a list of imaging types from the map in output from CellMiaDataService
     * @return a List of Imaging Types
     */
    List<ImagingType> getImagingTypes();
}
