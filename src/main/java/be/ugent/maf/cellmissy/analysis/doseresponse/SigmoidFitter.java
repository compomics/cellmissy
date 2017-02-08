/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponsePair;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import java.util.List;

/**
 * Interface for the fitter. Every parameter contraint combination is handled by
 * a different method.
 *
 * @author Gwendolien
 */
public interface SigmoidFitter {

    /**
     * Fits the data to a sigmoid model. Adds the fitted parameters to the
     * results holder.
     *
     * @param dataToFit The data that will be fit
     * @param resultsHolder Holds the results from the fitting.
     * @param standardHillslope For initial parameter extimation or constraining
     */
    void fitNoConstrain(List<DoseResponsePair> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillslope);

    void fitBotConstrain(List<DoseResponsePair> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillslope);

    void fitTopConstrain(List<DoseResponsePair> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillslope);

    void fitBotTopConstrain(List<DoseResponsePair> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillslope);

    /**
     * Fitter methods that include a constrained hillslope. Commented because of
     * little biological significance from a cell migration view. Possible to be
     * re-implemented at some point.
     */
//    void fitHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillSlope);
//    void fitBotHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillSlope);
//    void fitTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillSlope);
//    void fitBotTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillSlope);
}
