/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.parser;

import be.ugent.maf.cellmissy.entity.ImagingType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
public interface ObsepFileParser {

    Map<ImagingType, String> mapImagingTypetoPosList();

    List<Double> getExperimentInfo();
}
