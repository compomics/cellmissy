/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.cellmia;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.exception.CellMiaFoldersException;
import be.ugent.maf.cellmissy.gui.experiment.load.cellmia.LoadFromCellMiaMetadataPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Experiment Data Controller: get experiment metadata from microscope (as well
 * conditions from DB) Parent Controller: Load Experiment Controller
 *
 * @author Paola Masuzzo
 */
@Controller("experimentMetadataController")
public class CellMiaExperimentDataController {

    private static final Logger LOG = Logger.getLogger(LoadExperimentFromCellMiaController.class);
    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private LoadFromCellMiaMetadataPanel loadFromCellMiaMetadataPanel;
    //parent controller
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    //child controllers
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //create main panel
        loadFromCellMiaMetadataPanel = new LoadFromCellMiaMetadataPanel();
        //init main view
        initExperimentMetadataPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public LoadFromCellMiaMetadataPanel getLoadFromCellMiaMetadataPanel() {
        return loadFromCellMiaMetadataPanel;
    }

    public ObservableList<Experiment> getExperimentBindingList() {
        return experimentBindingList;
    }

    public void setExpListRenderer(User currentUser) {
        ExperimentsListRenderer experimentsListRenderer = new ExperimentsListRenderer(currentUser);
        loadFromCellMiaMetadataPanel.getExperimentsList().setCellRenderer(experimentsListRenderer);
    }

    /**
     * Reset after user has chosen another project/experiment
     */
    public void resetAfterUserInteraction() {
        // reset collection of wellHasImagingType on plate panel
        for (WellGui wellGui : loadExperimentFromCellMiaController.getImagedPlatePanel().getWellGuiList()) {
            wellGui.getWell().getWellHasImagingTypeList().clear();
        }
        // set imaging type back to null
        loadExperimentFromCellMiaController.getImagedPlatePanel().setImagingTypeList(null);
        // repaint the plate view
        loadExperimentFromCellMiaController.getImagedPlatePanel().repaint();
        // disable all buttons
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpDataButton().setEnabled(false);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().setEnabled(false);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getFinishButton().setEnabled(false);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getCancelButton().setEnabled(false);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getStartButton().setEnabled(false);
        // reset text fields
        loadFromCellMiaMetadataPanel.getTimeFramesTextField().setText("");
        loadFromCellMiaMetadataPanel.getIntervalTextField().setText("");
        loadFromCellMiaMetadataPanel.getDurationTextField().setText("");
        // set text area to empty field
        loadFromCellMiaMetadataPanel.getProjectDescriptionTextArea().setText("");
        experimentService.resetFolders();
    }

    /**
     * Initialize Experiment Metadata panel
     */
    private void initExperimentMetadataPanel() {
        loadFromCellMiaMetadataPanel.getProjectDescriptionTextArea().setLineWrap(true);
        loadFromCellMiaMetadataPanel.getProjectDescriptionTextArea().setWrapStyleWord(true);
        // disable experiment metadata text fields
        loadFromCellMiaMetadataPanel.getDurationTextField().setEnabled(false);
        loadFromCellMiaMetadataPanel.getIntervalTextField().setEnabled(false);
        loadFromCellMiaMetadataPanel.getTimeFramesTextField().setEnabled(false);
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        loadFromCellMiaMetadataPanel.getInfoLabel().setIcon(scaledIcon);
        loadFromCellMiaMetadataPanel.getInfoLabel1().setIcon(scaledIcon);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadFromCellMiaMetadataPanel.getProjectsList());
        bindingGroup.addBinding(jListBinding);
        //do the binding
        bindingGroup.bind();

        // fill in combobox with units
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            loadFromCellMiaMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        loadFromCellMiaMetadataPanel.getProjectsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected project
                    int selectedIndex = loadFromCellMiaMetadataPanel.getProjectsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedIndex);
                        if (loadExperimentFromCellMiaController.getExperiment() == null) {
                            // if experiment is still null, project is being selected for the first time
                            onSelectedProject(selectedProject);
                            // if experiment is not null and a different project is selected, reset redo on selected project
                        } else if (loadExperimentFromCellMiaController.getExperiment() != null && !loadExperimentFromCellMiaController.getExperiment().getProject().equals(selectedProject)) {
                            resetOnANewProject();
                            onSelectedProject(selectedProject);
                        }
                    }
                }
            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        loadFromCellMiaMetadataPanel.getExperimentsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // retrieve selected experiment
                    int selectedIndex = loadFromCellMiaMetadataPanel.getExperimentsList().getSelectedIndex();
                    if (selectedIndex != -1) {
                        Experiment selectedExperiment = experimentBindingList.get(selectedIndex);
                        if (selectedExperiment != null && loadExperimentFromCellMiaController.getExperiment() == null) {
                            // if the experiment is still null, it is being selected for the first time
                            onSelectedExperiment(selectedExperiment);
                            // otherwise, if a different experiment has being selected, reset and recall the onselected experiment
                        } else if (selectedExperiment != null && !loadExperimentFromCellMiaController.getExperiment().equals(selectedExperiment)) {
                            resetOnANewExperiment();
                            onSelectedExperiment(selectedExperiment);
                        }
                    }
                }
            }
        });
    }

    /**
     * Reset after having chosen a new project
     */
    private void resetOnANewProject() {
        // reset conditions list and plate view
        loadExperimentFromCellMiaController.setExperiment(null);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPlatePanel().getConditionsList().setCellRenderer(null);
        loadExperimentFromCellMiaController.getImagedPlatePanel().setExperiment(null);
        resetAfterUserInteraction();
    }

    /**
     * Reset after having chosen a new experiment
     */
    private void resetOnANewExperiment() {
        resetAfterUserInteraction();
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        JList conditionsList = loadExperimentFromCellMiaController.getLoadFromCellMiaPlatePanel().getConditionsList();
        //set Cell Renderer for Condition List
        conditionsList.setCellRenderer(new ConditionsLoadListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, conditionsList);
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Action on selected experiment, retrieve plate conditions and repaint
     * plate panel
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        // get current user from parent controller
        User currentUser = loadExperimentFromCellMiaController.getCurrentUser();
        // get user of selected experiment
        // these two entities might not be the same
        User expUser = selectedExperiment.getUser();
        // if the user has a standard role, check if its the same as the user for the exp, and if so, proceed to analysis
        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            if (currentUser.equals(expUser)) {
                proceedToLoading(selectedExperiment);
            } else {
                String message = "It seems like you have no rights to load data for this experiment..." + "\n" + "Ask to user (" + expUser.getFirstName() + " " + expUser.getLastName() + ") !";
                loadExperimentFromCellMiaController.showMessage(message, "accessing other experiment data", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // if current user has ADMIN role, can do whatever he wants to...
            proceedToLoading(selectedExperiment);
        }
    }

    /**
     *
     * @param selectedExperiment
     */
    private void proceedToLoading(Experiment selectedExperiment) {
        //set experiment of parent controller
        loadExperimentFromCellMiaController.setExperiment(selectedExperiment);
        // init a new list of plate conditions
        plateConditionList = selectedExperiment.getPlateConditionList();
        Dimension parentDimension = loadExperimentFromCellMiaController.getLoadFromCellMiaPlatePanel().getPlateParentPanel().getSize();
        loadExperimentFromCellMiaController.getImagedPlatePanel().initPanel(selectedExperiment.getPlateFormat(), parentDimension);
        // repaint plate panel
        loadExperimentFromCellMiaController.getImagedPlatePanel().setExperiment(selectedExperiment);
        loadExperimentFromCellMiaController.getImagedPlatePanel().repaint();
        // show Conditions JList
        showConditionsList();
        String info;
        try {
            //load experiment folders
            experimentService.loadFolderStructure(selectedExperiment);
            LOG.debug("Folders for experiment " + selectedExperiment.getExperimentNumber() + " have been loaded");
            info = "Folders have been loaded. Click on Exp Data to get data from microscope.";
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel().setForeground(Color.black);
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), info);
            // enable button to look for experiment data
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpDataButton().setEnabled(true);
        } catch (CellMiaFoldersException ex) {
            LOG.error(ex.getMessage());
            info = "ERROR: please check folder structure for current experiment!";
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel().setForeground(Color.red);
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), info);
            loadExperimentFromCellMiaController.showMessage(ex.getMessage(), "Error in loading folder structure", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Action on selected project, find all relative in progress experiments, if
     * any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        // show project description
        String projectDescription = selectedProject.getProjectDescription();
        loadFromCellMiaMetadataPanel.getProjectDescriptionTextArea().setText(projectDescription);
        // show relative experiments
        Long projectid = selectedProject.getProjectid();
        List<Experiment> experimentList = experimentService.findExperimentsByProjectIdAndStatus(projectid, ExperimentStatus.IN_PROGRESS);
        //init experimentJList
        if (experimentList != null) {
            experimentBindingList = ObservableCollections.observableList(experimentList);
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadFromCellMiaMetadataPanel.getExperimentsList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
            loadExperimentFromCellMiaController.showMessage("There are no experiments in progress for this project!", "No experiments found", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
