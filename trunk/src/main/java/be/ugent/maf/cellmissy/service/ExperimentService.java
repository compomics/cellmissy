/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.exception.FolderStructureException;
import java.io.File;
import java.util.List;

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
    void init(File microscopeDirectory);

    /**
     * Create Folder Structure (if folders do not exist) for a new Experiment
     *
     * @param newExperiment
     */
    void createFolderStructure(Experiment newExperiment);

    /**
     * Load existing folders for a certain experiment
     *
     * @param experiment
     * @throws FolderStructureException  if directory structure is not OK.
     */
    void loadFolderStructure(Experiment experiment) throws FolderStructureException;

    /**
     * Reset folders to null
     */
    void resetFolders();
    /**
     * Find all instruments
     *
     * @return
     */
    List<Instrument> findAllInstruments();

    /**
     * Find all magnifications
     *
     * @return
     */
    List<Magnification> findAllMagnifications();

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
     * Find certain experiments that belong to a project and that have a certain status
     *
     * @param projectId
     * @param experimentStatus
     * @return
     */
    List<Experiment> findExperimentsByProjectIdAndStatus(Long projectId, ExperimentStatus experimentStatus);

    /**
     * Save motility data for an already performed experiment This experiment has been previously set up, so it needs to be updated later
     *
     * @param entity
     */
    void saveMotilityDataForExperiment(Experiment entity);
}
