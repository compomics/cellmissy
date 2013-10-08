package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.exception.PositionListMismatchException;

import java.util.List;
import java.util.Map;

/**
 * This interface processes data from the microscope
 * @author Paola Masuzzo
 */
public interface MicroscopeDataService {

    /**
     * Initialize the service
     * @param experiment 
     */
    void init(Experiment experiment);

    /**
     * This method uses both ObsepFileParser and PositionListParser classes to process data from the microscope
     * @return a map from ImagingType to WellHasImagingType entities
     * @throws FileParserException
     * @throws PositionListMismatchException  
     */
    Map<ImagingType, List<WellHasImagingType>> processMicroscopeData() throws FileParserException, PositionListMismatchException;
}
