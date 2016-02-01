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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class contains the list of conditions selected by the user for dose-
 * response analysis, together with the results of this analysis.
 *
 * @author Gwendolien
 */
public class DoseResponseAnalysisGroup {

    //An experiment might have multiple treatments. Per condition the concentrations for a treatment may vary.
    //The concentration value is mapped to his unit of measurement.
    private LinkedHashMap<Treatment, LinkedHashMap<Double, String>> concentrationsMap;

    //A condition can have multiple replicates, each with their own velocity.
    private LinkedHashMap<PlateCondition, List<Double>> velocitiesMap;

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

        //PlateCondition has List<Treatment>, Treatment has double concentration AND concentrationunit
        this.concentrationsMap = new LinkedHashMap<>();
        //initialize nested map
        LinkedHashMap<Double, String> nestedMap = new LinkedHashMap<>();

        for (PlateCondition plateCondition : plateConditions) {
            //1 platecondition might have multiple treatments
            List<Treatment> treatmentList = plateCondition.getTreatmentList();

            for (Treatment treatment : treatmentList) {
                double concentration = treatment.getConcentration();
                //concentration unit needs to be saved, needed for log-transformation
                String concentrationUnit = treatment.getConcentrationUnit();

                //put in map
                nestedMap.put(concentration, concentrationUnit);
                concentrationsMap.put(treatment, nestedMap);
            }
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
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public LinkedHashMap<Treatment, LinkedHashMap<Double, String>> getConcentrationsMap() {
        return concentrationsMap;
    }

    public void setConcentrationsMap(LinkedHashMap<Treatment, LinkedHashMap<Double, String>> concentrationsMap) {
        this.concentrationsMap = concentrationsMap;
    }

    public LinkedHashMap<PlateCondition, List<Double>> getVelocitiesMap() {
        return velocitiesMap;
    }

    public void setVelocitiesMap(LinkedHashMap<PlateCondition, List<Double>> velocitiesMap) {
        this.velocitiesMap = velocitiesMap;
    }

}
