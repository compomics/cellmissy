/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentMetadataPanel;
import be.ugent.maf.cellmissy.gui.experiment.load.ExperimentOverviewPanel;
import be.ugent.maf.cellmissy.gui.view.renderer.ConditionsLoadListRenderer;
import be.ugent.maf.cellmissy.parser.impl.ObsepFileParserImpl.CycleTimeUnit;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("genericExperimentDataController")
public class GenericExperimentDataController {

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
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
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
        //create main panels
        experimentMetadataPanel = new ExperimentMetadataPanel();
        experimentOverviewPanel = new ExperimentOverviewPanel();
        //init main view
        initExperimentInfoPanel();
    }

    /**
     * this method checks experiment Info
     * @return 
     */
    public List<String> setExperimentMetadata() {
        Experiment experiment = loadExperimentFromGenericInputController.getExperiment();
        List<String> messages = validateExperimentMetadata();
        // check that info is not left blank
        if (messages.isEmpty()) {
            try {
                // time frames
                experiment.setTimeFrames(Integer.parseInt(experimentMetadataPanel.getTimeFramesTextField().getText()));
                // interval
                experiment.setExperimentInterval(Double.parseDouble(experimentMetadataPanel.getIntervalTextField().getText()));
                // duration
                experiment.setDuration(Double.parseDouble(experimentMetadataPanel.getDurationTextField().getText()));
            } catch (NumberFormatException e) {
                messages.add("Please insert valid experiment metadata");
            }
        }
        return messages;
    }

    /**
     * Validate Experiment Metadata 
     * @return 
     */
    private List<String> validateExperimentMetadata() {
        List<String> messages = new ArrayList<>();

        String durationInfo = experimentMetadataPanel.getDurationTextField().getText();
        String intervalInfo = experimentMetadataPanel.getIntervalTextField().getText();
        String timeFramesInfo = experimentMetadataPanel.getTimeFramesTextField().getText();
        // if one of these fields is empty, set boolean to false
        if (durationInfo.equals("") || intervalInfo.equals("") || timeFramesInfo.equals("")) {
            messages.add("Please insert all experiment metadata.");
        }
        return messages;
    }

    /**
     * 
     */
    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentOverviewPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //do the binding
        bindingGroup.bind();

        // fill in combobox with units
        for (CycleTimeUnit unit : CycleTimeUnit.values()) {
            experimentMetadataPanel.getIntervalUnitComboBox().addItem(unit);
        }
        // show MINUTES as default
        experimentMetadataPanel.getIntervalUnitComboBox().setSelectedIndex(1);

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
                    loadExperimentFromGenericInputController.showMessage("There are no experiments in progress for this project!", 1);
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
                loadExperimentFromGenericInputController.setExperiment(experimentBindingList.get(locationToIndex));
                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(loadExperimentFromGenericInputController.getExperiment().getPlateConditionCollection());

                loadExperimentFromGenericInputController.getImagedPlatePanel().setExperiment(loadExperimentFromGenericInputController.getExperiment());
                Dimension parentDimension = loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getPlateViewParentPanel().getSize();
                loadExperimentFromGenericInputController.getImagedPlatePanel().initPanel(experimentBindingList.get(locationToIndex).getPlateFormat(), parentDimension);
                loadExperimentFromGenericInputController.getImagedPlatePanel().repaint();

                // show Conditions JList
                showConditionsList();
                loadExperimentFromGenericInputController.updateInfoLabel(loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getInfolabel(), "Add datasets and imaging types you want to import. Then select an imaging type to start importing data.");
                loadExperimentFromGenericInputController.enableButtons();
            }
        });

        //add view to parent panel
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getExpOverviewParentPanel().add(experimentOverviewPanel, gridBagConstraints);
        loadExperimentFromGenericInputController.getLoadFromGenericInputPanel().getExpMetadataParentPanel().add(experimentMetadataPanel, gridBagConstraints);
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
