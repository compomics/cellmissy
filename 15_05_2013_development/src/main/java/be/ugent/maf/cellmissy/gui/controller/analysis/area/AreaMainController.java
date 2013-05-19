/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.analysis.AreaUnitOfMeasurement;
import be.ugent.maf.cellmissy.analysis.MeasuredAreaType;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.AreaAnalysisHolder;
import be.ugent.maf.cellmissy.entity.AreaPreProcessingResults;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.TimeInterval;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.AreaAnalysisPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.OverviewExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.area.MetadataAreaPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.AreaUnitOfMeasurementComboBoxRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
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
    //view
    private AnalysisExperimentPanel analysisExperimentPanel;
    private OverviewExperimentPanel overviewExperimentPanel;
    private MetadataAreaPanel metadataAreaPanel;
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private AreaPreProcessingController areaPreProcessingController;
    @Autowired
    private AreaAnalysisController areaAnalysisController;
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
        //init views
        analysisExperimentPanel = new AnalysisExperimentPanel();
        overviewExperimentPanel = new OverviewExperimentPanel();
        metadataAreaPanel = new MetadataAreaPanel();
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        overviewExperimentPanel.getInfoLabel().setIcon(scaledIcon);
        metadataAreaPanel.getInfoLabel1().setIcon(scaledIcon);
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
        areaAnalysisHolder = new AreaAnalysisHolder();
        //init child controllers
        areaPreProcessingController.init();
        areaAnalysisController.init();
        // init other views
        initPlatePanel();
        initMainPanel();
        initMetadataAreaPanel();
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

    public Algorithm getSelectedAlgorithm() {
        return algorithmBindingList.get(metadataAreaPanel.getAlgorithmComboBox().getSelectedIndex());
    }

    public ImagingType getSelectedImagingType() {
        return imagingTypeBindingList.get(metadataAreaPanel.getImagingTypeComboBox().getSelectedIndex());
    }

    /**
     * Check if current analysis has been saved before leaving the view
     *
     * @return
     */
    public boolean analysisWasStarted() {
        if (experiment != null) {
            return true;
        } else {
            return false;
        }
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
        List<Well> wellList = new ArrayList<>();
        wellList.addAll(plateCondition.getWellList());
        Algorithm selectedAlgorithm = getSelectedAlgorithm();
        ImagingType selectedImagingType = getSelectedImagingType();
        //fetch time steps for each well
        for (int i = 0; i < wellList.size(); i++) {
            //fetch time step collection for the wellhasimagingtype of interest
            wellService.fetchTimeSteps(wellList.get(i), selectedAlgorithm.getAlgorithmid(), selectedImagingType.getImagingTypeid());
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
     * Initialize plate panel view
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
    private void showInfoMessage(String messageToShow) {
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
     * Highlight label (both color and size)
     *
     * @param label
     */
    private void highlightLabel(JLabel label) {
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setForeground(new Color(72, 61, 169));
    }

    /**
     * Reset label (both size and color)
     *
     * @param label
     */
    private void resetLabel(JLabel label) {
        label.setFont(new Font("Tahoma", Font.PLAIN, 12));
        label.setForeground(GuiUtils.getDefaultColor());
    }

    /**
     * Check for card name when switching
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
        switch (currentCardName) {
            case "resultsImporterPanel":
                // disable previous button
                analysisExperimentPanel.getPreviousButton().setEnabled(false);
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                showInfoMessage("Area values are shown for each well, together with (column, row) coordinates");
                break;
            case "preProcessingPanel":
                boolean proceedToAnalysis = areaPreProcessingController.isProceedToAnalysis();
                analysisExperimentPanel.getNextButton().setEnabled(proceedToAnalysis);
                // cell covered area radio button is not visible if area is already a cell covered one
                if (areaAnalysisHolder.getMeasuredAreaType().equals(MeasuredAreaType.CELL_COVERED_AREA)) {
                    areaPreProcessingController.getAreaAnalysisPanel().getCellCoveredAreaRadioButton().setVisible(false);
                } else {
                    areaPreProcessingController.getAreaAnalysisPanel().getCellCoveredAreaRadioButton().setVisible(true);
                }
                highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                showInfoMessage("Data are normalized and outliers correction is performed");
                break;
            case "globalViewPanel":
                areaPreProcessingController.onGlobalView();
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getResultsImportingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                showInfoMessage("Temporal evolution of the area is plotted for each condition");
                break;
            case "linearModelPanel":
                // disable next button
                analysisExperimentPanel.getNextButton().setEnabled(false);
                areaPreProcessingController.onLinearRegressionModel();
                highlightLabel(areaPreProcessingController.getAreaAnalysisPanel().getLinearRegressionModelLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getGlobalViewLabel());
                resetLabel(areaPreProcessingController.getAreaAnalysisPanel().getPreProcessingLabel());
                showInfoMessage("Choose conditions from the linerar regression table and assign them to a group to perform analysis");
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
        String message = "Please select a project and an experiment to analyse motility data.";
        showInfoMessage(message);
        // action listener on start button: this is switching the views in order to start the analysis
        analysisExperimentPanel.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analysisExperimentPanel.getStartButton().setEnabled(false);
                analysisExperimentPanel.getCancelButton().setEnabled(true);
                // switch between the two panels
                GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), dataAnalysisPanel, overviewExperimentPanel);
                analysisExperimentPanel.getTopPanel().repaint();
                analysisExperimentPanel.getTopPanel().revalidate();
                getCardLayout().first(areaPreProcessingController.getAreaAnalysisPanel().getBottomPanel());
                onCardSwitch();
                // update experiment info
                dataAnalysisPanel.getExperimentNumberTextField().setText(experiment.toString());
                dataAnalysisPanel.getTimeFramesNumberTextField().setText("" + experiment.getTimeFrames());
                dataAnalysisPanel.getDatasetTextField().setText(getSelectedAlgorithm().getAlgorithmName());
                dataAnalysisPanel.getImagingTypeTextField().setText(getSelectedImagingType().getName());
                showInfoMessage("Select a condition to start with analysis");
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
        String message = "Please select a project and an experiment to analyse motility data.";
        showInfoMessage(message);
        algorithmBindingList.clear();
        imagingTypeBindingList.clear();
        // clear plate conditions list, if not null
        if (plateConditionList != null) {
            plateConditionList.clear();
        }
        areaAnalysisController.getGroupsBindingList().clear();
        // clear selection on lists
        overviewExperimentPanel.getProjectJList().clearSelection();
        overviewExperimentPanel.getExperimentJList().clearSelection();
        // set text area to empty field
        overviewExperimentPanel.getProjectDescriptionTextArea().setText("");
        areaPreProcessingController.getPreProcessingMap().clear();
        currentCondition = null;
        experiment = null;
        areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().setSelected(true);
        GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), overviewExperimentPanel, dataAnalysisPanel);
        analysisExperimentPanel.getTopPanel().repaint();
        analysisExperimentPanel.getTopPanel().revalidate();
        resetExperimentMetadataFields();
        analysisExperimentPanel.getStartButton().setEnabled(true);
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
        overviewExperimentPanel.getUserTextField().setText("");
        overviewExperimentPanel.getInstrumentTextField().setText("");
        overviewExperimentPanel.getMagnificationTextField().setText("");
        overviewExperimentPanel.getPurposeTextArea().setText("");
        overviewExperimentPanel.getTimeFramesTextField().setText("");
    }

    /**
     * Initialize metadata area panel
     */
    private void initMetadataAreaPanel() {
        overviewExperimentPanel.getPurposeTextArea().setLineWrap(true);
        overviewExperimentPanel.getPurposeTextArea().setWrapStyleWord(true);
        overviewExperimentPanel.getProjectDescriptionTextArea().setLineWrap(true);
        overviewExperimentPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, overviewExperimentPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, algorithmBindingList, metadataAreaPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, imagingTypeBindingList, metadataAreaPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        // add area unit of measure to combo box
        for (AreaUnitOfMeasurement areaUnitOfMeasurement : AreaUnitOfMeasurement.values()) {
            metadataAreaPanel.getAreaUnitOfMeasurementComboBox().addItem(areaUnitOfMeasurement);
        }

        // choose area unit of measurement
        metadataAreaPanel.getAreaUnitOfMeasurementComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AreaUnitOfMeasurement areaUnitOfMeasurement = (AreaUnitOfMeasurement) metadataAreaPanel.getAreaUnitOfMeasurementComboBox().getSelectedItem();
                areaAnalysisHolder.setAreaUnitOfMeasurement(areaUnitOfMeasurement);
            }
        });

        metadataAreaPanel.getAreaUnitOfMeasurementComboBox().setRenderer(new AreaUnitOfMeasurementComboBoxRenderer());
        // set default unit of measurement: micro meters
        metadataAreaPanel.getAreaUnitOfMeasurementComboBox().setSelectedItem(AreaUnitOfMeasurement.MICRO_METERS);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project        
        overviewExperimentPanel.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected project
                int locationToIndex = overviewExperimentPanel.getProjectJList().locationToIndex(e.getPoint());
                Project selectedProject = projectBindingList.get(locationToIndex);
                if (experiment == null || !selectedProject.equals(experiment.getProject()) || experimentBindingList.isEmpty()) {
                    // project is being selected for the first time
                    onSelectedProject(selectedProject);
                }
            }
        });

        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions setup
        overviewExperimentPanel.getExperimentJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected experiment
                int locationToIndex = overviewExperimentPanel.getExperimentJList().locationToIndex(e.getPoint());
                Experiment selectedExperiment = experimentBindingList.get(locationToIndex);
                if (experiment == null || !selectedExperiment.equals(experiment)) {
                    onSelectedExperiment(selectedExperiment);
                }
            }
        });

        // bind information fields
        // exp user
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, overviewExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.user.firstName"), overviewExperimentPanel.getUserTextField(), BeanProperty.create("text"), "experimentuserbinding");
        bindingGroup.addBinding(binding);
        // exp purpose
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, overviewExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.purpose"), overviewExperimentPanel.getPurposeTextArea(), BeanProperty.create("text"), "experimentpurposebinding");
        bindingGroup.addBinding(binding);
        // instrument
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, overviewExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.instrument.name"), overviewExperimentPanel.getInstrumentTextField(), BeanProperty.create("text"), "instrumentbinding");
        bindingGroup.addBinding(binding);
        // resolution
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, overviewExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.magnification.magnificationNumber"), overviewExperimentPanel.getMagnificationTextField(), BeanProperty.create("text"), "magnificationbinding");
        bindingGroup.addBinding(binding);
        // exp time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, overviewExperimentPanel.getExperimentJList(), BeanProperty.create("selectedElement.timeFrames"), overviewExperimentPanel.getTimeFramesTextField(), BeanProperty.create("text"), "experimentimeframesbinding");
        bindingGroup.addBinding(binding);
        // do the binding       
        bindingGroup.bind();


        // button group for radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(metadataAreaPanel.getCellCoveredAreaRadioButton());
        buttonGroup.add(metadataAreaPanel.getOpenAreaRadioButton());

        /**
         * item listeners to the radio buttons
         */
        metadataAreaPanel.getCellCoveredAreaRadioButton().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.CELL_COVERED_AREA);
                } else {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.OPEN_AREA);
                }
            }
        });

        metadataAreaPanel.getOpenAreaRadioButton().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.OPEN_AREA);
                } else {
                    areaAnalysisHolder.setMeasuredAreaType(MeasuredAreaType.CELL_COVERED_AREA);
                }
            }
        });

        // select cell covered area as default
        metadataAreaPanel.getCellCoveredAreaRadioButton().setSelected(true);
        overviewExperimentPanel.getMetadataParentPanel().add(metadataAreaPanel, gridBagConstraints);
        analysisExperimentPanel.getTopPanel().add(overviewExperimentPanel, gridBagConstraints);
    }

    /**
     * Initialize data analysis panel
     */
    private void initDataAnalysisPanel() {
        //when a certain condition is selected, fetch time steps for each well of the condition
        dataAnalysisPanel.getConditionsList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = dataAnalysisPanel.getConditionsList().locationToIndex(e.getPoint());
                PlateCondition selectedCondition = plateConditionList.get(locationToIndex);
                if (currentCondition == null || !currentCondition.equals(selectedCondition)) {
                    // Execute Swing Worker to fetch Selected Condition: 
                    FetchConditionSwingWorker fetchSelectedConditionSW = new FetchConditionSwingWorker();
                    fetchSelectedConditionSW.execute();
                }
                currentCondition = selectedCondition;
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
        ExperimentsListRenderer experimentsListRenderer = new ExperimentsListRenderer(cellMissyController.getCurrentUser());
        overviewExperimentPanel.getExperimentJList().setCellRenderer(experimentsListRenderer);

        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }

        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        overviewExperimentPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, overviewExperimentPanel.getExperimentJList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            cellMissyController.showMessage("There are no experiments performed yet for this project!", "No experiments found", JOptionPane.INFORMATION_MESSAGE);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }

    /**
     * Action on selected experiment, retrieve plate conditions and repaint
     * plate panel.
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        // get current user from main controller
        User currentUser = cellMissyController.getCurrentUser();
        // get user of selected experiment
        // these two entities might not be the same
        User expUser = selectedExperiment.getUser();
        // if the user has a standard role, check if its the same as the user for the exp, and if so, proceed to analysis
        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            if (currentUser.equals(expUser)) {
                proceedToAnalysis(selectedExperiment);
            } else {
                String message = "It seems like you have no rights to analyze these data..." + "\n" + "Ask to user (" + expUser.getFirstName() + " " + expUser.getLastName() + ") !";
                cellMissyController.showMessage(message, "accessing other experiment data", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // if current user has ADMIN role, can do whatever he wants to...
            proceedToAnalysis(selectedExperiment);
        }
    }

    /**
     *
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
        metadataAreaPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        metadataAreaPanel.getImagingTypeComboBox().setSelectedIndex(0);
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
     * Swing Worker to fetch one condition time steps at once: The user selects
     * a condition, a waiting cursor is shown on the screen and time steps
     * results are fetched from DB. List of time steps is updated. In addition,
     * map of child controller is updated: computations are performed here and
     * then shown in the done method of the class.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Well> wellList = new ArrayList<>();
            wellList.addAll(currentCondition.getWellList());
            //fetch time steps for each well of condition 
            for (int i = 0; i < wellList.size(); i++) {
                //fetch time step collection for the wellhasimagingtype of interest
                Algorithm algorithm = getSelectedAlgorithm();
                ImagingType imagingType = getSelectedImagingType();
                wellService.fetchTimeSteps(wellList.get(i), algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
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
                if (!areaPreProcessingController.getTimeStepsBindingList().isEmpty()) {
                    //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO look at motility track RESULTS
                    areaPreProcessingController.showTimeStepsInTable();
                    onCardSwitch();
                    //check which button is selected for analysis:
                    if (areaPreProcessingController.getAreaAnalysisPanel().getNormalizeAreaButton().isSelected())   {
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
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                showMessage("Unexpected error occured: " + ex.getMessage() + ", please try to restart the application.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
