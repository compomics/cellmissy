/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.List;
import java.util.Map;

/**
 * This interface processes data from CellMIA, i.e. sets Tracks and TimeSteps of the WellHasImagingType entities
 * @author Paola
 */
public interface CellMiaDataService {

    /**
     * initializes the service
     * @param cellMiaFolder 
     */
    void init(Experiment experiment);

    /**
     * this method gets the MicroscopeDataService, which is then used by this interface in the processCellMiaData() method
     * @return MicroscopeDataService instance
     */
    MicroscopeDataService getMicroscopeDataService();

    /**
     * this method sets Tracks and TimeSteps of the WellHasImagingType entities 
     * @return a map from ImagingType to WellHasImagingType entities
     */
    Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processCellMiaData();
    
    
    Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processAlgorithms();
}
