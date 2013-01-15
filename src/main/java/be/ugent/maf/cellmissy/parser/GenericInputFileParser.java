/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.TimeStep;
import java.io.File;
import java.util.List;

/**
 *
 * @author Paola Masuzzo
 */
public interface GenericInputFileParser {

    /**
     * 
     * @param bulkCellFile
     * @return 
     */
    List<TimeStep> parseBulkCellFile(File bulkCellFile);
}
