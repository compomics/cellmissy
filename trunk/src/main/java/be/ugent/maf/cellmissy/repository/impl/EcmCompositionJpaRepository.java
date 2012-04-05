/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository.impl;

import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.repository.EcmCompositionRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Paola
 */
@Repository("ecmCompositionRepository")
public class EcmCompositionJpaRepository extends GenericJpaRepository<EcmComposition, Long> implements EcmCompositionRepository {
}
