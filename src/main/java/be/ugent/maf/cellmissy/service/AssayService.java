/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import java.util.List;


/**
 *
 * @author Paola
 */
public interface AssayService extends GenericService<Assay, Long> {
    
    List<Assay> findByMatrixDimensionName(String matrixDimensionName);
    
}
