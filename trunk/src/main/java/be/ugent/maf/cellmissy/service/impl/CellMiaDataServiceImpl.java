/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
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
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola
 */
@Service("cellMiaDataService")
public class CellMiaDataServiceImpl implements CellMiaDataService {

    private Experiment experiment;
    @Autowired
    private MicroscopeDataService microscopeDataService;
    @Autowired
    private CellMiaFileParser cellMiaFileParser;
    private Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap;
    private Map<ImagingType, List<WellHasImagingType>> imagingTypeMap;
    private static final Logger LOG = Logger.getLogger(CellMiaDataService.class);

    @Override
    public void init(Experiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processCellMiaData() {

        long currentTimeMillis = System.currentTimeMillis();
        imagingTypeMap = microscopeDataService.processMicroscopeData();
        List<File> batchFiles = new ArrayList<>();
        algoMap = new HashMap<>();
        File[] algoFiles = experiment.getMiaFolder().listFiles();

        for (File file : algoFiles) {

            if (!file.getName().endsWith("algo-0")) {
                Algorithm algo = new Algorithm();
                algo.setAlgorithmName(file.getName());
                algo.setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
                Map<ImagingType, List<WellHasImagingType>> map = copyMap();
                batchFiles.addAll(Arrays.asList(file.listFiles()));
                for (File batchFile : batchFiles) {

                    // sample folders
                    // the number of sampleFiles is equal to the number of WellHasImagingType entities for one algorithm
                    File[] sampleFiles = batchFile.listFiles(sampleFilter);

                    // listFiles does not guarantee any order; sort files in alphabetical order
                    Arrays.sort(sampleFiles);

                    int imageTypeStartFolder = 0;
                    for (ImagingType imagingType : map.keySet()) {
                        List<WellHasImagingType> wellHasImagingTypeList = map.get(imagingType);

                        for (int i = imageTypeStartFolder; i < wellHasImagingTypeList.size() + imageTypeStartFolder; i++) {
                            WellHasImagingType wellHasImagingType = wellHasImagingTypeList.get(i - imageTypeStartFolder);

                            wellHasImagingType.setAlgorithm(algo);

                            // iterate trough the folders and look for the text files, parse the files with cellMiaFileParser
                            // results folders
                            File[] resultsFiles = sampleFiles[i].listFiles(resultsFilter);
                            for (int j = 0; j < resultsFiles.length; j++) {
                                // text files
                                File[] textFiles = resultsFiles[j].listFiles(textfilesFilter);

                                for (File textFile : textFiles) {

                                    // parse bulk cell file
                                    if (textFile.getName().endsWith("bulkcell.txt")) {
                                        List<TimeStep> timeStepList = cellMiaFileParser.parseBulkCellFile(textFile);
                                        for (TimeStep timeStep : timeStepList) {
                                            timeStep.setWellHasImagingType(wellHasImagingType);
                                        }
                                        wellHasImagingType.setTimeStepCollection(timeStepList);

                                        // parse tracking cell file
                                    } else if (textFile.getName().endsWith("tracking.txt")) {
                                        List<Track> trackList = cellMiaFileParser.parseTrackingFile(textFile);
                                        for (Track track : trackList) {
                                            track.setWellHasImagingType(wellHasImagingType);
                                        }
                                        wellHasImagingType.setTrackCollection(trackList);
                                    }
                                }
                            }
                        }
                        imageTypeStartFolder += wellHasImagingTypeList.size();
                    }
                    long currentTimeMillis1 = System.currentTimeMillis();
                    LOG.debug("CellMia data processed in " + ((currentTimeMillis1 - currentTimeMillis) / 1000) + " s");
                }
                for (List<WellHasImagingType> wellHasImagingTypes : map.values()) {
                    algo.getWellHasImagingTypeCollection().addAll(wellHasImagingTypes);
                }

                algoMap.put(algo, map);
            }
        }
        return algoMap;
    }
    /**
     * set file filters for CellMIA 
     */
    private FilenameFilter sampleFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.startsWith("sample");
        }
    };
    private FilenameFilter resultsFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.startsWith("results");
        }
    };
    private FilenameFilter textfilesFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.endsWith(".txt");
        }
    };

    @Override
    public MicroscopeDataService getMicroscopeDataService() {
        return microscopeDataService;
    }

    /**
     * copy the map with imaging types and well has imaging types properties
     * @return 
     */
    private Map<ImagingType, List<WellHasImagingType>> copyMap() {
        Map<ImagingType, List<WellHasImagingType>> map = new HashMap<>();

        for (ImagingType imagingType : imagingTypeMap.keySet()) {
            List<WellHasImagingType> wellHasImagingTypeList = new ArrayList<>();

            for (WellHasImagingType wellHasImagingType : imagingTypeMap.get(imagingType)) {
                WellHasImagingType newWellHasImagingType = new WellHasImagingType(wellHasImagingType.getSequenceNumber(), wellHasImagingType.getXCoordinate(), wellHasImagingType.getYCoordinate(), wellHasImagingType.getImagingType());
                wellHasImagingTypeList.add(newWellHasImagingType);
            }
            map.put(imagingType, wellHasImagingTypeList);
        }

        return map;
    }
}
