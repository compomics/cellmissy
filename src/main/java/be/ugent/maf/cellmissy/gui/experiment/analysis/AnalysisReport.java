/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis;

import be.ugent.maf.cellmissy.entity.AreaAnalysisResults;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import java.util.Map;

/**
 * Class to generate a report for analysis
 * @author Paola Masuzzo
 */
public class AnalysisReport {

    private Experiment experiment;
    private Map<PlateCondition, AreaPreProcessingResults> preProcessingMap;
    private Map<PlateCondition, AreaAnalysisResults> analysisMap;

    public AnalysisReport(Experiment experiment, Map<PlateCondition, AreaPreProcessingResults> preProcessingMap, Map<PlateCondition, AreaAnalysisResults> analysisMap) {
        this.experiment = experiment;
        this.preProcessingMap = preProcessingMap;
        this.analysisMap = analysisMap;
    }

    private void setUpReport() {
        for (PlateCondition plateCondition : analysisMap.keySet()) {
            AreaAnalysisResults areaAnalysisResults = analysisMap.get(plateCondition);
            // number of replicates used for condition
            int numberOfReplicates = areaAnalysisResults.getSlopes().length;
            
        }
    }
}
