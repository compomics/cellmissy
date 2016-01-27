/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains the list of conditions selected by the user for dose-
 * response analysis, together with the results of this analysis.
 *
 * @author Gwendolien
 */
public class DoseResponseAnalysisGroup {

    //list of concentrations: iterate and get from Treatment
    private List<Double> concentrations;

    //list of velocities, coming from AreaAnalysisResults
    private List<Double> velocities;

    // list of dose response analysis results to be shown in table
    private List<DoseResponseAnalysisResults> doseResponseAnalysisResults;

    /**
     * Constructor
     */
    public DoseResponseAnalysisGroup() {
    }

    /**
     * Constructor
     *
     * @param plateConditions
     * @param areaAnalysisResults
     */
    public DoseResponseAnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> areaAnalysisResults) {

        //concentrations are ONLY used for computation in the dose-response part        
        //parameters for the methods are mostly always PlateCondition.
        //PlateCondition has List<Treatment>, Treatment has double concentration AND concentrationunit
        //this may need to be converted into standard form: xx * 10**-6 or 0.0000...xx
        //maybe not, because velocities have a corresponding unit, otherwise these also need to be converted
        
        //this might be better in a separate method in main dose-response or input controller
        this.concentrations = new ArrayList<>();
        
        for (PlateCondition plateCondition : plateConditions) {
            //1 platecondition = multiple wells
            List<Treatment> treatmentList = plateCondition.getTreatmentList();
            
            for (Treatment treatment : treatmentList) {
                double concentration = treatment.getConcentration();
                concentrations.add(concentration);
                //concentration unit should be saved somehow, needed for log-transformation
                String concentrationUnit = treatment.getConcentrationUnit();
            }
        }
        
        //setting the velocities from areaAnalysisResults
        //for every condition there are multiple replicates, this means multiple velocities
        this.velocities = new ArrayList<>();
        for (AreaAnalysisResults areaAnalysisResult : areaAnalysisResults) {
            velocities.addAll(Arrays.asList(areaAnalysisResult.getSlopes()));
        }

    }

    /**
     * Getters and setters
     *
     * @return
     */
    public List<DoseResponseAnalysisResults> getDoseResponseAnalysisResults() {
        return doseResponseAnalysisResults;
    }

    public void setDoseResponseAnalysisResults(List<DoseResponseAnalysisResults> doseResponseAnalysisResults) {
        this.doseResponseAnalysisResults = doseResponseAnalysisResults;
    }

    public List<Double> getConcentrations() {
        return concentrations;
    }

    public List<Double> getVelocities() {
        return velocities;
    }
    
}
