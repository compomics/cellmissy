/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import be.ugent.maf.cellmissy.repository.TreatmentRepository;
import be.ugent.maf.cellmissy.service.TreatmentService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("treatmentService")
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Override
    public Treatment findById(Long id) {
        return treatmentRepository.findById(id);
    }

    @Override
    public List<Treatment> findAll() {
        return treatmentRepository.findAll();
    }

    @Override
    public Treatment update(Treatment entity) {
        return treatmentRepository.update(entity);
    }

    @Override
    public void delete(Treatment entity) {
        entity = treatmentRepository.findById(entity.getTreatmentid());
        treatmentRepository.delete(entity);
    }

    @Override
    public List<TreatmentType> findByCategory(Integer treatmentCategory) {
        return treatmentRepository.findByCategory(treatmentCategory);
    }

    @Override
    public TreatmentType findByName(String name) {
        return treatmentRepository.findByName(name);
    }

    @Override
    public List<TreatmentType> findNewTreatmentTypes(Experiment experiment) {
        List<TreatmentType> treatmentTypeList = new ArrayList<>();
        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            for (Treatment treatment : treatmentList) {
                TreatmentType treatmentType = treatment.getTreatmentType();
                TreatmentType findByName = findByName(treatmentType.getName());
                if (findByName == null) {
                    if (!treatmentTypeList.contains(treatmentType)) {
                        treatmentTypeList.add(treatmentType);
                    }
                }
            }

        }
        return treatmentTypeList;
    }

    @Override
    public void saveTreatmentType(TreatmentType treatmentType) {
        treatmentRepository.saveTreatmentType(treatmentType);
    }

    @Override
    public List<String> findAllDrugSolvents() {
        return treatmentRepository.findAllDrugSolvents();
    }

    @Override
    public void save(Treatment entity) {
        treatmentRepository.save(entity);
    }
}
