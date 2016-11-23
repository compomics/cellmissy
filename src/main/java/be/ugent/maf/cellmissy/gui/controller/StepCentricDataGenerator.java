/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.InterpolationMethod;
import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellConditionPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellConditionOperator;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.InterpolatedTrack;
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
import java.util.Map;
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
        SingleCellConditionPreProcessor singleCellConditionPreProcessor = (SingleCellConditionPreProcessor) context.getBean("singleCellConditionPreProcessor");
        SingleCellConditionOperator singleCellConditionOperator = (SingleCellConditionOperator) context.getBean("singleCellConditionOperator");
        // get all the experiments from DB
        Project project = projectService.findById(1L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folder
        File folder = new File("C:\\Users\\Paola\\Documents\\UGent\\PhD\\Collaborations\\Marc");
        // subfolder for project
//        File subfolder = new File(folder, project + "_" + project.getProjectDescription());
//        subfolder.mkdir();
//        List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
        int totTracks = 0;
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 4) {

                List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
                String expPurpose = experiment.getPurpose();
                expPurpose = expPurpose.replace("/", "_");
                expPurpose = expPurpose.replaceAll("\\s+", "");
                expPurpose = expPurpose.replaceAll(",", "_");
                expPurpose = expPurpose.replace("-->", "_");

//                String expPurpose = "Single_cells_2D";
                System.out.println("exp: " + expPurpose);
                String fileName = experiment + "_" + expPurpose + "_interp.txt";
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
                    // create a new object to hold pre-processing results
                    SingleCellConditionDataHolder singleCellConditionDataHolder = new SingleCellConditionDataHolder(plateCondition);
                    System.out.println("****************computations started for condition: " + plateCondition);
                    // do the computations

                    singleCellConditionPreProcessor.generateDataHolders(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.generateDataStructure(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.preProcessStepsAndCells(singleCellConditionDataHolder, conversionFactor,
                            experiment.getExperimentInterval());
                    singleCellConditionPreProcessor.generateRawTrackCoordinatesMatrix(singleCellConditionDataHolder);
                    singleCellConditionPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellConditionDataHolder);

                    singleCellConditionOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateInstantaneousDisplacementsVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateDirectionalityRatiosVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateMedianDirectionalityRatiosVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateTrackDisplacementsVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateCumulativeDistancesVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateEuclideanDistancesVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateTrackSpeedsVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateEndPointDirectionalityRatiosVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateConvexHullsVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateDisplacementRatiosVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateOutreachRatiosVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateTurningAnglesVector(singleCellConditionDataHolder);
//                    singleCellConditionOperator.generateMedianTurningAnglesVector(singleCellConditionDataHolder);
                    singleCellConditionOperator.operateOnInterpolatedTracks(singleCellConditionDataHolder);

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    System.out.println("****************computations ended for condition: " + plateCondition);
                    biologicalConditions.add(trackDataHolders);
                    System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("$$$$$$ total tracks so far: " + totTracks);

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {

                    // header of the file
                    bufferedWriter.append("sampleid" + " " + "trackid" + " "
                            + "int_t" + " " + "int_x" + " " + "int_y");
                    // new line
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {

                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            Map<InterpolationMethod, InterpolatedTrack> interpolationMap = stepCentricDataHolder.getInterpolationMap();
                            InterpolatedTrack interpTrack = interpolationMap.get(InterpolationMethod.LOESS);
                            Track track = trackDataHolder.getTrack();
                            if (interpTrack != null) {

                                double[] interpolantTime = interpTrack.getInterpolantTime();
                                for (int j = 0; j < interpolantTime.length; j++) {

                                    double[][] coordinatesMatrix = interpTrack.getCoordinatesMatrix();
//                                double[][] coordTransp = AnalysisUtils.transpose2DArray(coordinatesMatrix);
                                    bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + track.getTrackid());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + interpolantTime[j]);
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + coordinatesMatrix[j][0]);
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + coordinatesMatrix[j][1]);

//                                bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
//                                bufferedWriter.append(" ");
//                                bufferedWriter.append("" + track.getTrackid());
//                                bufferedWriter.append(" ");
//                                bufferedWriter.append("" + timeIndexes[j]);
//                                bufferedWriter.append(" ");
//                           
//                                
//                                bufferedWriter.append("" + stepCentricDataHolder.getInstantaneousDisplacements()[j]);
//                                bufferedWriter.append(" ");
//
//                                bufferedWriter.append("" + stepCentricDataHolder.getInstantaneousDisplacements()[j] / experiment.getDuration());
//                                bufferedWriter.append(" ");
//
//                                bufferedWriter.append("" + stepCentricDataHolder.getTurningAngles()[j]);
//                                bufferedWriter.append(" ");
//                                if (j < timeIndexes.length - 3) {
//                                    bufferedWriter.append("" + stepCentricDataHolder.getDirectionAutocorrelations().get(1)[j]);
//                                } else {
//                                    bufferedWriter.append("NaN");
//                                }
//                                bufferedWriter.append(" ");
//                                bufferedWriter.append("" + stepCentricDataHolder.getMedianDirectionAutocorrelations()[j]);
//                                bufferedWriter.append(" ");
//                                bufferedWriter.append("" + stepCentricDataHolder.getDirectionalityRatios()[j]);
//                                bufferedWriter.append(" ");
                                    bufferedWriter.newLine();
                                }
                            }

//                            bufferedWriter.append("" + experiment.getExperimentid());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + track.getWellHasImagingType().getWellHasImagingTypeid());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + track.getTrackid());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + track.getTrackPointList().size());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + timeIndexes[timeIndexes.length - 1]);
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("" + experiment.getExperimentInterval());
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.append(" ");
//                            bufferedWriter.append("NaN");
//                            bufferedWriter.newLine();
                        }
                    }
                    System.out.println("file succ. created!");
                } catch (IOException ex) {
                    Logger.getLogger(StepCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
