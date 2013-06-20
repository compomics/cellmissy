/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.exception.CellMiaFoldersException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Paola
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * Initialize the service with the microscope directory
     *
     * @param microscopeDirectory
     */
    public void init(File microscopeDirectory);

    /**
     * Create Folder Structure (if folders do not exist) for a new Experiment
     *
     * @param newExperiment
     * @throws CellMiaFoldersException
     */
    public void createFolderStructure(Experiment newExperiment) throws CellMiaFoldersException;

    /**
     * Load existing folders for a certain experiment
     *
     * @param experiment
     * @throws CellMiaFoldersException if directory structure is not OK.
     */
    public void loadFolderStructure(Experiment experiment) throws CellMiaFoldersException;

    /**
     * Reset folders to null
     */
    public void resetFolders();

    /**
     * Find all instruments
     *
     * @return
     */
    public List<Instrument> findAllInstruments();

    /**
     * Find all magnifications
     *
     * @return
     */
    public List<Magnification> findAllMagnifications();

    /**
     * Finds all experiments numbers by id of a certain project
     *
     * @param projectId
     * @return
     */
    public List<Integer> findExperimentNumbersByProjectId(Long projectId);

    /**
     * Find all experiments by project id
     *
     * @param projectId
     * @return
     */
    public List<Experiment> findExperimentsByProjectId(Long projectId);

    /**
     * Find certain experiments that belong to a project and that have a certain
     * status
     *
     * @param projectId
     * @param experimentStatus
     * @return
     */
    public List<Experiment> findExperimentsByProjectIdAndStatus(Long projectId, ExperimentStatus experimentStatus);

    /**
     * Save motility data for an already performed experiment This experiment
     * has been previously set up, so it needs to be updated later
     *
     * @param entity
     */
    public void saveMigrationDataForExperiment(Experiment entity);

    /**
     * Copy the setup settings from an experiment to a new one.
     *
     * @param experimentToCopy: the experiment from which settings need to be copied
     * @param newExperiment: the experiment to which settings
     */
    public void copyExperimentSetup(Experiment experimentToCopy, Experiment newExperiment);

    /**
     * Export the setup template to an external text file for a given
     * experiment. The directory to save the file in must be specified as well.
     *
     * @param directory
     * @param experiment
     */
    public void exportSetupTemplateToXMLFile(File directory, Experiment experiment) throws JAXBException, FileNotFoundException;
}
