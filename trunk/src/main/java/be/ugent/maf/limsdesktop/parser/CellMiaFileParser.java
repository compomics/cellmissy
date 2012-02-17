/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.parser;

import be.ugent.maf.limsdesktop.entity.TimeStep;
import be.ugent.maf.limsdesktop.entity.Track;
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
