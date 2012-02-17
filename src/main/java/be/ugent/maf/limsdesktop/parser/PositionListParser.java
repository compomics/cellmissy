/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.parser;

import be.ugent.maf.limsdesktop.entity.ImagingType;
import be.ugent.maf.limsdesktop.entity.WellHasImagingType;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
public interface PositionListParser {

    Map<ImagingType, List<WellHasImagingType>> parsePositionLists(Map<ImagingType, String> imagingTypePositionListMap, File microscopeFolder);
}