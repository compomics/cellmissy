/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import java.io.File;

/**
 *
 * @author Paola
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    Experiment createNewExperiment(int experimentNumber, File projectFolder);
}
