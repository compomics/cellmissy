/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.cellmia;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentMetadataPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentOverviewPanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
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
 * Experiment Metadata Controller: get experiment metadata from microscope (as well conditions from DB)
 * Parent Controller: Load Experiment Controller
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
    private ExperimentOverviewPanel experimentOverviewPanel;
    private ExperimentMetadataPanel experimentMetadataPanel;
    //parent controller
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    //child controllers
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProjectService projectService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        bindingGroup = new BindingGroup();
        //create main panel
        experimentMetadataPanel = new ExperimentMetadataPanel();
        experimentOverviewPanel = new ExperimentOverviewPanel();
        //init main view
        initExperimentMetadataPanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public ExperimentMetadataPanel getExperimentMetadataPanel() {
        return experimentMetadataPanel;
    }

    /**
     * Initialize Experiment metadata panel
     */
    private void initExperimentMetadataPanel() {
        // disable experiment metadata text fields
        experimentMetadataPanel.getDurationTextField().setEnabled(false);
        experimentMetadataPanel.getIntervalTextField().setEnabled(false);
        experimentMetadataPanel.getTimeFramesTextField().setEnabled(false);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentOverviewPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        //do the binding
        bindingGroup.bind();

        // fill in combobox with units
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            experimentMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        experimentOverviewPanel.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected project
                int locationToIndex = experimentOverviewPanel.getProjectJList().locationToIndex(e.getPoint());
                Project selectedProject = projectBindingList.get(locationToIndex);
                if (loadExperimentFromCellMiaController.getExperiment() == null) {
                    // project is being selected for the first time
                    onSelectedProject(selectedProject);
                } else if (loadExperimentFromCellMiaController.getExperiment().getProject() != selectedProject) {
                    // another project has been selected, different from current one: warn the user!
                    Object[] options = {"Yes", "No"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved.\nContinue with another project?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    switch (showOptionDialog) {
                        case 0:
                            // another project is selected, reset objects and move on new project
                            resetOnANewProject();
                            onSelectedProject(selectedProject);
                            break;
                        case 1:
                            // ignore selection and select previous (current) prject
                            Project currentProject = loadExperimentFromCellMiaController.getExperiment().getProject();
                            experimentOverviewPanel.getProjectJList().setSelectedIndex(projectBindingList.indexOf(currentProject));
                            break;
                    }
                }
            }
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        experimentOverviewPanel.getExperimentJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // retrieve selected experiment
                int locationToIndex = experimentOverviewPanel.getExperimentJList().locationToIndex(e.getPoint());
                Experiment selectedExperiment = experimentBindingList.get(locationToIndex);
                // check if experiment is still null, then set it, otherwise warn the user, because an experiment was already chosen and import was started
                if (loadExperimentFromCellMiaController.getExperiment() == null) {
                    // experiment is being selected for the first time
                    onSelectedExperiment(selectedExperiment);
                } else if (loadExperimentFromCellMiaController.getExperiment() != selectedExperiment) {
                    // another experiment has been selected, different from current one: warn the user!
                    Object[] options = {"Yes", "No"};
                    int showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved.\nContinue with another experiment?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                    switch (showOptionDialog) {
                        case 0:
                            // another experiment will be processed (previously data not stored)
                            resetOnANewExperiment();
                            onSelectedExperiment(selectedExperiment);
                            break;
                        case 1:
                            // ignore selection and select previous experiment
                            Experiment currentExperiment = loadExperimentFromCellMiaController.getExperiment();
                            experimentOverviewPanel.getExperimentJList().setSelectedIndex(experimentBindingList.indexOf(currentExperiment));
                            return;
                    }
                } else if (loadExperimentFromCellMiaController.getExperiment() == selectedExperiment) {
                    // click again on same experiment: ignore selection
                    return;
                }
                //load experiment folders
                experimentService.loadFolderStructure(selectedExperiment);
                LOG.debug("Folders for experiment " + selectedExperiment.getExperimentNumber() + " have been loaded");
                loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), "Click <<Exp Data>> to get experiment data from microscope.");
                loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpDataButton().setEnabled(true);
            }
        });

        //add view to parent panel
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpOverviewParentPanel().add(experimentOverviewPanel, gridBagConstraints);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpMetadataParentPanel().add(experimentMetadataPanel, gridBagConstraints);
    }

    /**
     * Reset after having chosen a new project
     */
    private void resetOnANewProject() {
        // reset conditions list and plate view
        loadExperimentFromCellMiaController.setExperiment(null);
        experimentOverviewPanel.getConditionsList().setCellRenderer(null);
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
     * Reset after user has chosen another project/experiment
     */
    private void resetAfterUserInteraction() {
        // reset collection of wellHasImagingType on plate panel
        for (WellGui wellGui : loadExperimentFromCellMiaController.getImagedPlatePanel().getWellGuiList()) {
            wellGui.getWell().getWellHasImagingTypeCollection().clear();
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
        // reset text fields
        experimentMetadataPanel.getTimeFramesTextField().setText("");
        experimentMetadataPanel.getIntervalTextField().setText("");
        experimentMetadataPanel.getDurationTextField().setText("");
    }

    /**
     * this method shows a list of conditions once an experiment is selected
     */
    private void showConditionsList() {
        //set Cell Renderer for Condition List
        experimentOverviewPanel.getConditionsList().setCellRenderer(new ConditionsLoadListRenderer(plateConditionList));
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, experimentOverviewPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * Action on selected experiment, retrieve plate conditions and repaint plate panel
     *
     * @param selectedExperiment
     */
    private void onSelectedExperiment(Experiment selectedExperiment) {
        //set experiment of parent controller
        loadExperimentFromCellMiaController.setExperiment(selectedExperiment);
        // init a new list of plate conditions
        plateConditionList = new ArrayList<>();
        plateConditionList.addAll(selectedExperiment.getPlateConditionCollection());
        // repaint plate panel
        loadExperimentFromCellMiaController.getImagedPlatePanel().setExperiment(selectedExperiment);
        loadExperimentFromCellMiaController.getImagedPlatePanel().repaint();
        // show Conditions JList
        showConditionsList();
    }

    /**
     * Action on selected project, find all relative in progress experiments, if any
     *
     * @param selectedProject
     */
    private void onSelectedProject(Project selectedProject) {
        //init experimentJList
        if (experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
            experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(selectedProject.getProjectid(), ExperimentStatus.IN_PROGRESS));
            JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, experimentOverviewPanel.getExperimentJList());
            bindingGroup.addBinding(jListBinding);
            bindingGroup.bind();
        } else {
            loadExperimentFromCellMiaController.showMessage("There are no experiments in progress for this project!", 1);
            if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                experimentBindingList.clear();
            }
        }
    }
}
