/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Treatment;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface TreatmentRepository extends GenericRepository<Treatment, Long> {

    List<Treatment> findByType(Integer treatmentType);
}
