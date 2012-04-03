/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.MatrixDimension;
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
    private MatrixDimensionRepository matrixDimensionRepository;

    @Override
    public MatrixDimension findById(Long id) {
        return matrixDimensionRepository.findById(id);
    }

    @Override
    public List<MatrixDimension> findAll() {
        return matrixDimensionRepository.findAll();
    }

    @Override
    public MatrixDimension save(MatrixDimension entity) {
        return matrixDimensionRepository.save(entity);
    }

    @Override
    public void delete(MatrixDimension entity) {
        entity = matrixDimensionRepository.save(entity);
        matrixDimensionRepository.delete(entity);               
    }

    @Override
    public MatrixDimension findByDimension(String dimension) {
        return matrixDimensionRepository.findByDimension(dimension);
    }
}
