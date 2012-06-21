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
    private File experimentFolder;
    private File miaFolder;
    private File outputFolder;
    private File rawFolder;
    private File microscopeFolder;
    private File setupFolder;
    private File algoNullMiaFolder;
    private String projectFolderName;

    public File getExperimentFolder() {
        return experimentFolder;
    }

    public File getSetupFolder() {
        return setupFolder;
    }

    /**
     * create experiment folders from microscope directory
     * @param newExperiment
     * @param microscopeDirectory 
     */
    @Override
    public void createFolderStructure(Experiment newExperiment, File microscopeDirectory) {

        //create main folder
        experimentFolder = null;
        if (newExperiment.getProject().getProjectDescription().length() == 0) {
            projectFolderName = "CM_" + newExperiment.getProject().toString();
        } else {
            projectFolderName = "CM_" + newExperiment.getProject().toString() + "_" + newExperiment.getProject().getProjectDescription();
        }
        File[] listFiles = microscopeDirectory.listFiles();
        for (File file : listFiles) {
            if (file.getName().equals(projectFolderName)) {
                String substring = file.getName().substring(0, 7);
                String experimentFolderName = substring + "_" + newExperiment.toString();
                experimentFolder = new File(file, experimentFolderName);
                experimentFolder.mkdir();
                break;
            }
        }

        //create subfolders
        miaFolder = new File(experimentFolder, experimentFolder.getName() + "_MIA");
        miaFolder.mkdir();
        outputFolder = new File(experimentFolder, experimentFolder.getName() + "_output");
        outputFolder.mkdir();
        rawFolder = new File(experimentFolder, experimentFolder.getName() + "_raw");
        rawFolder.mkdir();

        //create subfolders in the raw folder
        microscopeFolder = new File(rawFolder, experimentFolder.getName() + "_microscope");
        microscopeFolder.mkdir();
        setupFolder = new File(rawFolder, experimentFolder.getName() + "_setup");
        setupFolder.mkdir();
        
        //create algo-0 subfolder in the MIA folder
        algoNullMiaFolder = new File(miaFolder, miaFolder.getName() + "_algo-0");
        algoNullMiaFolder.mkdir();
        
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
