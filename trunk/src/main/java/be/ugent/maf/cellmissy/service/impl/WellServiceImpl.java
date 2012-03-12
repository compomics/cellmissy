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
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.repository.WellRepository;
import be.ugent.maf.cellmissy.service.CellMiaDataService;
import be.ugent.maf.cellmissy.service.WellService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("wellService")
@Transactional
public class WellServiceImpl implements WellService {

    @Autowired
    private WellRepository wellRepository;
    @Autowired
    private CellMiaDataService cellMiaDataService;
    private static final double offset = 0.5;
    // this Map maps ImagingType (keys) to list of WellHasImagingType (values)
    private Map<ImagingType, List<WellHasImagingType>> imagingTypeMap;

    @Override
    public void updateWellGuiListWithImagingType(ImagingType imagingType, PlateFormat plateFormat, WellGui firstWellGui, List<WellGui> wellGuiList) {

        List<WellHasImagingType> wellHasImagingTypeList = imagingTypeMap.get(imagingType);
        WellHasImagingType firstWellHasImagingType = wellHasImagingTypeList.get(0);

        double xOffset = (Math.abs(firstWellHasImagingType.getXCoordinate()) / plateFormat.getWellSize()) - (firstWellGui.getColumnNumber() - offset);
        double yOffset = (Math.abs(firstWellHasImagingType.getYCoordinate()) / plateFormat.getWellSize()) - (firstWellGui.getRowNumber() - offset);

        for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
            double scaledX = Math.abs(wellHasImagingType.getXCoordinate()) / plateFormat.getWellSize();
            double scaledY = Math.abs(wellHasImagingType.getYCoordinate()) / plateFormat.getWellSize();

            double shiftedX = scaledX - xOffset;
            double shiftedY = scaledY - yOffset;

            WellGui wellGui = getWellGuiByCoords(wellGuiList, shiftedX, shiftedY);
            wellGui.getWell().getWellHasImagingTypeCollection().add(wellHasImagingType);
        }
    }

    private WellGui getWellGuiByCoords(List<WellGui> wellGuiList, double shiftedX, double shiftedY) {
        for (WellGui wellGui : wellGuiList) {
            if (wellGui.getColumnNumber() == ((int) Math.nextUp(shiftedX) + 1) && wellGui.getRowNumber() == ((int) Math.nextUp(shiftedY) + 1)) {
                return wellGui;
            }
        }
        return null;
    }

    @Override
    public void init() {
        cellMiaDataService.init(new File(PropertiesConfigurationHolder.getInstance().getString("cellMiaFolder")));
        cellMiaDataService.getMicroscopeDataService().init(new File(PropertiesConfigurationHolder.getInstance().getString("microscopeFolder")), new File(PropertiesConfigurationHolder.getInstance().getString("obsepFile")));
        imagingTypeMap = cellMiaDataService.processCellMiaData();
    }

    @Override
    public List<ImagingType> getImagingTypes() {

        List<ImagingType> imagingTypeList = new ArrayList<>();
        Set<ImagingType> imagingTypeSet = imagingTypeMap.keySet();

        for (ImagingType imagingType : imagingTypeSet) {
            imagingTypeList.add(imagingType);
        }
        return imagingTypeList;
    }

    @Override
    public Well findById(Long id) {
        return wellRepository.findById(id);
    }

    @Override
    public List<Well> findAll() {
        return wellRepository.findAll();
    }

    @Override
    public Well save(Well entity) {
        return wellRepository.save(entity);
    }

    @Override
    public void delete(Well entity) {
        entity = wellRepository.save(entity);
        wellRepository.delete(entity);
    }
}
