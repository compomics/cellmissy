/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser.impl;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.exception.FileParserException;
import be.ugent.maf.cellmissy.parser.GenericInputFileParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * This is parsing a single generic input file.
 *
 * @author Paola Masuzzo
 */
@Service("genericInputFileParser")
public class GenericInputFileParserImpl implements GenericInputFileParser {

    private static final Logger LOG = Logger.getLogger(GenericInputFileParser.class);

    @Override
    public List<TimeStep> parseBulkCellFile(File bulkCellFile) throws FileParserException {
        List<TimeStep> timeStepList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(bulkCellFile))) {
            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                //check if the line is the header
                if (strRead.startsWith("Time")) {
                    continue;
                }
                String[] splitarray = strRead.split("\t");
                // check for number of columns in generic file 
                if (splitarray.length == 2) {
                    //create new timestep object and set class members
                    TimeStep timeStep = new TimeStep();
                    try {
                        timeStep.setTimeStepSequence(Integer.parseInt(splitarray[0]));
                        timeStep.setArea(Double.parseDouble(splitarray[1]));
                        //add timestep to the list
                        timeStepList.add(timeStep);
                    } catch (NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new FileParserException("Please make sure each line of your import file contains numbers!");
                    }
                } else {
                    throw new FileParserException("Please make sure your import file has 2 columns!");
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FileParserException(ex.getMessage());
        }
        return timeStepList;
    }

    @Override
    public List<Track> parseTrackFile(File trackFile) throws FileParserException {
        List<Track> trackList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(trackFile))) {
            // we keep a current track in memory
            Track currentTrack = null;
            List<TrackPoint> currentTrackPointList = new ArrayList<>();
            int currentNumber = -1;

            String strRead;
            while ((strRead = bufferedReader.readLine()) != null) {
                // check for the header of the file
                if (strRead.toLowerCase().startsWith("track")) {
                    continue;
                }
                String[] splitarray = strRead.split("\t");
                // check for number of columns in the file 
                if (splitarray.length == 4) {
                    try {
                        // take the current track number from the first column
                        int currentTrackNumber = Integer.parseInt(splitarray[0]);
                        //check if the currentTrackNumber differs from the currentRowNumber
                        if (currentTrackNumber != currentNumber) {

                            if (currentTrack != null) {
                                trackList.add(currentTrack); //add the currentTrack to the track list
                            }
                            // create new track object and set some class members
                            currentTrack = new Track();
                            currentTrack.setTrackNumber(Integer.parseInt(splitarray[0]));
                            currentNumber = currentTrackNumber;
                            currentTrackPointList = new ArrayList<>();
                        }

                        // create trackpoint object and set class members                      
                        TrackPoint trackPoint = new TrackPoint();
                        Double parseDouble = Double.parseDouble(splitarray[1]);
                        trackPoint.setTimeIndex(parseDouble.intValue());
                        trackPoint.setCellRow(Double.parseDouble(splitarray[2]));
                        trackPoint.setCellCol(Double.parseDouble(splitarray[3]));
                        trackPoint.setTrack(currentTrack);
                        //add current track point to currentTrackPointList
                        currentTrackPointList.add(trackPoint);
                        //set the currentTrack trackpoint List
                        currentTrack.setTrackPointList(currentTrackPointList);
                        // set the lenght of the track
                        currentTrack.setTrackLength(currentTrackPointList.size());
                    } catch (NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new FileParserException("Please make sure each line of your import file contains numbers!");
                    }
                } else {
                    throw new FileParserException("Please make sure your import file has 4 columns!");
                }
            }
            // when all the file is read, add the last track to the list
            trackList.add(currentTrack);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FileParserException(ex.getMessage());
        }
        return trackList;
    }

    @Override
    public List<DoseResponsePair> parseDoseResponseFile(File doseResponseFile) throws FileParserException {

        List<DoseResponsePair> doseResponseData = new ArrayList<>();
        String fileName = doseResponseFile.getName();
        Double dose;
        List<Double> responses = new ArrayList<>();

        //case CSV and TSV
        if (fileName.endsWith(".tsv") || fileName.endsWith(".csv")) {
            CSVParser csvFileParser;
            FileReader fileReader;
            CSVFormat csvFileFormat;
            //different fileformat specification depending on delimination
            if (fileName.endsWith(".csv")) {
                csvFileFormat = CSVFormat.DEFAULT.withHeader();
            } else {
                csvFileFormat = CSVFormat.TDF.withHeader();
            }
            try {
                // initialize the file reader
                fileReader = new FileReader(doseResponseFile);
                //initialize CSVParser object
                csvFileParser = new CSVParser(fileReader, csvFileFormat);
                // get the csv records
                List<CSVRecord> csvRecords = csvFileParser.getRecords();

                // read the CSV file records
                for (CSVRecord cSVRecord: csvRecords) {
                    //create an iterator for the record values
                    Iterator<String> iter = cSVRecord.iterator();

                    // the dose is in the first column
                    dose = Double.parseDouble(iter.next());
                    //read the rest of the rows as long as they contain numbers
                    while (iter.hasNext()) {
                        //check if next is not empty, otherwise it will throw an exception
                        String next = iter.next();
                        if (!next.equals("")){
                            responses.add(Double.parseDouble(next));
                        }
                                            }
                    //when the record end has been reached, save the doses and responses
                    doseResponseData.add(new DoseResponsePair(dose, responses));
                    responses = new ArrayList<>();
                }

            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new FileParserException("It seems like a line contains something other than a number!\nPlease check your files!");
            }

            return doseResponseData;
        }

        //CASE XLS or XLSX
        try {
            FileInputStream fileInputStream = new FileInputStream(doseResponseFile);
            Workbook workbook = null;
            // xls extension
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else if (fileName.endsWith("xlsx")) { // xlsx extension
                workbook = new XSSFWorkbook(fileInputStream);
            }
            if (workbook != null) {
                // check that at least one sheet is present
                if (workbook.getNumberOfSheets() > 0) {
                    Sheet sheet = workbook.getSheetAt(0);
                    // iterate through all the rows
                    // we need to remember the dose value in case of a pure 2 column file (rows contain repeated doses)
                    for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                        // get the row
                        Row row = sheet.getRow(i);

                        // the dose is in the first column
                        dose = row.getCell(0).getNumericCellValue();

                        //read the rest of the rows as long as they contain numbers
                        int column = 1;
                        //blank cells return 0
                        while (row.getCell(column).getNumericCellValue() != 0) {
                            responses.add(row.getCell(column).getNumericCellValue());
                            column++;
                        }
                        doseResponseData.add(new DoseResponsePair(dose, responses));
                        responses = new ArrayList<>();
                    }

                } else {
                    throw new FileParserException("It seems an Excel file does not have any sheets!\nPlease check your files!");
                }
            } else {
                throw new FileParserException("The parser did not find a single workbook!\nCheck your files!!");
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FileParserException("It seems like a line does not contain a number!\nPlease check your files!");
        } catch (IllegalStateException ex) {
            LOG.error("Only numbers are allowed in the input file, no letters or words.\nPlease check your files!", ex);
        }
        return doseResponseData;
    }

}
