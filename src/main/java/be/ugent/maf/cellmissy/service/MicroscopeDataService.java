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
 * To change this template use File | Settings | File Templates.
 */
public interface MicroscopeDataService {

    void init(File microscopeFolder, File obsepFile);

    Map<ImagingType, List<WellHasImagingType>> processMicroscopeData();

}
