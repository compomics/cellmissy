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
import be.ugent.maf.cellmissy.parser.impl.CellMiaFileParserImpl;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import be.ugent.maf.cellmissy.service.CellMiaOutputService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// test
/**
 *
 * @author Paola
 */
@Service("cellMiaOutputService")
@Transactional
public class CellMiaOutputServiceImpl implements CellMiaOutputService {

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

            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {

                for (int i = 0; i < sampleFiles.length; i++) {

                    CellMiaFileParser cellMiaParser = new CellMiaFileParserImpl();

                    // iterate trough the folders and look for the text files, read them with cellmia parser
                    File[] resultsFiles = sampleFiles[i].listFiles(resultsFilter);
                    for (int j = 0; j < resultsFiles.length; j++) {
                        File[] textFiles = resultsFiles[j].listFiles(textfilesFilter);

                        for (int n = 0; n < textFiles.length; n++) {

                            if (textFiles[n].getName().endsWith("bulkcell.txt")) {
                                List<TimeStep> timeStepList = cellMiaParser.parseBulkCellFile(textFiles[n]);
                                for (TimeStep timeStep : timeStepList) {
                                    timeStep.setWellHasImagingType(wellHasImagingType);
                                }
                                wellHasImagingType.setTimeStepCollection(timeStepList);

                            } else if (textFiles[n].getName().endsWith("tracking.txt")) {
                                List<Track> trackList = cellMiaParser.parseTrackingFile(textFiles[n]);
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
//    public static void main(String[] args) {
//
//        //load applicationContext
//        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
//        CellMiaOutputService cellMiaResultsService = (CellMiaOutputService) context.getBean("cellMiaResultsService");
//        File cellMiaFolder = new File("M:\\CM\\CM_P002_Neuroblastoma_Project_2\\CM_P002_E001\\CM_P002_E001_MIA\\CM_P002_E001_MIA_algo-1\\batch--8U5T2801_DocumentFiles");
//        cellMiaResultsService.processCellMiaOutput(cellMiaFolder);
//    }
}
