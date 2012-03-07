/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import java.io.File;
import java.util.List;

/**
 * This interface parses Files in output from CellMIA analysis (one file, i.e. one sample)
 * @author Paola
 */
public interface CellMiaFileParser {

    /**
     * this method parses one BulkCellFile
     * @param bulkCellFile
     * @return a List of TimeSteps
     */
    List<TimeStep> parseBulkCellFile(File bulkCellFile);

    /**
     * this method parses one TrackingFile
     * @param trackingFile
     * @return a List of Tracks
     */
    List<Track> parseTrackingFile(File trackingFile);
}
