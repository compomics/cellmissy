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

    List<EcmComposition> findByMatrixDimensionName(String matrixDimensionName);

    void saveEcmComposition(EcmComposition ecmComposition);

    EcmComposition findEcmCompositionByType(String ecmCompositionType);

    EcmComposition findEcmCompositionByTypeAndMatrixDimensionName(String type, String matrixDimensionName);
}
