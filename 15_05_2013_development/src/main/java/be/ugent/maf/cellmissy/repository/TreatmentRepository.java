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

    public List<TreatmentType> findByCategory(Integer treatmentCategory);

    public TreatmentType findByName(String treatmentType);

    public void saveTreatmentType(TreatmentType treatmentType);

    public List<String> findAllDrugSolvents();
}
