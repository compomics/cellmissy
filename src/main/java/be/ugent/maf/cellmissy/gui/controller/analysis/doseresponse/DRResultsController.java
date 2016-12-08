/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseStatisticsHolder;
import be.ugent.maf.cellmissy.entity.result.doseresponse.SigmoidFittingResultsHolder;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.GridBagConstraints;
import java.util.LinkedHashMap;
import java.util.List;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author CompOmics Gwen
 */
public abstract class DRResultsController {
    
    protected static final Font bodyFont = new Font(Font.HELVETICA, 8);
    protected static final Font boldFont = new Font(Font.HELVETICA, 8, Font.BOLD);
    protected static final Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    protected static final int chartWidth = 500;
    protected static final int chartHeight = 450;
    
    //model
    protected NonEditableTableModel tableModel;
    protected ChartPanel dupeInitialChartPanel;
    protected ChartPanel dupeNormalizedChartPanel;
    protected Experiment experiment;
    protected Document document;
    protected PdfWriter writer;
    
    // services
    protected GridBagConstraints gridBagConstraints;
    
    
    
    
    
    
    
    protected void calculateStatistics(DoseResponseStatisticsHolder statisticsHolder, SigmoidFittingResultsHolder resultsHolder, LinkedHashMap<Double, List<Double>> dataToFit) {
        //calculate and set R² and EC50
        statisticsHolder.setGoodnessOfFit(AnalysisUtils.computeRSquared(dataToFit, resultsHolder));
        statisticsHolder.setEc50(Math.pow(10, resultsHolder.getLogEC50()));

        //calculate and set standard errors of parameters
        //calculate and set 95% confidence interval boundaries
        double[] standardErrors = AnalysisUtils.calculateStandardErrors(dataToFit, resultsHolder);
        statisticsHolder.setStdErrBottom(standardErrors[0]);
        statisticsHolder.setcIBottom(checkAndGetCI(resultsHolder.getBottom(), standardErrors[0]));
        statisticsHolder.setStdErrTop(standardErrors[1]);
        statisticsHolder.setcITop(checkAndGetCI(resultsHolder.getTop(), standardErrors[1]));
        statisticsHolder.setStdErrLogEC50(standardErrors[2]);
        statisticsHolder.setcILogEC50(checkAndGetCI(resultsHolder.getLogEC50(), standardErrors[2]));
        statisticsHolder.setStdErrHillslope(standardErrors[3]);
        statisticsHolder.setcIHillslope(checkAndGetCI(resultsHolder.getHillslope(), standardErrors[3]));

        //confidence interval for ec50 (antilog of logec50 ci)
        double[] cILogEc50 = statisticsHolder.getcILogEC50();
        double[] cIEc50 = new double[2];
        for (int i = 0; i < cILogEc50.length; i++) {
            cIEc50[i] = Math.pow(10, cILogEc50[i]);
        }
        statisticsHolder.setcIEC50(cIEc50);
    }

    protected double[] checkAndGetCI(double parameter, double standardError) {
        if (standardError != 0.0) {
            return AnalysisUtils.calculateConfidenceIntervalBoundaries(parameter, standardError);
        } else {
            return null;
        }
    }
    
    
}
