/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.singlecell.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.ProjectHasUser;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.MetadataSingleCellPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.SingleCellAnalysisInfoDialog;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.list.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.list.CoordinatesUnitOfMeasurementComboBoxRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.GuiUtils;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main Controller for single cell analysis.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("singleCellMainController")
public class SingleCellMainController {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SingleCellMainController.class);
    // model
    private Experiment experiment;
    private PlateCondition currentCondition;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Format format;
    // view
    private AnalysisExperimentPanel analysisExperimentPanel;
    private MetadataSingleCellPanel metadataSingleCellPanel;
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    private SingleCellAnalysisInfoDialog singleCellAnalysisInfoDialog;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // child controllers
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // services
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
        metadataSingleCellPanel = new MetadataSingleCellPanel();
        singleCellAnalysisInfoDialog = new SingleCellAnalysisInfoDialog(cellMissyController.getCellMissyFrame(), true);
        // set icon for info labels
        Icon informationIcon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledInfoIcon = GuiUtils.getScaledIcon(informationIcon);
        metadataSingleCellPanel.getInfoLabel1().setIcon(scaledInfoIcon);
        metadataSingleCellPanel.getInfoLabel2().setIcon(scaledInfoIcon);
        // set icon for question button
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        ImageIcon scaledQuestionIcon = GuiUtils.getScaledIcon(questionIcon);
        metadataSingleCellPanel.getQuestionButton().setIcon(scaledQuestionIcon);
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        format = new DecimalFormat(PropertiesConfigurationHolder.getInstance().getString("dataFormat"));
        // init child controllers
        singleCellPreProcessingController.init();
        // init other views
        initPlatePanel();
        initMainPanel();
        initMetadataSingleCellPanel();
        initDataAnalysisPanel();
    }

    /**
     * Getters and setters
     *
     * @return
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    Algorithm getSelectedAlgorithm() {
        return algorithmBindingList.get(metadataSingleCellPanel.getAlgorithmComboBox().getSelectedIndex());
    }

    ImagingType getSelectedImagingType() {
        return imagingTypeBindingList.get(metadataSingleCellPanel.getImagingTypeComboBox().getSelectedIndex());
    }

    public TrackCoordinatesUnitOfMeasurement getCoordinatesUnitOfMeasurement() {
        return (TrackCoordinatesUnitOfMeasurement) metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().getSelectedItem();
    }

    public PlateCondition getCurrentCondition() {
        return currentCondition;
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public Format getFormat() {
        return format;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public CellMissyFrame getCellMissyFrame() {
        return cellMissyController.getCellMissyFrame();
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
     * Set cursor from main controller
     */
    public void setCursor(Cursor cursor) {
        cellMissyController.setCursor(cursor);
    }

    /**
     * Handle unexpected errors through the main controller
     *
     * @param ex: the thrown exception
     */
    public void handleUnexpectedError(Exception ex) {
        cellMissyController.handleUnexpectedError(ex);
    }

    /**
     * The condition is loaded and plate view is refreshed with not imaged wells
     * highlighted in gray
     *
     * @param plateCondition
     */
    void showNotImagedWells(PlateCondition plateCondition) {
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
     * Using the wellService, fetch track points from DB for selected condition
     * Use only imaged wells, i.e. wells with a non empty collection of
     * WellHasImagingType.
     * @param plateCondition
     */
    public void fetchTrackPoints(PlateCondition plateCondition) {
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well imagedWell : imagedWells) {
            Algorithm selectedAlgorithm = getSelectedAlgorithm();
            ImagingType selectedImagingType = getSelectedImagingType();
            String info = "** fetching data for sample: " + imagedWell + " **";
            singleCellPreProcessingController.appendInfo(info);
            wellService.fetchTrackPoints(imagedWell, selectedAlgorithm.getAlgorithmid(), selectedImagingType.getImagingTypeid());
        }
    }

    /**
     * @param plateCondition
     */
    public void fetchTracks(PlateCondition plateCondition) {
        //fetch tracks for each well of condition
        for (int i = 0; i < plateCondition.getWellList().size(); i++) {
            //fetch tracks collection for the wellhasimagingtype of interest
            Algorithm algorithm = getSelectedAlgorithm();
            ImagingType imagingType = getSelectedImagingType();
            wellService.fetchTracks(plateCondition.getWellList().get(i), algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
        }
    }

    /**
     * Update track points list with objects from a selected track in upper
     * table.
     *
     * @param plateCondition
     * @param selectedTrack
     */
    public void updateTrackPointsList(PlateCondition plateCondition, Track selectedTrack) {
        // clear the actual tracksList
        if (!singleCellPreProcessingController.getTrackPointsBindingList().isEmpty()) {
            singleCellPreProcessingController.getTrackPointsBindingList().clear();
        }
        // get only the wells that have been imaged
        for (Well well : plateCondition.getImagedWells()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeList()) {
                for (Track track : wellHasImagingType.getTrackList()) {
                    if (track.equals(selectedTrack)) {
                        for (TrackPoint trackPoint : track.getTrackPointList()) {
                            singleCellPreProcessingController.getTrackPointsBindingList().add(trackPoint);
                        }
                    }
                }
            }
        }
    }

    /**
     * Private methods and classes
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
     * Update information message in the bottom panel
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
        return (CardLayout) singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel().getLayout();
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
                GuiUtils.switchChildPanels(analysisExperimentPanel.getTopPanel(), dataAnalysisPanel, metadataSingleCellPanel);
                analysisExperimentPanel.getTopPanel().repaint();
                analysisExperimentPanel.getTopPanel().revalidate();
                getCardLayout().first(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
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
                getCardLayout().previous(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
                onCardSwitch();
            }
        });

        // action listener on next button
        analysisExperimentPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go forward of one step
                getCardLayout().next(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
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
                    //onCancel();
                }
            }
        });

        cellMissyController.getCellMissyFrame().getSingleCellAnalysisParentPanel().add(analysisExperimentPanel, gridBagConstraints);
    }

    /**
     * Initialize metadata area panel
     */
    private void initMetadataSingleCellPanel() {
        metadataSingleCellPanel.getPurposeTextArea().setLineWrap(true);
        metadataSingleCellPanel.getPurposeTextArea().setWrapStyleWord(true);
        metadataSingleCellPanel.getProjectDescriptionTextArea().setLineWrap(true);
        metadataSingleCellPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        //init projectJList: find all the projects from DB and sort them
        List<Project> allProjects = projectService.findAll();
        Collections.sort(allProjects);
        ObservableList<Project> projectBindingList = ObservableCollections.observableList(allProjects);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, metadataSingleCellPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, algorithmBindingList, metadataSingleCellPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, imagingTypeBindingList, metadataSingleCellPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        // add track coordinates unit of measure to combo box
        for (TrackCoordinatesUnitOfMeasurement trackCoordinatesUnitOfMeasurement : TrackCoordinatesUnitOfMeasurement.values()) {
            metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().addItem(trackCoordinatesUnitOfMeasurement);
        }

        metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().setRenderer(new CoordinatesUnitOfMeasurementComboBoxRenderer());
        // set default unit of measurement: pixels
        // then a conversion is applied to go to micrometers !
        metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().setSelectedItem(TrackCoordinatesUnitOfMeasurement.PIXELS);

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project
        metadataSingleCellPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    Project selectedProject = (Project) metadataSingleCellPanel.getProjectsList().getSelectedValue();
                    if (selectedProject != null) {
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
        metadataSingleCellPanel.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected experiment
                    Experiment selectedExperiment = (Experiment) metadataSingleCellPanel.getExperimentsList().getSelectedValue();
                    if (selectedExperiment != null) {
                        if (experiment == null || !selectedExperiment.equals(experiment)) {
                            onSelectedExperiment(selectedExperiment);
                        }
                    }
                }
            }
        });
        // add action Listener to the question/info button
        metadataSingleCellPanel.getQuestionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pack and show info dialog
                GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), singleCellAnalysisInfoDialog);
                singleCellAnalysisInfoDialog.setVisible(true);
            }
        });

        // bind information fields
        // exp user
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel.getExperimentsList(), BeanProperty.create("selectedElement.user.firstName"), metadataSingleCellPanel.getUserTextField(), BeanProperty.create("text"), "experimentuserbinding");
        bindingGroup.addBinding(binding);
        // exp purpose
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel.getExperimentsList(), BeanProperty.create("selectedElement.purpose"), metadataSingleCellPanel.getPurposeTextArea(), BeanProperty.create("text"), "experimentpurposebinding");
        bindingGroup.addBinding(binding);
        // instrument
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel.getExperimentsList(), BeanProperty.create("selectedElement.instrument.name"), metadataSingleCellPanel.getInstrumentTextField(), BeanProperty.create("text"), "instrumentbinding");
        bindingGroup.addBinding(binding);
        // exp time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, metadataSingleCellPanel.getExperimentsList(), BeanProperty.create("selectedElement.timeFrames"), metadataSingleCellPanel.getTimeFramesTextField(), BeanProperty.create("text"), "experimentimeframesbinding");
        bindingGroup.addBinding(binding);
        // do the binding
        bindingGroup.bind();

        analysisExperimentPanel.getTopPanel().add(metadataSingleCellPanel, gridBagConstraints);
    }

    /**
     * Initialize data analysis panel
     */
    private void initDataAnalysisPanel() {
        //when a certain condition is selected, fetch tracks for each well of the condition
        dataAnalysisPanel.getConditionsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    PlateCondition selectedCondition = (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue();
                    if (selectedCondition != null) {
                        // if we are clicking for the first time, current condition is still null
                        // check also that we are not clicking again the same condition
                        if (currentCondition == null || !currentCondition.equals(selectedCondition)) {
                            // clean track points list if not empty
                            if (!singleCellPreProcessingController.getTrackPointsBindingList().isEmpty()) {
                                singleCellPreProcessingController.getTrackPointsBindingList().clear();
                            }
                            // Execute Swing Worker to fetch Selected Condition:
                            FetchConditionSwingWorker fetchConditionSwingWorker = new FetchConditionSwingWorker();
                            fetchConditionSwingWorker.execute();
                        }
                        currentCondition = selectedCondition;
                    }
                }
            }
        });
    }

    /**
     * Action on selected project, find all relative performed experiments, if
     * any.
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
        metadataSingleCellPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments, fetch them from DB and then sort them
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.PERFORMED);
        if (experimentList != null) {
            Collections.sort(experimentList);
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, metadataSingleCellPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();// check if the user has privileges on the selected project
            // if not, show a message and disable the experiments list
            if (!userHasPrivileges(selectedProject)) {
                String message = "Sorry, you don't have enough privileges for the selected project!";
                cellMissyController.showMessage(message, "no enough privileges", JOptionPane.WARNING_MESSAGE);
                metadataSingleCellPanel.getExperimentsList().setEnabled(false);
            } else {
                metadataSingleCellPanel.getExperimentsList().setEnabled(true);
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
     * Proceed with the analysis of the data, given a chosen experiment to
     * analyse.
     *
     * @param experimentToAnalyse
     */
    private void proceedToAnalysis(Experiment experimentToAnalyse) {
        // clear current lists
        if (!imagingTypeBindingList.isEmpty()) {
            imagingTypeBindingList.clear();
        }
        if (!algorithmBindingList.isEmpty()) {
            algorithmBindingList.clear();
        }
        // set experiment
        experiment = experimentToAnalyse;
        // init a new list of plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(experiment.getPlateConditionList());
        Dimension parentDimension = dataAnalysisPanel.getAnalysisPlateParentPanel().getSize();
        analysisPlatePanel.initPanel(experiment.getPlateFormat(), parentDimension);
        // repaint plate panel
        analysisPlatePanel.setExperiment(experiment);
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
        singleCellPreProcessingController.initMapWithConditions();
        //set selected algorithm to the first of the list
        metadataSingleCellPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        metadataSingleCellPanel.getImagingTypeComboBox().setSelectedIndex(0);
        // enable start button
        analysisExperimentPanel.getStartButton().setEnabled(true);
    }

    /**
     * Update track list with objects from actual selected condition.
     *
     * @param plateCondition
     */
    private void updateTracksList(PlateCondition plateCondition) {
        // clear the actual tracksList
        if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
            singleCellPreProcessingController.getTracksBindingList().clear();
        }
        // get only the wells that have been imaged
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well well : imagedWells) {
            List<WellHasImagingType> wellHasImagingTypeList = well.getWellHasImagingTypeList();
            for (WellHasImagingType wellHasImagingType : wellHasImagingTypeList) {
                List<Track> trackList = wellHasImagingType.getTrackList();
                for (Track track : trackList) {
                    singleCellPreProcessingController.getTracksBindingList().add(track);
                }
            }
        }
    }

    /**
     * Disable/Enable some GUI components. Mainly used in Swing workers. In the
     * background, the application is busy in fetching data from DB, no
     * interaction should be possible anymore with the GUI. In the done, the
     * components are set to enabled again.
     *
     * @param enabled, F if disabled, T if enabled
     */
    private void controlGuiComponents(boolean enabled) {
        dataAnalysisPanel.getConditionsList().setEnabled(enabled);
        analysisExperimentPanel.getNextButton().setEnabled(enabled);
        analysisExperimentPanel.getPreviousButton().setEnabled(enabled);
        analysisExperimentPanel.getCancelButton().setEnabled(enabled);
    }

    /**
     * Swing Worker to fetch one condition tracks at once. The user selects a
     * condition, a waiting cursor is shown on the screen and tracks results are
     * fetched from DB. List of tracks is updated.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            // show a waiting cursor and disable GUI components
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            controlGuiComponents(false);
            fetchTracks(currentCondition);
            // when all wells re fetched, update tracks list
            updateTracksList(currentCondition);  // if tracks were actually fetched from DB, update map
            if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
                //put the plate condition together with a pre-processing results holder in the map
                singleCellPreProcessingController.updateMapWithCondition(currentCondition);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                controlGuiComponents(true);
                dataAnalysisPanel.getConditionsList().requestFocusInWindow();
                if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
                    // since we are moving from one condition to another one,
                    // we clear the list of tracks to plot, if it's not empty
                    if (!singleCellPreProcessingController.getTrackDataHolderBindingList().isEmpty()) {
                        singleCellPreProcessingController.getTrackDataHolderBindingList().clear();
                    }
                    // update GUI according to current view on the Card Layout
                    onCardSwitch();
                    // we get the category to plot from the selected tab in the GUI
                    int categoryToPlot = singleCellPreProcessingController.getCategoryToplot();
                    // and we finally generate the random tracks to plot again
                    // note that this is not done on the card switch method itself, because there we want to keep the same random tracks every time we switch from one view to another one.
                    singleCellPreProcessingController.generateRandomTrackDataHolders(categoryToPlot);
                    //Select the first row of the table to show first track as default
                    singleCellPreProcessingController.getSingleCellAnalysisPanel().getTracksTable().setRowSelectionInterval(0, 0);
                }
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

    /**
     * Update conditions list for current experiment.
     */
    private void showConditionsList() {
        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsAnalysisListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Check for card name when switching.
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
        switch (currentCardName) {
            case "inspectingDataPanel":
                // disable previous button
                analysisExperimentPanel.getPreviousButton().setEnabled(false);
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                showInfoMessage("Tracks are shown for each well, together with (column, row) coordinates");
                singleCellPreProcessingController.showTracksInTable();
                break;
            case "trackCoordinatesParentPanel":
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                showInfoMessage("Track Coordinates are shown for each well");
                singleCellPreProcessingController.updateTracksNumberInfo();
                singleCellPreProcessingController.updateWellBindingList(currentCondition);
                //check which button is selected for analysis:
                boolean useRawCoordinates = singleCellPreProcessingController.getTrackCoordinatesPanel().getUnshiftedCoordinatesRadioButton().isSelected();
                singleCellPreProcessingController.plotRandomTrackCoordinates(currentCondition, useRawCoordinates);
                singleCellPreProcessingController.showPlottedTracksInTable();
                if (useRawCoordinates) {
                    singleCellPreProcessingController.showRawTrackCoordinatesInTable(currentCondition);
                } else {
                    singleCellPreProcessingController.showShiftedTrackCoordinatesInTable(currentCondition);
                }
                break;
            case "velocitiesParentPanel":
                GuiUtils.highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                GuiUtils.resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                showInfoMessage("Single Cell Displacements and Speeds");
                // check which button is selected for analysis
                if (singleCellPreProcessingController.getSpeedsPanel().getInstantaneousDisplRadioButton().isSelected()) {
                    singleCellPreProcessingController.showInstantaneousSpeedsInTable(currentCondition);
                } else if (singleCellPreProcessingController.getSpeedsPanel().getTrackDisplRadioButton().isSelected()) {
                    singleCellPreProcessingController.showTrackDisplInTable(currentCondition);
                } else if (singleCellPreProcessingController.getSpeedsPanel().getTrackSpeedsRadioButton().isSelected()) {
                    singleCellPreProcessingController.showTrackSpeedsInTable(currentCondition);
                }
                break;
        }
    }
}
