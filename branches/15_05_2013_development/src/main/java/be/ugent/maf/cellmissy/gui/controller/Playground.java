 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.analysis.singlecell.SingleCellPreProcessor;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Playground class
 *
 * @author Paola Masuzzo
 */
public class Playground {

    public static void main(String[] args) {

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        WellService wellService = (WellService) context.getBean("wellService");
        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");
        // get all the experiments from DB
//        List<Experiment> experiments = experimentService.findAll();
        // find the experiments by project id
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(5L); // immune cells, project id 5
        for (Experiment experiment : experiments) {
            Magnification magnification = experiment.getMagnification();
            double instrumentConversionFactor = experiment.getInstrument().getConversionFactor();
            double magnificationValue = magnification.getMagnificationValue();
            double conversionFactor = instrumentConversionFactor * magnificationValue / 10;
            System.out.println("$$$ C.F. is: " + conversionFactor);
//        Experiment experiment = experimentService.findById(21L);
            Project project = experiment.getProject();
            // make the folders
            File file = new File("C:\\Users\\paola\\Desktop\\P013");
//            File trackFolder = new File(file, project + "_" + experiment + "_" + "trackFiles");
//            trackFolder.mkdir();
            File globalFolder = new File(file, project + "_" + experiment + "_" + "globalFiles");
            globalFolder.mkdir();

            // fetch the migration data
            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                List<Well> wells = new ArrayList<>();
                for (Well well : plateCondition.getWellList()) {
                    Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                    wells.add(fetchedWell);
                }
                plateCondition.setWellList(wells);
            }
            List<Algorithm> algorithms = experimentService.getAlgorithms(experiment);
            List<ImagingType> imagingTypes = experimentService.getImagingTypes(experiment);
            for (Algorithm algorithm : algorithms) {
                for (ImagingType imagingType : imagingTypes) {
                    //**************************************************************************
//                    File trackDataFolder = new File(trackFolder, project + "_" + experiment + "_" + "trackFiles_" + algorithm + "_" + imagingType);
//                    trackDataFolder.mkdir();
                    //**************************************************************************
                    File globalDataFolder = new File(globalFolder, project + "_" + experiment + "_" + "globalFiles" + algorithm + "_" + imagingType);
                    globalDataFolder.mkdir();

                    for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                        List<List<Integer>> numbersOfTrackPoints = new ArrayList<>();
                        List<List<Integer>> numberOfTracks = new ArrayList<>();
                        for (int i = 0; i < plateCondition.getWellList().size(); i++) {
                            Well currentWell = plateCondition.getWellList().get(i);
                            //fetch tracks collection for the wellhasimagingtype of interest
                            wellService.fetchTracks(currentWell, algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
                            wellService.fetchTrackPoints(currentWell, algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
                            numbersOfTrackPoints.add(AnalysisUtils.getNumbersOfTrackPoints(currentWell));
                            numberOfTracks.add(AnalysisUtils.getNumbersOfTracks(currentWell));
                        }
                        // create a new object to hold pre-processing results
                        SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
                        // do computations
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

                        // we create a file for each condition
                        String name1 = project + "_" + experiment + "_" + plateCondition + ".txt";
                        Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
                        Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
                        Double[][] shiftedTrackCoordinatesMatrix = singleCellPreProcessingResults.getShiftedTrackCoordinatesMatrix();
                        Double[] instantaneousDisplacementsVector = singleCellPreProcessingResults.getInstantaneousDisplacementsVector();
                        Double[] turningAnglesVector = singleCellPreProcessingResults.getTurningAnglesVector();
//                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(trackDataFolder, name1)))) {
//                            // HEADER
//                            bufferedWriter.append("well" + "\t" + "track" + "\t" + "time" + "\t" + "location" + "\t" + "x" + "\t" + "y" + "\t" + "shiftX" + "\t" + "shiftY" + "\t" + "instDispl" + "\t" + "turningAngle");
//                            bufferedWriter.newLine();
//                            int counter = 0;
//                            for (List<Integer> list : numbersOfTrackPoints) {
//                                for (Integer currentNumber : list) {
//                                    for (int row = counter; row < counter + currentNumber; row++) {
//                                        // this is just the data structure
//                                        Object[] currentRow = dataStructure[row];
//                                        for (int column = 0; column < currentRow.length; column++) {
//                                            bufferedWriter.append(currentRow[column].toString());
//                                            bufferedWriter.append("\t");
//                                        }
//                                        bufferedWriter.append("" + (list.indexOf(currentNumber) + 1));
//                                        bufferedWriter.append("\t");
//                                        // this is the rest of the calculation
//                                        bufferedWriter.append(rawTrackCoordinatesMatrix[row][0].toString());
//                                        bufferedWriter.append("\t");
//                                        bufferedWriter.append(rawTrackCoordinatesMatrix[row][1].toString());
//                                        bufferedWriter.append("\t");
//                                        bufferedWriter.append(shiftedTrackCoordinatesMatrix[row][0].toString());
//                                        bufferedWriter.append("\t");
//                                        bufferedWriter.append(shiftedTrackCoordinatesMatrix[row][1].toString());
//                                        bufferedWriter.append("\t");
//                                        if (instantaneousDisplacementsVector[row] != null) {
//                                            bufferedWriter.append(instantaneousDisplacementsVector[row].toString());
//                                        }
//                                        bufferedWriter.append("\t");
//                                        if (turningAnglesVector[row] != null) {
//                                            bufferedWriter.append(turningAnglesVector[row].toString());
//                                        }
//                                        bufferedWriter.newLine();
//                                    }
//                                    counter += currentNumber;
//                                }
//                            }
//                        } catch (IOException ex) {
//                            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        String name2 = project + "_" + experiment + "_" + plateCondition + ".txt";
                        List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(globalDataFolder, name2)))) {
                            // HEADER
                            bufferedWriter.append("w" + "\t" + "tr" + "\t" + "loc" + "\t" + "cd" + "\t" + "ed" + "\t" + "dir" + "\t" + "md" + "\t" + "ms" + "\t" + "mta" + "\t" + "max dis" + "\t" + "dr" + "\t" + "or" + "\t" + "perim" + "\t" + "area" + "\t" + "acirc" + "\t" + "dir2");
                            bufferedWriter.newLine();
                            int counter = 0;
                            for (List<Integer> list : numberOfTracks) {
                                for (Integer currentNumber : list) {
                                    for (int row = counter; row < counter + currentNumber; row++) {
                                        TrackDataHolder trackDataHolder = trackDataHolders.get(row);
                                        bufferedWriter.append(trackDataHolder.getTrack().getWellHasImagingType().getWell().toString());
                                        bufferedWriter.append("\t");
                                        bufferedWriter.append("" + trackDataHolder.getTrack().getTrackNumber());
                                        bufferedWriter.append("\t");
                                        bufferedWriter.append("" + (list.indexOf(currentNumber) + 1));
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
                                        bufferedWriter.newLine();
                                    }
                                    counter += currentNumber;
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(project + "_" + experiment + "_" + plateCondition + " processed");
                    }
                    System.out.println("****" + project + "_" + experiment + "_" + algorithm + ", " + imagingType + " processed");
                }
            }
            System.out.println("*-*-*-*-*" + project + "_" + experiment + " processed");
        }
        //        WellService wellService = (WellService) context.getBean("wellService");
        //        Experiment experiment = experimentService.findById(1L);
        //        ExperimentStatus experimentStatus = experiment.getExperimentStatus();
        //        System.out.println("" + experimentStatus);
        //
        //        for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
        //            List<Well> wells = new ArrayList<>();
        //            for (Well well : plateCondition.getWellList()) {
        //                Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
        //                wells.add(fetchedWell);
        //            }
        //            plateCondition.setWellList(wells);
        //        }
        //
        //
        //        try {
        //            experimentService.exportExperimentToXMLFile(experiment, new File("C:\\Users\\paola\\Desktop\\test.xml"));
        //        } catch (JAXBException | FileNotFoundException ex) {
        //            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
        //        }
        //        LocalContainerEntityManagerFactoryBean fb = (LocalContainerEntityManagerFactoryBean) context.getBean("&entityManagerFactory");
        //        Ejb3Configuration cfg = new Ejb3Configuration();
        //        Ejb3Configuration configured = cfg.configure(fb.getPersistenceUnitInfo(), fb.getJpaPropertyMap());
        //        // export the database schema
        //        SchemaExport schemaExport = new SchemaExport(configured.getHibernateConfiguration());
        //
        //        schemaExport.setOutputFile("C:\\Users\\paola\\Desktop\\testing_schema.sql");
        //        schemaExport.setFormat(true);
        //        schemaExport.execute(true, false, false, true);
        //        schemaExport.execute(true, false, false, true);
    }
}
