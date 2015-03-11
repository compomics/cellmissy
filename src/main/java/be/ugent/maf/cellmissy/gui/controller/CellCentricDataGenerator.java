/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellOperator;
import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
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
        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");
        SingleCellOperator singleCellOperator = (SingleCellOperator) context.getBean("singleCellOperator");
        // get all the experiments from DB
        Project project = projectService.findById(3L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folder
        File folder = new File("E:\\P013");
        // subfolder for project
        File subfolder = new File(folder, project + "_" + project.getProjectDescription());
        subfolder.mkdir();
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
                    System.out.println("****************cell-centric computations started for condition: " + plateCondition);
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
                    System.out.println("****************cell-centric computations ended for condition: " + plateCondition);
                    biologicalConditions.add(trackDataHolders);
                    System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("$$$$$$ total tracks so far: " + totTracks);
//            }

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(subfolder, fileName)))) {
                    System.out.println("csv file succ. created!");
                    // header of the file
                    bufferedWriter.append("expid" + " " + "condid" + " " + "sampleid" + " " + "trackid" + " " + "steps" + " " + "xmin" + " " + "xmax" + " " + "ymin" + " " + "ymax" + " " + "xnd" + " " + "ynd" + " " + "cumdist" + " " + "eucldist" + " " + "endpointdir" + " " + "meddirect" + " " + "meddispl" + " " + "medspeed" + " " + "medturnangle" + " " + "maxdis" + " " + "displratio" + " " + "outrratio" + " " + "perim" + " " + "area" + " " + "acirc" + " " + "direct" + " " + "vertices");
                    // new line
                    bufferedWriter.newLine();
                    for (List<TrackDataHolder> conditionTracks : biologicalConditions) {
                        int tracksNumber = conditionTracks.size();
                        for (TrackDataHolder trackDataHolder : conditionTracks) {
                            CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
                            Track track = trackDataHolder.getTrack();
                            bufferedWriter.append("" + experiment.getExperimentid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getWellHasImagingType().getWell().getPlateCondition().getPlateConditionid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getWellHasImagingType().getWellHasImagingTypeid());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + track.getTrackid());
                            bufferedWriter.append(" ");

                            bufferedWriter.append("" + trackDataHolder.getTrack().getTrackPointList().size());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getxMin());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getxMax());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getyMin());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getyMax());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + (cellCentricDataHolder.getxNetDisplacement()));
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + (cellCentricDataHolder.getyNetDisplacement()));
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getCumulativeDistance());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getEuclideanDistance());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getEndPointDirectionalityRatio());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getMedianDirectionalityRatio());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getMedianDisplacement());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getMedianSpeed());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getMedianTurningAngle());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getMostDistantPointsPair().getMaxSpan());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getDisplacementRatio());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getOutreachRatio());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getPerimeter());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getArea());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getAcircularity());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getDirectionality());
                            bufferedWriter.append(" ");
                            bufferedWriter.append("" + cellCentricDataHolder.getConvexHull().getHullSize());
                            bufferedWriter.newLine();
                        }
                    }
                    bufferedWriter.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(CellCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
}
