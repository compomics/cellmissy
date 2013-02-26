package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.exception.PositionListMismatchException;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.parser.PositionListParser;
import be.ugent.maf.cellmissy.service.MicroscopeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private Experiment experiment;
    @Autowired
    private ObsepFileParser obsepFileParser;
    @Autowired
    private PositionListParser positionListParser;

    @Override
    public void init(Experiment experiment) {
        this.experiment = experiment;
        obsepFileParser.parseObsepFile(experiment.getObsepFile());
    }

    @Override
    public Map<ImagingType, List<WellHasImagingType>> processMicroscopeData() throws FileParserException, PositionListMismatchException{

        Map<ImagingType, String> imagingTypeToPosListMap = obsepFileParser.mapImagingTypetoPositionList();

        Map<ImagingType, List<WellHasImagingType>> imagingTypeMap = positionListParser.parsePositionList(imagingTypeToPosListMap, experiment.getSetupFolder());

        return imagingTypeMap;
    }
}
