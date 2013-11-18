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

    public List<TreatmentType> findByCategory(Integer treatmentCategory);

    public TreatmentType findByName(String name);

    public List<TreatmentType> findNewTreatmentTypes(Experiment experiment);

    public void saveTreatmentType(TreatmentType treatmentType);

    public List<String> findAllDrugSolvents();
}
