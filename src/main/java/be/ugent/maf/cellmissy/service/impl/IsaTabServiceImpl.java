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
    public List<List<String>> createInvestigation(Experiment experimentToExport) {
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
        rowToCellMissyValue.put(114, experimentToExport.getUser().getOrcid());

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
    public List<List<String>> createStudy(Experiment experimentToExport) {
        List<List<String>> entries = new ArrayList<>();
        String extra1 = "Term Source REF";
        String extra2 = "Term Accession Number";
        //properties are in the first row
        List<String> header = Arrays.asList(
                "Source Name",
                "Characteristics[organism]", extra1, extra2,
                "Characteristics[cell]", extra1, extra2,
                "Characteristics[cell provider]", extra1, extra2,
                "Characteristics[cellular component]", extra1, extra2,
                "Characteristics[life cycle stage]", extra1, extra2,
                "Characteristics[genotype]", extra1, extra2,
                "Term Source REF",
                "Term Accession Number",
                "Characteristics[passage number]", extra1, extra2,
                "Protocol REF",
                "Parameter Value[cell culture vessel]", extra1, extra2,
                "Parameter Value[cell culture configuration]", extra1, extra2,
                "Parameter Value[cell culture matrix]", extra1, extra2,
                "Parameter Value[seeding density]", extra1, extra2,
                "Parameter Value[cell culture medium]", extra1, extra2,
                "Parameter Value[cell culture serum]", extra1, extra2,
                "Parameter Value[cell culture serum concentration]",
                "Unit", extra1, extra2,
                "Parameter Value[cell culture antibiotics]", extra1, extra2,
                "Parameter Value[cell culture temperature]",
                "Unit", extra1, extra2,
                "Parameter Value[cell culture CO2 partial pressure]", extra1, extra2,
                "Parameter Value[cell confluence level]",
                "Unit", extra1, extra2,
                "Parameter Value[plate identifier]", extra1, extra2,
                "Parameter Value[plate well number]", extra1, extra2,
                "Parameter Value[plate well column coordinate]", extra1, extra2,
                "Parameter Value[plate well row coordinate]", extra1, extra2,
                "Protocol REF",
                "Parameter Value[perturbation order]", extra1, extra2,
                "Parameter Value[perturbation agent type]", extra1, extra2,
                "Parameter Value[perturbation agent]", extra1, extra2,
                "Parameter Value[perturbation agent delivery]", extra1, extra2,
                "Parameter Value[perturbation agent solvent]", extra1, extra2,
                "Parameter Value[perturbation agent solvent concentration]",
                "Unit", extra1, extra2,
                "Parameter Value[perturbation dose]", extra1, extra2,
                "Parameter Value[perturbation duration]",
                "Unit", extra1, extra2,
                "Parameter Value[targeted gene identifier]", extra1, extra2,
                "Parameter Value[gene construct tag]", extra1, extra2,
                "Parameter Value[mode of expression]", extra1, extra2,
                "Sample Name",
                "Characteristics[genotype]", extra1, extra2,
                "Factor Value[perturbation agent]", extra1, extra2,
                "Factor Value[perturbation dose]", extra1, extra2,
                "Factor Value[perturbation time post exposure]",
                "Unit", extra1, extra2);

        //header can go integral into entries list
        entries.add(header);
        //for each well add row
        //for the "extra" CV columns, check if equal? and then add empty string
        for (int i = 0; i < experimentToExport.getPlateConditionList().size(); i++) {
            PlateCondition condition = experimentToExport.getPlateConditionList().get(i);
            for (int j = 0; j < condition.getWellList().size(); j++) {
                Well well = condition.getWellList().get(j);

            }
        }

        for (int i = 0; i < header.size(); i++) {
            List<String> row = new ArrayList<>();
            columnInfo.add(header.get(i));
            columnInfo.add(rowToCellMissyValue.get(i));
            //remove any null values from getting int that is not in the map
            columnInfo.removeAll(Collections.singleton(null));
            entries.add(columnInfo);
        }
        return entries;
    }

    @Override
    public List<List<String>> createAssay(Experiment experimentToExport) {
        List<List<String>> entries = new ArrayList<>();
        String extra1 = "Term Source REF";
        String extra2 = "Term Accession Number";
        //properties are in the first row
        List<String> header = Arrays.asList(
                "Sample Name",
                "Protocol REF",
                "Parameter Value[medium]", extra1, extra2,
                "Parameter Value[serum]", extra1, extra2,
                "Parameter Value[serum concentration]",
                "Unit", extra1, extra2,
                "Parameter Value[medium volume]",
                "Unit", extra1, extra2,
                "Parameter Value[migration modulator]", extra1, extra2,
                "Parameter Value[modulator concentration]",
                "Unit", extra1, extra2,
                "Parameter Value[modulator distribution]", extra1, extra2,
                "Protocol REF",
                "Parameter Value[imaging technique]", extra1, extra2,
                "Parameter Value[imaging technique temporal feature]", extra1, extra2,
                "Parameter Value[acquisition duration]",
                "Unit", extra1, extra2,
                "Parameter Value[time interval]",
                "Unit", extra1, extra2,
                "Parameter Value[objective type]", extra1, extra2,
                "Parameter Value[objective magnification]" extra1, extra2,
                "Parameter Value[objective numerical aperture]", extra1, extra2,
                "Parameter Value[acquisition channel count]", extra1, extra2,
                "Parameter Value[reporter]", extra1, extra2,
                "Parameter Value[voxel size]",
                "Unit", extra1, extra2,
                "Assay Name",
                "Raw Data File",
                "Protocol REF",
                "Parameter Value[software]", extra1, extra2
    

,
                "Data Transformation Name",
                "Derived Data File");
    }

}
