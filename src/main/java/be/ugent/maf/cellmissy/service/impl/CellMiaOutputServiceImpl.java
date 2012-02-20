/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.parser.CellMiaFileParser;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.parser.PositionListParser;
import be.ugent.maf.cellmissy.parser.impl.CellMiaFileParserImpl;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl;
import be.ugent.maf.cellmissy.parser.impl.PositionListParserImpl;
import be.ugent.maf.cellmissy.service.CellMiaOutputService;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

// test
/**
 *
 * @author Paola
 */
@Service("cellMiaOutputService")
public class CellMiaOutputServiceImpl implements CellMiaOutputService {

    private CellMiaFileParser cellMiaParser = new CellMiaFileParserImpl();

    @Override
    public void processCellMiaOutput(Map<ImagingType, List<WellHasImagingType>> imagingTypePositionListMap, File cellMiaFolder) {

        //define filters to search for cellmia text files
        FilenameFilter sampleFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.startsWith("sample");
            }
        };

        FilenameFilter resultsFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.startsWith("results");
            }
        };

        FilenameFilter textfilesFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        };

        File[] sampleFiles = cellMiaFolder.listFiles(sampleFilter);

        // listFiles does not guarantee any order; sort files in alphabetical order
        Arrays.sort(sampleFiles);

        for (ImagingType imagingType : imagingTypePositionListMap.keySet()) {
            List<WellHasImagingType> wellHasImagingTypeList = imagingTypePositionListMap.get(imagingType);

            for (int i = 0; i < wellHasImagingTypeList.size(); i++) {
                WellHasImagingType wellHasImagingType = wellHasImagingTypeList.get(i);

                // iterate trough the folders and look for the text files, read them with cellmia parser
                File[] resultsFiles = sampleFiles[i].listFiles(resultsFilter);
                for (int j = 0; j < resultsFiles.length; j++) {
                    File[] textFiles = resultsFiles[j].listFiles(textfilesFilter);

                    for (int k = 0; k < textFiles.length; k++) {

                        if (textFiles[k].getName().endsWith("bulkcell.txt")) {
                            List<TimeStep> timeStepList = cellMiaParser.parseBulkCellFile(textFiles[k]);
                            for (TimeStep timeStep : timeStepList) {
                                timeStep.setWellHasImagingType(wellHasImagingType);
                            }
                            wellHasImagingType.setTimeStepCollection(timeStepList);

                        } else if (textFiles[k].getName().endsWith("tracking.txt")) {
                            List<Track> trackList = cellMiaParser.parseTrackingFile(textFiles[k]);
                            for (Track track : trackList) {
                                track.setWellHasImagingType(wellHasImagingType);
                            }
                            wellHasImagingType.setTrackCollection(trackList);

                        } else {
                            continue;
                        }
                    }
                }
            }
        }
    }
}
