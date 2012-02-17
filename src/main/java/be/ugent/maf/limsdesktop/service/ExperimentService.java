/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service;

import be.ugent.maf.limsdesktop.entity.Experiment;
import java.io.File;

/**
 *
 * @author Paola
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    Experiment createNewExperiment(int experimentNumber, File projectFolder);
}
