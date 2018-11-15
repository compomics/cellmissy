package be.ugent.maf.cellmissy.service;

import be.ugent.maf.cellmissy.entity.Experiment;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Service for ISA-tab files. Currently used for exporting experiments to a CMSO
 * standard.
 *
 * @author Gwendolien Sergeant
 */
public interface IsaTabService {

    List<List<String>> createInvestigation(Experiment experimentToExport,String orcid);

    List<List<String>> createStudy(Experiment experimentToExport, String organism);

    List<List<String>> createAssay(Experiment experimentToExport);

}
