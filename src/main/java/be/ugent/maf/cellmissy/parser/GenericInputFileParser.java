/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.exception.FileParserException;
import java.io.File;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface GenericInputFileParser {

    /**
     * Parse generic input area (bulk) file
     *
     * @param bulkCellFile
     * @return a list of time step objects with measurements
     * @throws FileParserException
     */
    List<TimeStep> parseBulkCellFile(File bulkCellFile) throws FileParserException;

    /**
     * Parse generic input single cell (tracks) file
     *
     * @param trackFile
     * @return a list of track objects with measurements (just x, y)
     * @throws FileParserException
     */
    List<Track> parseTrackFile(File trackFile) throws FileParserException;
    
    /**
     * Parse generic input dose-response file
     * 
     * @param doseResponseFile Possible formats are tsv, csv, xls and xlsx
     * @return 
     * @throws FileParserException
     */
    List<DoseResponsePair> parseDoseResponseFile(File doseResponseFile) throws FileParserException;
}
