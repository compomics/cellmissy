/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import java.util.List;
import java.util.Map;

/**
 * This interface uses CellMiaDataService and 
 * @author Paola
 */
public interface WellService extends GenericService<Well, Long> {

    /**
     * Initializes the service
     * this method initializes also CellMiaDataService and MicroscopeDataService
     * @param experiment 
     */
    void init(Experiment experiment);

    /**
     * This method uses the plate format and the first WellGui selected by the user to update the wellGui List with the right ones
     * @param imagingType
     * @param plateFormat
     * @param firstWellGui
     * @param wellGUIList  
     */
    void updateWellGuiListWithImagingType(ImagingType imagingType, PlateFormat plateFormat, WellGui firstWellGui, List<WellGui> wellGUIList);

    /**
     * This method gets a list of imaging types from the map in output from CellMiaDataService
     * @return a List of Imaging Types
     */
    List<ImagingType> getImagingTypes();

    /**
     * This method gets a map between algorithms and samples (indexed by imaging types)
     * @return a map Algorithm, map imaging type--wellHasImagingType
     */
    Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> getMap();

    /**
     * find all algorithms for one wellId
     * @param wellId
     * @return 
     */
    List<Algorithm> findAlgosByWellId(Integer wellId);

    /**
     * find all imaging types for one wellId
     * @param wellId
     * @return 
     */
    List<ImagingType> findImagingTypesByWellId(Integer wellId);

    /**
     * fetch time steps collection only for some wellHasImagingTypes
     * @param well
     * @param AlgorithmId
     * @param ImagingTpeId  
     */
    void fetchTimeSteps(Well well, Integer AlgorithmId, Integer ImagingTpeId);
}
