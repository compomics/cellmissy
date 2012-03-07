package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: niels
 * Date: 22/02/12
 * Time: 11:11
 * This interface processes data from the microscope
 * To change this template use File | Settings | File Templates.
 */
public interface MicroscopeDataService {

    /**
     * initializes the service
     * @param microscopeFolder
     * @param obsepFile 
     */
    void init(File microscopeFolder, File obsepFile);

    /**
     * this method uses both ObsepFileParser and PositionListParser classes to process data from the microscope
     * @return a map from ImagingType to WellHasImagingType entities
     */
    Map<ImagingType, List<WellHasImagingType>> processMicroscopeData();
}
