/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.repository.ExperimentRepository;
import be.ugent.maf.cellmissy.repository.InstrumentRepository;
import be.ugent.maf.cellmissy.repository.MagnificationRepository;
import be.ugent.maf.cellmissy.service.ExperimentService;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("experimentService")
@Transactional
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MagnificationRepository magnificationRepository;

    /**
     * create experiment folders from microscope directory
     * @param newExperiment
     * @param microscopeDirectory 
     */
    @Override
    public void createFolderStructure(Experiment newExperiment, File microscopeDirectory) {

        //create main folder
        File experimentFolder = null;
        String projectFolderName = "CM_" + newExperiment.getProject().toString();
        File[] listFiles = microscopeDirectory.listFiles();
        for (File file : listFiles) {
            if (file.getName().equals(projectFolderName)) {
                String experimentFolderName = file.getName() + "_" + newExperiment.toString();
                experimentFolder = new File(file, experimentFolderName);
                experimentFolder.mkdir();
                break;
            }
        }

        //create subfolders
        File miaFolder = new File(experimentFolder, experimentFolder.getName() + "_MIA");
        miaFolder.mkdir();
        File outputFolder = new File(experimentFolder, experimentFolder.getName() + "_output");
        outputFolder.mkdir();
        File rawFolder = new File(experimentFolder, experimentFolder.getName() + "_raw");
        rawFolder.mkdir();
    }

    @Override
    public Experiment findById(Long id) {
        return experimentRepository.findById(id);
    }

    @Override
    public List<Experiment> findAll() {
        return experimentRepository.findAll();
    }

    @Override
    public Experiment save(Experiment entity) {
        return experimentRepository.save(entity);
    }

    @Override
    public void delete(Experiment entity) {
        entity = experimentRepository.save(entity);
        experimentRepository.delete(entity);
    }

    @Override
    public List<Instrument> findAllInstruments() {
        return instrumentRepository.findAll();
    }

    @Override
    public List<Magnification> findAllMagnifications() {
        return magnificationRepository.findAll();
    }

    @Override
    public List<Integer> findExperimentNumbersByProjectId(Integer projectId) {
        return experimentRepository.findExperimentNumbersByProjectId(projectId);
    }

    @Override
    public List<Experiment> findExperimentsByProjectIdAndStatus(Integer projectId, ExperimentStatus experimentStatus) {
        return experimentRepository.findExperimentsByProjectIdAndStatus(projectId, experimentStatus);
    }
}
