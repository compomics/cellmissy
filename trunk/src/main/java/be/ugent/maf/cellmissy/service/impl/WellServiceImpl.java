/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.service.CellMiaDataService;
import be.ugent.maf.cellmissy.service.MicroscopeDataService;
import be.ugent.maf.cellmissy.service.WellService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Paola
 */
@Service("wellService")
public class WellServiceImpl implements WellService {

    @Autowired
    private CellMiaDataService cellMiaDataService;
    private static final double offset = 0.5;

    @Override
    public List<Well> PositionWellsByImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell) {
        Map<ImagingType, List<WellHasImagingType>> map = cellMiaDataService.processCellMiaData();

        List< Well> wellList = new ArrayList<Well>();

        List<WellHasImagingType> wellHasImagingTypeList = map.get(imagingType);
        WellHasImagingType firstWellHasImagingType = wellHasImagingTypeList.get(0);

        double xoffset = firstWellHasImagingType.getXCoordinate() - (firstWell.getRowNumber() - offset);
        double yoffset = firstWellHasImagingType.getYCoordinate() - (firstWell.getColumnNumber() - offset);

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
            double x = Math.abs(wellHasImagingType.getXCoordinate()) / plateFormat.getWellSize();
            double y = Math.abs(wellHasImagingType.getYCoordinate()) / plateFormat.getWellSize();

            double shiftedx = x - xoffset;
            double shiftedy = y - yoffset;

            Well well = new Well();
            well.setRowNumber((int) Math.nextUp(shiftedx));
            well.setColumnNumber((int) Math.nextUp(shiftedy));
            wellList.add(well);
        }

        return wellList;
    }
}
