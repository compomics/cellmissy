/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.CellLine;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface CellLineRepository extends GenericRepository<CellLine, Long> {
    
    List<String> findAllGrowthMedia();
    
    List<String> findAllSera();
}
