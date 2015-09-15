/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface TreatmentRepository extends GenericRepository<Treatment, Long> {

    List<TreatmentType> findByCategory(Integer treatmentCategory);

    TreatmentType findByName(String treatmentType);

    void saveTreatmentType(TreatmentType treatmentType);

    List<String> findAllDrugSolvents();
}
