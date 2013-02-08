/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.repository.ExperimentRepository;
import be.ugent.maf.cellmissy.repository.InstrumentRepository;
import be.ugent.maf.cellmissy.repository.MagnificationRepository;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import be.ugent.maf.cellmissy.service.ExperimentService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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

    private static final Logger LOG = Logger.getLogger(ExperimentService.class);
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private WellHasImagingTypeRepository wellHasImagingTypeRepository;
    @Autowired
    private MagnificationRepository magnificationRepository;
    private File projectFolder;
    private File experimentFolder;
    private File miaFolder;
    private File outputFolder;
    private File rawFolder;
    private File microscopeFolder;
    private File setupFolder;
    private File algoNullFolder;
    private File mainDirectory;
    private String projectFolderName;

    /**
     * create experiment obsepFolders from microscope directory
     *
     * @param newExperiment
     */
    @Override
    public void createFolderStructure(Experiment newExperiment) {

        //create main folder
        experimentFolder = null;
        if (newExperiment.getProject().getProjectDescription().length() == 0) {
            projectFolderName = "CM_" + newExperiment.getProject().toString();
        } else {
            projectFolderName = "CM_" + newExperiment.getProject().toString() + "_" + newExperiment.getProject().getProjectDescription();
        }
        File[] listFiles = mainDirectory.listFiles();
        for (File file : listFiles) {
            if (file.getName().equals(projectFolderName)) {
                String substring = file.getName().substring(0, 7);
                String experimentFolderName = substring + "_" + newExperiment.toString();
                experimentFolder = new File(file, experimentFolderName);
                //set experiment folder for the experiment
                newExperiment.setExperimentFolder(experimentFolder);
                boolean mkdir = experimentFolder.mkdir();
                if (mkdir) {
                    LOG.debug("Experiment Folder is created: " + experimentFolderName);
                }
                break;
            }
        }

        //create subfolders
        miaFolder = new File(experimentFolder, experimentFolder.getName() + "_MIA");
        boolean mkdir = miaFolder.mkdir();
        if (mkdir) {
            LOG.debug("Mia Folder is created: " + miaFolder.getName());
        }
        outputFolder = new File(experimentFolder, experimentFolder.getName() + "_output");
        boolean mkdir1 = outputFolder.mkdir();
        if (mkdir1) {
            LOG.debug("Output folder is created: " + outputFolder.getName());
        }

        rawFolder = new File(experimentFolder, experimentFolder.getName() + "_raw");
        boolean mkdir2 = rawFolder.mkdir();
        if (mkdir2) {
            LOG.debug("Raw folder is created: " + rawFolder.getName());
        }

        //create subfolders in the raw folder
        microscopeFolder = new File(rawFolder, experimentFolder.getName() + "_microscope");
        boolean mkdir3 = microscopeFolder.mkdir();
        if (mkdir3) {
            LOG.debug("Microscope folder is created: " + microscopeFolder.getName());
        }

        setupFolder = new File(rawFolder, experimentFolder.getName() + "_set-up");
        //set the setupFolder
        newExperiment.setSetupFolder(setupFolder);
        boolean mkdir4 = setupFolder.mkdir();
        if (mkdir4) {
            LOG.debug("Setup folder is created: " + setupFolder.getName());
        }

        //create algo-0 subfolder in the MIA folder
        algoNullFolder = new File(miaFolder, miaFolder.getName() + "_algo-0");
        boolean mkdir5 = algoNullFolder.mkdir();
        if (mkdir5) {
            LOG.debug("AlgoNull folder is created: " + algoNullFolder.getName());
        }
    }

    @Override
    public void loadFolderStructure(Experiment experiment) {
        for (File file : mainDirectory.listFiles()) {
            if (file.getName().contains(experiment.getProject().toString())) {
                //project folder
                projectFolder = file;
                break;
            }
        }

        for (File file : projectFolder.listFiles()) {
            if (file.getName().contains(experiment.toString())) {
                //experiment folder
                experimentFolder = file;
                //set experiment folde
                experiment.setExperimentFolder(experimentFolder);
                break;
            }
        }


        for (File file : experimentFolder.listFiles()) {
            if (file.getName().endsWith("raw")) {
                //raw folder
                rawFolder = file;
            } else if (file.getName().endsWith("MIA")) {
                //Mia folder
                miaFolder = file;
                //set experiment mia folder
                experiment.setMiaFolder(miaFolder);
            }
        }

        for (File file : rawFolder.listFiles()) {
            if (file.getName().endsWith("set-up")) {
                setupFolder = file;
                //set setup folder of the experiment
                experiment.setSetupFolder(setupFolder);
            } else if (file.getName().endsWith("microscope")) {
                microscopeFolder = file;
            }
        }

        //from microscope folder look for obsep folder(s)
        File docFiles = null;
        File[] listFiles = microscopeFolder.listFiles();
        // still need to list the folders if the lenght is equal to one
        if (listFiles.length == 1) {
            File[] subFiles = listFiles[0].listFiles();
            for (File file : subFiles) {
                if (file.getName().endsWith("Files")) {
                    docFiles = file;
                    break;
                }
            }
        } else {
            for (File file : listFiles) {
                if (file.getName().endsWith("Files")) {
                    docFiles = file;
                    break;
                }
            }
        }

        List<File> obsepFolders = new ArrayList<>();
        for (int i = 0; i < docFiles.listFiles().length; i++) {
            if (docFiles.listFiles()[i].getName().startsWith("D")) {
                obsepFolders.add(docFiles.listFiles()[i]);
            }
        }

        //obsep file
        File obsepFile = null;

        if (obsepFolders.size() == 1) {
            File aFile = obsepFolders.get(0);
            for (File file : aFile.listFiles()) {
                if (file.getName().endsWith(".obsep")) {
                    obsepFile = file;
                }
            }
        }
        //set experiment obsep file
        experiment.setObsepFile(obsepFile);
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
    public Experiment update(Experiment entity) {
        return experimentRepository.update(entity);
    }

    @Override
    public void delete(Experiment entity) {
        entity = experimentRepository.update(entity);
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
    public List<Integer> findExperimentNumbersByProjectId(Long projectId) {
        return experimentRepository.findExperimentNumbersByProjectId(projectId);
    }

    @Override
    public List<Experiment> findExperimentsByProjectIdAndStatus(Long projectId, ExperimentStatus experimentStatus) {
        return experimentRepository.findExperimentsByProjectIdAndStatus(projectId, experimentStatus);
    }

    @Override
    public void init(File microscopeDirectory) {
        this.mainDirectory = microscopeDirectory;
    }

    @Override
    public List<Experiment> findExperimentsByProjectId(Long projectId) {
        return experimentRepository.findExperimentsByProjectId(projectId);
    }

    @Override
    public void savePerformedExperiment(Experiment entity) {
        for (PlateCondition plateCondition : entity.getPlateConditionCollection()) {
            for (Well well : plateCondition.getWellCollection()) {
                for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                    wellHasImagingTypeRepository.save(wellHasImagingType);
                    wellHasImagingTypeRepository.flush();
                }
            }
        }
    }

    @Override
    public void save(Experiment entity) {
        experimentRepository.save(entity);
    }
}
