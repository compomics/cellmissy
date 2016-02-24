/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.impl.doseresponse;

import be.ugent.maf.cellmissy.analysis.area.doseresponse.SigmoidFitter;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.optimization.fitting.CurveFitter;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

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
        
        LinkedHashMap concentrations = analysisGroup.getConcentrationsMap().get( analysisGroup.getTreatmentToAnalyse() );
        Collection<List<Double>> velocities = analysisGroup.getVelocitiesMap().values();
        
        for (Double concentration : concentrations.entrySet()) {
            
            for (int i = 0; i<velocities.size();i++)
            
            fitter.addObservedPoint(concentration, velocity);
        }
        
        ParametricUnivariateFunction function = new ParametricUnivariateFunction() {
            /**
             * @param conc The concentration of the drug, log transformed
             * @param paramaters The fitted parameters (bottom, top, logEC50 and hillslope)
             * @return The velocity
             */
            @Override
            public double value( double conc, double[] parameters ) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];
                double hillslope = parameters[3];
                
                return ( bottom + (top-bottom) / (1+ Math.pow(10,(logEC50-conc)*hillslope)) );
            }
            
            @Override
            public double[] gradient( double conc, double[] parameters ) {
                double bottom = parameters[0];
                double top = parameters[1];
                double logEC50 = parameters[2];
                double hillslope = parameters[3];
                
                return new double[] { 
                    1- (1/ (Math.pow(10,(logEC50-conc)*hillslope) +1 )),
                    1/ (Math.pow(10, (logEC50-conc)*hillslope)+1) ,
                    (hillslope*Math.log(10)*Math.pow(10, hillslope*(conc+logEC50))*(bottom-top)) / 
                        (Math.pow((Math.pow(10, conc*hillslope) + Math.pow(10,logEC50*hillslope)) ,2)),
                    -( (Math.log(10)*(logEC50-conc)*(top-bottom)*Math.pow(10, (logEC50-conc)*hillslope)) / 
                        (Math.pow( (Math.pow(10,(logEC50-conc)*hillslope)+1),2)))
                
                };
                
            }
            
        };
        
        double[] params = null;
        
        params = fitter.fit(function, new double[] {1,1,1,1} );
        
        double bottom = params[0]; 
        double top = params[1];
        double logEC50 = params[2];
        double hillslope = params[3];
        
        resultsHolder.setBottom(bottom);
        resultsHolder.setTop(top);
        resultsHolder.setLogEC50(logEC50);
        resultsHolder.setHillslope(hillslope);
    }

}
