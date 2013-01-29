/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EcmService extends GenericService<Ecm, Long> {

    public List<MatrixDimension> findAllMatrixDimension();

    public List<EcmComposition> findEcmCompositionByMatrixDimensionName(String matrixDimensionName);
    
    public void saveEcmComposition(EcmComposition ecmComposition);
    
    public List<BottomMatrix> findAllBottomMatrix();
    
    public List<EcmDensity> findAllEcmDensity();
    
    public List<String> findAllPolimerysationPh();
    
}
