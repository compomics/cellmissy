/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.TreatmentType;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface TreatmentService extends GenericService<Treatment, Long> {

    List<TreatmentType> findByCategory(Integer treatmentCategory);

    TreatmentType findByName(String name);

    List<TreatmentType> findNewTreatmentTypes(Experiment experiment);

    void saveTreatmentType(TreatmentType treatmentType);

    List<String> findAllDrugSolvents();
}
