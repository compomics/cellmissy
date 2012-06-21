/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.experiment.LoadExperimentPanel;
import be.ugent.maf.cellmissy.parser.ObsepFileParser;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadExperimentPanelController {

    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private BindingGroup bindingGroup;
    private Experiment experiment;
    //view
    private LoadExperimentPanel loadExperimentPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controllers
    private LoadDataPlatePanelController loadDataPlatePanelController;
    //services
    private ExperimentService experimentService;
    private ProjectService projectService;
    private ObsepFileParser obsepFileParser;
    private ApplicationContext context;

    /**
     * constructor
     * @param cellMissyController 
     */
    public LoadExperimentPanelController(CellMissyController cellMissyController) {

        this.cellMissyController = cellMissyController;

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        experimentService = (ExperimentService) context.getBean("experimentService");
        projectService = (ProjectService) context.getBean("projectService");
        obsepFileParser = (ObsepFileParser) context.getBean("obsepFileParser");

        bindingGroup = new BindingGroup();

        //init views
        loadExperimentPanel = new LoadExperimentPanel();

        //init child controllers
        loadDataPlatePanelController = new LoadDataPlatePanelController(this);

        //left panel: experiment data: data need to be retrieved from obsep file (microscope file)
        initExperimentDataPanel();
    }

    /*
     * getters and setters
     */
    public LoadExperimentPanel getLoadExperimentPanel() {
        return loadExperimentPanel;
    }

    public void updateInfoLabel(JLabel label, String message) {
        cellMissyController.updateInfoLabel(label, message);
    }

    /*
     * private methods and classes
     */
    /**
     * initializes the loading data panel
     */
    private void initExperimentDataPanel() {

        loadExperimentPanel.getFinishButton().setEnabled(false);

        //update info message
        cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Select a project and then an experiment to load CELLMIA data. Choose the microscope file to retrieve experiment data.");

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, loadExperimentPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
        
        //init experiment binding
        //bind Duration
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, loadExperimentPanel.getDurationTextField(), ELProperty.create("${text}"), experiment, BeanProperty.create("duration"), "durationbinding");
        bindingGroup.addBinding(binding);
        //bind Interval
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, loadExperimentPanel.getIntervalTextField(), ELProperty.create("${text}"), experiment, BeanProperty.create("experimentInterval"), "intervalbinding");
        bindingGroup.addBinding(binding);
        //bind Time frames
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, loadExperimentPanel.getTimeFramesTextField(), ELProperty.create("${text}"), experiment, BeanProperty.create("timeFrames"), "timeframesbinding");
        bindingGroup.addBinding(binding);
 

        //bo the binding
        bindingGroup.bind();


        /**
         * add mouse listeners
         */
        loadExperimentPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = loadExperimentPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.IN_PROGRESS));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, loadExperimentPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    cellMissyController.showMessage("There are no experiments in progress for this project!", 1);
                    if (!experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });

        loadExperimentPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int locationToIndex = loadExperimentPanel.getExperimentJList().locationToIndex(e.getPoint());
                experiment = experimentBindingList.get(locationToIndex);
                Dimension parentDimension = loadExperimentPanel.getLoadDataPlateParentPanel().getSize();
                //init plate panel with current experiment plate format
                loadDataPlatePanelController.getLoadDataPlatePanel().initPanel(experiment.getPlateFormat(), parentDimension);
                loadDataPlatePanelController.getLoadDataPlatePanel().repaint();

                cellMissyController.updateInfoLabel(loadExperimentPanel.getInfolabel(), "Click on forward to process imaging data for the selected experiment.");
            }
        });

        /**
         * add action listeners
         */
        //parse obseo file from the microscope
        loadExperimentPanel.getParseObsepFileButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //choose file to parse form microscope hierarchy
                JFileChooser chooseObsepFile = new JFileChooser();
                chooseObsepFile.setFileFilter(new FileFilter() {
                    // to select only (.obsep) files

                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().endsWith(".obsep");
                    }

                    @Override
                    public String getDescription() {
                        return ("(.obsep)");
                    }
                });

                // in response to the button click, show open dialog 
                int returnVal = chooseObsepFile.showOpenDialog(cellMissyController.cellMissyFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File obsepFile = chooseObsepFile.getSelectedFile();
                    obsepFileParser.parseObsepFile(obsepFile);
                    List<Double> experimentInfo = obsepFileParser.getExperimentInfo();
                    loadExperimentPanel.getTimeFramesTextField().setText(experimentInfo.get(0).toString());
                    loadExperimentPanel.getIntervalTextField().setText(experimentInfo.get(1).toString() + " " + obsepFileParser.getUnit());
                    loadExperimentPanel.getDurationTextField().setText(experimentInfo.get(2).toString() + " HOURS");
                } else {
                    cellMissyController.showMessage("Open command cancelled by user", 1);
                }
            }
        });
    }
}
