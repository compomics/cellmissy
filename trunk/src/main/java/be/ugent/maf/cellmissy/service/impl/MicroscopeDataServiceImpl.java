package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.parser.PositionListParser;
import be.ugent.maf.cellmissy.service.MicroscopeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: niels
 * Date: 22/02/12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
@Service("microscopeDataService")
public class MicroscopeDataServiceImpl implements MicroscopeDataService {

    private File microscopeFolder;
    @Autowired
    private ObsepFileParser obsepFileParser;
    @Autowired
    private PositionListParser positionListParser;

    @Override
    public void init(File microscopeFolder, File obsepFile) {
        this.microscopeFolder = microscopeFolder;
        obsepFileParser.parseObsepFile(obsepFile);
    }

    @Override
    public Map<ImagingType, List<WellHasImagingType>> processMicroscopeData() {

        Map<ImagingType, String> imagingTypePositionListMap = obsepFileParser.mapImagingTypetoPosList();

        Map<ImagingType, List<WellHasImagingType>> imagingTypeListOfWellHasImagingTypeMap = positionListParser.parsePositionList(imagingTypePositionListMap, microscopeFolder);

        return imagingTypeListOfWellHasImagingTypeMap;
    }
}
