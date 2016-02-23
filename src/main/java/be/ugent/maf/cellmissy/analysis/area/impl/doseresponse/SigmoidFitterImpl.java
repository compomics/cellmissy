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
import java.util.Map;

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
        
        LinkedHashMap concentrations = analysisGroup.getConcentrationsMap().get( analysisGroup.getTreatmentToAnalyse() );
        analysisGroup.getVelocitiesMap();
        
        for (Map.Entry<Double,String> entry : concentrations.entrySet()) {
            
            
            
            fitter.addObservedPoint(concentration, velocity);
        }
        
        ParametricRealFunction function = new ParametricRealFunction() {
            @Override
            public double value( double c, double[] paramaters ) {
                
            double d = paramaters[0];
            double n = paramaters[1];
            double c_pow_n = Math.pow( c, n );
                
            return c_pow_n / (c_pow_n + Math.pow(d, n) );
            }
            
            @Override
            public double[] gradient( double c, double[] paramaters ) {
        
                double d = paramaters[0];
                double n = paramaters[1];
                double c_pow_n = Math.pow( c, n );
                double d_pow_n = Math.pow( d, n );
                
                double ddd = -n * c_pow_n * Math.pow( d, n-1 )
                             /
                             Math.pow( c_pow_n + d_pow_n, 2 );
                
                double ddn = (c_pow_n * d_pow_n * (Math.log(c) - Math.log(d))) 
                             /
                             Math.pow( c_pow_n + d_pow_n, 2);
                
                return new double[] {ddd, ddn};
            }
            
        };
        
        double[] params = null;
        
        params = fitter.fit(function, new double[] {1,1} );
        
        double d = params[0]; 
        double n = params[1];
        
        System.out.println("d=" + d);
        System.out.println("n=" + n);
        
        resultsHolder.se Math.pow( -(0.5 - 1) * Math.pow( d, -n ) 
                             /
                             0.5, 
                         -1/n );
    }

}
