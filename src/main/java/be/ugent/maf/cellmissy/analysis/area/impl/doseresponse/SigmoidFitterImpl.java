/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import java.util.LinkedHashMap;

import org.apache.commons.math.optimization.fitting.CurveFitter;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math.optimization.fitting.ParametricRealFunction;

/**
 *
 * @author Gwendolien
 */
public class SigmoidFitterImpl implements SigmoidFitter {

    //implementation of interface method
    @Override
    public void fitData(DoseResponseAnalysisGroup analysisGroup, SigmoidFittingResultsHolder resultsHolder) {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter( optimizer );
        
        LinkedHashMap concentrations = analysisGroup.getConcentrationsMap().get(analysisGroup.getTreatmentToAnalyse());
        analysisGroup.getVelocitiesMap();
        
        for (Double concentration : concentrations.keySet()) {
            
        }
    }
    
    
}
