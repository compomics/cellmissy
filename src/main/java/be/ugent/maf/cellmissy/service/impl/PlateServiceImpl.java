/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.repository.PlateFormatRepository;
import be.ugent.maf.cellmissy.service.PlateService;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("plateService")
@Transactional
public class PlateServiceImpl implements PlateService {

    @Autowired
    private PlateFormatRepository plateFormatRepository;

    @Override
    public PlateFormat findById(Long id) {
        return plateFormatRepository.findById(id);
    }

    @Override
    public List<PlateFormat> findAll() {
        return plateFormatRepository.findAll();
    }

    @Override
    public PlateFormat update(PlateFormat entity) {
        return plateFormatRepository.update(entity);
    }

    @Override
    public void delete(PlateFormat entity) {
        entity = plateFormatRepository.findById(entity.getPlateFormatid());
        plateFormatRepository.delete(entity);
    }

    @Override
    public PlateFormat findByFormat(int format) {
        return plateFormatRepository.findByFormat(format);
    }

    @Override
    public void save(PlateFormat entity) {
        plateFormatRepository.save(entity);
    }

    @Override
    public PlateFormat fetchExperiments(PlateFormat plateFormat) {
        PlateFormat findById = findById(plateFormat.getPlateFormatid());
        Hibernate.initialize(findById.getExperimentList());
        return findById;
    }
}
