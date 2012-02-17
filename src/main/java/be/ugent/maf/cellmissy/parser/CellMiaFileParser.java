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
 *
 * @author Paola
 */
public interface CellMiaFileParser {
    
    List<TimeStep> parseBulkCellFile(File bulkCellFile);
    
    List<Track> parseTrackingFile(File trackingFile);
    
}
