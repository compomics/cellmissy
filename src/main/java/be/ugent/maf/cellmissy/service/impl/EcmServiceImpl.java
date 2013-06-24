/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.repository.BottomMatrixRepository;
import be.ugent.maf.cellmissy.repository.EcmCompositionRepository;
import be.ugent.maf.cellmissy.repository.EcmDensityRepository;
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
    @Autowired
    private BottomMatrixRepository bottomMatrixRepository;
    @Autowired
    private EcmDensityRepository ecmDensityRepository;

    @Override
    public Ecm findById(Long id) {
        return ecmRepository.findById(id);
    }

    @Override
    public List<Ecm> findAll() {
        return ecmRepository.findAll();
    }

    @Override
    public Ecm update(Ecm entity) {
        return ecmRepository.update(entity);
    }

    @Override
    public void delete(Ecm entity) {
        entity = ecmRepository.findById(entity.getEcmid());
        ecmRepository.delete(entity);
    }

    @Override
    public List<MatrixDimension> findAllMatrixDimension() {
        return matrixDimensionRepository.findAll();
    }

    @Override
    public List<EcmComposition> findEcmCompositionByMatrixDimensionName(String matrixDimensionName) {
        return ecmCompositionRepository.findByMatrixDimensionName(matrixDimensionName);
    }

    @Override
    public List<BottomMatrix> findAllBottomMatrix() {
        return bottomMatrixRepository.findAll();
    }

    @Override
    public List<EcmDensity> findAllEcmDensity() {
        return ecmDensityRepository.findAll();
    }

    @Override
    public void saveEcmComposition(EcmComposition ecmComposition) {
        ecmCompositionRepository.saveEcmComposition(ecmComposition);
    }

    @Override
    public void saveBottomMatrix(BottomMatrix bottomMatrix) {
        bottomMatrixRepository.save(bottomMatrix);
    }

    @Override
    public void saveEcmDensity(EcmDensity ecmDensity) {
        ecmDensityRepository.save(ecmDensity);
    }

    @Override
    public List<String> findAllPolimerysationPh() {
        return ecmRepository.findAllPolimerysationPh();
    }

    @Override
    public void save(Ecm entity) {
        ecmRepository.save(entity);
    }

    @Override
    public BottomMatrix findBottomMatrixByType(String bottomMatrixType) {
        return bottomMatrixRepository.findBottomMatrixByType(bottomMatrixType);
    }

    @Override
    public EcmComposition findEcmCompositionByType(String ecmCompositionType) {
        return ecmCompositionRepository.findEcmCompositionByType(ecmCompositionType);
    }

    @Override
    public EcmComposition findEcmCompositionByTypeAndMatrixDimensionName(String type, String matrixDimensionName) {
        return ecmCompositionRepository.findEcmCompositionByTypeAndMatrixDimensionName(type, matrixDimensionName);
    }

    @Override
    public EcmDensity findByEcmDensity(Double ecmDensity) {
        return ecmDensityRepository.findByEcmDensity(ecmDensity);
    }
}
