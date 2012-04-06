/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
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
     * This method uses the plate format and the first WellGui selected by the user to update the wellGui List with the right ones
     * @param imagingType
     * @param plateFormat
     * @param firstWell
     * @return a List of Well entities
     */
    void updateWellGuiListWithImagingType(ImagingType imagingType, PlateFormat plateFormat, WellGui firstWellGui, List<WellGui> wellGUIList);
/**
     * This method gets a list of imaging types from the map in output from CellMiaDataService
     * @return a List of Imaging Types
     */
    List<ImagingType> getImagingTypes();
}