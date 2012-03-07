/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This interface parses the PositionList(s) used in the Experiment
 * @author Paola
 */
public interface PositionListParser {

    /**
     * this method maps the Imaging Types to a List of WellHasImagingType entities
     * @param imagingTypeToPositionList
     * @param microscopeFolder
     * @return a map from ImagingType to a List of WellHasImagingType entities
     */
    Map<ImagingType, List<WellHasImagingType>> parsePositionList(Map<ImagingType, String> imagingTypeToPositionList, File microscopeFolder);
}