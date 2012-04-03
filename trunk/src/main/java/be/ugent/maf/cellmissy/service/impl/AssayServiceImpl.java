/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.repository.AssayRepository;
import be.ugent.maf.cellmissy.service.AssayService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("assayService")
@Transactional
public class AssayServiceImpl implements AssayService {
    
    @Autowired
    private AssayRepository assayRepository;

    @Override
    public Assay findById(Long id) {
        return assayRepository.findById(id);
    }

    @Override
    public List<Assay> findAll() {
        return assayRepository.findAll();
    }

    @Override
    public Assay save(Assay entity) {
        return assayRepository.save(entity);
    }

    @Override
    public void delete(Assay entity) {
        entity = assayRepository.save(entity);
        assayRepository.delete(entity);
    }

    @Override
    public List<Assay> findByMatrixDimension(MatrixDimension matrixDimension) {
        return assayRepository.findByMatrixDimension(matrixDimension);
    }    
    
}
