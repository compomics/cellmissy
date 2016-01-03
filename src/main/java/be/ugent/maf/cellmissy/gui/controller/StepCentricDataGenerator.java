/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
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
class StepCentricDataGenerator {

    public static void main(String[] args) {

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        ProjectService projectService = (ProjectService) context.getBean("projectService");
        WellService wellService = (WellService) context.getBean("wellService");
        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");
        SingleCellOperator singleCellOperator = (SingleCellOperator) context.getBean("singleCellOperator");
        // get all the experiments from DB
        Project project = projectService.findById(3L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folder
        File folder = new File("E:\\P013");
        // subfolder for project
//        File subfolder = new File(folder, project + "_" + project.getProjectDescription());
//        subfolder.mkdir();
//        List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
        int totTracks = 0;
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 11) {

                List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
                String expPurpose = experiment.getPurpose();
                expPurpose = expPurpose.replace("/", "_");
                expPurpose = expPurpose.replaceAll("\\s+", "");
                expPurpose = expPurpose.replaceAll(",", "_");
                expPurpose = expPurpose.replace("-->", "_");

//                String expPurpose = "Single_cells_2D";
                System.out.println("exp: " + expPurpose);
                String fileName = project + "_" + project.getProjectDescription() + "_" + experiment + "_" + expPurpose + ".csv";
                System.out.println("STARTING WITH EXPERIMENT: " + experiment + ": " + expPurpose);

//            if (experiment.getExperimentid() != 59) {
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
                    // create a new object to hold pre-processing results
                    SingleCellConditionDataHolder singleCellConditionDataHolder = new SingleCellConditionDataHolder();
                    System.out.println("****************computations started for condition: " + plateCondition);
                    // do the computations
                    singleCellPreProcessor.generateTrackDataHolders(singleCellConditionDataHolder, plateCondition);
                    singleCellPreProcessor.generateDataStructure(singleCellConditionDataHolder);
                    singleCellPreProcessor.preProcessStepsAndCells(singleCellConditionDataHolder, conversionFactor,
                            experiment.getExperimentInterval());
                    singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellConditionDataHolder);
                    singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellConditionDataHolder);
                    singleCellOperator.generateInstantaneousDisplacementsVector(singleCellConditionDataHolder);
                    singleCellOperator.generateDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellOperator.generateMedianDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellOperator.generateTrackDisplacementsVector(singleCellConditionDataHolder);
                    singleCellOperator.generateCumulativeDistancesVector(singleCellConditionDataHolder);
                    singleCellOperator.generateEuclideanDistancesVector(singleCellConditionDataHolder);
                    singleCellOperator.generateTrackSpeedsVector(singleCellConditionDataHolder);
                    singleCellOperator.generateEndPointDirectionalityRatiosVector(singleCellConditionDataHolder);
                    singleCellOperator.generateConvexHullsVector(singleCellConditionDataHolder);
                    singleCellOperator.generateDisplacementRatiosVector(singleCellConditionDataHolder);
                    singleCellOperator.generateOutreachRatiosVector(singleCellConditionDataHolder);
                    singleCellOperator.generateTurningAnglesVector(singleCellConditionDataHolder);
                    singleCellOperator.generateMedianTurningAnglesVector(singleCellConditionDataHolder);
                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    System.out.println("****************computations ended for condition: " + plateCondition);
                    biologicalConditions.add(trackDataHolders);
                    System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("$$$$$$ total tracks so far: " + totTracks);
//            }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {

                    // header of the file
                    bufferedWriter.append("expid" + " " + "condid" + " " + "sampleid" + " " + "trackid" + " " + "steps" + " " + "timeIndex" + " " + "timeLapse" + " " + "instDispl" + " " + "instSpeed" + " " + "turningAngle" + " " + "dirAutocorr" + " " + "medDirAutocorr" + " " + "dirRatio");
                    // new line
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        int tracksNumber = conditionTracks.size();
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            Track track = trackDataHolder.getTrack();
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            double[] timeIndexes = stepCentricDataHolder.getTimeIndexes(); //
                            for (int j = 0; j < timeIndexes.length - 1; j++) {
                                bufferedWriter.append("" + experiment.getExperimentid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + track.getWellHasImagingType().getWellHasImagingTypeid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + track.getTrackid());
                                bufferedWriter.append(" ");

                                bufferedWriter.append("" + track.getTrackPointList().size());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + timeIndexes[j]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + experiment.getExperimentInterval());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + stepCentricDataHolder.getInstantaneousDisplacements()[j]);
                                bufferedWriter.append(" ");

                                bufferedWriter.append("" + stepCentricDataHolder.getInstantaneousDisplacements()[j] / experiment.getDuration());
                                bufferedWriter.append(" ");

                                bufferedWriter.append("" + stepCentricDataHolder.getTurningAngles()[j]);
                                bufferedWriter.append(" ");
                                if (j < timeIndexes.length - 3) {
                                    bufferedWriter.append("" + stepCentricDataHolder.getDirectionAutocorrelations().get(1)[j]);
                                } else {
                                    bufferedWriter.append("NaN");
                                }
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + stepCentricDataHolder.getMedianDirectionAutocorrelations()[j]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + stepCentricDataHolder.getDirectionalityRatios()[j]);
                                bufferedWriter.append(" ");
                                bufferedWriter.newLine();
                            }

                            bufferedWriter.append("" + experiment.getExperimentid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getWellHasImagingType().getWellHasImagingTypeid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getTrackid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getTrackPointList().size());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + timeIndexes[timeIndexes.length - 1]);
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + experiment.getExperimentInterval());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.append(" ");
                            bufferedWriter.append("NaN");
                            bufferedWriter.newLine();
                        }
                    }
                    System.out.println("csv file succ. created!");
                } catch (IOException ex) {
                    Logger.getLogger(StepCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
