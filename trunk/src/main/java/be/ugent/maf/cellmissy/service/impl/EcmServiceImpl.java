/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.repository.EcmCompositionRepository;
import be.ugent.maf.cellmissy.repository.EcmRepository;
import be.ugent.maf.cellmissy.repository.MatrixDimensionRepository;
import be.ugent.maf.cellmissy.service.EcmService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("ecmService")
@Transactional
public class EcmServiceImpl implements EcmService {

    @Autowired
    private EcmRepository ecmRepository;
    @Autowired
    private MatrixDimensionRepository matrixDimensionRepository;
    @Autowired
    private EcmCompositionRepository ecmCompositionRepository;

    @Override
    public Ecm findById(Long id) {
        return ecmRepository.findById(id);
    }

    @Override
    public List<Ecm> findAll() {
        return ecmRepository.findAll();
    }

    @Override
    public Ecm save(Ecm entity) {
        return ecmRepository.save(entity);
    }

    @Override
    public void delete(Ecm entity) {
        entity = ecmRepository.save(entity);
        ecmRepository.delete(entity);
    }

    @Override
    public List<MatrixDimension> findAllMatrixDimension() {
        return matrixDimensionRepository.findAll();
    }

    @Override
    public List<EcmComposition> findAllEcmComposition() {
        return ecmCompositionRepository.findAll();
    }
}
