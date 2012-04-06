/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.repository.CellLineRepository;
import be.ugent.maf.cellmissy.service.CellLineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paola
 */
@Service("cellLineService")
@Transactional
public class CellLineServiceImpl implements CellLineService {

    @Autowired
    private CellLineRepository cellLineRepository;

    @Override
    public CellLine findById(Long id) {
        return cellLineRepository.findById(id);
    }

    @Override
    public List<CellLine> findAll() {
        return cellLineRepository.findAll();
    }

    @Override
    public CellLine save(CellLine entity) {
        return cellLineRepository.save(entity);
    }

    @Override
    public void delete(CellLine entity) {
        entity = cellLineRepository.save(entity);
        cellLineRepository.delete(entity);
    }
}