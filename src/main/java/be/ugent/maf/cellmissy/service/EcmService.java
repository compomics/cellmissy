/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Ecm;
import be.ugent.maf.cellmissy.entity.BottomMatrix;
import be.ugent.maf.cellmissy.entity.EcmComposition;
import be.ugent.maf.cellmissy.entity.EcmDensity;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import java.util.List;

/**
 *
 * @author Paola
 */
public interface EcmService extends GenericService<Ecm, Long> {

    List<MatrixDimension> findAllMatrixDimension();

    List<EcmComposition> findEcmCompositionByMatrixDimensionName(String matrixDimensionName);

    void saveEcmComposition(EcmComposition ecmComposition);

    void saveBottomMatrix(BottomMatrix bottomMatrix);

    void saveEcmDensity(EcmDensity ecmDensity);

    List<BottomMatrix> findAllBottomMatrix();

    BottomMatrix findBottomMatrixByType(String bottomMatrixType);

    List<BottomMatrix> findNewBottomMatrices(Experiment experiment);

    EcmComposition findEcmCompositionByType(String ecmCompositionType);

    EcmComposition findEcmCompositionByTypeAndMatrixDimensionName(String type, String matrixDimensionName);

    List<EcmComposition> findNewEcmCompositions(Experiment experiment);

    List<EcmDensity> findAllEcmDensity();

    EcmDensity findByEcmDensity(Double ecmDensity);

    List<EcmDensity> findNewEcmDensities(Experiment experiment);

    List<String> findAllPolimerysationPh();
}
