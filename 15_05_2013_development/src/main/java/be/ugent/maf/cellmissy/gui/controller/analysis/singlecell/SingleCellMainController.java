/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import be.ugent.maf.cellmissy.analysis.TrackCoordinatesUnitOfMeasurement;
import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.TrackPoint;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.analysis.AnalysisExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.OverviewExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell.MetadataSingleCellPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsAnalysisListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.CoordinatesUnitOfMeasurementComboBoxRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
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
 * Main Controller for single cell analysis
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
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    private Format format;
    // view
    private AnalysisExperimentPanel analysisExperimentPanel;
    private OverviewExperimentPanel overviewExperimentPanel;
    private MetadataSingleCellPanel metadataSingleCellPanel;
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
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
        overviewExperimentPanel = new OverviewExperimentPanel();
        metadataSingleCellPanel = new MetadataSingleCellPanel();
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        overviewExperimentPanel.getInfoLabel().setIcon(scaledIcon);
        metadataSingleCellPanel.getInfoLabel().setIcon(scaledIcon);
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

    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public Algorithm getSelectedAlgorithm() {
        return algorithmBindingList.get(metadataSingleCellPanel.getAlgorithmComboBox().getSelectedIndex());
    }

    public ImagingType getSelectedImagingType() {
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
     *
     * @param type
     */
    public void setCursor(Cursor cursor) {
        cellMissyController.setCursor(cursor);
    }

    /**
     * Using the wellService, fetch track points from DB for selected condition
     * Use only imaged wells, i.e. wells with a non empty collection of
     * WellHasImagingType.
     */
    public void fetchTrackPoints(PlateCondition plateCondition) {
        List<Well> imagedWells = plateCondition.getImagedWells();
        for (Well imagedWell : imagedWells) {
            Algorithm selectedAlgorithm = getSelectedAlgorithm();
            ImagingType selectedImagingType = getSelectedImagingType();
            wellService.fetchTrackPoints(imagedWell, selectedAlgorithm.getAlgorithmid(), selectedImagingType.getImagingTypeid());
        }
    }

    /**
     * Update track points list with objects from a selected track in upper
     * table
     *
     * @param plateCondition
     * @param selectedTrack
     */
    public void updateTrackPointsList(PlateCondition plateCondition, Track selectedTrack) {
        // clear the actual tracksList
        if (!singleCellPreProcessingController.getTrackPointsBindingList().isEmpty()) {
            singleCellPreProcessingController.getTrackPointsBindingList().clear();
        }
        for (Well well : plateCondition.getWellList()) {
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
        return (CardLayout) singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel().getLayout();
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
        overviewExperimentPanel.getPurposeTextArea().setLineWrap(true);
        overviewExperimentPanel.getPurposeTextArea().setWrapStyleWord(true);
        overviewExperimentPanel.getProjectDescriptionTextArea().setLineWrap(true);
        overviewExperimentPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, overviewExperimentPanel.getProjectJList());
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
        // set default unit of measurement: micro meters
        metadataSingleCellPanel.getCoordinatesUnitOfMeasurementComboBox().setSelectedItem(TrackCoordinatesUnitOfMeasurement.MICRO_METERS);

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

        overviewExperimentPanel.getMetadataParentPanel().add(metadataSingleCellPanel, gridBagConstraints);
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
            JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, overviewExperimentPanel.getExperimentJList());
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
        singleCellPreProcessingController.initMapWithConditions();
        //set selected algorithm to the first of the list
        metadataSingleCellPanel.getAlgorithmComboBox().setSelectedIndex(0);
        //set selected imaging types to the first of the list
        metadataSingleCellPanel.getImagingTypeComboBox().setSelectedIndex(0);
        // enable start button
        analysisExperimentPanel.getStartButton().setEnabled(true);
    }

    /**
     * Update track list with objects from actual selected condition
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
     * Swing Worker to fetch one condition tracks at once. The user selects a
     * condition, a waiting cursor is shown on the screen and tracks results are
     * fetched from DB. List of tracks is updated.
     */
    private class FetchConditionSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //fetch tracks for each well of condition 
            for (int i = 0; i < currentCondition.getWellList().size(); i++) {
                //fetch tracks collection for the wellhasimagingtype of interest
                Algorithm algorithm = getSelectedAlgorithm();
                ImagingType imagingType = getSelectedImagingType();
                wellService.fetchTracks(currentCondition.getWellList().get(i), algorithm.getAlgorithmid(), imagingType.getImagingTypeid());
            }
            // when all wells re fetched, update tracks list
            updateTracksList(currentCondition);  // if time steps were actually fetched from DB, update map
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
                if (!singleCellPreProcessingController.getTracksBindingList().isEmpty()) {
                    onCardSwitch();
                }
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            } catch (ExecutionException ex) {
                showMessage("Unexpected error occured: " + ex.getMessage() + ", please try to restart the application.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
            }
            cellMissyController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * update conditions list for current experiment
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
     * Check for card name when switching
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getCurrentCardName(singleCellPreProcessingController.getSingleCellAnalysisPanel().getBottomPanel());
        switch (currentCardName) {
            case "inspectingDataPanel":
                // disable previous button
                analysisExperimentPanel.getPreviousButton().setEnabled(false);
                // enable next button
                analysisExperimentPanel.getNextButton().setEnabled(true);
                highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                showInfoMessage("Tracks are shown for each well, together with (column, row) coordinates");
                singleCellPreProcessingController.showTracksInTable();
                break;
            case "trackCoordinatesParentPanel":
                highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                showInfoMessage("Track Coordinates are shown for each well");
                singleCellPreProcessingController.updateTrackNumberLabel();
                singleCellPreProcessingController.updateWellBindingList(currentCondition);
                singleCellPreProcessingController.resetRandomTracks();
                boolean plotLines = singleCellPreProcessingController.getTrackCoordinatesPanel().getPlotLinesCheckBox().isSelected();
                boolean plotPoints = singleCellPreProcessingController.getTrackCoordinatesPanel().getPlotPointsCheckBox().isSelected();
                //check which button is selected for analysis:
                boolean useRawCoordinates = singleCellPreProcessingController.getTrackCoordinatesPanel().getUnshiftedCoordinatesRadioButton().isSelected();
                singleCellPreProcessingController.plotRandomTrackCoordinates(currentCondition, useRawCoordinates, plotLines, plotPoints);
                if (useRawCoordinates) {
                    singleCellPreProcessingController.showRawTrackCoordinatesInTable(currentCondition);
                } else {
                    singleCellPreProcessingController.showShiftedTrackCoordinatesInTable(currentCondition);
                }
                break;
            case "velocitiesParentPanel":
                highlightLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getVelocitiesLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getInspectingDataLabel());
                resetLabel(singleCellPreProcessingController.getSingleCellAnalysisPanel().getTrackCoordinatesLabel());
                showInfoMessage("Single Cell Velocities");
                // check which button is selected for analysis
                if (singleCellPreProcessingController.getVelocitiesPanel().getInstantaneousVelocitiesRadioButton().isSelected()) {
                    singleCellPreProcessingController.showInstantaneousVelocitiesInTable(currentCondition);
                } 
                break;
        }
    }
}
