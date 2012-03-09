/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.plate.WellGUI;
import java.io.Serializable;
import java.util.List;

/**
 * This interface uses CellMiaDataService and 
 * @author Paola
 */
public interface WellService extends GenericService<Well, Long> {

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
    void updateWellGUIListWithImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell, List<WellGUI> wellGUIList);

    /**
     * This method gets a list of imaging types from the map in output from CellMiaDataService
     * @return a List of Imaging Types
     */
    List<ImagingType> getImagingTypes();
}
