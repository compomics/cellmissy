/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.service.impl;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.service.IsaTabService;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author CompOmics Gwen
 */
public class IsaTabServiceImpl implements IsaTabService {

    @Override
    public List<List<String>> createInvestigation(Experiment experimentToExport, String orcid) {
        List<List<String>> entries = new ArrayList<>();
        //first column: all properties
        List<String> firstColumnProperties = Arrays.asList(
                "ONTOLOGY SOURCE REFERENCE", // index 0
                "Term Source Name",
                "Term Source File",
                "Term Source Version",
                "Term Source Description",
                "INVESTIGATION",
                "Investigation Identifier",
                "Investigation Title",
                "Investigation Description",
                "Investigation Submission Date",
                "Investigation Public Release Date", //index 10
                "Comment[Investigation Conclusions]",
                "Comment[Created with configuration]",
                "Comment[Last Opened With Configuration]",
                "Comment[ISA-Tab Files Version]",
                "Comment[Created With Configuration]",
                "Comment[Last Opened With Configuration]",
                "INVESTIGATION PUBLICATIONS",
                "Investigation PubMed ID",
                "Investigation Publication DOI",
                "Investigation Publication Author List", //index 20
                "Investigation Publication Title",
                "Investigation Publication Status",
                "Investigation Publication Status Term Accession Number",
                "Investigation Publication Status Term Source REF",
                "INVESTIGATION CONTACTS",
                "Investigation Person Last Name",
                "Investigation Person First Name",
                "Investigation Person Mid Initials",
                "Investigation Person Email",
                "Investigation Person Phone", //index 30
                "Investigation Person Fax",
                "Investigation Person Address",
                "Investigation Person Affiliation",
                "Investigation Person Roles",
                "Investigation Person Roles Term Accession Number",
                "Investigation Person Roles Term Source REF",
                "STUDY",
                "Study Identifier",
                "Study Title",
                "Study Description", // 40
                "Study Start Date",
                "Study Submission Date",
                "Study Public Release Date",
                "Study File Name",
                "Comment[Funding Organization Name]",
                "Comment[Funding Organization Identifier]",
                "Comment[Grant Identifier]",
                "Comment[Grant Name]",
                "Comment[Basic Approach]",
                "Comment[Study Conclusions]", //50
                "Comment[MIACME Version]",
                "Comment[MIACME Compliance]",
                "Comment[Study Status]",
                "Comment[Plate Type]",
                "Comment[Plate Format]",
                "Comment[Plate Manufacturer]",
                "Comment[Plate Coating]",
                "Comment[Plate Coating Concentration]",
                "Comment[Study Grant Number]",
                "Comment[Study Funding Agency]", //60
                "STUDY DESIGN DESCRIPTORS",
                "Study Design Type",
                "Study Design Type Term Accession Number",
                "Study Design Type Term Source REF",
                "STUDY PUBLICATIONS",
                "Study PubMed ID",
                "Study Publication DOI",
                "Study Publication Author List",
                "Study Publication Title",
                "Study Publication Status", //70
                "Study Publication Status Term Accession Number",
                "Study Publication Status Term Source REF",
                "STUDY FACTORS",
                "Study Factor Name",
                "Study Factor Type",
                "Study Factor Type Term Accession Number",
                "Study Factor Type Term Source REF",
                "STUDY ASSAYS",
                "Study Assay File Name",
                "Study Assay Measurement Type", //80
                "Study Assay Measurement Type Term Accession Number",
                "Study Assay Measurement Type Term Source REF",
                "Study Assay Technology Type",
                "Study Assay Technology Type Term Accession Number",
                "Study Assay Technology Type Term Source REF",
                "Study Assay Technology Platform",
                "STUDY PROTOCOLS",
                "Study Protocol Name",
                "Study Protocol Type",
                "Study Protocol Type Term Accession Number", //90
                "Study Protocol Type Term Source REF",
                "Study Protocol Description",
                "Study Protocol URI",
                "Study Protocol Version",
                "Study Protocol Parameters Name",
                "Study Protocol Parameters Name Term Accession Number",
                "Study Protocol Parameters Name Term Source REF",
                "Study Protocol Components Name",
                "Study Protocol Components Type",
                "Study Protocol Components Type Term Accession Number", //100
                "Study Protocol Components Type Term Source REF",
                "STUDY CONTACTS",
                "Study Person Last Name",
                "Study Person First Name",
                "Study Person Mid Initials",
                "Study Person Email",
                "Study Person Phone",
                "Study Person Fax",
                "Study Person Address",
                "Study Person Affiliation", //110
                "Study Person Roles",
                "Study Person Roles Term Accession Number",
                "Study Person Roles Term Source REF",
                "Comment[Study Person Identifier]");    // 114

        //map with row index of property to database item
        HashMap<Integer, String> rowToCellMissyValue = new HashMap<>();
        rowToCellMissyValue.put(6, Integer.toString(experimentToExport.getProject().getProjectNumber()));
        rowToCellMissyValue.put(8, experimentToExport.getProject().getProjectDescription());
        //duplicate person information for investigation and study contacts sections
        rowToCellMissyValue.put(29, experimentToExport.getUser().getEmail());
        rowToCellMissyValue.put(106, experimentToExport.getUser().getEmail());
        rowToCellMissyValue.put(26, experimentToExport.getUser().getLastName());
        rowToCellMissyValue.put(103, experimentToExport.getUser().getLastName());
        rowToCellMissyValue.put(27, experimentToExport.getUser().getFirstName());
        rowToCellMissyValue.put(104, experimentToExport.getUser().getFirstName());
        rowToCellMissyValue.put(34, experimentToExport.getUser().getRole().toString()); //might not be needed
        rowToCellMissyValue.put(111, experimentToExport.getUser().getRole().toString()); //might not be needed
        rowToCellMissyValue.put(55, experimentToExport.getPlateFormat().getFormat() + "-well plate");
        rowToCellMissyValue.put(41, experimentToExport.getExperimentDate().getDate() + "/" + (experimentToExport.getExperimentDate().getMonth() + 1) + "/"
                + (experimentToExport.getExperimentDate().getYear() + 1900));
        rowToCellMissyValue.put(38, Integer.toString(experimentToExport.getExperimentNumber()));
        rowToCellMissyValue.put(39, experimentToExport.getPurpose());
        rowToCellMissyValue.put(40, experimentToExport.getPurpose());
        rowToCellMissyValue.put(53, experimentToExport.getExperimentStatus().toString());

        //extra stuff
        rowToCellMissyValue.put(44, "s_1.txt");
        rowToCellMissyValue.put(51, "0.3");
        rowToCellMissyValue.put(54, "cell culture multiwell plate");
        rowToCellMissyValue.put(79, "a_1_cell_migration_assay_microscopy_imaging.txt");
        rowToCellMissyValue.put(80, "cell migration assay");
        rowToCellMissyValue.put(83, "microscopy imaging");

        //add info filled in GUI by user
        rowToCellMissyValue.put(114, orcid);

        for (int i = 0; i < firstColumnProperties.size(); i++) {
            List<String> columnInfo = new ArrayList<>();
            columnInfo.add(firstColumnProperties.get(i));
            columnInfo.add(rowToCellMissyValue.get(i));
            //remove any null values from getting int that is not in the map
            columnInfo.removeAll(Collections.singleton(null));
            entries.add(columnInfo);
        }
        return entries;
    }

    @Override
    public List<List<String>> createStudy(Experiment experimentToExport, String organism) {
        List<List<String>> entries = new ArrayList<>();
        //properties are in the first row
        List<String> header = Arrays.asList(
                "Source Name",
                "Characteristics[organism]",
                "Characteristics[cell]",
                "Characteristics[genotype]",
                "Protocol REF",
                "Parameter Value[cell culture vessel]",
                "Parameter Value[cell culture matrix]",
                "Parameter Value[seeding density]",
                "Parameter Value[cell culture medium]",
                "Parameter Value[cell culture serum]",
                "Parameter Value[cell culture serum concentration]", "Unit",
                "Parameter Value[plate identifier]",
                //                "Parameter Value[plate well number]",
                "Parameter Value[plate well column coordinate]",
                "Parameter Value[plate well row coordinate]",
                "Protocol REF",
                "Parameter Value[perturbation order]",
                "Parameter Value[perturbation agent type]",
                "Parameter Value[perturbation agent]",
                "Parameter Value[perturbation agent delivery]",
                "Parameter Value[perturbation agent solvent]",
                "Parameter Value[perturbation agent solvent concentration]", "Unit",
                "Parameter Value[perturbation dose]",
                "Sample Name",
                "Characteristics[genotype]",
                "Factor Value[perturbation agent]",
                "Factor Value[perturbation dose]");

        //header can go integral into entries list
        entries.add(header);
        //for each well add row
        for (int i = 0; i < experimentToExport.getPlateConditionList().size(); i++) {
            PlateCondition condition = experimentToExport.getPlateConditionList().get(i);
            for (int j = 0; j < condition.getWellList().size(); j++) {
                Well well = condition.getWellList().get(j);
                //row needs to contain the same amount of entries as the header
                List<String> row = Arrays.asList(
                        well.getPlateCondition().getCellLine().getCellLineType().getName(),
                        organism,
                        well.getPlateCondition().getCellLine().getCellLineType().getName(),
                        well.getPlateCondition().getCellLine().getCellLineType().getName(),
                        "cell growth",
                        "microplate",
                        well.getPlateCondition().getEcm().getEcmComposition().getCompositionType(),
                        well.getPlateCondition().getCellLine().getSeedingDensity() + " cells/well",
                        well.getPlateCondition().getCellLine().getGrowthMedium(),
                        well.getPlateCondition().getCellLine().getSerum(),
                        Double.toString(well.getPlateCondition().getCellLine().getSerumConcentration()), "%",
                        experimentToExport.getPlateFormat().getPlateFormatid().toString(),
                        //according to examples, this is the number in the order of imaged wells. CellMissy does not have this information
                        //                        "Parameter Value[plate well number]", 
                        Integer.toString(well.getColumnNumber()),
                        AnalysisUtils.RowCoordinateToString(well.getRowNumber()),
                        "perturbation",
                        "1", //???
                        "chemical compound", //???
                        well.getPlateCondition().getTreatmentList().get(0).getTreatmentType().getName(),
                        "addition to medium",
                        well.getPlateCondition().getTreatmentList().get(0).getDrugSolvent(),
                        Double.toString(well.getPlateCondition().getTreatmentList().get(0).getDrugSolventConcentration()), "micromolar",
                        "medium dose",
                        "culture" + (j + 1),
                        well.getPlateCondition().getCellLine().getCellLineType().getName(),
                        well.getPlateCondition().getTreatmentList().get(0).getTreatmentType().getName(),
                        well.getPlateCondition().getTreatmentList().get(0).getConcentration() + " " + well.getPlateCondition().getTreatmentList().get(0).getConcentrationUnit());

                entries.add(row);
            }
        }
        return entries;
    }

    @Override
    public List<List<String>> createAssay(Experiment experimentToExport) {
        List<List<String>> entries = new ArrayList<>();
        //properties are in the first row
        List<String> header = Arrays.asList(
                "Sample Name",
                "Protocol REF",
                "Parameter Value[medium]",
                "Parameter Value[serum]",
                "Parameter Value[serum concentration]", "Unit",
                "Parameter Value[medium volume]", "Unit",
                "Protocol REF",
                "Parameter Value[imaging modality]",
                "Parameter Value[imagine sequence type]",
                "Parameter Value[observation period]", "Unit",
                "Parameter Value[time series interval]", "Unit",
                "Parameter Value[objective magnification]",
                "Parameter Value[channel definition]",
                "Parameter Value[pixel identifier]",
                "Parameter Value[pixel dimension order]",
                "Parameter Value[pixel type]",
                "Parameter Value[pixel sizeX]",
                "Parameter Value[pixel sizeY]",
                "Parameter Value[pixel sizeZ]",
                "Parameter Value[pixel sizeC]",
                "Parameter Value[pixel sizeT]",
                "Assay Name",
                "Protocol REF",
                "Parameter Value[software]",
                "Data Transformation Name",
                "Derived Data File");

        //header can go integral into entries list
        entries.add(header);
        //for each well add row
        for (int i = 0; i < experimentToExport.getPlateConditionList().size(); i++) {
            PlateCondition condition = experimentToExport.getPlateConditionList().get(i);
            for (int j = 0; j < condition.getWellList().size(); j++) {
                Well well = condition.getWellList().get(j);
                String software = algorithmToSoftware(well.getWellHasImagingTypeList().get(0).getAlgorithm().getAlgorithmName());
                //row needs to contain the same amount of entries as the header
                List<String> row = Arrays.asList(
                        "culture" + (j + 1),
                        "migration assay",
                        well.getPlateCondition().getAssayMedium().getMedium(),
                        well.getPlateCondition().getAssayMedium().getSerum(),
                        Double.toString(well.getPlateCondition().getAssayMedium().getSerumConcentration()), "%",
                        Double.toString(well.getPlateCondition().getAssayMedium().getVolume()), "microliter",
                        "imaging",
                        "phase-contrast microscopy",
                        "time-series",
                        Double.toString(experimentToExport.getDuration()), "hour",
                        Double.toString(experimentToExport.getExperimentInterval()), "minute",
                        experimentToExport.getMagnification().getMagnficationNumber(),
                        "phase-contrast",
                        "Pixels:0",
                        "-Find in .ome companion file-",    // IDEA: in future update, let user link ome filepath, get this info and put here
                        "-Find in .ome companion file-",
                        "-Find in .ome companion file-",
                        "-Find in .ome companion file-",
                        "1",
                        "1",
                        Double.toString((experimentToExport.getDuration()*60)/experimentToExport.getExperimentInterval()),
                        "culture" + (j + 1),
                        "data transformation",
                        software,
                        well.getWellHasImagingTypeList().get(0).getAlgorithm().getAlgorithmName(),
                        software + "//" + well.getColumnNumber() + "_" + AnalysisUtils.RowCoordinateToString(well.getRowNumber()) );

                entries.add(row);
            }
        }
        return entries;
    }
    
    private String algorithmToSoftware(String algoName){
        if (algoName.contains("algo")){
        return "CellMIA";
    } else {
            return algoName;
            }
    }

}
