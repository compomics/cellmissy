// /*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package be.ugent.maf.cellmissy.gui.controller;
//
//import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
//import be.ugent.maf.cellmissy.entity.Experiment;
//import be.ugent.maf.cellmissy.entity.PlateCondition;
//import be.ugent.maf.cellmissy.entity.Project;
//import be.ugent.maf.cellmissy.entity.Track;
//import be.ugent.maf.cellmissy.entity.Well;
//import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
//import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
//import be.ugent.maf.cellmissy.service.ExperimentService;
//import be.ugent.maf.cellmissy.service.ProjectService;
//import be.ugent.maf.cellmissy.service.WellService;
//import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.springframework.context.ApplicationContext;
//
///**
// * Copy of Playground class
// *
// * @author Paola Masuzzo
// */
//public class Playground1 {
//
//    public static void main(String[] args) {
//
//        // get the application context
//        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
//        // get the services we need
//        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
//        ProjectService projectService = (ProjectService) context.getBean("projectService");
//        WellService wellService = (WellService) context.getBean("wellService");
//        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");
//        // get all the experiments from DB
//        Project project = projectService.findById(7L);
//        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
//        // root folder
//        File folder = new File("Z:\\paola\\new_data_muppet");
//        // subfolder for project
//        File subfolder = new File(folder, project + "_" + project.getProjectDescription());
//        subfolder.mkdir();
////        List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
//        int totTracks = 0;
//        for (Experiment experiment : experiments) {
//
//            List<List<TrackDataHolder>> biologicalConditions = new ArrayList<>();
//            String expPurpose = experiment.getPurpose();
//            expPurpose = expPurpose.replace("/", "_");
//            expPurpose = expPurpose.replaceAll("\\s+", "");
//            expPurpose = expPurpose.replaceAll(",", "_");
//            expPurpose = expPurpose.replace("-->", "_");
//
////                String expPurpose = "Single_cells_2D";
//
//
//            System.out.println("exp: " + expPurpose);
//            String fileName = project + "_" + project.getProjectDescription() + "_" + experiment + "_" + expPurpose + ".csv";
//            System.out.println("STARTING WITH EXPERIMENT: " + experiment + ": " + expPurpose);
//
////            if (experiment.getExperimentid() != 59) {
//            double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
//            double magnificationValue = experiment.getMagnification().getMagnificationValue();
//            double conversionFactor = instrumentConversionFactor * magnificationValue / 10;
//            // fetch the migration data
//            System.out.println("fetching data for project: " + project + ", experiment: " + experiment + " ...");
//            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
//                List<Well> wells = new ArrayList<>();
//                for (Well well : plateCondition.getWellList()) {
//                    Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
//                    wells.add(fetchedWell);
//                }
//                plateCondition.setWellList(wells);
//            }
//            // now do the computations
//            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
//                // create a new object to hold pre-processing results
//                SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
//                System.out.println("****************computations started for condition: " + plateCondition);
//                // do the computations
//                singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition);
//                singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateTimeIndexes(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateTrackDurations(experiment.getExperimentInterval(), singleCellPreProcessingResults);
//                singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults, conversionFactor);
//                singleCellPreProcessor.computeCoordinatesRanges(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateDirectionalitiesVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateConvexHullsVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateDisplacementRatiosVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateOutreachRatiosVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
//                singleCellPreProcessor.generateMedianTurningAnglesVector(singleCellPreProcessingResults);
//                List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
//                System.out.println("****************computations ended for condition: " + plateCondition);
//                biologicalConditions.add(trackDataHolders);
//                System.out.println("$$$ tracks for current conditions: " + trackDataHolders.size());
//                System.out.println("*-*-*" + plateCondition + " processed");
//                totTracks += trackDataHolders.size();
//            }
//            System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
//            System.out.println("$$$$$$ total tracks so far: " + totTracks);
////            }
//
//            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(subfolder, fileName)))) {
//                System.out.println("csv file succ. created!");
//                // header of the file
//                bufferedWriter.append("id" + " " + "label" + " " + "dur" + " " + "xmin" + " " + "xmax" + " " + "ymin" + " " + "ymax" + " "
//                        + "xnd" + " " + "ynd" + " " + "cd" + " " + "ed" + " " + "dir" + " " + "md" + " " + "ms" + " " + "mta" + " " + "maxdis" + " "
//                        + "dr" + " " + "or" + " " + "perim" + " " + "area" + " " + "acirc" + " " + "dir2" + " " + "vertices");
//                // new line
//                bufferedWriter.newLine();
//                for (int i = 0; i < biologicalConditions.size(); i++) {
//                    List<TrackDataHolder> conditionTracks = biologicalConditions.get(i);
//                    int tracksNumber = conditionTracks.size();
//                    for (int row = 0; row < tracksNumber; row++) {
//                        TrackDataHolder trackDataHolder = conditionTracks.get(row);
//                        Track track = trackDataHolder.getTrack();
//                        bufferedWriter.append("" + track.getTrackid());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("-1");
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getDuration());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getxMin());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getxMax());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getyMin());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getyMax());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + (trackDataHolder.getxMax() - trackDataHolder.getxMin()));
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + (trackDataHolder.getyMax() - trackDataHolder.getyMin()));
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getCumulativeDistance());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getEuclideanDistance());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getDirectionality());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getMedianDisplacement());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getMedianSpeed());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getMedianTurningAngle());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getMostDistantPointsPair().getMaxSpan());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getDisplacementRatio());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getOutreachRatio());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getPerimeter());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getArea());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getAcircularity());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getDirectionality());
//                        bufferedWriter.append(" ");
//                        bufferedWriter.append("" + trackDataHolder.getConvexHull().getHullSize());
//                        bufferedWriter.newLine();
//                    }
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(Playground1.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//    }
//}
