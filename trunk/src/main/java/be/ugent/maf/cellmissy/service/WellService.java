/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface WellService {

    List<Well> PositionWellsByImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell);
}
