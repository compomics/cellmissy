/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import java.io.File;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * 
     * @param microscopeDirectory
     */
    void init(File microscopeDirectory);

    /**
     * 
     * @param newExperiment
     */
    void createFolderStructure(Experiment newExperiment);

    /**
     * 
     * @param experiment
     */
    void loadFolderStructure(Experiment experiment);

    /**
     * 
     * @return
     */
    List<Instrument> findAllInstruments();

    /**
     * 
     * @return
     */
    List<Magnification> findAllMagnifications();

    /**
     * 
     * @param projectId
     * @return
     */
    List<Integer> findExperimentNumbersByProjectId(Integer projectId);

    /**
     * 
     * @param projectId
     * @return
     */
    List<Experiment> findExperimentsByProjectId(Integer projectId);

    /**
     * 
     * @param projectId
     * @param experimentStatus
     * @return
     */
    List<Experiment> findExperimentsByProjectIdAndStatus(Integer projectId, ExperimentStatus experimentStatus);
}
