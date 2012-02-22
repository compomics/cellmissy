/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.service.WellService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola
 */
@Service("wellService")
public class WellServiceImpl implements WellService {

    private static final double offset = 0.5;

    @Override
    public List<Well> getWells(Map<ImagingType, List<WellHasImagingType>> map, PlateFormat plateFormat, Well firstWell) {
        List<Well> wellList = new ArrayList<Well>();

        double xoffset = firstWell.getRowNumber() - offset;
        double yoffset = firstWell.getColumnNumber() - offset;

        for (ImagingType imagingType : map.keySet()) {
            List<WellHasImagingType> wellHasImagingTypeList = map.get(imagingType);

            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                double x = wellHasImagingType.getXCoordinate() / plateFormat.getWellSize();
                double y = wellHasImagingType.getYCoordinate() / plateFormat.getWellSize();


            }
        }





        return wellList;
    }
}
