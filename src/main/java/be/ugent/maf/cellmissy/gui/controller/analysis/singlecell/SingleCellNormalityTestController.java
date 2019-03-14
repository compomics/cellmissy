/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.AnalysisPanel;
import javax.swing.DefaultListModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import umontreal.ssj.gof.GofStat;
import umontreal.ssj.probdist.NormalDist;
import com.datumbox.framework.core.statistics.nonparametrics.onesample.ShapiroWilk;
import be.ugent.maf.cellmissy.analysis.impl.ShapiroWilkTest;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.result.singlecell.QQPlotDatasets;
import be.ugent.maf.cellmissy.entity.result.singlecell.SingleCellConditionDataHolder;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisPanel;
import static be.ugent.maf.cellmissy.utils.AnalysisUtils.computeMean;
import static be.ugent.maf.cellmissy.utils.AnalysisUtils.computeStandardDeviation;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.jdesktop.beansbinding.BindingGroup;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Controller;
import smile.plot.PlotCanvas;
import smile.plot.QQPlot;
import smile.stat.distribution.GaussianDistribution;



/**
 *
 * @author ninad
 */
@Controller("singleCellNormalityTestController")
public class SingleCellNormalityTestController {
    
    private static final Logger LOG = Logger.getLogger(SingleCellStatisticsController.class);
    // model
    private BindingGroup bindingGroup;
    private HashMap<String, List<double[]>> datasetHashMap;
    // view
    private SingleCellAnalysisPanel singleCellAnalysisPanel;
    private ChartPanel qqPlotChartPanel; 
    // parent controller
    @Autowired
    private SingleCellAnalysisController singleCellAnalysisController;
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers
    // services
    private GridBagConstraints gridBagConstraints;
    
    @Autowired
    private SingleCellMainController singleCellMainController;

    
    private QQPlotDatasets qqplotDatasets;
    private SingleCellConditionDataHolder dataholder;
   

    /**
     * Initialize controller
     * 
     */
    
    
    public JTabbedPane getNormalityTestParentPanel() {
        return singleCellAnalysisController.getAnalysisPanel().getNormalityTestParentPanel();
    }


    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        bindingGroup = new BindingGroup();
        qqplotDatasets = new QQPlotDatasets();

        // initialize the main view

        initMainView();
    }
    public PlateCondition getCurrentCondition() {
        return singleCellPreProcessingController.getCurrentCondition();
    }
    public SingleCellConditionDataHolder getConditionDataHolder(PlateCondition plateCondition) {
        return singleCellPreProcessingController.getConditionDataHolder(plateCondition);
    }
    
    /**
     * Initialize main view.
     */

    private void initMainView() {
       
        // the view is kept in the parent controllers
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        analysisPanel.getConditionList().setModel(new DefaultListModel());

        /**
        * Change Listener to the Tabbed Pane
        */

        // When normality test button is clickes, show outcomes for selected condition in first tab
        analysisPanel.getNormalityTestsRadioButton().addActionListener((ActionEvent e) -> {
            plotQQPlots("0");
            ComputeStatisticalTests("0");
            computeSkewness("0");
            computeKurtosis("0");
            fillInConditionTextFields();
        });
        // Update outcomes when user selects another tab
        analysisPanel.getNormalityTestParentPanel().addChangeListener((ChangeEvent e) -> {
            
            String parameter = Integer.toString(analysisPanel.getNormalityTestParentPanel().getSelectedIndex());  
            plotQQPlots(parameter);
            ComputeStatisticalTests(parameter);
            computeSkewness(parameter);
            computeKurtosis(parameter);
            fillInConditionTextFields();


       });
        
        
        
    }
    
    /**
     * Compute normality tests
     * @param x
     * @return 
    */
        //compute Anderson Darling and return p-value
    public double executeAndersonDarling(double[] x) {
        double mu = computeMean(x);
        double sigma = computeStandardDeviation(x);
        NormalDist dist = new NormalDist(mu, sigma);
        return GofStat.andersonDarling(x, dist)[1]; 
    }
        //compute Shapiro-Wilk and return p-value
    public double executeShapiroWilk(Double[] x){
        return ShapiroWilkTest.shapiroWilkW(x);
    }
        //Anderson Darling: significant or not?
    public boolean significanttestAD(double[] x){
        boolean rejectH0 = false;
        double p = executeAndersonDarling(x);
        if (p <= 0.01) {
            rejectH0 = true;
        }
        return rejectH0;
    }
        //Shapiro-Wilk: significant or not?
    public boolean significanttestSW(Double[] x){
        boolean rejectH0 = false;
        double p = executeShapiroWilk(x);
        if (p <= 0.01) {
            rejectH0 = true;
        }
        return rejectH0;
    }
    
    
    /**
     * Plot QQPlots for a given condition
     * @param parameter
     */
    public void plotQQPlots(String parameter){
        // get condition
        // get right feature -> right tab in the tabbed pane
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        
        // Get data for selected condition 
        Boolean filteredData = singleCellAnalysisController.isFilteredData();
        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>();

        if (filteredData) {
            conditionDataHolders.addAll(singleCellAnalysisController.getFilteringMap().keySet());
        } else {
            conditionDataHolders.addAll(singleCellAnalysisController.getPreProcessingMap().values());
        }
        
        List<PlateCondition> plateConditionList = singleCellMainController.getPlateConditionList();
        PlateCondition condition = singleCellMainController.getSelectedCondition();
        
        // Get features for selected condition
        datasetHashMap = qqplotDatasets.getDatasetHashMap(conditionDataHolders);
        List<double[]> datasetCondition = datasetHashMap.get(condition.toString());
        double[] accumdist = datasetCondition.get(0);
        double[] euclid = datasetCondition.get(1);
        double[] direct = datasetCondition.get(2);
        double[] speed = datasetCondition.get(3);
        
        // Plot the QQ plot for selected condition and feature
        
        if ("0".equals(parameter)){
            //reset if someone clicks on same tab again
            analysisPanel.getAccumulatedQQPlotPanel().removeAll();
            double mu = computeMean(accumdist);
            double sigma = computeStandardDeviation(accumdist);
            GaussianDistribution distribution = new GaussianDistribution(mu, sigma);
            PlotCanvas accumDistJPanel = QQPlot.plot(accumdist, distribution);
            accumDistJPanel.setMargin(0.05);
            analysisPanel.getAccumulatedQQPlotPanel().add(accumDistJPanel, gridBagConstraints);
            analysisPanel.getAccumulatedQQPlotPanel().repaint();
            analysisPanel.getAccumulatedQQPlotPanel().revalidate();
        }
        else if ("1".equals(parameter)) {
            analysisPanel.getEuclidianQQPlotPanel().removeAll();
            double mu = computeMean(euclid);
            double sigma = computeStandardDeviation(euclid);
            GaussianDistribution distribution = new GaussianDistribution(mu, sigma);
            PlotCanvas eucdistJPanel = QQPlot.plot(euclid, distribution);
            eucdistJPanel.setMargin(0.05);
            analysisPanel.getEuclidianQQPlotPanel().add(eucdistJPanel, gridBagConstraints);
            analysisPanel.getEuclidianQQPlotPanel().repaint();
            analysisPanel.getEuclidianQQPlotPanel().revalidate();
        }
        else if ("2".equals(parameter)){
            analysisPanel.getDirectionalityQQPlotpanel().removeAll();
            double mu = computeMean(direct);
            double sigma = computeStandardDeviation(direct);
            GaussianDistribution distribution = new GaussianDistribution(mu, sigma);
            PlotCanvas directJPanel = QQPlot.plot(direct, distribution);
            directJPanel.setMargin(0.05);
            analysisPanel.getDirectionalityQQPlotpanel().add(directJPanel, gridBagConstraints);
            analysisPanel.getDirectionalityQQPlotpanel().repaint();
            analysisPanel.getDirectionalityQQPlotpanel().revalidate();
        }
        else if ("3".equals(parameter)){
            analysisPanel.getSpeedQQPlotPanel().removeAll();
            double mu = computeMean(speed);
            double sigma = computeStandardDeviation(speed);
            GaussianDistribution distribution = new GaussianDistribution(mu, sigma);
            PlotCanvas speedJPanel = QQPlot.plot(speed, distribution);
            speedJPanel.setMargin(0.05);
            analysisPanel.getSpeedQQPlotPanel().add(speedJPanel, gridBagConstraints);
            analysisPanel.getSpeedQQPlotPanel().repaint();
            analysisPanel.getSpeedQQPlotPanel().revalidate();
        }
        
        
    }

    /**
     * Compute statistical tests for a given condition
     */
    
    public void ComputeStatisticalTests(String parameter){
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        
        Boolean filteredData = singleCellAnalysisController.isFilteredData();
        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>();
        
         if (filteredData) {
            conditionDataHolders.addAll(singleCellAnalysisController.getFilteringMap().keySet());
        } else {
            conditionDataHolders.addAll(singleCellAnalysisController.getPreProcessingMap().values());
        }
        
        PlateCondition condition = singleCellMainController.getSelectedCondition();
        List<double[]> datasetCondition = datasetHashMap.get(condition.toString());
        double[] accumdist = datasetCondition.get(0);
        double[] euclid = datasetCondition.get(1);
        //there is a NAN value in the directionality vector for some reason
        double[] direct = datasetCondition.get(2);
        double[] speed = datasetCondition.get(3);

        
        if ("0".equals(parameter)){
            double ADP = executeAndersonDarling(accumdist);
            Double SW = executeShapiroWilk(ArrayUtils.toObject(accumdist));
            boolean rejectH0AD = significanttestAD(accumdist);
            boolean rejectH0SW = significanttestSW(ArrayUtils.toObject(accumdist));

            analysisPanel.getAndersonPADTextField().setText(Double.toString(ADP));
            analysisPanel.getShapiroPTextField().setText(Double.toString(SW));
            if (rejectH0AD){
                analysisPanel.getAndersonOutcomeADTextField().setText("not normally distributed");
            }
            else {
                analysisPanel.getAndersonOutcomeADTextField().setText("normally distributed");
            }
            if (rejectH0SW){
                analysisPanel.getShapiroOutcomeADTextField().setText("not normally distributed");
            }
            else {
                analysisPanel.getShapiroOutcomeADTextField().setText("normally distributed");
            }
        }
        else if ("1".equals(parameter)) {
            double ADP = executeAndersonDarling(euclid);
            Double SW = executeShapiroWilk(ArrayUtils.toObject(euclid));
            boolean rejectH0AD = significanttestAD(euclid);  
            boolean rejectH0SW = significanttestSW(ArrayUtils.toObject(euclid));

            analysisPanel.getAndersonPTextField1().setText(Double.toString(ADP));
            analysisPanel.getShapiroPTextField1().setText(Double.toString(SW));
            if (rejectH0AD){
                analysisPanel.getAndersonOutcomeTextField1().setText("not normally distributed");
            }
            else {
                analysisPanel.getAndersonOutcomeTextField1().setText("normally distributed");
            }
            if (rejectH0SW){
                analysisPanel.getShapiroOutcomeTextField1().setText("not normally distributed");
            }
            else {
                analysisPanel.getShapiroOutcomeTextField1().setText("normally distributed");
            }
        }
        else if ("2".equals(parameter)){
            double ADP = executeAndersonDarling(direct);
            Double SW = executeShapiroWilk(ArrayUtils.toObject(direct));
            boolean rejectH0AD = significanttestAD(direct);   
            boolean rejectH0SW = significanttestSW(ArrayUtils.toObject(direct));

            analysisPanel.getAndersonPTextField3().setText(Double.toString(ADP));
            analysisPanel.getShapiroPTextField3().setText(Double.toString(SW));
            if (rejectH0AD){
                analysisPanel.getAndersonOutcomeTextField3().setText("not normally distributed");
            }
            else {
                analysisPanel.getAndersonOutcomeTextField3().setText("normally distributed");
            }
            if (rejectH0SW){
                analysisPanel.getShapiroOutcomeTextField3().setText("not normally distributed");
            }
            else {
                analysisPanel.getShapiroOutcomeTextField3().setText("normally distributed");
            }
        }
        else if ("3".equals(parameter)){
            double ADP = executeAndersonDarling(speed);
            Double SW = executeShapiroWilk(ArrayUtils.toObject(speed));
            boolean rejectH0AD = significanttestAD(speed);    
            boolean rejectH0SW = significanttestSW(ArrayUtils.toObject(speed));

            analysisPanel.getAndersonPTextField2().setText(Double.toString(ADP));
            analysisPanel.getShapiroPTextField2().setText(Double.toString(SW));
            if (rejectH0AD){
                analysisPanel.getAndersonOutcomeTextField2().setText("not normally distributed");
            }
            else {
                analysisPanel.getAndersonOutcomeTextField2().setText("normally distributed");
            }
            if (rejectH0SW){
                analysisPanel.getShapiroOutcomeTextField2().setText("not normally distributed");
            }
            else {
                analysisPanel.getShapiroOutcomeTextField2().setText("normally distributed");
            }
        }        
        
    }
    
    /**
     * Compute skewness and kurtosis for given condition
     */
    public void computeSkewness(String parameter){
         AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        
        Boolean filteredData = singleCellAnalysisController.isFilteredData();
        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>();
        
         if (filteredData) {
            conditionDataHolders.addAll(singleCellAnalysisController.getFilteringMap().keySet());
        } else {
            conditionDataHolders.addAll(singleCellAnalysisController.getPreProcessingMap().values());
        }
        
        PlateCondition condition = singleCellMainController.getSelectedCondition();
        List<double[]> datasetCondition = datasetHashMap.get(condition.toString());
        double[] accumdist = datasetCondition.get(0);
        double[] euclid = datasetCondition.get(1);
        //there is a NAN value in the directionality vector for some reason
        double[] direct = datasetCondition.get(2);
        double[] speed = datasetCondition.get(3);    
        Skewness skew = new Skewness();
        switch(parameter){
            case "0": skew.setData(accumdist);
            double skewaccum = skew.evaluate();
            analysisPanel.getSkewnessPTextField().setText(Double.toString(skewaccum));
            if (skewaccum < -0.5){
                analysisPanel.getSkewnessOutcomeADTextField().setText("Left skewed");
            }
            else if (skewaccum > 0.5){
                analysisPanel.getSkewnessOutcomeADTextField().setText("Right skewed"); 
            }
            else {
                analysisPanel.getSkewnessOutcomeADTextField().setText("Symmetrical");
            }
            break;
            case "1": skew.setData(euclid);
            double skeweuclid = skew.evaluate();
            analysisPanel.getSkewnessPTextField1().setText(Double.toString(skeweuclid));
            if (skeweuclid < -0.5){
                analysisPanel.getSkewnessOutcomeTextField1().setText("Left skewed");
            }
            else if (skeweuclid > 0.5){
                analysisPanel.getSkewnessOutcomeTextField1().setText("Right skewed"); 
            }
            else {
                analysisPanel.getSkewnessOutcomeTextField1().setText("Symmetrical");
            }
            break;
            case "2" : skew.setData(direct);
            double skewdirect = skew.evaluate();
            analysisPanel.getSkewnessPTextField3().setText(Double.toString(skewdirect));
            if (skewdirect < -0.5){
                analysisPanel.getSkewnessOutcomeTextField3().setText("Left skewed");
            }
            else if (skewdirect > 0.5){
                analysisPanel.getSkewnessOutcomeTextField3().setText("Right skewed"); 
            }
            else {
                analysisPanel.getSkewnessOutcomeTextField3().setText("Symmetrical");
            }
            break;
            case "3" : skew.setData(speed);
            double skewspeed = skew.evaluate();
            analysisPanel.getSkewnessPTextField2().setText(Double.toString(skewspeed));
            if (skewspeed < -0.5){
                analysisPanel.getSkewnessOutcomeTextField2().setText("Left skewed");
            }
            else if (skewspeed > 0.5){
                analysisPanel.getSkewnessOutcomeTextField2().setText("Right skewed"); 
            }
            else {
                analysisPanel.getSkewnessOutcomeTextField2().setText("Symmetrical");
            }
            break;
        }
    }
    
        public void computeKurtosis(String parameter){
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        
        Boolean filteredData = singleCellAnalysisController.isFilteredData();
        List<SingleCellConditionDataHolder> conditionDataHolders = new ArrayList<>();
        
         if (filteredData) {
            conditionDataHolders.addAll(singleCellAnalysisController.getFilteringMap().keySet());
        } else {
            conditionDataHolders.addAll(singleCellAnalysisController.getPreProcessingMap().values());
        }
        
        PlateCondition condition = singleCellMainController.getSelectedCondition();
        List<double[]> datasetCondition = datasetHashMap.get(condition.toString());
        double[] accumdist = datasetCondition.get(0);
        double[] euclid = datasetCondition.get(1);
        //there is a NAN value in the directionality vector for some reason
        double[] direct = datasetCondition.get(2);
        double[] speed = datasetCondition.get(3);    
        Kurtosis kurt = new Kurtosis();
        
        switch(parameter){
            case "0": kurt.setData(accumdist);
            double kurtaccum = kurt.evaluate();
            analysisPanel.getKurtosisPTextField().setText(Double.toString(kurtaccum));
            if (kurtaccum < -0.5){
                analysisPanel.getKurtosisOutcomeADTextField().setText("Platykurtic");
            }
            else if (kurtaccum > 0.5){
                analysisPanel.getKurtosisOutcomeADTextField().setText("Leptokurtic"); 
            }
            else {
                analysisPanel.getKurtosisOutcomeADTextField().setText("Mesokurtic");
            }
            break;
            case "1": kurt.setData(euclid);
            double kurteuclid = kurt.evaluate();
            analysisPanel.getKurtosisPTextField1().setText(Double.toString(kurteuclid));
            if (kurteuclid < -0.5){
                analysisPanel.getKurtosisOutcomeTextField1().setText("Platykurtic");
            }
            else if (kurteuclid > 0.5){
                analysisPanel.getKurtosisOutcomeTextField1().setText("Leptokurtic"); 
            }
            else {
                analysisPanel.getKurtosisOutcomeTextField1().setText("Mesokurtic");
            }
            break;
            case "2" : kurt.setData(direct);
            double kurtdirect = kurt.evaluate();
            analysisPanel.getKurtosisPTextField3().setText(Double.toString(kurtdirect));
            if (kurtdirect < -0.5){
                analysisPanel.getKurtosisOutcomeTextField3().setText("Platykurtic");
            }
            else if (kurtdirect > 0.5){
                analysisPanel.getKurtosisOutcomeTextField3().setText("Leptokurtic"); 
            }
            else {
                analysisPanel.getKurtosisOutcomeTextField3().setText("Mesokurtic");
            }
            break;
            case "3" : kurt.setData(speed);
            double kurtspeed = kurt.evaluate();
            analysisPanel.getKurtosisPTextField2().setText(Double.toString(kurtspeed));
            if (kurtspeed < -0.5){
                analysisPanel.getKurtosisOutcomeTextField2().setText("Platykurtic");
            }
            else if (kurtspeed > 0.5){
                analysisPanel.getKurtosisOutcomeTextField2().setText("Leptokurtic"); 
            }
            else {
                analysisPanel.getKurtosisOutcomeTextField2().setText("Mesokurtic");
            }
            break;
        }
    }
    
    /**
     * Fill in condition textfields
     */
    
    public void fillInConditionTextFields(){
        AnalysisPanel analysisPanel = singleCellAnalysisController.getAnalysisPanel();
        PlateCondition condition = singleCellMainController.getSelectedCondition();
        
        analysisPanel.getAccumulatedConditionTextField().setText(condition.toString());
        analysisPanel.getEuclidianConditionTextField().setText(condition.toString());    
        analysisPanel.getDirectionalityConditionTextField().setText(condition.toString());           
        analysisPanel.getSpeedConditionTextField().setText(condition.toString());
            
        }
    
}

