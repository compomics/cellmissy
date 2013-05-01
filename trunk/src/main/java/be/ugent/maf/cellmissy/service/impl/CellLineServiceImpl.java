/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.repository.CellLineRepository;
import be.ugent.maf.cellmissy.repository.CellLineTypeRepository;
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
    @Autowired
    private CellLineTypeRepository cellLineTypeRepository;

    @Override
    public CellLine findById(Long id) {
        return cellLineRepository.findById(id);
    }

    @Override
    public List<CellLine> findAll() {
        return cellLineRepository.findAll();
    }

    @Override
    public CellLine update(CellLine entity) {
        return cellLineRepository.update(entity);
    }

    @Override
    public void delete(CellLine entity) {
        entity = cellLineRepository.findById(entity.getCellLineid());
        cellLineRepository.delete(entity);
    }

    @Override
    public List<CellLineType> findAllCellLineTypes() {
        return cellLineTypeRepository.findAll();
    }

    @Override
    public void saveCellLineType(CellLineType entity) {
        cellLineTypeRepository.save(entity);
    }

    @Override
    public List<String> findAllGrowthMedia() {
        return cellLineRepository.findAllGrowthMedia();
    }

    @Override
    public List<String> findAllSera() {
        return cellLineRepository.findAllSera();
    }

    @Override
    public void save(CellLine entity) {
        cellLineRepository.save(entity);
    }
}
