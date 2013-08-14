/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.repository.AssayRepository;
import be.ugent.maf.cellmissy.service.AssayService;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Hibernate;
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
    public Assay update(Assay entity) {
        return assayRepository.update(entity);
    }

    @Override
    public void delete(Assay entity) {
        entity = assayRepository.findById(entity.getAssayid());
        assayRepository.delete(entity);
    }

    @Override
    public List<Assay> findByMatrixDimensionName(String matrixDimensionName) {
        return assayRepository.findByMatrixDimensionName(matrixDimensionName);
    }

    @Override
    public void save(Assay entity) {
        assayRepository.save(entity);
    }

    @Override
    public Assay findByAssayType(String assayType) {
        return assayRepository.findByAssayType(assayType);
    }

    @Override
    public Assay findByAssayTypeAndMatrixDimensionName(String assayType, String matrixDimensionName) {
        return assayRepository.findByAssayTypeAndMatrixDimensionName(assayType, matrixDimensionName);
    }

    @Override
    public List<Assay> findNewAssays(Experiment experiment) {
        List<Assay> assayList = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            Assay assay = plateCondition.getAssay();
            String assayType = assay.getAssayType();
            String dimension = assay.getMatrixDimension().getDimension();
            Assay foundAssay = findByAssayTypeAndMatrixDimensionName(assayType, dimension);
            if (foundAssay == null) {
                if (!assayList.contains(assay)) {
                    assayList.add(assay);
                }
            }
        }
        return assayList;
    }

    @Override
    public Assay fetchPlateConditions(Assay assay) {
        Assay findById = findById(assay.getAssayid());
        Hibernate.initialize(findById.getPlateConditionList());
        return findById;
    }
}
