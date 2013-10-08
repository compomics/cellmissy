/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.Assay;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface AssayRepository extends GenericRepository<Assay, Long> {

    public List<Assay> findByMatrixDimensionName(String matrixDimensionName);

    public Assay findByAssayType(String assayType);

    public Assay findByAssayTypeAndMatrixDimensionName(String assayType, String matrixDimensionName);
}
