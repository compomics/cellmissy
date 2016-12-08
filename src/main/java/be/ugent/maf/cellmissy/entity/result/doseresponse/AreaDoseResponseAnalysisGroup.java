/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * This class contains the list of conditions selected by the user for dose-
 * response analysis, together with the results of this analysis.
 *
 * @author Gwendolien
 */
public class AreaDoseResponseAnalysisGroup implements DoseResponseAnalysisGroup {

    //An experiment might have multiple treatments. Per condition the concentrations for a treatment may vary.
    //The concentration value is mapped to his unit of measurement.
    private LinkedHashMap<String, LinkedHashMap<Double, String>> concentrationsMap;

    //A condition can have multiple replicates, each with their own velocity.
    private LinkedHashMap<PlateCondition, List<Double>> velocitiesMap;

    //The name of the treatment to be analyzed
    private String treatmentToAnalyse;

    //The results of the analysis
    private DoseResponseAnalysisResults doseResponseAnalysisResults;

    
    /**
     * Constructor
     *
     * @param plateConditions
     * @param areaAnalysisResults
     */
    public AreaDoseResponseAnalysisGroup(List<PlateCondition> plateConditions, List<AreaAnalysisResults> areaAnalysisResults) {

        //PlateCondition has List<Treatment>, Treatment has double concentration AND concentrationunit
        this.concentrationsMap = new LinkedHashMap<>();

        //make set of all different treatments in selected conditions
        Set<String> treatmentNames = new HashSet<>();
        for (PlateCondition plateCondition : plateConditions) {
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                treatmentNames.add(treatment.getTreatmentType().getName());
            }
        }

        for (String treatmentName : treatmentNames) {
            //every treatment will have it's own map of concentrations
            LinkedHashMap<Double, String> nestedMap = new LinkedHashMap<>();
            for (PlateCondition plateCondition : plateConditions) {

                for (Treatment treatment : plateCondition.getTreatmentList()) {
                    if (treatment.getTreatmentType().getName().equals(treatmentName)) {
                        nestedMap.put(treatment.getConcentration(), treatment.getConcentrationUnit());
                    }
                }
            }
            concentrationsMap.put(treatmentName, nestedMap);
        }

        //setting the velocities from areaAnalysisResults
        this.velocitiesMap = new LinkedHashMap<>();

        for (int i = 0; i < plateConditions.size(); i++) {
            //for every condition there are multiple replicates, this means multiple velocities
            List<Double> replicateVelocities = new ArrayList<>();
            //get condition and corresponding velocities
            PlateCondition condition = plateConditions.get(i);
            replicateVelocities.addAll(Arrays.asList(areaAnalysisResults.get(i).getSlopes()));

            //put in map
            velocitiesMap.put(condition, replicateVelocities);
        }
        this.doseResponseAnalysisResults = new DoseResponseAnalysisResults();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public LinkedHashMap<String, LinkedHashMap<Double, String>> getConcentrationsMap() {
        return concentrationsMap;
    }

    public void setConcentrationsMap(LinkedHashMap<String, LinkedHashMap<Double, String>> concentrationsMap) {
        this.concentrationsMap = concentrationsMap;
    }

    public LinkedHashMap<PlateCondition, List<Double>> getVelocitiesMap() {
        return velocitiesMap;
    }

    public void setVelocitiesMap(LinkedHashMap<PlateCondition, List<Double>> velocitiesMap) {
        this.velocitiesMap = velocitiesMap;
    }

    public String getTreatmentToAnalyse() {
        return treatmentToAnalyse;
    }

    public void setTreatmentToAnalyse(String treatmentToAnalyse) {
        this.treatmentToAnalyse = treatmentToAnalyse;
    }

    public DoseResponseAnalysisResults getDoseResponseAnalysisResults() {
        return doseResponseAnalysisResults;
    }

}
