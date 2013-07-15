/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.repository.InstrumentRepository;
import be.ugent.maf.cellmissy.repository.MagnificationRepository;
import be.ugent.maf.cellmissy.service.InstrumentService;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Instrument Service Implementation.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Service("instrumentService")
@Transactional
public class InstrumentServiceImpl implements InstrumentService {

    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MagnificationRepository magnificationRepository;

    @Override
    public Instrument findById(Long id) {
        return instrumentRepository.findById(id);
    }

    @Override
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }

    @Override
    public Instrument update(Instrument entity) {
        return instrumentRepository.update(entity);
    }

    @Override
    public void delete(Instrument entity) {
        entity = instrumentRepository.findById(entity.getInstrumentid());
        instrumentRepository.delete(entity);
    }

    @Override
    public void save(Instrument entity) {
        instrumentRepository.save(entity);
    }

    @Override
    public List<Magnification> findAllMagnifications() {
        return magnificationRepository.findAll();
    }

    @Override
    public Instrument fetchExperiments(Instrument instrument) {
        Instrument findById = findById(instrument.getInstrumentid());
        Hibernate.initialize(findById.getExperimentList());
        return findById;
    }
}
