/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.AssayMedium;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.exception.CellMiaFoldersException;
import be.ugent.maf.cellmissy.parser.XMLParser;
import be.ugent.maf.cellmissy.repository.ExperimentRepository;
import be.ugent.maf.cellmissy.repository.WellHasImagingTypeRepository;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.CellLineService;
import be.ugent.maf.cellmissy.service.EcmService;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

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
    private WellHasImagingTypeRepository wellHasImagingTypeRepository;
    @Autowired
    private PlateService plateService;
    @Autowired
    private AssayService assayService;
    @Autowired
    private EcmService ecmService;
    @Autowired
    private CellLineService cellLineService;
    @Autowired
    private TreatmentService treatmentService;
    @Autowired
    private XMLParser xMLParser;
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
    public void createFolderStructure(Experiment newExperiment) throws CellMiaFoldersException {
        //create main folder for experiment
        experimentFolder = null;
        if (newExperiment.getProject().getProjectDescription().length() == 0) {
            projectFolderName = "CM_" + newExperiment.getProject().toString();
        } else {
            projectFolderName = "CM_" + newExperiment.getProject().toString() + "_" + newExperiment.getProject().getProjectDescription();
        }
        File[] listFiles = mainDirectory.listFiles();
        if (listFiles != null) {
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
        } else {
            throw new CellMiaFoldersException("No folders found in main directory (M:\\CM)\nBe sure you are connected to the server!");
        }

        // if experiment folder was successfully created, proceed with the inner folders
        if (experimentFolder != null) {
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
        } else {
            throw new CellMiaFoldersException("Experiment folder could not be created.\nPlease check folder structure!");
        }

    }

    @Override
    public void loadFolderStructure(Experiment experiment) throws CellMiaFoldersException {
        File[] listFiles = mainDirectory.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().contains(experiment.getProject().toString())) {
                    //project folder
                    projectFolder = file;
                    break;
                }
            }
        } else {
            throw new CellMiaFoldersException("No folders found in main directory (M:\\CM)\nBe sure you are connected to the server!");
        }

        // check for project folder
        if (projectFolder != null) {
            for (File file : projectFolder.listFiles()) {
                if (file.getName().contains(experiment.toString())) {
                    //experiment folder
                    experimentFolder = file;
                    break;
                }
            }
        } else {
            throw new CellMiaFoldersException("No project folder found.\nPlease check folder structure!");
        }

        // check for experiment folder
        if (experimentFolder != null) {
            //set experiment folde
            experiment.setExperimentFolder(experimentFolder);
            for (File file : experimentFolder.listFiles()) {
                if (file.getName().endsWith("raw")) {
                    //raw folder
                    rawFolder = file;
                } else if (file.getName().endsWith("MIA")) {
                    //Mia folder
                    miaFolder = file;
                    if (miaFolder != null) {
                        //set experiment mia folder
                        experiment.setMiaFolder(miaFolder);
                    } else {
                        throw new CellMiaFoldersException("No MIA folder found.\nPlease check folder structure!");
                    }
                }
            }
        } else {
            throw new CellMiaFoldersException("No experiment folder found.\nPlease check folder structure!");
        }

        // check for raw folder
        if (rawFolder != null) {
            for (File file : rawFolder.listFiles()) {
                if (file.getName().endsWith("set-up")) {
                    setupFolder = file;
                } else if (file.getName().endsWith("microscope")) {
                    microscopeFolder = file;
                }
            }
        } else {
            throw new CellMiaFoldersException("No raw folder found.\nPlease check folder structure!");
        }

        // check for set-up folder
        if (setupFolder != null) {
            //set setup folder of the experiment
            experiment.setSetupFolder(setupFolder);
        } else {
            throw new CellMiaFoldersException("No set-up folder found.\nPlease check folder structure!");
        }

        // check for microscope folder
        if (microscopeFolder != null) {
            //from microscope folder look for obsep folder(s)
            File docFiles = null;
            File[] microscopeFiles = microscopeFolder.listFiles();
            // check if microscope folder is empty
            if (microscopeFiles.length == 0) {
                throw new CellMiaFoldersException("Folder: " + microscopeFolder.getName() + " seems to be empty!\nPlease check folder structure!");
            }
            // still need to list the folders if the lenght is equal to one
            if (microscopeFiles.length == 1) {
                File[] subFiles = microscopeFiles[0].listFiles();
                for (File file : subFiles) {
                    if (file.getName().endsWith("Files")) {
                        docFiles = file;
                        break;
                    }
                }
            } else {
                for (File file : microscopeFiles) {
                    if (file.getName().endsWith("Files")) {
                        docFiles = file;
                        break;
                    }
                }
            }

            // check for folder containg obsep file
            if (docFiles != null) {
                List<File> obsepFolders = new ArrayList<>();
                File[] docFilesListed = docFiles.listFiles();
                for (int i = 0; i < docFilesListed.length; i++) {
                    if (docFilesListed[i].getName().startsWith("D")) {
                        obsepFolders.add(docFilesListed[i]);
                    }
                }
                //obsep file
                File obsepFile = null;

                if (obsepFolders.isEmpty()) {
                    throw new CellMiaFoldersException("Wrong structure in folder: " + docFiles.getName() + "\nNo folders containing obsep files found.");
                }

                if (obsepFolders.size() == 1) {
                    File aFile = obsepFolders.get(0);
                    for (File file : aFile.listFiles()) {
                        if (file.getName().endsWith(".obsep")) {
                            obsepFile = file;
                        }
                    }
                }

                //set experiment obsep file (the check for this null is done after, in the controller)
                experiment.setObsepFile(obsepFile);
            } else {
                throw new CellMiaFoldersException("Wrong structure in folder: " + microscopeFolder.getName());
            }
        } else {
            throw new CellMiaFoldersException("No microscope folder found.\nPlease check folder structure!");
        }
    }

    @Override
    public void resetFolders() {
        // set folders back to null
        experimentFolder = null;
        setupFolder = null;
        miaFolder = null;
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
        entity = experimentRepository.findById(entity.getExperimentid());
        experimentRepository.delete(entity);
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
    public void saveMigrationDataForExperiment(Experiment entity) {
        for (PlateCondition plateCondition : entity.getPlateConditionList()) {
            for (Well well : plateCondition.getWellList()) {
                for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                    wellHasImagingTypeRepository.save(wellHasImagingType);
                }
            }
        }
    }

    @Override
    public void save(Experiment entity) {
        experimentRepository.save(entity);
    }

    @Override
    public void copySetupSettingsFromOtherExperiment(Experiment experimentToCopy, Experiment newExperiment) {
        // get all the settings from the experiment to be copied
        // plate format
        newExperiment.setPlateFormat(experimentToCopy.getPlateFormat());
        // plate conditions
        List<PlateCondition> plateConditionList = experimentToCopy.getPlateConditionList();
        List<PlateCondition> conditions = new ArrayList<>();
        for (int i = 0; i < plateConditionList.size(); i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            // create a new condition
            PlateCondition newPlateCondition = new PlateCondition();
            newPlateCondition.setName("Condition " + (i + 1));
            // assay
            newPlateCondition.setAssay(plateCondition.getAssay());
            // ecm
            Ecm ecm = plateCondition.getEcm();
            Ecm newEcm = new Ecm(ecm.getConcentration(), ecm.getVolume(), ecm.getCoatingTime(), ecm.getCoatingTemperature(), ecm.getPolymerisationTime(), ecm.getPolymerisationTemperature(), ecm.getBottomMatrix(), ecm.getEcmComposition(), ecm.getEcmDensity(), ecm.getConcentrationUnit(), ecm.getVolumeUnit());
            newPlateCondition.setEcm(newEcm);
            // cell line
            CellLine cellLine = plateCondition.getCellLine();
            CellLine newCellLine = new CellLine(cellLine.getSeedingTime(), cellLine.getSeedingDensity(), cellLine.getGrowthMedium(), cellLine.getSerumConcentration(), cellLine.getCellLineType(), cellLine.getSerum());
            newPlateCondition.setCellLine(newCellLine);
            // assay medium
            AssayMedium assayMedium = plateCondition.getAssayMedium();
            AssayMedium newAssayMedium = new AssayMedium(assayMedium.getMedium(), assayMedium.getSerum(), assayMedium.getSerumConcentration(), assayMedium.getVolume());
            newPlateCondition.setAssayMedium(newAssayMedium);
            // treatments
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            List<Treatment> treatments = new ArrayList<>();
            for (Treatment treatment : treatmentList) {
                Treatment newTreatment = new Treatment(treatment.getConcentration(), treatment.getConcentrationUnit(), treatment.getTiming(), treatment.getDrugSolvent(), treatment.getDrugSolventConcentration(), treatment.getTreatmentType());
                newTreatment.setPlateCondition(newPlateCondition);
                treatments.add(newTreatment);
            }
            newPlateCondition.setTreatmentList(treatments);
            // wells
            List<Well> wellList = plateCondition.getWellList();
            List<Well> wells = new ArrayList<>();
            for (Well well : wellList) {
                Well newWell = new Well(well.getColumnNumber(), well.getRowNumber());
                newWell.setPlateCondition(newPlateCondition);
                wells.add(newWell);
            }
            newPlateCondition.setWellList(wells);
            conditions.add(newPlateCondition);
        }
        newExperiment.setPlateConditionList(conditions);
    }

    @Override
    public void exportExperimentToXMLFile(Experiment experiment, File xmlFile) throws JAXBException, FileNotFoundException {
        // we call the XML parser to marshal the object to a XML file
        xMLParser.marshal(Experiment.class, experiment, xmlFile);
    }

    @Override
    public Experiment getExperimentFromXMLFile(File xmlFile) throws JAXBException, SAXException, IOException {
        // we call the XML parser to unmarshal the XML file to an experiment
        // we need to cast the result object to an experiment
        return xMLParser.unmarshal(Experiment.class, xmlFile);
    }

    @Override
    public List<String> getXmlValidationErrorMesages() {
        return xMLParser.getValidationErrorMesage();
    }

    @Override
    public void copySetupSettingsFromXMLExperiment(Experiment xmlExperiment, Experiment newExperiment) {
        // get all the settings from the experiment to be copied
        // plate format: we check in the DB for the plate format: if it's there, we use it !
        PlateFormat plateFormat = xmlExperiment.getPlateFormat();
        PlateFormat foundPlateFormat = plateService.findByFormat(plateFormat.getFormat());
        if (foundPlateFormat != null) {
            newExperiment.setPlateFormat(foundPlateFormat);
        } else {
            newExperiment.setPlateFormat(plateFormat);
        }
        // get all the matrix dimensions in the DB
        List<MatrixDimension> findAllMatrixDimension = ecmService.findAllMatrixDimension();
        // plate conditions
        List<PlateCondition> plateConditionList = xmlExperiment.getPlateConditionList();
        List<PlateCondition> conditions = new ArrayList<>();
        for (int i = 0; i < plateConditionList.size(); i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            // create a new condition
            PlateCondition newPlateCondition = new PlateCondition();
            newPlateCondition.setName("Condition " + (i + 1));
            // assay: we check in the DB for the assay as well: if it's there, we use it !
            Assay assay = plateCondition.getAssay();
            String assayType = assay.getAssayType();
            String dimension = assay.getMatrixDimension().getDimension();
            Assay foundAssay = assayService.findByAssayTypeAndMatrixDimensionName(assayType, dimension);
            if (foundAssay != null) {
                newPlateCondition.setAssay(foundAssay); // foundAssay already has an id and a matrix dimension with an id
            } else {
                newPlateCondition.setAssay(assay); // assay has a null id, and a md with a null id as well, so we need to check for it
                MatrixDimension matrixDimension = assay.getMatrixDimension();
                for (MatrixDimension md : findAllMatrixDimension) {
                    if (matrixDimension.getDimension().equals(md.getDimension())) {
                        assay.setMatrixDimension(md); // md has an id !!
                        break;
                    }
                }
            }
            // ecm
            Ecm ecm = plateCondition.getEcm();
            Ecm newEcm = new Ecm();
            // composition type: we check in the DB for the ecm composition as well: if it's there, we use it !
            EcmComposition ecmComposition = ecm.getEcmComposition();
            String compositionType = ecmComposition.getCompositionType();
            String ecmDimension = ecmComposition.getMatrixDimension().getDimension();
            EcmComposition foundEcmComposition = ecmService.findEcmCompositionByTypeAndMatrixDimensionName(compositionType, ecmDimension);
            if (foundEcmComposition != null) {
                newEcm.setEcmComposition(foundEcmComposition);  // same issue for the matrix dimension as before: findEcmCompositionByType already has a md with an id
            } else {
                newEcm.setEcmComposition(ecmComposition);
                MatrixDimension matrixDimension = ecmComposition.getMatrixDimension(); // ecmComposition has a md with a null id
                for (MatrixDimension md : findAllMatrixDimension) {
                    if (matrixDimension.getDimension().equals(md.getDimension())) {
                        ecmComposition.setMatrixDimension(md); // md has an id !!
                        break;
                    }
                }
            }
            // for other fields, no check is needed, but we need to swith between the 2D, 3D and 2.D cases
            switch (newPlateCondition.getAssay().getMatrixDimension().getDimension()) {
                case "2D":
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    break;
                case "3D": // more parameters for the 3D !!
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    // polym temperature
                    newEcm.setPolymerisationTemperature(ecm.getPolymerisationTemperature());
                    // polym time
                    newEcm.setPolymerisationTime(ecm.getPolymerisationTime());
                    // polym ph
                    newEcm.setPolymerisationPh(ecm.getPolymerisationPh());
                    // bottom matrix volume
                    newEcm.setBottomMatrixVolume(ecm.getBottomMatrixVolume());
                    // top matrix volume
                    newEcm.setTopMatrixVolume(ecm.getTopMatrixVolume());
                    // bottom matrix type: we check in the DB for the bottom matrix type: if it's there, we use it !
                    BottomMatrix bottomMatrix = ecm.getBottomMatrix();
                    BottomMatrix foundBottomMatrix = ecmService.findBottomMatrixByType(bottomMatrix.getType());
                    if (foundBottomMatrix != null) {
                        newEcm.setBottomMatrix(foundBottomMatrix);
                    } else {
                        newEcm.setBottomMatrix(bottomMatrix);
                    }
                    // ecm density: we do the same for the ecm density
                    EcmDensity foundEcmDensity = ecmService.findByEcmDensity(ecm.getEcmDensity().getEcmDensity());
                    if (foundEcmDensity != null) {
                        newEcm.setEcmDensity(foundEcmDensity);
                    } else {
                        newEcm.setEcmDensity(ecm.getEcmDensity());
                    }
                    break;
                case "2.5D": // same as 3D, but NO top matrix volume is required
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    // polym temperature
                    newEcm.setPolymerisationTemperature(ecm.getPolymerisationTemperature());
                    // polym time
                    newEcm.setPolymerisationTime(ecm.getPolymerisationTime());
                    // polym ph
                    newEcm.setPolymerisationPh(ecm.getPolymerisationPh());
                    // bottom matrix volume
                    newEcm.setBottomMatrixVolume(ecm.getBottomMatrixVolume());
                    // top matrix volume
                    newEcm.setTopMatrixVolume(ecm.getTopMatrixVolume());
                    // bottom matrix type: we check in the DB for the bottom matrix type: if it's there, we use it !
                    BottomMatrix bottomMatrix1 = ecm.getBottomMatrix();
                    BottomMatrix foundBottomMatrix1 = ecmService.findBottomMatrixByType(bottomMatrix1.getType());
                    if (foundBottomMatrix1 != null) {
                        newEcm.setBottomMatrix(foundBottomMatrix1);
                    } else {
                        newEcm.setBottomMatrix(bottomMatrix1);
                    }
                    // ecm density
                    EcmDensity foundEcmDensity1 = ecmService.findByEcmDensity(ecm.getEcmDensity().getEcmDensity());
                    if (foundEcmDensity1 != null) {
                        newEcm.setEcmDensity(foundEcmDensity1);
                    } else {
                        newEcm.setEcmDensity(ecm.getEcmDensity());
                    }
                    break;
            }
            newPlateCondition.setEcm(newEcm);
            // cell line !
            CellLine cellLine = plateCondition.getCellLine();
            CellLine newCellLine = new CellLine();
            CellLineType cellLineType = cellLine.getCellLineType();
            // we need to check if the cell line type is already present in the DB !
            CellLineType foundCellLineType = cellLineService.findByName(cellLineType.getName());
            if (foundCellLineType != null) {
                newCellLine.setCellLineType(foundCellLineType);
            } else {
                newCellLine.setCellLineType(cellLineType);
            }
            // other fiedls: no check is needed
            // seeding time
            newCellLine.setSeedingTime(cellLine.getSeedingTime());
            // seeding density
            newCellLine.setSeedingDensity(cellLine.getSeedingDensity());
            // growth medium
            newCellLine.setGrowthMedium(cellLine.getGrowthMedium());
            // serum
            newCellLine.setSerum(cellLine.getSerum());
            // serum concentration
            newCellLine.setSerumConcentration(cellLine.getSerumConcentration());
            newPlateCondition.setCellLine(newCellLine);
            // assay medium : no check is needed in the database !!
            AssayMedium assayMedium = plateCondition.getAssayMedium();
            AssayMedium newAssayMedium = new AssayMedium(assayMedium.getMedium(), assayMedium.getSerum(), assayMedium.getSerumConcentration(), assayMedium.getVolume());
            newPlateCondition.setAssayMedium(newAssayMedium);
            // treatments: here, we need to check for the tratment type !
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            List<Treatment> treatments = new ArrayList<>();
            for (Treatment treatment : treatmentList) {
                Treatment newTreatment = new Treatment();
                // we check here for the treatment type
                TreatmentType treatmentType = treatment.getTreatmentType();
                TreatmentType foundTreatmentType = treatmentService.findByName(treatmentType.getName());
                if (foundTreatmentType != null) {
                    newTreatment.setTreatmentType(foundTreatmentType);
                } else {
                    newTreatment.setTreatmentType(treatmentType);
                }
                // no check is needed for other fields
                // concentration
                newTreatment.setConcentration(treatment.getConcentration());
                // concentration unit
                newTreatment.setConcentrationUnit(treatment.getConcentrationUnit());
                // timing
                newTreatment.setTiming(treatment.getTiming());
                // drug solvent, if not null
                String drugSolvent = treatment.getDrugSolvent();
                if (drugSolvent != null) {
                    newTreatment.setDrugSolvent(drugSolvent);
                }
                // drug solvent concentration
                newTreatment.setDrugSolventConcentration(treatment.getDrugSolventConcentration());
                newTreatment.setPlateCondition(newPlateCondition);
                treatments.add(newTreatment);
            }
            newPlateCondition.setTreatmentList(treatments);
            // wells : no check needed
            List<Well> wellList = plateCondition.getWellList();
            List<Well> wells = new ArrayList<>();
            for (Well well : wellList) {
                Well newWell = new Well(well.getColumnNumber(), well.getRowNumber());
                newWell.setPlateCondition(newPlateCondition);
                wells.add(newWell);
            }
            newPlateCondition.setWellList(wells);
            conditions.add(newPlateCondition);
        }
        newExperiment.setPlateConditionList(conditions);
    }

    @Override
    public void copyExperimentFromXML(Experiment xmlExperiment) {
        // get all the settings from the experiment to be copied
        // plate format: we check in the DB for the plate format: if it's there, we use it !
        PlateFormat plateFormat = xmlExperiment.getPlateFormat();
        PlateFormat foundPlateFormat = plateService.findByFormat(plateFormat.getFormat());
        if (foundPlateFormat != null) {
            xmlExperiment.setPlateFormat(foundPlateFormat);
        } else {
            xmlExperiment.setPlateFormat(plateFormat);
        }
        // get all the matrix dimensions in the DB
        List<MatrixDimension> findAllMatrixDimension = ecmService.findAllMatrixDimension();
        // plate conditions
        List<PlateCondition> plateConditionList = xmlExperiment.getPlateConditionList();
        List<PlateCondition> conditions = new ArrayList<>();
        for (int i = 0; i < plateConditionList.size(); i++) {
            PlateCondition plateCondition = plateConditionList.get(i);
            // create a new condition
            PlateCondition newPlateCondition = new PlateCondition();
            // assay: we check in the DB for the assay as well: if it's there, we use it !
            Assay assay = plateCondition.getAssay();
            String assayType = assay.getAssayType();
            String dimension = assay.getMatrixDimension().getDimension();
            Assay foundAssay = assayService.findByAssayTypeAndMatrixDimensionName(assayType, dimension);
            if (foundAssay != null) {
                newPlateCondition.setAssay(foundAssay); // foundAssay already has an id and a matrix dimension with an id
            } else {
                newPlateCondition.setAssay(assay); // assay has a null id, and a md with a null id as well, so we need to check for it
                MatrixDimension matrixDimension = assay.getMatrixDimension();
                for (MatrixDimension md : findAllMatrixDimension) {
                    if (matrixDimension.getDimension().equals(md.getDimension())) {
                        assay.setMatrixDimension(md); // md has an id !!
                        break;
                    }
                }
            }
            // ecm
            Ecm ecm = plateCondition.getEcm();
            Ecm newEcm = new Ecm();
            // composition type: we check in the DB for the ecm composition as well: if it's there, we use it !
            EcmComposition ecmComposition = ecm.getEcmComposition();
            String compositionType = ecmComposition.getCompositionType();
            String ecmDimension = ecmComposition.getMatrixDimension().getDimension();
            EcmComposition foundEcmComposition = ecmService.findEcmCompositionByTypeAndMatrixDimensionName(compositionType, ecmDimension);
            if (foundEcmComposition != null) {
                newEcm.setEcmComposition(foundEcmComposition);  // same issue for the matrix dimension as before: findEcmCompositionByType already has a md with an id
            } else {
                newEcm.setEcmComposition(ecmComposition);
                MatrixDimension matrixDimension = ecmComposition.getMatrixDimension(); // ecmComposition has a md with a null id
                for (MatrixDimension md : findAllMatrixDimension) {
                    if (matrixDimension.getDimension().equals(md.getDimension())) {
                        ecmComposition.setMatrixDimension(md); // md has an id !!
                        break;
                    }
                }
            }
            // for other fields, no check is needed, but we need to swith between the 2D, 3D and 2.D cases
            switch (newPlateCondition.getAssay().getMatrixDimension().getDimension()) {
                case "2D":
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    break;
                case "3D": // more parameters for the 3D !!
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    // polym temperature
                    newEcm.setPolymerisationTemperature(ecm.getPolymerisationTemperature());
                    // polym time
                    newEcm.setPolymerisationTime(ecm.getPolymerisationTime());
                    // polym ph
                    newEcm.setPolymerisationPh(ecm.getPolymerisationPh());
                    // bottom matrix volume
                    newEcm.setBottomMatrixVolume(ecm.getBottomMatrixVolume());
                    // top matrix volume
                    newEcm.setTopMatrixVolume(ecm.getTopMatrixVolume());
                    // bottom matrix type: we check in the DB for the bottom matrix type: if it's there, we use it !
                    BottomMatrix bottomMatrix = ecm.getBottomMatrix();
                    BottomMatrix foundBottomMatrix = ecmService.findBottomMatrixByType(bottomMatrix.getType());
                    if (foundBottomMatrix != null) {
                        newEcm.setBottomMatrix(foundBottomMatrix);
                    } else {
                        newEcm.setBottomMatrix(bottomMatrix);
                    }
                    // ecm density: we do the same for the ecm density
                    EcmDensity foundEcmDensity = ecmService.findByEcmDensity(ecm.getEcmDensity().getEcmDensity());
                    if (foundEcmDensity != null) {
                        newEcm.setEcmDensity(foundEcmDensity);
                    } else {
                        newEcm.setEcmDensity(ecm.getEcmDensity());
                    }
                    break;
                case "2.5D": // same as 3D, but NO top matrix volume is required
                    // concentration
                    newEcm.setConcentration(ecm.getConcentration());
                    newEcm.setConcentrationUnit(ecm.getConcentrationUnit());
                    // volume
                    newEcm.setVolume(ecm.getVolume());
                    newEcm.setVolumeUnit(ecm.getVolumeUnit());
                    // coating temperature
                    newEcm.setCoatingTemperature(ecm.getCoatingTemperature());
                    // coating time
                    newEcm.setCoatingTime(ecm.getCoatingTime());
                    // polym temperature
                    newEcm.setPolymerisationTemperature(ecm.getPolymerisationTemperature());
                    // polym time
                    newEcm.setPolymerisationTime(ecm.getPolymerisationTime());
                    // polym ph
                    newEcm.setPolymerisationPh(ecm.getPolymerisationPh());
                    // bottom matrix volume
                    newEcm.setBottomMatrixVolume(ecm.getBottomMatrixVolume());
                    // top matrix volume
                    newEcm.setTopMatrixVolume(ecm.getTopMatrixVolume());
                    // bottom matrix type: we check in the DB for the bottom matrix type: if it's there, we use it !
                    BottomMatrix bottomMatrix1 = ecm.getBottomMatrix();
                    BottomMatrix foundBottomMatrix1 = ecmService.findBottomMatrixByType(bottomMatrix1.getType());
                    if (foundBottomMatrix1 != null) {
                        newEcm.setBottomMatrix(foundBottomMatrix1);
                    } else {
                        newEcm.setBottomMatrix(bottomMatrix1);
                    }
                    // ecm density
                    EcmDensity foundEcmDensity1 = ecmService.findByEcmDensity(ecm.getEcmDensity().getEcmDensity());
                    if (foundEcmDensity1 != null) {
                        newEcm.setEcmDensity(foundEcmDensity1);
                    } else {
                        newEcm.setEcmDensity(ecm.getEcmDensity());
                    }
                    break;
            }
            newPlateCondition.setEcm(newEcm);
            // cell line !
            CellLine cellLine = plateCondition.getCellLine();
            CellLine newCellLine = new CellLine();
            CellLineType cellLineType = cellLine.getCellLineType();
            // we need to check if the cell line type is already present in the DB !
            CellLineType foundCellLineType = cellLineService.findByName(cellLineType.getName());
            if (foundCellLineType != null) {
                newCellLine.setCellLineType(foundCellLineType);
            } else {
                newCellLine.setCellLineType(cellLineType);
            }
            // other fiedls: no check is needed
            // seeding time
            newCellLine.setSeedingTime(cellLine.getSeedingTime());
            // seeding density
            newCellLine.setSeedingDensity(cellLine.getSeedingDensity());
            // growth medium
            newCellLine.setGrowthMedium(cellLine.getGrowthMedium());
            // serum
            newCellLine.setSerum(cellLine.getSerum());
            // serum concentration
            newCellLine.setSerumConcentration(cellLine.getSerumConcentration());
            newPlateCondition.setCellLine(newCellLine);
            // assay medium : no check is needed in the database !!
            AssayMedium assayMedium = plateCondition.getAssayMedium();
            AssayMedium newAssayMedium = new AssayMedium(assayMedium.getMedium(), assayMedium.getSerum(), assayMedium.getSerumConcentration(), assayMedium.getVolume());
            newPlateCondition.setAssayMedium(newAssayMedium);
            // treatments: here, we need to check for the tratment type !
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            List<Treatment> treatments = new ArrayList<>();
            for (Treatment treatment : treatmentList) {
                Treatment newTreatment = new Treatment();
                // we check here for the treatment type
                TreatmentType treatmentType = treatment.getTreatmentType();
                TreatmentType foundTreatmentType = treatmentService.findByName(treatmentType.getName());
                if (foundTreatmentType != null) {
                    newTreatment.setTreatmentType(foundTreatmentType);
                } else {
                    newTreatment.setTreatmentType(treatmentType);
                }
                // no check is needed for other fields
                // concentration
                newTreatment.setConcentration(treatment.getConcentration());
                // concentration unit
                newTreatment.setConcentrationUnit(treatment.getConcentrationUnit());
                // timing
                newTreatment.setTiming(treatment.getTiming());
                // drug solvent, if not null
                String drugSolvent = treatment.getDrugSolvent();
                if (drugSolvent != null) {
                    newTreatment.setDrugSolvent(drugSolvent);
                }
                // drug solvent concentration
                newTreatment.setDrugSolventConcentration(treatment.getDrugSolventConcentration());
                newTreatment.setPlateCondition(newPlateCondition);
                treatments.add(newTreatment);
            }
            newPlateCondition.setTreatmentList(treatments);
            // wells : no check needed
            List<Well> wellList = plateCondition.getWellList();
            List<Well> wells = new ArrayList<>();
            for (Well well : wellList) {
                Well newWell = new Well(well.getColumnNumber(), well.getRowNumber());
                newWell.setPlateCondition(newPlateCondition);
                newWell.setWellHasImagingTypeList(well.getWellHasImagingTypeList());

                //the other way around: set the well for each wellHasImagingType
                for (WellHasImagingType wellHasImagingType : newWell.getWellHasImagingTypeList()) {
                    // time steps
                    for (TimeStep timeStep : wellHasImagingType.getTimeStepList()) {
                        timeStep.setWellHasImagingType(wellHasImagingType);
                    }
                    // tracks
                    for (Track track : wellHasImagingType.getTrackList()) {
                        track.setWellHasImagingType(wellHasImagingType);
                        // track points
                        for (TrackPoint trackPoint : track.getTrackPointList()) {
                            trackPoint.setTrack(track);
                        }
                    }
                    wellHasImagingType.setWell(newWell);
                }
                wells.add(newWell);
            }
            newPlateCondition.setWellList(wells);
            conditions.add(newPlateCondition);
        }
        xmlExperiment.setPlateConditionList(conditions);
        // we set other fields
        xmlExperiment.setDuration(xmlExperiment.getDuration());
        xmlExperiment.setExperimentStatus(xmlExperiment.getExperimentStatus());
        xmlExperiment.setTimeFrames(xmlExperiment.getTimeFrames());
        xmlExperiment.setExperimentInterval(xmlExperiment.getExperimentInterval());
        xmlExperiment.setPurpose(xmlExperiment.getPurpose());
        xmlExperiment.setExperimentDate(xmlExperiment.getExperimentDate());
        xmlExperiment.setExperimentNumber(xmlExperiment.getExperimentNumber());
    }

    @Override
    public List<Algorithm> getAlgorithms(Experiment experiment) {
        List<Algorithm> algorithms = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            for (Well well : plateCondition.getWellList()) {
                for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                    Algorithm algorithm = wellHasImagingType.getAlgorithm();
                    if (!algorithms.contains(algorithm)) {
                        algorithms.add(algorithm);
                    }
                }
            }
        }
        return algorithms;
    }

    @Override
    public List<ImagingType> getImagingTypes(Experiment experiment) {
        List<ImagingType> imagingTypes = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            for (Well well : plateCondition.getWellList()) {
                for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                    ImagingType imagingType = wellHasImagingType.getImagingType();
                    if (!imagingTypes.contains(imagingType)) {
                        imagingTypes.add(imagingType);
                    }
                }
            }
        }
        return imagingTypes;
    }
}
