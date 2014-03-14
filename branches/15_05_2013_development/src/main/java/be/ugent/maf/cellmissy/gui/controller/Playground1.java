 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
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
 * Copy of Playground class
 *
 * @author Paola Masuzzo
 */
public class Playground1 {

    public static void main(String[] args) {

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        ProjectService projectService = (ProjectService) context.getBean("projectService");
        WellService wellService = (WellService) context.getBean("wellService");
        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");
        // get all the experiments from DB
        Project project = projectService.findById(5L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // folder and name for the output file
        File folder = new File("C:\\Users\\paola\\Desktop\\datasets");
        String fileName = "dataset_unlabeled.txt";
        List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
        int totTracks = 0;
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentid() != 59) {
                double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
                double magnificationValue = experiment.getMagnification().getMagnificationValue();
                double conversionFactor = instrumentConversionFactor * magnificationValue / 10;
                // fetch the migration data
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
                    SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
                    // do the computations
                    singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition);
                    singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateTimeIndexes(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateTrackDurations(experiment.getExperimentInterval(), singleCellPreProcessingResults);
                    singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults, conversionFactor);
                    singleCellPreProcessor.computeCoordinatesRanges(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateDirectionalitiesVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateConvexHullsVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateDisplacementRatiosVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateOutreachRatiosVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
                    singleCellPreProcessor.generateMedianTurningAnglesVector(singleCellPreProcessingResults);
                    List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
                    biologicalConditions.add(trackDataHolders);
                    System.out.println("tracks for current conditions: " + trackDataHolders.size());
                    System.out.println("*-*-*" + plateCondition + " processed");
                    totTracks += trackDataHolders.size();
                }
                System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
                System.out.println("total tracks (cells): " + totTracks);
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {
                // header of the file
                bufferedWriter.append("id" + "\t" + "dur" + "\t" + "xmin" + "\t" + "xmax" + "\t" + "ymin" + "\t" + "ymax" + "\t"
                        + "xnd" + "\t" + "ynd" + "\t" + "cd" + "\t" + "ed" + "\t" + "dir" + "\t" + "md" + "\t" + "ms" + "\t" + "mta" + "\t" + "maxdis" + "\t"
                        + "dr" + "\t" + "or" + "\t" + "perim" + "\t" + "area" + "\t" + "acirc" + "\t" + "dir2" + "\t" + "label");
                // new line
                bufferedWriter.newLine();
                for (int i = 0; i < biologicalConditions.size(); i++) {
                    List<TrackDataHolder> conditionTracks = biologicalConditions.get(i);
                    int tracksNumber = conditionTracks.size();
                    for (int row = 0; row < tracksNumber; row++) {
                        TrackDataHolder trackDataHolder = conditionTracks.get(row);
                        Track track = trackDataHolder.getTrack();
                        bufferedWriter.append("" + track.getTrackid());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getDuration());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getxMin());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getxMax());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getyMin());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getyMax());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + (trackDataHolder.getxMax() - trackDataHolder.getxMin()));
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + (trackDataHolder.getyMax() - trackDataHolder.getyMin()));
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getCumulativeDistance());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getEuclideanDistance());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getDirectionality());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getMedianDisplacement());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getMedianSpeed());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getMedianTurningAngle());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getMostDistantPointsPair().getMaxSpan());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getDisplacementRatio());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getOutreachRatio());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getPerimeter());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getArea());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getAcircularity());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getDirectionality());
                        bufferedWriter.append("\t");
                        bufferedWriter.append("-1");
                        bufferedWriter.newLine();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Playground1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
