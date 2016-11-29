/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

/**
 * This class holds the results of the dose-response analysis All these results
 * will be shown in a table on the results tab and this table will also be put
 * in the report.
 *
 * @author Gwendolien Sergeant
 */
public class DoseResponseAnalysisResults {

    //results of the fitting for the initial data
    private SigmoidFittingResultsHolder initialFittingResults;

    //results of the fitting for the normalized data
    private SigmoidFittingResultsHolder normalizedFittingResults;
    
    //initial fitting statistics
    private DoseResponseStatisticsHolder initialFittingStatistics;
    
    //normalized fitting statistics
    private DoseResponseStatisticsHolder normalizedFittingStatistics;

    /**
     * Constructor
     */
    public DoseResponseAnalysisResults() {
        this.initialFittingResults = new SigmoidFittingResultsHolder();
        this.normalizedFittingResults = new SigmoidFittingResultsHolder();
        this.initialFittingStatistics = new DoseResponseStatisticsHolder();
        this.normalizedFittingStatistics = new DoseResponseStatisticsHolder();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public SigmoidFittingResultsHolder getFittingResults(boolean normalized) {
        if (!normalized) {
            return initialFittingResults;
        } else {
            return normalizedFittingResults;
        }
    }
    
    public DoseResponseStatisticsHolder getStatistics(boolean normalized) {
        if (!normalized) {
            return initialFittingStatistics;
        } else {
            return normalizedFittingStatistics;
        }
    }

    

}
