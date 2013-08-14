/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.repository;

import be.ugent.maf.cellmissy.entity.EcmComposition;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EcmCompositionRepository extends GenericRepository<EcmComposition, Long> {

    public List<EcmComposition> findByMatrixDimensionName(String matrixDimensionName);

    public void saveEcmComposition(EcmComposition ecmComposition);

    public EcmComposition findEcmCompositionByType(String ecmCompositionType);

    public EcmComposition findEcmCompositionByTypeAndMatrixDimensionName(String type, String matrixDimensionName);
}
