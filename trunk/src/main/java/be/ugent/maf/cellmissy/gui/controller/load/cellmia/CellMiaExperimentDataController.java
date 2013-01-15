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
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Experiment Metadata Controller: get experiment metadata from microscope (as well conditions from DB)
 * Parent Controller: Load Experiment Controller
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
        initExperimentInfoPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public ExperimentMetadataPanel getLoadExperimentInfoPanel() {
        return experimentMetadataPanel;
    }

    private void initExperimentInfoPanel() {
        // disable experiment metadata text fields
        experimentMetadataPanel.getDurationTextField().setEnabled(false);
        experimentMetadataPanel.getIntervalTextField().setEnabled(false);
        experimentMetadataPanel.getTimeFramesTextField().setEnabled(false);

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentOverviewPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //init experiment binding
        //bind Duration
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentOverviewPanel.getExperimentJList(), BeanProperty.create("selectedElement.duration"), experimentMetadataPanel.getDurationTextField(), BeanProperty.create("text"), "durationbinding");
        bindingGroup.addBinding(binding);
        //bind Interval
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentOverviewPanel.getExperimentJList(), BeanProperty.create("selectedElement.experimentInterval"), experimentMetadataPanel.getIntervalTextField(), BeanProperty.create("text"), "intervalbinding");
        bindingGroup.addBinding(binding);
        //bind Time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentOverviewPanel.getExperimentJList(), BeanProperty.create("selectedElement.timeFrames"), experimentMetadataPanel.getTimeFramesTextField(), BeanProperty.create("text"), "timeframesbinding");
        bindingGroup.addBinding(binding);

        //do the binding
        bindingGroup.bind();

        // fill in combobox with units
        for(CycleTimeUnit unit: CycleTimeUnit.values()){
            experimentMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }
        
        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments in progress for that project
        experimentOverviewPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = experimentOverviewPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS));
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
        });

        //when an experiment from the list is selected, show the right plate format with the wells sorrounded by rectangles if conditions were selected
        experimentOverviewPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int locationToIndex = experimentOverviewPanel.getExperimentJList().locationToIndex(e.getPoint());
                //set experiment of parent controller
                loadExperimentFromCellMiaController.setExperiment(experimentBindingList.get(locationToIndex));
                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(loadExperimentFromCellMiaController.getExperiment().getPlateConditionCollection());

                loadExperimentFromCellMiaController.getImagedPlatePanel().setExperiment(loadExperimentFromCellMiaController.getExperiment());
                loadExperimentFromCellMiaController.getImagedPlatePanel().repaint();

                // show Conditions JList
                showConditionsList();

                //load experiment folders
                experimentService.loadFolderStructure(loadExperimentFromCellMiaController.getExperiment());
                LOG.debug("Folders have been loaded");
                loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), "Click <<Exp Data>> to get experiment data from microscope.");
                loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpDataButton().setEnabled(true);
            }
        });

        //add view to parent panel
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpOverviewParentPanel().add(experimentOverviewPanel, gridBagConstraints);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getExpMetadataParentPanel().add(experimentMetadataPanel, gridBagConstraints);
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
}
