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
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellPreProcessingResults;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
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
        Map<PlateCondition, SingleCellPreProcessingResults> preProcessingMap = new LinkedHashMap<>();

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        WellService wellService = (WellService) context.getBean("wellService");
        SingleCellPreProcessor singleCellPreProcessor = (SingleCellPreProcessor) context.getBean("singleCellPreProcessor");

        // get the experiment
        Experiment experiment = experimentService.findById(50L);
        Project project = experiment.getProject();

        // make the folders
        File file = new File("C:\\Users\\paola\\Desktop\\singleCell_outFiles");
        File trackFolder = new File(file, project + "_" + experiment + "_" + "trackFiles");
        trackFolder.mkdir();
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
            // create a new object to hold pre-processing results
            SingleCellPreProcessingResults singleCellPreProcessingResults = new SingleCellPreProcessingResults();
            // do computations
            singleCellPreProcessor.generateTrackDataHolders(singleCellPreProcessingResults, plateCondition);
            singleCellPreProcessor.generateDataStructure(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTimeIndexes(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackDurations(experiment.getExperimentInterval(), singleCellPreProcessingResults);
            singleCellPreProcessor.generateRawTrackCoordinatesMatrix(singleCellPreProcessingResults, 1.55038);
            singleCellPreProcessor.computeCoordinatesRanges(singleCellPreProcessingResults);
            singleCellPreProcessor.generateShiftedTrackCoordinatesMatrix(singleCellPreProcessingResults);
            singleCellPreProcessor.generateInstantaneousDisplacementsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackDisplacementsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateCumulativeDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateEuclideanDistancesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackSpeedsVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateDirectionalitiesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTurningAnglesVector(singleCellPreProcessingResults);
            singleCellPreProcessor.generateTrackAnglesVector(singleCellPreProcessingResults);
            preProcessingMap.put(plateCondition, singleCellPreProcessingResults);
        }

        // we create a file for each condition
        for (PlateCondition plateCondition : preProcessingMap.keySet()) {
            String name1 = "trackFile_" + project + "_" + experiment + "_" + plateCondition + ".txt";
            SingleCellPreProcessingResults singleCellPreProcessingResults = preProcessingMap.get(plateCondition);
            Object[][] dataStructure = singleCellPreProcessingResults.getDataStructure();
            Double[][] rawTrackCoordinatesMatrix = singleCellPreProcessingResults.getRawTrackCoordinatesMatrix();
            Double[][] shiftedTrackCoordinatesMatrix = singleCellPreProcessingResults.getShiftedTrackCoordinatesMatrix();
            Double[] instantaneousDisplacementsVector = singleCellPreProcessingResults.getInstantaneousDisplacementsVector();
            Double[] turningAnglesVector = singleCellPreProcessingResults.getTurningAnglesVector();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(trackFolder, name1)))) {
                // HEADER
                bufferedWriter.append("well" + "\t" + "track" + "\t" + "timeIndex" + "\t" + "x" + "\t" + "y" + "\t" + "shiftX" + "\t" + "shiftY" + "\t" + "instDispl" + "\t" + "turningAngle");
                bufferedWriter.newLine();
                for (int row = 0; row < dataStructure.length; row++) {
                    Object[] currentRow = dataStructure[row];
                    for (int column = 0; column < currentRow.length; column++) {
                        bufferedWriter.append(currentRow[column].toString());
                        bufferedWriter.append("\t");
                    }
                    bufferedWriter.append(rawTrackCoordinatesMatrix[row][0].toString());
                    bufferedWriter.append("\t");
                    bufferedWriter.append(rawTrackCoordinatesMatrix[row][1].toString());
                    bufferedWriter.append("\t");
                    bufferedWriter.append(shiftedTrackCoordinatesMatrix[row][0].toString());
                    bufferedWriter.append("\t");
                    bufferedWriter.append(shiftedTrackCoordinatesMatrix[row][1].toString());
                    bufferedWriter.append("\t");
                    if (instantaneousDisplacementsVector[row] != null) {
                        bufferedWriter.append(instantaneousDisplacementsVector[row].toString());
                    }
                    bufferedWriter.append("\t");
                    if (turningAnglesVector[row] != null) {
                        bufferedWriter.append(turningAnglesVector[row].toString());
                    }
                    bufferedWriter.newLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
            }
            String name2 = "globalFile_" + project + "_" + experiment + "_" + plateCondition + ".txt";
            List<TrackDataHolder> trackDataHolders = singleCellPreProcessingResults.getTrackDataHolders();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(globalFolder, name2)))) {
                // HEADER
                bufferedWriter.append("well" + "\t" + "track" + "\t" + "cumDist" + "\t" + "euclDist" + "\t" + "directionality" + "\t" + "meanDispl" + "\t" + "meanSpeed" + "\t" + "meanTurningAngle");
                bufferedWriter.newLine();
                for (TrackDataHolder trackDataHolder : trackDataHolders) {
                    bufferedWriter.append(trackDataHolder.getTrack().getWellHasImagingType().getWell().toString());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getTrack().getTrackNumber());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getCumulativeDistance());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getEuclideanDistance());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getDirectionality());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getTrackMeanDisplacement());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getTrackMeanSpeed());
                    bufferedWriter.append("\t");
                    bufferedWriter.append("" + trackDataHolder.getTrackAngle());
                    bufferedWriter.newLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
            }

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
