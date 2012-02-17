/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.service.impl;

import be.ugent.maf.limsdesktop.entity.PlateFormat;
import be.ugent.maf.limsdesktop.repository.PlateFormatRepository;
import be.ugent.maf.limsdesktop.service.PlateService;
import java.util.List;
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
    public PlateFormat save(PlateFormat entity) {
        return plateFormatRepository.save(entity);
    }

    @Override
    public void delete(PlateFormat entity) {
        entity = plateFormatRepository.save(entity);
        plateFormatRepository.delete(entity);
    }
}
