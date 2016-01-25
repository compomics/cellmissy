/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.area.doseresponse;

import be.ugent.maf.cellmissy.entity.result.area.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.entity.result.area.doseresponse.SigmoidFittingResultsHolder;

/**
 *
 * @author Gwendolien
 */
public interface SigmoidFitter {
    
    void fitData(DoseResponseAnalysisGroup analysisGroup, SigmoidFittingResultsHolder resultsHolder);
}
