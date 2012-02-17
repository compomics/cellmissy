/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.parser;

import be.ugent.maf.limsdesktop.entity.ImagingType;
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
