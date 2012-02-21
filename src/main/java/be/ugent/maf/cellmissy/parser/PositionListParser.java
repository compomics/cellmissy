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
 *
 * @author Paola
 */
public interface PositionListParser {

    Map<ImagingType, List<WellHasImagingType>> parsePositionList(Map<ImagingType, String> imagingTypePositionListMap, File microscopeFolder);
}