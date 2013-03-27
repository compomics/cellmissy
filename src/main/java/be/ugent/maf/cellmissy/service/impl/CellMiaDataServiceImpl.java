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
import be.ugent.maf.cellmissy.exception.CellMiaDataLoadingException;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.exception.PositionListMismatchException;
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
import java.util.Iterator;
import java.util.Set;
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
    public Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> processCellMiaData() throws FileParserException, PositionListMismatchException, CellMiaDataLoadingException {
        long currentTimeMillis = System.currentTimeMillis();
        imagingTypeMap = microscopeDataService.processMicroscopeData();
        algoMap = new HashMap<>();
        File[] algoFiles = experiment.getMiaFolder().listFiles();

        for (File file : algoFiles) {
            //default algo-0 folder does not contain data to store
            if (!file.getName().endsWith("algo-0")) {
                //create a new algorithm
                Algorithm algo = new Algorithm();
                //give name to the algorithm (according to folder name)
                algo.setAlgorithmName(file.getName().substring(file.getName().indexOf("MIA_"), file.getName().length()));
                algo.setWellHasImagingTypeCollection(new ArrayList<WellHasImagingType>());
                //create a new Map copying all the wellhasimagingtypes fields (but new objects)
                Map<ImagingType, List<WellHasImagingType>> map = copyMap();
                for (File batchFile : Arrays.asList(file.listFiles())) {

                    // sample folders
                    // the number of sampleFiles is equal to the number of WellHasImagingType entities for one algorithm
                    File[] sampleFiles = batchFile.listFiles(sampleFilter);

                    // listFiles does not guarantee any order; sort files in alphabetical order
                    Arrays.sort(sampleFiles);
                    int imageTypeStartFolder = 0;
                    // make use of an iterator on a EntrySet is more efficient then iterating through the keys of the map
                    Set<Map.Entry<ImagingType, List<WellHasImagingType>>> entrySet = map.entrySet();
                    Iterator<Map.Entry<ImagingType, List<WellHasImagingType>>> iterator = entrySet.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<ImagingType, List<WellHasImagingType>> next = iterator.next();
                        ImagingType imagingType = next.getKey();
                        List<WellHasImagingType> wellHasImagingTypeList = next.getValue();

                        // number of sample files need to be same as number of samples that need to be processed, otherwise, throw an exception
                        if (sampleFiles.length == getExpectedNumberOfSamples()) {
                            //**********************************************************************************************//
                            for (int i = imageTypeStartFolder; i < wellHasImagingTypeList.size() + imageTypeStartFolder; i++) {
                                WellHasImagingType wellHasImagingType = wellHasImagingTypeList.get(i - imageTypeStartFolder);
                                wellHasImagingType.setAlgorithm(algo);
                                // iterate trough the folders and look for the text files, parse the files with cellMiaFileParser
                                // results folders
                                File[] resultsFiles = sampleFiles[i].listFiles(resultsFilter);
                                for (int j = 0; j < resultsFiles.length; j++) {
                                    // text files
                                    File[] textFiles = resultsFiles[j].listFiles(textfilesFilter);
                                    Arrays.sort(textFiles);
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
                                LOG.debug("Sample " + (i + 1) + " (" + wellHasImagingType.getXCoordinate() + ", " + wellHasImagingType.getYCoordinate() + ")" + " processed." + " Imaging Type: " + imagingType + ", algo: " + algo.getAlgorithmName());
                            }
                            LOG.debug("Imaging type: " + imagingType + " processed");
                            //***********************************************************************************************//

                            //update collection of imaging type
                            imagingType.setWellHasImagingTypeCollection(wellHasImagingTypeList);
                            //start over through the other folders (next imaging type)
                            imageTypeStartFolder += wellHasImagingTypeList.size();

                            //one algo was processed ===========================
                            long currentTimeMillis1 = System.currentTimeMillis();
                            LOG.debug(algo.getAlgorithmName() + " processed in " + ((currentTimeMillis1 - currentTimeMillis) / 1000) + " s");
                            //add all the samples to the algorithm
                            for (List<WellHasImagingType> wellHasImagingTypes : map.values()) {
                                algo.getWellHasImagingTypeCollection().addAll(wellHasImagingTypes);
                            }
                            //update the map
                            algoMap.put(algo, map);
                        } else {
                            throw new CellMiaDataLoadingException("There are " + sampleFiles.length + " files to be processed, but " + wellHasImagingTypeList.size() + " samples!\nPlease check your files structure in the MIA folder.");
                        }
                    }
                }
            }
        }
        return algoMap;
    }
    /**
     * set file filters for CellMIA
     */
    private static FilenameFilter sampleFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("sample");
        }
    };
    private static FilenameFilter resultsFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("results");
        }
    };
    private static FilenameFilter textfilesFilter = new FilenameFilter() {
        @Override
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
     *
     * @return
     */
    private Map<ImagingType, List<WellHasImagingType>> copyMap() {
        Map<ImagingType, List<WellHasImagingType>> map = new HashMap<>();
        for (Iterator<ImagingType> it = imagingTypeMap.keySet().iterator(); it.hasNext();) {
            ImagingType imagingType = it.next();
            List<WellHasImagingType> wellHasImagingTypeList = new ArrayList<>();
            for (WellHasImagingType wellHasImagingType : imagingTypeMap.get(imagingType)) {
                WellHasImagingType newWellHasImagingType = new WellHasImagingType(wellHasImagingType.getSequenceNumber(), wellHasImagingType.getXCoordinate(), wellHasImagingType.getYCoordinate(), wellHasImagingType.getImagingType());
                wellHasImagingTypeList.add(newWellHasImagingType);
            }
            map.put(imagingType, wellHasImagingTypeList);
        }
        return map;
    }

    @Override
    public int getNumberOfSamples() throws CellMiaDataLoadingException {
        int numberOfSamples = 0;
        File[] algoFiles = experiment.getMiaFolder().listFiles();
        for (File file : algoFiles) {
            //default algo-0 folder does not contain data to store
            if (!file.getName().endsWith("algo-0")) {
                for (File batchFile : Arrays.asList(file.listFiles())) {
                    // sample folders
                    File[] sampleFiles = batchFile.listFiles(sampleFilter);
                    numberOfSamples += sampleFiles.length;
                }
            }
        }
        if (numberOfSamples != 0) {
            return numberOfSamples;
        } else {
            throw new CellMiaDataLoadingException("No data to be imported were found.\nPlease make sure there's at least one algorithm folder with raw data to be imported.");
        }
    }

    @Override
    public int getExpectedNumberOfSamples() {
        int total = 0;
        for (Iterator<ImagingType> it = imagingTypeMap.keySet().iterator(); it.hasNext();) {
            ImagingType imagingType = it.next();
            List<WellHasImagingType> list = imagingTypeMap.get(imagingType);
            total += list.size();
        }
        return total;
    }
}
