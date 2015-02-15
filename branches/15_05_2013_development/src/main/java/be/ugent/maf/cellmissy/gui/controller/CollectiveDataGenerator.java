/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
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
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
class CollectiveDataGenerator {

    public static void main(String[] args) {

        // get the application context
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        // get the services we need
        ExperimentService experimentService = (ExperimentService) context.getBean("experimentService");
        ProjectService projectService = (ProjectService) context.getBean("projectService");
        WellService wellService = (WellService) context.getBean("wellService");
        // get all the experiments from DB
        Project project = projectService.findById(1L);
        List<Experiment> experiments = experimentService.findExperimentsByProjectId(project.getProjectid());
        // root folder
//        File folder = new File("Z:\\paola\\CellMissy_data\\mini_screening_drugs_4");

        File folder = new File("C:\\Users\\paola\\Desktop\\UGent\\Weizmann Institute\\mini_screening_drugs");
        for (Experiment experiment : experiments) {

            // test with one experiment
//            if (experiment.getExperimentNumber() == 48) {
            String expPurpose = experiment.getPurpose();
            expPurpose = expPurpose.replace("/", "_");
            expPurpose = expPurpose.replaceAll("\\s+", "");
            expPurpose = expPurpose.replaceAll(",", "_");
            expPurpose = expPurpose.replace("-->", "_");

            System.out.println("exp: " + expPurpose);
            String fileName = experiment + "_" + expPurpose + ".csv";
            System.out.println("STARTING WITH EXPERIMENT: " + experiment + ": " + expPurpose);

            // fetch the migration data
            System.out.println("fetching data for experiment: " + experiment + " ...");
            for (PlateCondition plateCondition : experiment.getPlateConditionList()) {
                List<Well> wells = new ArrayList<>();
                for (Well well : plateCondition.getWellList()) {
                    Well fetchedWell = wellService.fetchMigrationData(well.getWellid());
                    wells.add(fetchedWell);
                }
                plateCondition.setWellList(wells);
            }
            System.out.println(experiment + " loaded");

            // write to file
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {
                // header of the file
                bufferedWriter.append("cond_id" + " " + "replicate" + " " + "settings" + " " + "drug_name" + " " + "drug_conc" + " " + "cell_line" + " "
                        + "cell_density" + " " + "ecm_type" + " "
                        + "ecm_dens/conc" + " " + "ecm_dimens" + " " + "area" + " " + "time");
                // new line
                bufferedWriter.newLine();
                System.out.println("I AM WRITING TO FILE!");
                for (PlateCondition plateCondition : experiment.getPlateConditionList()) {

                    // these are the technical replicates
                    List<Well> wellList = plateCondition.getWellList();
                    // for each well different imaging types can be possible!
                    for (Well well : wellList) {
                        List<WellHasImagingType> wellHasImagingTypeList = well.getWellHasImagingTypeList();
                        for (WellHasImagingType whit : wellHasImagingTypeList) {
                            // skip algo-x, BAD results
                            String algorithmName = whit.getAlgorithm().getAlgorithmName();
                            if (!algorithmName.contains("algo-x")) {
                                // all the consecutive area time points
                                List<TimeStep> timeStepList = whit.getTimeStepList();
                                int numberOfTimeSteps = timeStepList.size();

                                for (TimeStep aTimeStepList : timeStepList) {
                                    bufferedWriter.append("" + plateCondition.getPlateConditionid());
                                    bufferedWriter.append(" ");
//                                    bufferedWriter.append("" + whit);
                                    bufferedWriter.append("" + whit.getSequenceNumber());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + whit);
                                    bufferedWriter.append(" ");
                                    List<Treatment> treatmentList = plateCondition.getTreatmentList();
                                    String treatmentName = "";
                                    String concentration = "";
                                    if (!treatmentList.isEmpty()) {

                                        if (treatmentList.size() == 1) {
                                            treatmentName = plateCondition.getTreatmentList().get(0).getTreatmentType().getName();
                                            concentration = plateCondition.getTreatmentList().get(0).getConcentration().toString();
                                        } else {
                                            for (int j = 0; j < treatmentList.size(); j++) {
                                                Treatment treatment = treatmentList.get(j);
                                                String tempName = treatment.getTreatmentType().getName();
                                                String tempConc = treatment.getConcentration().toString();
                                                if (j != treatmentList.size() - 1) {
                                                    treatmentName = treatmentName.concat(tempName + "+");
                                                    concentration = concentration.concat(tempConc + "+");
                                                } else {
                                                    treatmentName = treatmentName.concat(tempName);
                                                    concentration = concentration.concat(tempConc);
                                                }
                                            }
                                        }
                                    } else {
                                        treatmentName = "missing_Control";
                                    }
                                    treatmentName = treatmentName.replace(" ", "_");
                                    treatmentName = treatmentName.replace(",", "_");
                                    bufferedWriter.append("" + treatmentName);
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + concentration);
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + plateCondition.getCellLine().getCellLineType());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + plateCondition.getCellLine().getSeedingDensity());
                                    bufferedWriter.append(" ");
                                    String compositionType = plateCondition.getEcm().getEcmComposition().getCompositionType();
                                    compositionType = compositionType.replace(" ", "_");
                                    compositionType = compositionType.replace(",", "_");

                                    bufferedWriter.append("" + compositionType);
                                    bufferedWriter.append(" ");
                                    MatrixDimension matrixDimension = plateCondition.getAssay().getMatrixDimension();
                                    // if the dimension is 2D get the matrix concentration
                                    if ("2D".equals(matrixDimension.getDimension())) {
                                        bufferedWriter.append("" + plateCondition.getEcm().getConcentration());
                                    } else {
                                        // else, write the matrix density
                                        bufferedWriter.append("" + plateCondition.getEcm().getEcmDensity());
                                    }

                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + plateCondition.getAssay().getMatrixDimension());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + aTimeStepList.getArea());
                                    bufferedWriter.append(" ");
                                    bufferedWriter.append("" + aTimeStepList.getTimeStepSequence());

                                    bufferedWriter.newLine();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(StepCentricDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
//            }
        }
    }
}
