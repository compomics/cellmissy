/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import java.util.LinkedHashMap;
import java.util.List;

/**
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
     */
    void fitNoConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder);
    
    void fitBotConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain);
    
    void fitTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain);
    
    void fitHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, int standardHillSlope);
    
    void fitBotTopConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain);
    
    void fitBotHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, int standardHillSlope);
    
    void fitTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double topConstrain, int standardHillSlope);
    
    void fitBotTopHillConstrain(LinkedHashMap<Double, List<Double>> dataToFit, SigmoidFittingResultsHolder resultsHolder, Double bottomConstrain, Double topConstrain, int standardHillSlope);
}
