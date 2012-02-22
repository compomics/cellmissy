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
import be.ugent.maf.cellmissy.service.CellMiaDataService;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import be.ugent.maf.cellmissy.service.MicroscopeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// test
/**
 *
 * @author Paola
 */
@Service("cellMiaDataService")
public class CellMiaDataServiceImpl implements CellMiaDataService {

    @Autowired
    private MicroscopeDataService microscopeDataService;
    @Autowired
    private CellMiaFileParser cellMiaFileParser;

    @Override
    public Map<ImagingType, List<WellHasImagingType>> processCellMiaData(File cellMiaFolder) {

        Map<ImagingType, List<WellHasImagingType>> imagingTypeListOfWellHasImagingTypeMap = microscopeDataService.processMicroscopeData();

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

        for (ImagingType imagingType : imagingTypeListOfWellHasImagingTypeMap.keySet()) {
            List<WellHasImagingType> wellHasImagingTypeList = imagingTypeListOfWellHasImagingTypeMap.get(imagingType);

            for (int i = 0; i < wellHasImagingTypeList.size(); i++) {
                WellHasImagingType wellHasImagingType = wellHasImagingTypeList.get(i);

                // iterate trough the folders and look for the text files, read them with cellmia parser
                File[] resultsFiles = sampleFiles[i].listFiles(resultsFilter);
                for (int j = 0; j < resultsFiles.length; j++) {
                    File[] textFiles = resultsFiles[j].listFiles(textfilesFilter);

                    for (File textFile : textFiles) {

                        if (textFile.getName().endsWith("bulkcell.txt")) {
                            List<TimeStep> timeStepList = cellMiaFileParser.parseBulkCellFile(textFile);
                            for (TimeStep timeStep : timeStepList) {
                                timeStep.setWellHasImagingType(wellHasImagingType);
                            }
                            wellHasImagingType.setTimeStepCollection(timeStepList);

                        } else if (textFile.getName().endsWith("tracking.txt")) {
                            List<Track> trackList = cellMiaFileParser.parseTrackingFile(textFile);
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

        return imagingTypeListOfWellHasImagingTypeMap;
    }

    @Override
    public MicroscopeDataService getMicroscopeDataService() {
        return microscopeDataService;
    }
}
