/*
 * To change this template, choose Tools | Templates
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
import be.ugent.maf.cellmissy.entity.result.singlecell.BoundingBox;
import be.ugent.maf.cellmissy.entity.result.singlecell.CellCentricDataHolder;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
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
        SingleCellConditionPreProcessor singleCellConditionPreProcessor = (SingleCellConditionPreProcessor) context.getBean("singleCellConditionPreProcessor");
        SingleCellConditionOperator singleCellConditionOperator = (SingleCellConditionOperator) context.getBean("singleCellConditionOperator");
        // get all the experiments from DB
        Project project = projectService.findById(14L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folders
        File featFolder = new File("C:\\Users\\CompOmics Gwen\\Documents\\cellmissy_output2\\features");
        File trackFolder = new File("C:\\Users\\CompOmics Gwen\\Documents\\cellmissy_output2\\tracks");
        int totTracks = 0;
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentNumber() == 12) {

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
                    String fileName = plateCondition + ".txt";
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

                    List<TrackDataHolder> trackDataHolders = singleCellConditionDataHolder.getTrackDataHolders();
                    System.out.println("****************cell-centric computations ended for condition: " + plateCondition);

                    System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(featFolder, fileName)))) {
                        // header of the file
                        String header = "track_id\tx_nd\ty_nd\tch_perim\tch_area\tch_acirc\tch_direct\tcum_dist\teucl_dist\tdispl_ratio\toutreach_ratio\t"
                                + "max_span\tep_dr\tmedian_displ\tmedian_ta\tmedian_dir_ratio";
                        bufferedWriter.append(header);
                        // new line
                        bufferedWriter.newLine();
                        for (TrackDataHolder trackDataHolder : trackDataHolders) {
                            CellCentricDataHolder cellCentricDataHolder = trackDataHolder.getCellCentricDataHolder();
                            BoundingBox boundingBox = cellCentricDataHolder.getBoundingBox();
                            ConvexHull convexHull = cellCentricDataHolder.getConvexHull();
                            bufferedWriter.append(trackDataHolder.getTrack().getTrackid() + "\t" + boundingBox.getxNetDisplacement() + "\t" + boundingBox.getyNetDisplacement() + "\t"
                                    + convexHull.getPerimeter() + "\t" + convexHull.getArea() + "\t"
                                    + convexHull.getAcircularity() + "\t" + convexHull.getDirectionality() + "\t"
                                    + cellCentricDataHolder.getCumulativeDistance() + "\t" + cellCentricDataHolder.getEuclideanDistance() + "\t"
                                    + cellCentricDataHolder.getDisplacementRatio() + "\t" + cellCentricDataHolder.getOutreachRatio() + "\t"
                                    + convexHull.getMostDistantPointsPair().getMaxSpan() + "\t" + cellCentricDataHolder.getEndPointDirectionalityRatio()
                                    + "\t" + cellCentricDataHolder.getMedianDisplacement() + "\t" + cellCentricDataHolder.getMedianTurningAngle()
                                    + "\t" + cellCentricDataHolder.getMedianDirectionalityRatio());
                            bufferedWriter.newLine();
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(CellCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(trackFolder, fileName)))) {
                        // header of the file
                        String header = "track_id\tt\tx\ty";
                        bufferedWriter.append(header);
                        // new line
                        bufferedWriter.newLine();
                        for (TrackDataHolder trackDataHolder : trackDataHolders) {
                            for (TrackPoint tp : trackDataHolder.getTrack().getTrackPointList()) {
                                bufferedWriter.append(trackDataHolder.getTrack().getTrackid() + "\t" + tp.getTimeIndex() + "\t" + tp.getCellRow() + "\t"
                                        + tp.getCellCol());
                                bufferedWriter.newLine();
                            }

                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CellCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("$$$$$$ total tracks so far: " + totTracks);

            }
        }
    }
}
