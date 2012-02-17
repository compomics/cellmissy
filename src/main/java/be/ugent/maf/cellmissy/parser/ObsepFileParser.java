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
 *
 * @author Paola
 */
public interface ObsepFileParser {

    void parseObsepFile(File obsepFile);

    Map<ImagingType, String> mapImagingTypetoPosList();

    List<Double> getExperimentInfo();
}
