/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.CellLine;
import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.entity.Experiment;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface CellLineService extends GenericService<CellLine, Long> {

    List<CellLineType> findAllCellLineTypes();

    CellLineType findByName(String cellLineName);

    void saveCellLineType(CellLineType entity);

    List<String> findAllGrowthMedia();

    List<String> findAllSera();

    public List<CellLineType> findNewCellLines(Experiment experiment);
}
