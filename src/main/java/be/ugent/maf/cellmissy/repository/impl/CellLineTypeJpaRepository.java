/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.CellLineType;
import be.ugent.maf.cellmissy.repository.CellLineTypeRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola Masuzzo
 */
@Repository("cellLineTypeRepository")
public class CellLineTypeJpaRepository extends GenericJpaRepository<CellLineType, Long> implements CellLineTypeRepository {
}
