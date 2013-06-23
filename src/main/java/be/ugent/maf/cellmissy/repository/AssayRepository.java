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

    List<Assay> findByMatrixDimensionName(String matrixDimensionName);

    Assay findByAssayType(String assayType);
}
