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
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
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

import org.springframework.context.ApplicationContext;

/**
 * This is a playground to generate files for downstream analysis.
 *
 * @author Paola Masuzzo
 */
class TrajGenerator {

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
        File folder = new File("C:\\Users\\Paola\\Desktop\\benchmark\\g\\tm_xml\\cellmissy_output");

        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 2) {
                List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
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
//                    singleCellConditionOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
//                    singleCellConditionOperator.operateOnInterpolatedTracks(singleCellConditionDataHolder);

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    biologicalConditions.add(trackDataHolders);
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_raw_traj.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "t" + " " + "x" + " " + "y");
                    bufferedWriter.newLine();
                    for (PlateCondition condition : experiment.getPlateConditionList()) {
                        for (Well well : condition.getWellList()) {
                            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                                for (Track track : wellHasImagingType.getTrackList()) {
                                    for (TrackPoint point : track.getTrackPointList()) {
                                        bufferedWriter.append("" + wellHasImagingType.getWell().getPlateCondition().getPlateConditionid());
                                        bufferedWriter.append(" ");
                                        bufferedWriter.append("" + track.getTrackid());
                                        bufferedWriter.append(" ");
                                        bufferedWriter.append("" + point.getTimeIndex());
                                        bufferedWriter.append(" ");
                                        bufferedWriter.append("" + point.getCellRow());
                                        bufferedWriter.append(" ");
                                        bufferedWriter.append("" + point.getCellCol());
                                        bufferedWriter.newLine();
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_shifted_traj.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "x" + " " + "y");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            Double[][] shiftedCoordinatesMatrix = stepCentricDataHolder.getShiftedCoordinatesMatrix();
                            for (int i = 0; i < shiftedCoordinatesMatrix.length; i++) {
                                bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + stepCentricDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + shiftedCoordinatesMatrix[i][0]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + +shiftedCoordinatesMatrix[i][1]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

//                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "interp_traj.txt")))) {
//                    // header of the file
//                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "int_t" + " " + "int_x" + " " + "int_y");
//                    bufferedWriter.newLine();
//                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
//                        for (TrackDataHolder trackDataHolder : conditionTracks) {
//                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
//                            Map<InterpolationMethod, InterpolatedTrack> interpolationMap = stepCentricDataHolder.getInterpolationMap();
//                            InterpolatedTrack interpTrack = interpolationMap.get(InterpolationMethod.LOESS);
//                            if (interpTrack != null) {
//                                double[] interpolantTime = interpTrack.getInterpolantTime();
//                                for (int j = 0; j < interpolantTime.length; j++) {
//                                    double[][] coordinatesMatrix = interpTrack.getCoordinatesMatrix();
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + interpolantTime[j]);
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + coordinatesMatrix[j][0]);
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + coordinatesMatrix[j][1]);
//                                    bufferedWriter.newLine();
//                                }
//                            }
//                        }
//                    }
//                    System.out.println("txt file succ. created!");
//                } catch (IOException ex) {
//                }
//                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "interp_shift_traj.txt")))) {
//                    // header of the file
//                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "int_t" + " " + "int_x" + " " + "int_y");
//                    bufferedWriter.newLine();
//                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
//                        for (TrackDataHolder trackDataHolder : conditionTracks) {
//                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
//                            Map<InterpolationMethod, InterpolatedTrack> interpolationMap = stepCentricDataHolder.getInterpolationMap();
//                            InterpolatedTrack interpTrack = interpolationMap.get(InterpolationMethod.LOESS);
//                            if (interpTrack != null) {
//                                double[] interpolantTime = interpTrack.getInterpolantTime();
//                                for (int j = 0; j < interpolantTime.length; j++) {
//                                    double[][] shiftedCoordinatesMatrix = interpTrack.getShiftedCoordinatesMatrix();
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + interpolantTime[j]);
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + shiftedCoordinatesMatrix[j][0]);
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + shiftedCoordinatesMatrix[j][1]);
//                                    bufferedWriter.newLine();
//                                }
//                            }
//                        }
//                    }
//                    System.out.println("txt file succ. created!");
//                } catch (IOException ex) {
//                }
            }
        }
    }
}
