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
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
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
class TrajGenerator1 {

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
                    singleCellConditionOperator.operateOnStepsAndCells(singleCellConditionDataHolder);
//                    singleCellConditionOperator.operateOnInterpolatedTracks(singleCellConditionDataHolder);

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    biologicalConditions.add(trackDataHolders);
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_turn_angle.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "ta" + " ");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            Double[] turningAngles = stepCentricDataHolder.getTurningAngles();
                            for (int i = 0; i < turningAngles.length; i++) {
                                bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + stepCentricDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + turningAngles[i]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_str_index.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "si" + " ");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
                            bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getEndPointDirectionalityRatio());
                            bufferedWriter.newLine();
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_dynamic_dr.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "dr" + " " + "t");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            Double[] directionalityRatios = stepCentricDataHolder.getDirectionalityRatios();
                            double[] timeIndexes = stepCentricDataHolder.getTimeIndexes();
                            for (int i = 0; i < directionalityRatios.length; i++) {
                                bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + directionalityRatios[i]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + timeIndexes[i]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_dir_autoc.txt")))) {
                    // header of the file
                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "dir_autoc" + " " + "t");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            Double[] dirAutocorrelations = stepCentricDataHolder.getDirectionAutocorrelations().get(1);
                            double[] timeIndexes = stepCentricDataHolder.getTimeIndexes();
                            for (int i = 0; i < dirAutocorrelations.length; i++) {
                                bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + dirAutocorrelations[i]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + timeIndexes[i]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }

//                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "interp_turn_angle.txt")))) {
//                    // header of the file
//                    bufferedWriter.append("sample_id" + " " + "traj_id" + " " + "int_ta");
//                    bufferedWriter.newLine();
//                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
//                        for (TrackDataHolder trackDataHolder : conditionTracks) {
//                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
//                            Map<InterpolationMethod, InterpolatedTrack> interpolationMap = stepCentricDataHolder.getInterpolationMap();
//                            InterpolatedTrack interpTrack = interpolationMap.get(InterpolationMethod.LOESS);
//                            if (interpTrack != null) {
//                                double[] turningAngles = interpTrack.getTurningAngles();
//                                for (int j = 0; j < turningAngles.length; j++) {
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + trackDataHolder.getTrack().getTrackid());
//                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + turningAngles[j]);
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
