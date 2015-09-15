/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.exception.CellMiaFoldersException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 * Experiment Service interface
 *
 * @author Paola
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * Initialize the service with the microscope directory
     *
     * @param microscopeDirectory
     */
    void init(File microscopeDirectory);

    /**
     * Create Folder Structure (if folders do not exist) for a new Experiment
     *
     * @param newExperiment
     * @throws CellMiaFoldersException
     */
    void createFolderStructure(Experiment newExperiment) throws CellMiaFoldersException;

    /**
     * Load existing folders for a certain experiment
     *
     * @param experiment
     * @throws CellMiaFoldersException if directory structure is not OK.
     */
    void loadFolderStructure(Experiment experiment) throws CellMiaFoldersException;

    /**
     * Reset folders to null
     */
    void resetFolders();

    /**
     * Finds all experiments numbers by id of a certain project
     *
     * @param projectId
     * @return
     */
    List<Integer> findExperimentNumbersByProjectId(Long projectId);

    /**
     * Find all experiments by project id
     *
     * @param projectId
     * @return
     */
    List<Experiment> findExperimentsByProjectId(Long projectId);

    /**
     * Find certain experiments that belong to a project and that have a certain
     * status
     *
     * @param projectId
     * @param experimentStatus
     * @return
     */
    List<Experiment> findExperimentsByProjectIdAndStatus(Long projectId, ExperimentStatus experimentStatus);

    /**
     * Save motility data for an already performed experiment This experiment
     * has been previously set up, so it needs to be updated later
     *
     * @param entity
     */
    void saveMigrationDataForExperiment(Experiment entity);

    /**
     * Copy the setup settings from an experiment to a new one. This method is
     * used only if the new experiment and the old one are inside the same
     * database.
     *
     * @param experimentToCopy: the experiment from which settings need to be
     * copied
     * @param newExperiment: the experiment to which settings
     */
    void copySetupSettingsFromOtherExperiment(Experiment experimentToCopy, Experiment newExperiment);

    /**
     * Export the setup template to an XML file for a given experiment. The
     * directory to save the file in must be specified as well.
     *
     * @param experiment
     * @param xmlFile
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    void exportExperimentToXMLFile(Experiment experiment, File xmlFile) throws JAXBException, FileNotFoundException;

    /**
     * Unmarshaling an XML file, we get the experiment back.
     *
     * @param xmlFile: the file to unmarshal
     * @return
     * @throws JAXBException
     * @throws SAXException
     * @throws IOException
     */
    Experiment getExperimentFromXMLFile(File xmlFile) throws JAXBException, SAXException, IOException;

    /**
     * Get all the errors (if any) that come from a not successful
     * XMLvalidation.
     *
     * @return a List of Error Messages
     */
    List<String> getXmlValidationErrorMesages();

    /**
     * Copy the setup settings from an experiment to a new one. This method is
     * used if the first experiment is obtained parsing an external XML file,
     * i.e. it might very well be that the settings are not saved in the current
     * database yet, so some checking are required before we can persist the
     * experiment.
     *
     * @param xmlExperiment
     * @param newExperiment
     */
    void copySetupSettingsFromXMLExperiment(Experiment xmlExperiment, Experiment newExperiment);

    /**
     * Copy an experiment from XML file to another experiment: along with set-up
     * settings and migration data as well.
     *
     * @param xmlExperiment
     */
    void copyExperimentFromXML(Experiment xmlExperiment);

    /**
     *
     * @param experiment
     * @return
     */
    List<Algorithm> getAlgorithms(Experiment experiment);

    /**
     *
     * @param experiment
     * @return
     */
    List<ImagingType> getImagingTypes(Experiment experiment);
}
