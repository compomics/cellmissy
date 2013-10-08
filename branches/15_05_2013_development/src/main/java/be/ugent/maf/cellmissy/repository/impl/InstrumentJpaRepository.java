/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.repository.InstrumentRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("instrumentRepository")
public class InstrumentJpaRepository extends GenericJpaRepository<Instrument, Long> implements InstrumentRepository{
    
}
