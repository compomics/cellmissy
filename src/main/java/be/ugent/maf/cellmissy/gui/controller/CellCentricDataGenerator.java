/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellConditionPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellConditionOperator;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.EnclosingBall;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.StepCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

/**
 * This is a playground to generate files for downstream analysis.
 *
 * @author Paola Masuzzo
 */
class CellCentricDataGenerator {

    public static void main(String[] args) {

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        ProjectService projectService = (ProjectService) context.getBean("projectService");
        WellService wellService = (WellService) context.getBean("wellService");
        SingleCellConditionPreProcessor singleCellConditionPreProcessor = (SingleCellConditionPreProcessor) context.getBean("singleCellConditionPreProcessor");
        SingleCellConditionOperator singleCellConditionOperator = (SingleCellConditionOperator) context.getBean("singleCellConditionOperator");
        // get all the experiments from DB
        Project project = projectService.findById(4L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folder
        File folder = new File("C:\\Users\\Paola\\Desktop\\benchmark\\cellmissy");
        // subfolder for project
//        File subfolder = new File(folder, project + "_" + project.getProjectDescription());
//        subfolder.mkdir();
//        List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
        int totTracks = 0;
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 1) {

                List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
                String expPurpose = experiment.getPurpose();
                expPurpose = expPurpose.replace("/", "_");
                expPurpose = expPurpose.replaceAll("\\s+", "");
                expPurpose = expPurpose.replaceAll(",", "_");
                expPurpose = expPurpose.replace("-->", "_");

                System.out.println("exp: " + expPurpose);

                System.out.println("STARTING WITH EXPERIMENT: " + experiment + ": " + expPurpose);

                double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
                double magnificationValue = experiment.getMagnification().getMagnificationValue();
                double conversionFactor = instrumentConversionFactor * magnificationValue / 10;
                // fetch the migration data
                System.out.println("fetching data for project: " + project + ", experiment: " + experiment + " ...");
                for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                    List<Well> wells = new ArrayList<>();
                    for (Well well : plateCondition.getWellList()) {
                        Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                        wells.add(fetchedWell);
                    }
                    plateCondition.setWellList(wells);
                }
                // now do the computations
                for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                    String fileName = plateCondition + ".csv";
                    // create a new object to hold pre-processing results
                    SingleCellConditionDataHolder singleCellConditionDataHolder = new SingleCellConditionDataHolder(plateCondition);
                    System.out.println("****************cell-centric computations started for condition: " + plateCondition);
                    // do the computations
                    singleCellConditionPreProcessor.generateDataHolders(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.generateDataStructure(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.preProcessStepsAndCells(singleCellConditionDataHolder, conversionFactor,
                            experiment.getExperimentInterval());
                    singleCellConditionPreProcessor.generateRawTrackCoordinatesMatrix(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellConditionDataHolder);

                    singleCellConditionOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateInstantaneousDisplacementsVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateMedianDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateTrackDisplacementsVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateCumulativeDistancesVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateEuclideanDistancesVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateTrackSpeedsVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateEndPointDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateConvexHullsVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateDisplacementRatiosVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateOutreachRatiosVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateTurningAnglesVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.generateMedianTurningAnglesVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.operateOnInterpolatedTracks(singleCellConditionDataHolder);

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    System.out.println("****************cell-centric computations ended for condition: " + plateCondition);
                    biologicalConditions.add(trackDataHolders);
                    System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("$$$$$$ total tracks so far: " + totTracks);
//            }

                double r_min = PropertiesConfigurationHolder.getInstance().getDouble("r_min");
                double r_max = PropertiesConfigurationHolder.getInstance().getDouble("r_max");
                double r_step = PropertiesConfigurationHolder.getInstance().getDouble("r_step");
                int N = (int) ((r_max - r_min) / r_step) + 1;
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_ec.txt")))) {
                    // header of the file
                    bufferedWriter.append("traj_id" + " " + "log(1/r)" + " " + "log(N)");
                    // new line
                    bufferedWriter.newLine();

                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {

                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            List<List<EnclosingBall>> xyEnclosingBalls = stepCentricDataHolder.getxYEnclosingBalls();
                            for (int j = 0; j < xyEnclosingBalls.size(); j++) {

                                bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                double radius = Math.log10(1 / (r_min + (j * r_step)));
                                bufferedWriter.append("" + radius);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + Math.log10(xyEnclosingBalls.get(j).size()));
                                if (j < xyEnclosingBalls.size() - 1) {
                                    bufferedWriter.append(" ");
                                }
                                bufferedWriter.newLine();
                            }

                        }

                    }
                } catch (IOException ex) {
                    Logger.getLogger(CellCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }

//                }
            }
        }
    }
}
