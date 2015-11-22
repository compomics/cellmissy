/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area.doseresponse;

/**
 * This class holds the results of the dose-response analysis
 * 
 * @author Gwendolien
 */
public class DoseResponseAnalysisResults {
    
    //minimum response, best-fit value or constrained by user
    private double bottomInitial;
    
    //maximum response, best-fit or constrained
    private double topInitial;
    
    //best-fit value or standard hillslope of curve
    private double hillslopeInitial;
    
    //log of 50% effective concentration
    private double logEC50Initial;
    
    //50% effective concentration
    private double ec50Initial;
    
    
    
    
    //same variables as above, only this time from fitting the normalized data
    private double bottomNormalized;
    
    private double topNormalized;
    
    private double hillslopeNormalized;
    
    private double logEC50Normalized;
    
    private double ec50Normalized;
    
    
    
}
