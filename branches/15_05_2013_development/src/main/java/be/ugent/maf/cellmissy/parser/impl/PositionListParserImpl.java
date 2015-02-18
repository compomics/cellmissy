/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.exception.PositionListMismatchException;
import be.ugent.maf.cellmissy.parser.PositionListParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola
 */
@Service("positionListParser")
public class PositionListParserImpl implements PositionListParser {

    private static final Logger LOG = Logger.getLogger(PositionListParser.class);

    @Override
    public Map<ImagingType, List<WellHasImagingType>> parsePositionList(Map<ImagingType, String> imagingTypeToPosListMap, File setupFolder) throws FileParserException, PositionListMismatchException {

        // this Map maps ImagingType (keys) to List of WellHasImagingType (values)
        Map<ImagingType, List<WellHasImagingType>> imagingTypeMap = new HashMap<>();
        // in the microscope folder, look for the text files to parse
        for (Map.Entry<ImagingType, String> next : imagingTypeToPosListMap.entrySet()) {
            ImagingType imagingType = next.getKey();
            String positionListName = next.getValue();

            List<WellHasImagingType> wellHasImagingTypeList = new ArrayList<>();
            File[] setupFiles = setupFolder.listFiles();
            File positionListFile = null;
            // iterate through files inside the folder and look for position lists file(s)
            for (File setupFile : setupFiles) {
                if (setupFile.getName().equals(positionListName + ".txt")) {
                    positionListFile = setupFile;
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(positionListFile))) {
                        String strRead;
                        // check if file actually contains line to read, otherwise throw an exception
                        if (bufferedReader.read() == -1) {
                            throw new FileParserException("Seems like your position list file is empty!\nPlease check your file.");
                        }

                        // create a new wellHasImagingType object for each line to be parsed
                        while ((strRead = bufferedReader.readLine()) != null) {
                            WellHasImagingType wellHasImagingType = new WellHasImagingType();
                            // tab separated text file!
                            String[] splitarray = strRead.split("\t");
                            // check for number of columns in position list file: needs to be equal to 2, otherwise throw an exception
                            if (splitarray.length == 2) {
                                try {
                                    // set wellHasImagingType class members: (x, y) coordinates and imaging type
                                    wellHasImagingType.setXCoordinate(Double.parseDouble(splitarray[0]));
                                    wellHasImagingType.setYCoordinate(Double.parseDouble(splitarray[1]));
                                    wellHasImagingType.setImagingType(imagingType);
                                    // add wellHasImagingType to the list
                                    wellHasImagingTypeList.add(wellHasImagingType);
                                    // set wellHasImagingType sequence number
                                    wellHasImagingType.setSequenceNumber(wellHasImagingTypeList.size());
                                } catch (NumberFormatException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    throw new FileParserException("Please make sure each line of position list contains (x, y) coordinates in numbers!");
                                }
                            } else {
                                throw new FileParserException("Please make sure position list contains 2 columns: (x, y) coordinates!");
                            }
                        }
                        // set the List of WellHasImagingTypes with the current List
                        imagingType.setWellHasImagingTypeList(wellHasImagingTypeList);

                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new FileParserException(ex.getMessage());
                    }
                    // fill in the map
                    imagingTypeMap.put(imagingType, wellHasImagingTypeList);
                }
                // if no valid files were found, throw an exception
                // this can mean two things: no file at all was found in the folder, or files with mismatching names, i.e. different from names parsed from obsep file
            }
            if (positionListFile == null) {
                throw new PositionListMismatchException("No valid position list was found.\nPlease check name of your text file(s) in the setup folder.");
            }
        }
        return imagingTypeMap;
    }
}
