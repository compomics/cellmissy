/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Once one experiment is set, one ObsepFile is automatically generated from the microscope software:
 * this interface parses the ObsepFile from the microscope
 * @author Paola
 */
public interface ObsepFileParser {

    /**
     * parses the file
     * @param obsepFile 
     */
    void parseObsepFile(File obsepFile);

    /**
     * Mapping Imaging Types to Position Lists
     * @return a Map of ImagingType (keys) and PositionList names (values)
     * One ImagingType is always mapped to one PositionList 
     */
    Map<ImagingType, String> mapImagingTypetoPositionList();

    /**
     * Retrieving Experiment Information from the ObsepFile(i.e. Experiment Time Frame, Interval and Duration)
     * @return a List of Double values
     */
    List<Double> getExperimentInfo();
}
