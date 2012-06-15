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

    Experiment createNewExperiment(int experimentNumber, File projectFolder);
    
    List<Instrument> findAllInstruments(); 
    
    List<Magnification> findAllMagnifications();
    
    List<Integer> findExperimentNumbersByProjectId(Integer projectId);
    
    List<Experiment> findExperimentsByProjectIdAndStatus(Integer projectId, ExperimentStatus experimentStatus);
}
