/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.area.AreaDoseResponseController;
import be.ugent.maf.cellmissy.analysis.area.AreaUnitOfMeasurement;
import be.ugent.maf.cellmissy.analysis.factory.DistanceMetricFactory;
import be.ugent.maf.cellmissy.analysis.factory.KernelDensityEstimatorFactory;
import be.ugent.maf.cellmissy.analysis.area.MeasuredAreaType;
import be.ugent.maf.cellmissy.analysis.factory.OutliersHandlerFactory;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisHolder;
import be.ugent.maf.cellmissy.entity.result.area.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.result.TimeInterval;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Treatment;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.entity.result.area.AreaAnalysisResults;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.AreaAnalysisInfoDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.MetadataAreaPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.AnalysisGroupListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.AreaUnitOfMeasurementComboBoxRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main controller for area analysis
 *
 * @author Paola Masuzzo
 */
@Controller("areaMainController")
public class AreaMainController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AreaMainController.class);
    //model
    private Experiment experiment;
    private AreaAnalysisHolder areaAnalysisHolder;
    private PlateCondition currentCondition;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Format format;
    private double[] analysisTimeFrames;
    private String outliersHandlerBeanName;
    private String kernelDensityEstimatorBeanName;
    private String distanceMetricBeanName;
    //view
    private AnalysisExperimentPanel analysisExperimentPanel;
    private MetadataAreaPanel metaDataAreaPanel;
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    private AreaAnalysisInfoDialog areaAnalysisInfoDialog;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private AreaPreProcessingController areaPreProcessingController;
    @Autowired
    private AreaAnalysisController areaAnalysisController;
    @Autowired
    private AreaDoseResponseController doseResponseController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private WellService wellService;
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * Initialize controller
     */
    public void init() {
        //init view
        analysisExperimentPanel = new AnalysisExperimentPanel();
        metaDataAreaPanel = new MetadataAreaPanel();
        areaAnalysisInfoDialog = new AreaAnalysisInfoDialog(cellMissyController.getCellMissyFrame());
        // set icon for info label
        Icon informationIcon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledInfoIcon = GuiUtils.getScaledIcon(informationIcon);
        metaDataAreaPanel.getInfoLabel().setIcon(scaledInfoIcon);
        metaDataAreaPanel.getInfoLabel1().setIcon(scaledInfoIcon);
        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        metaDataAreaPanel.getQuestionButton().setIcon(scaledQuestionIcon);
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
        areaAnalysisHolder = new AreaAnalysisHolder();
        //init child controllers
        areaPreProcessingController.init();
        areaAnalysisController.init();
        doseResponseController.init();
        // init other view
        initPlatePanel();
        initMainPanel();
        initMetadataAnalysisPanel();
        initDataAnalysisPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public AnalysisExperimentPanel getAnalysisExperimentPanel() {
        return analysisExperimentPanel;
    }

    public MetadataAreaPanel getMetaDataAreaPanel() {
        return metaDataAreaPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public String getAreaUnitOfMeasurement() {
        return areaAnalysisHolder.getAreaUnitOfMeasurement().getUnitOfMeasurementString();
    }

    public String getMeasuredAreaType() {
        return areaAnalysisHolder.getMeasuredAreaType().getStringForType();
    }

    public PlateCondition getCurrentCondition() {
        return currentCondition;
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public double[] getAnalysisTimeFrames() {
        return analysisTimeFrames;
    }

    public Format getFormat() {
        return format;
    }

    public AreaAnalysisPanel getAreaAnalysisPanel() {
        return areaPreProcessingController.getAreaAnalysisPanel();
    }

    public Map<PlateCondition, AreaPreProcessingResults> getPreProcessingMap() {
        return areaPreProcessingController.getPreProcessingMap();
    }

    public JFreeChart createGlobalAreaChart(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, MeasuredAreaType measuredAreaType) {
        return areaPreProcessingController.createGlobalAreaChart(plateConditionList, useCorrectedData, plotErrorBars, plotLines, plotPoints, measuredAreaType);
    }

    public JFreeChart createGlobalAreaChartInTimeInterval(List<PlateCondition> plateConditionList, boolean useCorrectedData, boolean plotErrorBars, boolean plotLines, boolean plotPoints, MeasuredAreaType measuredAreaType) {
        return areaPreProcessingController.createGlobalAreaChartInTimeInterval(plateConditionList, useCorrectedData, plotErrorBars, plotLines, plotPoints, measuredAreaType);
    }

    public JFreeChart createRawAreaChart(PlateCondition plateCondition) {
        return areaPreProcessingController.createRawAreaChart(plateCondition);
    }

    public JFreeChart createCorrectedAreaChart(PlateCondition plateCondition) {
        return areaPreProcessingController.createCorrectedAreaChart(plateCondition);
    }

    public List<PlateCondition> getProcessedConditions() {
        return areaPreProcessingController.getProcessedConditions();
    }

    public AreaAnalysisHolder getAreaAnalysisHolder() {
        return areaAnalysisHolder;
    }

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
    }

    public void onButtonsState(boolean isEnable) {
        analysisExperimentPanel.getPreviousButton().setEnabled(isEnable);
        analysisExperimentPanel.getNextButton().setEnabled(isEnable);
        analysisExperimentPanel.getCancelButton().setEnabled(isEnable);
    }

    public Algorithm getSelectedALgorithm() {
        return algorithmBindingList.get(metaDataAreaPanel.getAlgorithmComboBox().getSelectedIndex());
    }

    public ImagingType getSelectedImagingType() {
        return imagingTypeBindingList.get(metaDataAreaPanel.getImagingTypeComboBox().getSelectedIndex());
    }

    public String getOutliersHandlerBeanName() {
        return outliersHandlerBeanName;
    }

    void setOutliersHandlerBeanName(String outliersHandlerBeanName) {
        this.outliersHandlerBeanName = outliersHandlerBeanName;
    }

    public String getKernelDensityEstimatorBeanName() {
        return kernelDensityEstimatorBeanName;
    }

    void setKernelDensityEstimatorBeanName(String kernelDensityEstimatorBeanName) {
        this.kernelDensityEstimatorBeanName = kernelDensityEstimatorBeanName;
    }

    public String getDistanceMetricBeanName() {
        return distanceMetricBeanName;
    }

    void setDistanceMetricBeanName(String distanceMetricBeanName) {
        this.distanceMetricBeanName = distanceMetricBeanName;
    }
    
    //Dose-response input needs this mapping to initialize DR analysis group
    public Map<PlateCondition, AreaAnalysisResults> getLinearResultsAnalysisMap() {
        return areaAnalysisController.getAnalysisMap();
    }
    
    public List<Integer> getNumberOfReplicates() {
        return areaPreProcessingController.getNumberOfReplicates();
    }

    /**
     * Check if current analysis has been saved before leaving the view
     *
     * @return
     */
    public boolean analysisWasStarted() {
        return experiment != null;
    }

    /**
     * Called in the main controller, reset views and models if another view has
     * being shown
     */
    public void resetAfterCardSwitch() {
        onCancel();
    }

    /**
     * Fetch time steps objects from DB, update TimeStepList according to Plate
     * Condition
     *
     * @param plateCondition
     */
    public void fetchConditionTimeSteps(PlateCondition plateCondition) {
        //fetch time steps for each well
        for (int i = 0; i < plateCondition.getWellList().size(); i++) {
            //fetch time step collection for the wellhasimagingtype of interest
            wellService.fetchTimeSteps(plateCondition.getWellList().get(i), algorithmBindingList.get(metaDataAreaPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(metaDataAreaPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
        }
        //update timeStep List for current selected condition
        updateTimeStepsList(plateCondition);
    }

    /**
     * Set cursor from main controller
     *
     * @param type
     */
    public void setCursor(int type) {
        cellMissyController.setCursor(Cursor.getPredefinedCursor(type));
    }

    /**
     * Show Linear regression results from child controller
     *
     * @param useCorrectedData
     */
    public void showLinearModelInTable(boolean useCorrectedData) {
        areaAnalysisController.showLinearModelInTable(useCorrectedData);
    }

    /**
     * Update information on time frames and corrected data in analysis
     */
    public void updateAnalysisInfo() {
        areaAnalysisController.updateAnalysisInfo();
    }

    /**
     * Show message through the main controller
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        cellMissyController.showMessage(message, title, messageType);
    }

    public void handleUnexpectedError(Exception ex) {
        cellMissyController.handleUnexpectedError(ex);
    }

    /**
     * The condition is loaded and plate view is refreshed with not imaged wells
     * highlighted in gray
     *
     * @param plateCondition
     */
    public void showNotImagedWells(PlateCondition plateCondition) {
        plateCondition.setLoaded(true);
        analysisPlatePanel.repaint();
    }

    /**
     * Show the wells analysed for current condition: put a star inside each
     * well.
     *
     * @param plateCondition
     */
    void showWellsForCurrentCondition(PlateCondition plateCondition) {
        analysisPlatePanel.setCurrentCondition(plateCondition);
        analysisPlatePanel.repaint();
    }

    /**
     * The user has decided or not to work with corrected data?
     *
     * @return
     */
    public boolean useCorrectedData() {
        return areaPreProcessingController.getAreaAnalysisPanel().getUseCorrectedDataCheckBox().isSelected();
    }

    /**
     * This is computing the minimum time frame in order to proceed with
     * analysis
     */
    public void computeAnalysisTimeFrames() {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < experiment.getTimeFrames(); i++) {
            double timeFrame = i * experiment.getExperimentInterval();
            list.add(timeFrame);
        }
        int analysisFirstTimeFrame = 0;
        int analysisLastTimeFrame = list.size();

        Map<PlateCondition, AreaPreProcessingResults> preProcessingMap = areaPreProcessingController.getPreProcessingMap();
        for (PlateCondition plateCondition : getProcessedConditions()) {
            AreaPreProcessingResults areaPreProcessingResults = preProcessingMap.get(plateCondition);
            TimeInterval timeInterval = areaPreProcessingResults.getTimeInterval();
            int firstTimeFrame = timeInterval.getFirstTimeFrame();
            int lastTimeFrame = timeInterval.getLastTimeFrame();
            if (firstTimeFrame > analysisFirstTimeFrame) {
                analysisFirstTimeFrame = firstTimeFrame;
            }
            if (lastTimeFrame < analysisLastTimeFrame) {
                analysisLastTimeFrame = lastTimeFrame;
            }
        }
        analysisTimeFrames = new double[analysisLastTimeFrame - analysisFirstTimeFrame + 1];
        for (int i = 0; i < analysisTimeFrames.length; i++) {
            analysisTimeFrames[i] = list.get(i + analysisFirstTimeFrame);
        }
    }

    /**
     * private methods and classes
     */
    /**
     * initialize plate panel view
     */
    private void initPlatePanel() {
        //show as default a 96 plate format
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        dataAnalysisPanel.getAnalysisPlateParentPanel().add(analysisPlatePanel, gridBagConstraints);
        dataAnalysisPanel.getAnalysisPlateParentPanel().repaint();
    }

    /**
     * update information message in the bottom panel
     *
     * @param messageToShow
     */
    private void updateInfoMessage(String messageToShow) {
        cellMissyController.updateInfoLabel(analysisExperimentPanel.getInfoLabel(), messageToShow);
    }

    /**
     * get Card Layout
     *
     * @return
     */
    private CardLayout getCardLayout() {
        return (CardLayout) areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel().getLayout();
    }

    /**
     * Check for card name when switching
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
        switch (currentCardName) {
            case "dataInspectingPanel":
                // disable previous button
                analysisExperimentPanel.getPreviousButton().setEnabled(false);
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                // enable or disable the converted table in the tabbed pane
                AreaUnitOfMeasurement areaUnitOfMeasurement = (AreaUnitOfMeasurement) metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().getSelectedItem();
                if (areaUnitOfMeasurement.equals(AreaUnitOfMeasurement.PIXELS) | areaUnitOfMeasurement.equals(AreaUnitOfMeasurement.SPECIAL_MICRO_METERS)) {
                    areaPreProcessingController.getAreaAnalysisPanel().getDataInspectingTabbedPane().setEnabledAt(1, true);
                } else {
                    areaPreProcessingController.getAreaAnalysisPanel().getDataInspectingTabbedPane().setEnabledAt(1, false);
                }
                // enable conditions list
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                dataAnalysisPanel.getConditionsList().setSelectedIndex(plateConditionList.indexOf(currentCondition));
                analysisPlatePanel.setCurrentCondition(currentCondition);
                analysisPlatePanel.repaint();
                GuiUtils.highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getDoseResponseLabel());                
                updateInfoMessage("Area values are shown for each well, together with (column, row) coordinates");
                break;
            case "preprocessingPanel":
                boolean proceedToAnalysis = areaPreProcessingController.isProceedToAnalysis();
                analysisExperimentPanel.getNextButton().setEnabled(proceedToAnalysis);
                // enable conditions list
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                dataAnalysisPanel.getConditionsList().setSelectedIndex(plateConditionList.indexOf(currentCondition));
                analysisPlatePanel.setCurrentCondition(currentCondition);
                analysisPlatePanel.repaint();
                // cell covered area radio button is not visible if area is already a cell covered one
                if (areaAnalysisHolder.getMeasuredAreaType().equals(MeasuredAreaType.CELL_COVERED_AREA)) {
                    areaPreProcessingController.getAreaAnalysisPanel().getCellCoveredAreaRadioButton().setVisible(false);
                } else {
                    areaPreProcessingController.getAreaAnalysisPanel().getCellCoveredAreaRadioButton().setVisible(true);
                }
                GuiUtils.highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getDoseResponseLabel());                
                updateInfoMessage("Area values are normalized and outliers correction is performed (see %Area increase)");
                break;
            case "globalViewPanel":
                areaPreProcessingController.onGlobalView();
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                // disable conditions list
                dataAnalysisPanel.getConditionsList().setEnabled(false);
                dataAnalysisPanel.getConditionsList().clearSelection();
                analysisPlatePanel.setCurrentCondition(null);
                analysisPlatePanel.repaint();
                GuiUtils.highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getDoseResponseLabel());
                updateInfoMessage("Temporal evolution of the area is plotted for each biological condition");
                break;
            case "linearModelPanel":
                // if dose-response analysis is possible enable next button, otherwise disable
                if (DoseResponsePossible()) {
                    analysisExperimentPanel.getNextButton().setEnabled(true);
                } else {
                    analysisExperimentPanel.getNextButton().setEnabled(false);
                }
                // disable conditions list
                dataAnalysisPanel.getConditionsList().setEnabled(false);
                dataAnalysisPanel.getConditionsList().clearSelection();
                analysisPlatePanel.setCurrentCondition(null);
                analysisPlatePanel.repaint();
                areaPreProcessingController.onLinearRegressionModel();
                GuiUtils.highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getDoseResponseLabel());
                updateInfoMessage("Choose conditions from the linear regression table and assign them to a group to perform statistics");
                break;
            case "doseResponseParentPanel":
                doseResponseController.onDoseResponse();
                // disable next button
                analysisExperimentPanel.getNextButton().setEnabled(false);
                // disable conditions list
                dataAnalysisPanel.getConditionsList().setEnabled(false);
                dataAnalysisPanel.getConditionsList().clearSelection();
                analysisPlatePanel.setCurrentCondition(null);
                analysisPlatePanel.repaint();
                GuiUtils.highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getDoseResponseLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                GuiUtils.resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                updateInfoMessage("Fit the velocities of selected conditions to a dose-response model to quantify the effect of a drug");
                break;
        }
    }

    /**
     * Initialize main panel
     */
    private void initMainPanel() {
        // be sure buttons are disabled at the beginning
        analysisExperimentPanel.getStartButton().setEnabled(false);
        analysisExperimentPanel.getNextButton().setEnabled(false);
        analysisExperimentPanel.getPreviousButton().setEnabled(false);
        analysisExperimentPanel.getCancelButton().setEnabled(false);
        //hide progress bar at first time
        analysisExperimentPanel.getFetchAllConditionsProgressBar().setVisible(false);
        analysisExperimentPanel.getFetchAllConditionsProgressBar().setStringPainted(true);
        String message = "Select a project and an experiment to start with data analysis.";
        updateInfoMessage(message);
        // action listener on start button: this is switching the views in order to start the analysis
        analysisExperimentPanel.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analysisExperimentPanel.getStartButton().setEnabled(false);
                analysisExperimentPanel.getCancelButton().setEnabled(true);
                // switch between the two panels
                GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), dataAnalysisPanel, metaDataAreaPanel);
                analysisExperimentPanel.getTopPanel().repaint();
                analysisExperimentPanel.getTopPanel().revalidate();
                getCardLayout().first(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
                onCardSwitch();
                // update experiment info
                dataAnalysisPanel.getExperimentNumberTextField().setText(experiment.toString());
                dataAnalysisPanel.getTimeFramesNumberTextField().setText("" + experiment.getTimeFrames());
                dataAnalysisPanel.getDatasetTextField().setText(algorithmBindingList.get(metaDataAreaPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmName());
                dataAnalysisPanel.getImagingTypeTextField().setText(imagingTypeBindingList.get(metaDataAreaPanel.getImagingTypeComboBox().getSelectedIndex()).getName());
                updateInfoMessage("Select a condition to start with analysis");
            }
        });

        // action listener on previous button
        analysisExperimentPanel.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go back of one step
                getCardLayout().previous(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
                onCardSwitch();
            }
        });

        // action listener on next button
        analysisExperimentPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go forward of one step
                getCardLayout().next(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
                onCardSwitch();
                if (!analysisExperimentPanel.getPreviousButton().isEnabled()) {
                    analysisExperimentPanel.getPreviousButton().setEnabled(true);
                }
            }
        });

        // action listener on cancel button
        analysisExperimentPanel.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // warn the user and reset everything
                Object[] options = {"Yes", "No"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "Current analysis won't be saved. Continue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                if (showOptionDialog == 0) {
                    // reset everything
                    onCancel();
                }
            }
        });

        cellMissyController.getCellMissyFrame().getAreaAnalysisParentPanel().add(analysisExperimentPanel, gridBagConstraints);
    }

    /**
     * On cancel: reset views
     */
    private void onCancel() {
        areaPreProcessingController.resetOnCancel();
        areaAnalysisController.resetOnCancel();
        doseResponseController.resetOnCancel();
        String message = "Please select a project and an experiment to analyse motility data.";
        updateInfoMessage(message);
        algorithmBindingList.clear();
        imagingTypeBindingList.clear();
        // clear plate conditions list, if not null
        if (plateConditionList != null) {
            plateConditionList.clear();
        }
        areaAnalysisController.getGroupsBindingList().clear();
        // clear selection on lists
        metaDataAreaPanel.getProjectsList().clearSelection();
        metaDataAreaPanel.getExperimentsList().clearSelection();
        // set text area to empty field
        metaDataAreaPanel.getProjectDescriptionTextArea().setText("");
        areaPreProcessingController.getPreProcessingMap().clear();
        currentCondition = null;
        experiment = null;
        areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().setSelected(true);
        GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), metaDataAreaPanel, dataAnalysisPanel);
        analysisExperimentPanel.getTopPanel().repaint();
        analysisExperimentPanel.getTopPanel().revalidate();
        resetExperimentMetadataFields();
        analysisExperimentPanel.getStartButton().setEnabled(false);
        analysisExperimentPanel.getNextButton().setEnabled(false);
        analysisExperimentPanel.getCancelButton().setEnabled(false);
        analysisExperimentPanel.getPreviousButton().setEnabled(false);
        if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
            experimentBindingList.clear();
        }
        if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
            areaPreProcessingController.getTimeStepsBindingList().clear();
        }
    }

    /**
     * Reset text of experiment metadata fields
     */
    private void resetExperimentMetadataFields() {
        metaDataAreaPanel.getUserTextField().setText("");
        metaDataAreaPanel.getInstrumentTextField().setText("");
        metaDataAreaPanel.getPurposeTextArea().setText("");
        metaDataAreaPanel.getTimeFramesTextField().setText("");
    }

    /**
     * Initialize metadata analysis panel
     */
    private void initMetadataAnalysisPanel() {
        metaDataAreaPanel.getPurposeTextArea().setLineWrap(true);
        metaDataAreaPanel.getPurposeTextArea().setWrapStyleWord(true);
        metaDataAreaPanel.getProjectDescriptionTextArea().setLineWrap(true);
        metaDataAreaPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);

        //init projects list
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, metaDataAreaPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, algorithmBindingList, metaDataAreaPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, imagingTypeBindingList, metaDataAreaPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        // add area unit of measure to combo box
        for (AreaUnitOfMeasurement areaUnitOfMeasurement : AreaUnitOfMeasurement.values()) {
            metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().addItem(areaUnitOfMeasurement);
        }

        // choose area unit of measurement
        metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AreaUnitOfMeasurement areaUnitOfMeasurement = (AreaUnitOfMeasurement) metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().getSelectedItem();
                areaAnalysisHolder.setAreaUnitOfMeasurement(areaUnitOfMeasurement);
            }
        });

        metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().setRenderer(new AreaUnitOfMeasurementComboBoxRenderer());
        // set default unit of measurement: micro meters
        metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().setSelectedItem(AreaUnitOfMeasurement.MICRO_METERS);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project
        metaDataAreaPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = metaDataAreaPanel.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        if (experiment == null || !selectedProject.equals(experiment.getProject()) || experimentBindingList.isEmpty()) {
                            // project is being selected for the first time
                            onSelectedProject(selectedProject);
                        }
                    }
                }
            }
        });


        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions setup
        metaDataAreaPanel.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected experiment
                    int selectedIndex = metaDataAreaPanel.getExperimentsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Experiment selectedExperiment = experimentBindingList.get(selectedIndex);
                        if (experiment == null || !selectedExperiment.equals(experiment)) {
                            onSelectedExperiment(selectedExperiment);
                        }
                    }
                }
            }
        });

        // bind information fields
        // exp user
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metaDataAreaPanel.getExperimentsList(), BeanProperty.create("selectedElement.user.firstName"), metaDataAreaPanel.getUserTextField(), BeanProperty.create("text"), "experimentuserbinding");
        bindingGroup.addBinding(binding);
        // exp purpose
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metaDataAreaPanel.getExperimentsList(), BeanProperty.create("selectedElement.purpose"), metaDataAreaPanel.getPurposeTextArea(), BeanProperty.create("text"), "experimentpurposebinding");
        bindingGroup.addBinding(binding);
        // instrument
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metaDataAreaPanel.getExperimentsList(), BeanProperty.create("selectedElement.instrument.name"), metaDataAreaPanel.getInstrumentTextField(), BeanProperty.create("text"), "instrumentbinding");
        bindingGroup.addBinding(binding);
        // exp time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metaDataAreaPanel.getExperimentsList(), BeanProperty.create("selectedElement.timeFrames"), metaDataAreaPanel.getTimeFramesTextField(), BeanProperty.create("text"), "experimentimeframesbinding");
        bindingGroup.addBinding(binding);
        // do the binding
        bindingGroup.bind();

        // button group for radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(metaDataAreaPanel.getCellCoveredAreaRadioButton());
        buttonGroup.add(metaDataAreaPanel.getOpenAreaRadioButton());

        /**
         * item listeners to the radio buttons
         */
        metaDataAreaPanel.getCellCoveredAreaRadioButton().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.CELL_COVERED_AREA);
                } else {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.OPEN_AREA);
                }
            }
        });

        metaDataAreaPanel.getOpenAreaRadioButton().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.OPEN_AREA);
                } else {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.CELL_COVERED_AREA);
                }
            }
        });

        // add the analysis preferences to the comboboxes
        // these values are read from the spring XML config file
        // get all the outliers correction and detection algoritms from the factory
        Set<String> outliersHandlersBeanNames = OutliersHandlerFactory.getInstance().getOutliersHandlersBeanNames();
        for (String outliersAlgorithm : outliersHandlersBeanNames) {
            metaDataAreaPanel.getOutliersAlgorithmsComboBox().addItem(outliersAlgorithm);
        }

        // add the action listener
        metaDataAreaPanel.getOutliersAlgorithmsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOutliersAlgorithm = metaDataAreaPanel.getOutliersAlgorithmsComboBox().getSelectedItem().toString();
                setOutliersHandlerBeanName(selectedOutliersAlgorithm);
            }
        });
        // set as default the first one
        metaDataAreaPanel.getOutliersAlgorithmsComboBox().setSelectedIndex(0);

        // do exactly the same for the kernel density estimation and the distance metric
        Set<String> kernelDensityEstimatorsBeanNames = KernelDensityEstimatorFactory.getInstance().getKernelDensityEstimatorsBeanNames();
        for (String estimatorName : kernelDensityEstimatorsBeanNames) {
            metaDataAreaPanel.getKernelDensityEstimatorsComboBox().addItem(estimatorName);
        }

        // add the action listener
        metaDataAreaPanel.getKernelDensityEstimatorsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedKDEAlgorithm = metaDataAreaPanel.getKernelDensityEstimatorsComboBox().getSelectedItem().toString();
                setKernelDensityEstimatorBeanName(selectedKDEAlgorithm);
            }
        });
        // set as default the first one
        metaDataAreaPanel.getKernelDensityEstimatorsComboBox().setSelectedIndex(0);

        Set<String> distanceMetricsBeanNames = DistanceMetricFactory.getInstance().getDistanceMetricsBeanNames();
        for (String distanceMetricName : distanceMetricsBeanNames) {
            metaDataAreaPanel.getDistanceMetricsComboBox().addItem(distanceMetricName);
        }

        // add the action listener
        metaDataAreaPanel.getDistanceMetricsComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDistanceMetric = metaDataAreaPanel.getDistanceMetricsComboBox().getSelectedItem().toString();
                setDistanceMetricBeanName(selectedDistanceMetric);
            }
        });
        // set as default the first one
        metaDataAreaPanel.getDistanceMetricsComboBox().setSelectedIndex(0);

        // select cell covered area as default
        metaDataAreaPanel.getCellCoveredAreaRadioButton().setSelected(true);

        // add action Listener to the question/info button
        metaDataAreaPanel.getQuestionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pack and show info dialog
                GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), areaAnalysisInfoDialog);
                areaAnalysisInfoDialog.setVisible(true);
            }
        });

        analysisExperimentPanel.getTopPanel().add(metaDataAreaPanel, gridBagConstraints);
    }

    /**
     * Initialize data analysis panel
     */
    private void initDataAnalysisPanel() {
        //when a certain condition is selected, fetch time steps for each well of the condition
        dataAnalysisPanel.getConditionsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = dataAnalysisPanel.getConditionsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        PlateCondition selectedCondition = plateConditionList.get(selectedIndex);
                        if (currentCondition == null || !currentCondition.equals(selectedCondition)) {
                            // Execute Swing Worker to fetch Selected Condition:
                            FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                            fetchSelectedConditionSW.execute();
                        }
                        currentCondition = selectedCondition;
                    }
                }
            }
        });
    }

    /**
     * Action on selected project, find all relative performed experiments, if
     * any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        // clear up imaging and algorithm lists, if not empty
        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }
        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        metaDataAreaPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            Collections.sort(experimentList);
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, metaDataAreaPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
            // check if the user has privileges on the selected project
            // if not, show a message and disable the experiments list
            if (!userHasPrivileges(selectedProject)) {
                String message = "Sorry, you don't have enough privileges for the selected project!";
                cellMissyController.showMessage(message, "no enough privileges", JOptionPane.WARNING_MESSAGE);
                metaDataAreaPanel.getExperimentsList().setEnabled(false);
            } else {
                metaDataAreaPanel.getExperimentsList().setEnabled(true);
            }
        } else {
            String message = "There are no experiments performed yet for this project!";
            cellMissyController.showMessage(message, "no experiments found", JOptionPane.INFORMATION_MESSAGE);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }

    /**
     * Does the current user have privileges on the current project?
     *
     * @param project
     * @return true or false
     */
    private boolean userHasPrivileges(Project project) {
        boolean hasPrivileges = false;
        // get current user from main controller
        User currentUser = cellMissyController.getCurrentUser();
        // check for his/her role
        // ADMIN user: return true
        if (currentUser.getRole().equals(Role.ADMIN_USER)) {
            hasPrivileges = true;
        } else {
            // we have a STANDARD user
            // we need to check if he's involved in the selected project
            for (ProjectHasUser projectHasUser : project.getProjectHasUserList()) {
                if (projectHasUser.getUser().equals(currentUser)) {
                    hasPrivileges = true;
                    break;
                }
            }
        }
        return hasPrivileges;
    }

    /**
     * Action on selected experiment, retrieve plate conditions and repaint
     * plate panel.
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        proceedToAnalysis(selectedExperiment);
    }

    /**
     * @param experimentToAnalyze
     */
    private void proceedToAnalysis(Experiment experimentToAnalyze) {
        // clear current lists
        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }

        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // set experiment
        experiment = experimentToAnalyze;
        // init a new list of plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(experiment.getPlateConditionList());
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(experiment.getPlateFormat(), parentDimension);
        // repaint plate panel
        analysisPlatePanel.setExperiment(experiment);
        //dataAnalysisPanel.getAnalysisPlateParentPanel().repaint();
        analysisPlatePanel.repaint();
        //show conditions JList
        showConditionsList();
        // cell renderer
        areaAnalysisController.getLinearRegressionPanel().getGroupsList().setCellRenderer(new AnalysisGroupListRenderer(plateConditionList));
        // show algorithms and imaging types
        for (PlateCondition plateCondition : plateConditionList) {
            for (Well well : plateCondition.getWellList()) {
                List<Algorithm> algorithms = wellService.findAlgosByWellId(well.getWellid());
                if (algorithms != null) {
                    for (Algorithm algorithm : algorithms) {
                        if (!algorithmBindingList.contains(algorithm)) {
                            algorithmBindingList.add(algorithm);
                        }
                    }
                }

                List<ImagingType> imagingTypes = wellService.findImagingTypesByWellId(well.getWellid());
                if (imagingTypes != null) {
                    for (ImagingType imagingType : imagingTypes) {
                        if (imagingType != null && !imagingTypeBindingList.contains(imagingType)) {
                            imagingTypeBindingList.add(imagingType);
                        }
                    }
                }
            }
        }
        //init map with conditions and results holders
        areaPreProcessingController.initMapWithConditions();
        //set selected algorithm to the first of the list
        metaDataAreaPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        metaDataAreaPanel.getImagingTypeComboBox().setSelectedIndex(0);
        // enable start button
        analysisExperimentPanel.getStartButton().setEnabled(true);
    }

    /**
     * update conditions list for current experiment
     */
    private void showConditionsList() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsAnalysisListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Update time steps list with objects from actual selected condition
     *
     * @param plateCondition
     */
    private void updateTimeStepsList(PlateCondition plateCondition) {
        //clear the actual timeStepList
        if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
            areaPreProcessingController.getTimeStepsBindingList().clear();
        }
        // get only the wells that have been imaged
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well well : imagedWells) {
            List<WellHasImagingType> wellHasImagingTypeList = well.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                List<TimeStep> timeStepList = wellHasImagingType.getTimeStepList();
                for (TimeStep timeStep : timeStepList) {
                    areaPreProcessingController.getTimeStepsBindingList().add(timeStep);
                }
            }
        }
    }
    
    /**
     * Check if dose-response analysis is possible for the current experiment.
     * @return True if at least one condition has a treatment of category 1
     */
    private boolean DoseResponsePossible() {
        for (PlateCondition plateCondition : plateConditionList) {
            for (Treatment treatment : plateCondition.getTreatmentList()) {
                if (treatment.getTreatmentType().getTreatmentCategory() == 1) {
                    return true;
                }
            }
        }
        return false;
    }            

    /**
     * Swing Worker to fetch one condition time steps at once: The user selects
     * a condition, a waiting cursor is shown on the screen and time steps
     * result are fetched from DB. List of time steps is updated. In addition,
     * map of child controller is updated: computations are performed here and
     * then shown in the done method of the class.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dataAnalysisPanel.getConditionsList().setEnabled(false);
            analysisExperimentPanel.getNextButton().setEnabled(false);
            analysisExperimentPanel.getCancelButton().setEnabled(false);
            analysisExperimentPanel.getPreviousButton().setEnabled(false);
            // disable buttons as well
            List<Well> wellList = currentCondition.getWellList();
            //fetch time steps for each well of condition
            for (Well aWellList : wellList) {
                //fetch time step collection for the wellhasimagingtype of interest
                Algorithm algorithm = algorithmBindingList.get(metaDataAreaPanel.getAlgorithmComboBox().getSelectedIndex());
                ImagingType imagingType = imagingTypeBindingList.get(metaDataAreaPanel.getImagingTypeComboBox().getSelectedIndex());
                wellService.fetchTimeSteps(aWellList, algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
            }
            // when all wells were fetched, update TimeStepList
            updateTimeStepsList(currentCondition);
            // if time steps were actually fetched from DB, update map
            if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
                //put the plate condition together with a pre-processing results holder in the map
                areaPreProcessingController.updateMapWithCondition(currentCondition);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                dataAnalysisPanel.getConditionsList().setEnabled(true);
                dataAnalysisPanel.getConditionsList().requestFocusInWindow();
                analysisExperimentPanel.getNextButton().setEnabled(areaPreProcessingController.isProceedToAnalysis());
                analysisExperimentPanel.getCancelButton().setEnabled(true);
                analysisExperimentPanel.getPreviousButton().setEnabled(true);
                if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
                    //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO look at motility track RESULTS
                    areaPreProcessingController.showTimeStepsInTable();
                    // if the area unit of measurement is pixel or cellM µm², we show also the converted area values
                    AreaUnitOfMeasurement areaUnitOfMeasurement = (AreaUnitOfMeasurement) metaDataAreaPanel.getAreaUnitOfMeasurementComboBox().getSelectedItem();
                    if (areaUnitOfMeasurement.equals(AreaUnitOfMeasurement.PIXELS) | areaUnitOfMeasurement.equals(AreaUnitOfMeasurement.SPECIAL_MICRO_METERS)) {
                        areaPreProcessingController.showConvertedAreaInTable();
                    }
                    onCardSwitch();
                    //check which button is selected for analysis:
                    if (areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().isSelected()) {
                        //for current selected condition show normalized area values together with time frames
                        areaPreProcessingController.showNormalizedAreaInTable(currentCondition);
                        // show raw data plot (all replicates)
                        boolean plotLines = areaPreProcessingController.getRawAreaPanel().getPlotLinesCheckBox().isSelected();
                        boolean plotPoints = areaPreProcessingController.getRawAreaPanel().getPlotPointsCheckBox().isSelected();
                        areaPreProcessingController.plotRawAreaReplicates(currentCondition, plotLines, plotPoints);
                        areaPreProcessingController.showProcessedTimeFrames(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getCellCoveredAreaRadioButton().isSelected()) {
                        //for current selected condition show transformed area values together with time frames
                        areaPreProcessingController.showTransformedDataInTable(currentCondition);
                        // show raw data plot (all replicates)
                        boolean plotLines = areaPreProcessingController.getTransformedAreaPanel().getPlotLinesCheckBox().isSelected();
                        boolean plotPoints = areaPreProcessingController.getTransformedAreaPanel().getPlotPointsCheckBox().isSelected();
                        areaPreProcessingController.plotTransformedAreaReplicates(currentCondition, plotLines, plotPoints);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getDeltaAreaButton().isSelected()) {
                        //for current selected condition show delta area values
                        areaPreProcessingController.showDeltaAreaInTable(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getPercentageAreaIncreaseButton().isSelected()) {
                        //for current selected condition show %increments (for outliers detection)
                        areaPreProcessingController.showAreaIncreaseInTable(currentCondition);
                        //show density function for selected condition (Raw Data)
                        areaPreProcessingController.plotDensityFunctions(currentCondition);
                    }
                    if (areaPreProcessingController.getAreaAnalysisPanel().getCorrectedAreaButton().isSelected()) {
                        // update time frames list for current condition
                        areaPreProcessingController.updateTimeFramesList(currentCondition);
                        //for current selected condition show corrected area values (outliers have been deleted from distribution)
                        areaPreProcessingController.showCorrectedAreaInTable(currentCondition);
                        //show Area increases with time frames
                        boolean plotLines = areaPreProcessingController.getCorrectedAreaPanel().getPlotLinesCheckBox().isSelected();
                        boolean plotPoints = areaPreProcessingController.getCorrectedAreaPanel().getPlotPointsCheckBox().isSelected();
                        boolean showTimeInterval = areaPreProcessingController.getCorrectedAreaPanel().getShowTimeIntervalCheckBox().isSelected();
                        areaPreProcessingController.plotCorrectedArea(currentCondition, plotLines, plotPoints, showTimeInterval);
                    }
                } else {
                    // the entire condition was not imaged/analyzed: inform the user
                    showMessage("This condition was not imaged!", "Condition not imaged", JOptionPane.INFORMATION_MESSAGE);
                    areaPreProcessingController.resetViews();
                }
                // set cursor back to default and show all computed results for selected condition
                cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // the condition is loaded, and plate view is refreshed
                showNotImagedWells(currentCondition);
                showWellsForCurrentCondition(currentCondition);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }
}
