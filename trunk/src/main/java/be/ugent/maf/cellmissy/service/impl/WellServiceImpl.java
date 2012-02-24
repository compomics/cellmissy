/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.service.CellMiaDataService;
import be.ugent.maf.cellmissy.service.WellService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private Map<ImagingType, List<WellHasImagingType>> map;

    @Override
    public List<Well> positionWellsByImagingType(ImagingType imagingType, PlateFormat plateFormat, Well firstWell) {

        List<Well> wellList = new ArrayList<Well>();

        List<WellHasImagingType> wellHasImagingTypeList = map.get(imagingType);
        WellHasImagingType firstWellHasImagingType = wellHasImagingTypeList.get(0);

        double xoffset = (Math.abs(firstWellHasImagingType.getXCoordinate()) / plateFormat.getWellSize()) - (firstWell.getColumnNumber() - offset);
        double yoffset = (Math.abs(firstWellHasImagingType.getYCoordinate()) / plateFormat.getWellSize()) - (firstWell.getRowNumber() - offset);

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
            double scaledx = Math.abs(wellHasImagingType.getXCoordinate()) / plateFormat.getWellSize();
            double scaledy = Math.abs(wellHasImagingType.getYCoordinate()) / plateFormat.getWellSize();

            double shiftedx = scaledx - xoffset;
            double shiftedy = scaledy - yoffset;

            // create new Well entity and set its rowNumber and columnNumber
            Well well = new Well();
            well.setColumnNumber((int) Math.nextUp(shiftedx) + 1);
            well.setRowNumber((int) Math.nextUp(shiftedy) + 1);
            wellList.add(well);
        }

        return wellList;
    }

    @Override
    public void init() {
        cellMiaDataService.init(new File(PropertiesConfigurationHolder.getInstance().getString("cellMiaFolder")));
        cellMiaDataService.getMicroscopeDataService().init(new File(PropertiesConfigurationHolder.getInstance().getString("microscopeFolder")), new File(PropertiesConfigurationHolder.getInstance().getString("obsepFile")));
        map = cellMiaDataService.processCellMiaData();
    }

    @Override
    public List<ImagingType> getImagingTypes() {

        List<ImagingType> imagingTypeList = new ArrayList<ImagingType>();
        Set<ImagingType> imagingTypeSet = map.keySet();

        for (ImagingType imagingType : imagingTypeSet) {
            imagingTypeList.add(imagingType);
        }
        return imagingTypeList;
    }
}
