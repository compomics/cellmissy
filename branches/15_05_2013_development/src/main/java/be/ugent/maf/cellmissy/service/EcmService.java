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

    public List<MatrixDimension> findAllMatrixDimension();

    public List<EcmComposition> findEcmCompositionByMatrixDimensionName(String matrixDimensionName);

    public void saveEcmComposition(EcmComposition ecmComposition);

    public void saveBottomMatrix(BottomMatrix bottomMatrix);

    public void saveEcmDensity(EcmDensity ecmDensity);

    public List<BottomMatrix> findAllBottomMatrix();

    public BottomMatrix findBottomMatrixByType(String bottomMatrixType);

    public List<BottomMatrix> findNewBottomMatrices(Experiment experiment);

    public EcmComposition findEcmCompositionByType(String ecmCompositionType);

    public EcmComposition findEcmCompositionByTypeAndMatrixDimensionName(String type, String matrixDimensionName);

    public List<EcmComposition> findNewEcmCompositions(Experiment experiment);

    public List<EcmDensity> findAllEcmDensity();

    public EcmDensity findByEcmDensity(Double ecmDensity);

    public List<EcmDensity> findNewEcmDensities(Experiment experiment);

    public List<String> findAllPolimerysationPh();
}
