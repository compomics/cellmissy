/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.repository.WellRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("wellRepository")
class WellJpaRepository extends GenericJpaRepository<Well, Long> implements WellRepository {
}
