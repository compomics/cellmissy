/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.preprocessing.SingleCellConditionPreProcessor;
import be.ugent.maf.cellmissy.analysis.singlecell.processing.SingleCellConditionOperator;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
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
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola
 */
class MSDGenerator {

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

        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 1) {
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

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    biologicalConditions.add(trackDataHolders);
                }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, "bench_msd.txt")))) {
                    // header of the file
                    bufferedWriter.append("traj_id" + " " + "t_lag" + " " + "msd");
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            StepCentricDataHolder stepCentricDataHolder = trackDataHolder.getStepCentricDataHolder();
                            double[][] msd = stepCentricDataHolder.getMSD();
                            for (int i = 0; i < msd.length; i++) {
                                bufferedWriter.append("" + stepCentricDataHolder.getTrack().getTrackid());
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + msd[i][0]);
                                bufferedWriter.append(" ");
                                bufferedWriter.append("" + msd[i][1]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    System.out.println("txt file succ. created!");
                } catch (IOException ex) {
                }
            }
        }
    }
}
