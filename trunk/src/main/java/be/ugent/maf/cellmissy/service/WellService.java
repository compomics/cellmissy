/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paola
 */
public interface WellService {

    List<Well> getWells(Map<ImagingType, List<WellHasImagingType>> map, PlateFormat plateFormat, Well firstWell);
}
