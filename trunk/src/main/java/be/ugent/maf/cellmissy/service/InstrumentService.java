/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import java.util.List;

/**
 * Instrument Service interface.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface InstrumentService extends GenericService<Instrument, Long> {

    /**
     * Find all magnifications
     *
     * @return
     */
    public List<Magnification> findAllMagnifications();

    /**
     * For a given instrument, fetch all the experiments
     *
     * @param instrument
     */
    public Instrument fetchExperiments(Instrument instrument);
}
