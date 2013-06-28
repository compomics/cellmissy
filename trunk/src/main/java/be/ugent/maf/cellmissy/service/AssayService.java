/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.Experiment;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface AssayService extends GenericService<Assay, Long> {

    public List<Assay> findByMatrixDimensionName(String matrixDimensionName);

    public Assay findByAssayType(String assayType);

    public Assay findByAssayTypeAndMatrixDimensionName(String assayType, String matrixDimensionName);

    public List<Assay> findNewAssays(Experiment experiment);
}
