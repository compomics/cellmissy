/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.repository.AlgorithmRepository;
import be.ugent.maf.cellmissy.repository.ImagingTypeRepository;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import be.ugent.maf.cellmissy.repository.WellRepository;
import be.ugent.maf.cellmissy.service.CellMiaDataService;
import be.ugent.maf.cellmissy.service.WellService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Hibernate;
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
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private ImagingTypeRepository imagingTypeRepository;
    @Autowired
    private WellHasImagingTypeRepository wellHasImagingTypeRepository;
    @Autowired
    private CellMiaDataService cellMiaDataService;
    private static final double offset = 0.5;
    // this Map maps ImagingType (keys) to list of WellHasImagingType (maps)
    private Map<ImagingType, List<WellHasImagingType>> imagingTypeMap;
    private Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap;

    /**
     * Given first well imaged and an imaging type, this method is predicting the imaged pattern of wells on the given plate format.
     * @param imagingType
     * @param plateFormat
     * @param firstWellGui
     * @param wellGuiList 
     */
    @Override
    public void updateWellGuiListWithImagingType(ImagingType imagingType, PlateFormat plateFormat, WellGui firstWellGui, List<WellGui> wellGuiList) {

        // maps with imaging types and list of wellhasimagingtypes
        Collection<Map<ImagingType, List<WellHasImagingType>>> maps = algoMap.values();
        // iterate through the maps
        for (Map<ImagingType, List<WellHasImagingType>> map : maps) {
            // for the given imaging type get the list of wellhasimagingtypes
            List<WellHasImagingType> wellHasImagingTypeList = map.get(imagingType);
            // first wellhasimagingtype
            WellHasImagingType firstWellHasImagingType = wellHasImagingTypeList.get(0);
            // compute x and y offset according to wellSize
            double xOffset = (Math.abs(firstWellHasImagingType.getXCoordinate()) / plateFormat.getWellSize()) - (firstWellGui.getColumnNumber() - offset);
            double yOffset = (Math.abs(firstWellHasImagingType.getYCoordinate()) / plateFormat.getWellSize()) - (firstWellGui.getRowNumber() - offset);

            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                // compute x and y coordinates scaled to wellSize
                double scaledX = Math.abs(wellHasImagingType.getXCoordinate()) / plateFormat.getWellSize();
                double scaledY = Math.abs(wellHasImagingType.getYCoordinate()) / plateFormat.getWellSize();
                // actual values for x and y coordinates
                double shiftedX = scaledX - xOffset;
                double shiftedY = scaledY - yOffset;
                // given the two coordinates found, find the correspondent wellgui
                WellGui wellGui = getWellGuiByCoords(wellGuiList, shiftedX, shiftedY);
                // add the wellhasimagingtype to the well of the found wellgui
                wellGui.getWell().getWellHasImagingTypeCollection().add(wellHasImagingType);
            }
        }
    }

    /**
     * Given a list of wellGui and x and y shifted values, find a certain wellGui.
     * @param wellGuiList
     * @param shiftedX
     * @param shiftedY
     * @return 
     */
    private WellGui getWellGuiByCoords(List<WellGui> wellGuiList, double shiftedX, double shiftedY) {
        for (WellGui wellGui : wellGuiList) {
            if (wellGui.getColumnNumber() == ((int) Math.nextUp(shiftedX) + 1) && wellGui.getRowNumber() == ((int) Math.nextUp(shiftedY) + 1)) {
                return wellGui;
            }
        }
        return null;
    }

    /**
     * Initialize cell mia data service, microscope data service and process data from the microscope
     * @param experiment 
     */
    @Override
    public void init(Experiment experiment) {
        cellMiaDataService.init(experiment);
        cellMiaDataService.getMicroscopeDataService().init(experiment);
        imagingTypeMap = cellMiaDataService.getMicroscopeDataService().processMicroscopeData();
    }

    /**
     * Get imaging types used in the experiment
     * @return 
     */
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

    /**
     * get the global algorithm map from the cell mia data service
     * @return 
     */
    @Override
    public Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> getMap() {
        algoMap = cellMiaDataService.processCellMiaData();
        return algoMap;
    }

    @Override
    public List<Algorithm> findAlgosByWellId(Integer wellId) {
        return algorithmRepository.findAlgosByWellId(wellId);
    }

    @Override
    public List<ImagingType> findImagingTypesByWellId(Integer wellId) {
        return imagingTypeRepository.findImagingTypesByWellId(wellId);
    }

    /**
     * Fetch time steps objects from DB
     * @param well
     * @param AlgorithmId
     * @param ImagingTpeId 
     */
    @Override
    public void fetchTimeSteps(Well well, Integer AlgorithmId, Integer ImagingTpeId) {
        //well = wellRepository.save(well);
        //for well, get the wellhasimagingtype for a certain algorithm and imaging type
        WellHasImagingType wellHasImagingType = findByWellIdAlgoIdAndImagingTypeId(well.getWellid(), AlgorithmId, ImagingTpeId);
        //fetch time step collection of that wellHasImagingType
        Hibernate.initialize(wellHasImagingType.getTimeStepCollection());
        //assing the fetched wellHasImagingType to the well
        List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
        wellHasImagingTypes.add(wellHasImagingType);
        well.setWellHasImagingTypeCollection(wellHasImagingTypes);
    }

    /**
     * Fetch tracks objects from DB
     * @param well
     * @param AlgorithmId
     * @param ImagingTpeId 
     */
    @Override
    public void fetchTracks(Well well, Integer AlgorithmId, Integer ImagingTpeId) {
        WellHasImagingType wellHasImagingType = findByWellIdAlgoIdAndImagingTypeId(well.getWellid(), AlgorithmId, ImagingTpeId);
        //fetch time step collection of that wellHasImagingType
        Hibernate.initialize(wellHasImagingType.getTrackCollection());
        //assing the fetched wellHasImagingType to the well
        List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
        wellHasImagingTypes.add(wellHasImagingType);
        well.setWellHasImagingTypeCollection(wellHasImagingTypes);
    }

    /**
     * 
     * @param well 
     * @param AlgorithmId 
     * @param ImagingTpeId 
     */
    @Override
    public void fetchTrackPoints(Well well, Integer AlgorithmId, Integer ImagingTpeId) {
        WellHasImagingType wellHasImagingType = findByWellIdAlgoIdAndImagingTypeId(well.getWellid(), AlgorithmId, ImagingTpeId);
        List<Track> tracks = new ArrayList<>(wellHasImagingType.getTrackCollection());
        for (Track track : tracks) {
            // fetch track points
            fetchTrackPointsPerTrack(track);
        }
        List<WellHasImagingType> wellHasImagingTypes = new ArrayList<>();
        wellHasImagingTypes.add(wellHasImagingType);
        well.setWellHasImagingTypeCollection(wellHasImagingTypes);
    }

    /**
     * get wellHasImagingTypes for some wells, for a certain algorithm and for a certain imagingType
     * @param wellId
     * @param AlgorithmId
     * @param ImagingTpeId
     * @return a list of WellHasImagingType
     */
    private WellHasImagingType findByWellIdAlgoIdAndImagingTypeId(Integer wellId, Integer AlgorithmId, Integer ImagingTpeId) {
        return wellHasImagingTypeRepository.findByWellIdAlgoIdAndImagingTypeId(wellId, AlgorithmId, ImagingTpeId);
    }

    /**
     * 
     * @param track 
     */
    private void fetchTrackPointsPerTrack(Track track) {
        Hibernate.initialize(track.getTrackPointCollection());
        List<Track> tracks = new ArrayList<>();
        tracks.add(track);
    }
}
