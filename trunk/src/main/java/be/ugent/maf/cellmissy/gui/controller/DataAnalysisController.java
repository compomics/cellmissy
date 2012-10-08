/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.TimeStep;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.DataAnalysisPanel;
import be.ugent.maf.cellmissy.gui.plate.AnalysisPlatePanel;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.WellService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Data Analysis Controller
 * Parent Controller: CellMissy Controller (main controller)
 * Child Controllers: Bulk Cell Analysis Controller - Single Cell Analysis Controller
 * @author Paola Masuzzo
 */
@Controller("dataAnalysisController")
public class DataAnalysisController {

    //model
    private Experiment experiment;
    private ObservableList<Algorithm> algorithmBindingList;
    private ObservableList<ImagingType> imagingTypeBindingList;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private List<PlateCondition> plateConditionList;
    private BindingGroup bindingGroup;
    //view
    private DataAnalysisPanel dataAnalysisPanel;
    private AnalysisPlatePanel analysisPlatePanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private BulkCellAnalysisController bulkCellAnalysisPanelController;
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
     * initialize controller
     */
    public void init() {
        //init view
        dataAnalysisPanel = new DataAnalysisPanel();
        analysisPlatePanel = new AnalysisPlatePanel();
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        bulkCellAnalysisPanelController.init();
        initPlatePanel();
        initExperimentDataPanel();
        initAnalysisPanel();
    }

    /**
     * getters and setters
     * @return 
     */
    public DataAnalysisPanel getDataAnalysisPanel() {
        return dataAnalysisPanel;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public AnalysisPlatePanel getAnalysisPlatePanel() {
        return analysisPlatePanel;
    }

    public PlateCondition getSelectedCondition() {
        return (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue();
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
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
     * initialize left panel: projectList, experimentList, Algorithm and imaging type Combo box, plateConditions list
     */
    private void initExperimentDataPanel() {

        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, dataAnalysisPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);

        //init algorithms combobox
        algorithmBindingList = ObservableCollections.observableList(new ArrayList<Algorithm>());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, algorithmBindingList, dataAnalysisPanel.getAlgorithmComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        //init imagingtypes combo box
        imagingTypeBindingList = ObservableCollections.observableList(new ArrayList<ImagingType>());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, imagingTypeBindingList, dataAnalysisPanel.getImagingTypeComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //do the binding
        bindingGroup.bind();

        /**
         * add mouse listeners
         */
        //when a project from the list is selected, show all experiments performed for that project        
        dataAnalysisPanel.getProjectJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("project ***");
                //init experimentJList
                int locationToIndex = dataAnalysisPanel.getProjectJList().locationToIndex(e.getPoint());
                if (experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.PERFORMED) != null) {
                    experimentBindingList = ObservableCollections.observableList(experimentService.findExperimentsByProjectIdAndStatus(projectBindingList.get(locationToIndex).getProjectid(), ExperimentStatus.PERFORMED));
                    JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, experimentBindingList, dataAnalysisPanel.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    cellMissyController.showMessage("There are no experiments performed yet for this project!", 1);
                    if (experimentBindingList != null && !experimentBindingList.isEmpty()) {
                        experimentBindingList.clear();
                    }
                }
            }
        });

        //when an experiment is selected, show algorithms and imaging types used for that experiment
        //show also conditions in the Jlist behind and plate view according to the conditions
        dataAnalysisPanel.getExperimentJList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("experiment ***");
                int locationToIndex = dataAnalysisPanel.getExperimentJList().locationToIndex(e.getPoint());
                experiment = experimentBindingList.get(locationToIndex);

                plateConditionList = new ArrayList<>();
                plateConditionList.addAll(experiment.getPlateConditionCollection());

                for (PlateCondition plateCondition : plateConditionList) {
                    for (Well well : plateCondition.getWellCollection()) {
                        //show algorithms used for experiment
                        for (Algorithm algorithm : wellService.findAlgosByWellId(well.getWellid())) {
                            if (!algorithmBindingList.contains(algorithm)) {
                                algorithmBindingList.add(algorithm);
                            }
                        }
                        //show imaging types used for experiment
                        for (ImagingType imagingType : wellService.findImagingTypesByWellId(well.getWellid())) {
                            if (!imagingTypeBindingList.contains(imagingType)) {
                                imagingTypeBindingList.add(imagingType);
                            }
                        }
                    }
                }
                //set selected algorithm to the first of the list
                dataAnalysisPanel.getAlgorithmComboBox().setSelectedIndex(0);
                //set selected imaging types to the first of the list
                dataAnalysisPanel.getImagingTypeComboBox().setSelectedIndex(0);
                //show conditions for selected experiment
                //show conditions in the JList
                showConditions();
                //show conditions in the plate panel (with rectangles and colors)
                analysisPlatePanel.setExperiment(experiment);
                analysisPlatePanel.repaint();
            }
        });

        //when a certain condition is selected, fetch time steps for each well of the condition
        dataAnalysisPanel.getConditionsList().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int locationToIndex = dataAnalysisPanel.getConditionsList().locationToIndex(e.getPoint());
                System.out.println("condition ***");
                //create a list of wells for each condition (from a collection)
                List<Well> wellList = new ArrayList<>();
                wellList.addAll(plateConditionList.get(locationToIndex).getWellCollection());
                //fetch time steps for each well
                for (int i = 0; i < wellList.size(); i++) {
                    //fetch time step collection for the wellhasimagingtype of interest
                    wellService.fetchTimeSteps(wellList.get(i), algorithmBindingList.get(dataAnalysisPanel.getAlgorithmComboBox().getSelectedIndex()).getAlgorithmid(), imagingTypeBindingList.get(dataAnalysisPanel.getImagingTypeComboBox().getSelectedIndex()).getImagingTypeid());
                }
                //update timeStep List for current selected condition
                updateTimeStepList(plateConditionList.get(locationToIndex));
                //populate table with time steps for current condition (algorithm and imaging type assigned) === THIS IS ONLY TO VIEW CELLMIA RESULTS
                bulkCellAnalysisPanelController.showTimeSteps();

                //set time steps list for the area pre-processor
                bulkCellAnalysisPanelController.setTimeStepsList();
                //compute time frames array for child controller (bulk cell controller)
                bulkCellAnalysisPanelController.computeTimeFrames();

                //check which button is selected for analysis:
                if (dataAnalysisPanel.getNormalizeAreaButton().isSelected()) {
                    //for current selected condition show normalized area values together with time frames
                    bulkCellAnalysisPanelController.setNormalizedAreaTableData(plateConditionList.get(locationToIndex));
                }

                if (dataAnalysisPanel.getDeltaAreaButton().isSelected()) {
                    //for current selected condition show delta area values 
                    bulkCellAnalysisPanelController.setDeltaAreaTableData(plateConditionList.get(locationToIndex));
                }

                if (dataAnalysisPanel.getPercentageAreaIncreaseButton().isSelected()) {
                    //for current selected condition show %increments (for outliers detection)
                    bulkCellAnalysisPanelController.setAreaIncreaseTableData(plateConditionList.get(locationToIndex));
                    //show density function for selected condition (Raw Data)
                    bulkCellAnalysisPanelController.showRawDataDensityFunction();
                    bulkCellAnalysisPanelController.showCorrectedDataDensityFunction();
                }

                if (dataAnalysisPanel.getCorrectedAreaButton().isSelected()) {
                    //for current selected condition show corrected area values (outliers have been deleted from distribution)
                    bulkCellAnalysisPanelController.setCorrectedAreaTableData(bulkCellAnalysisPanelController.getDataTable(), plateConditionList.get(locationToIndex));
                    //show Area increases with time frames
                    bulkCellAnalysisPanelController.getCorrectedDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.showArea();
                    bulkCellAnalysisPanelController.setCorrectedAreaTableData(dataAnalysisPanel.getAreaTable(), plateConditionList.get(locationToIndex));
                    bulkCellAnalysisPanelController.showSlopesInTable(locationToIndex);
                }
            }
        });

    }

    /**
     * initialize analysis panel
     */
    private void initAnalysisPanel() {
        //create a ButtonGroup for the radioButtons used for analysis
        ButtonGroup buttonGroup = new ButtonGroup();
        //adding buttons to a ButtonGroup automatically deselect one when another one gets selected
        buttonGroup.add(dataAnalysisPanel.getNormalizeAreaButton());
        buttonGroup.add(dataAnalysisPanel.getDeltaAreaButton());
        buttonGroup.add(dataAnalysisPanel.getPercentageAreaIncreaseButton());
        buttonGroup.add(dataAnalysisPanel.getCorrectedAreaButton());
        //select as default first button (Delta Area values Computation)
        dataAnalysisPanel.getNormalizeAreaButton().setSelected(true);

        /**
         * Calculate Normalized Area (with corrected values for Jumps)
         */
        dataAnalysisPanel.getNormalizeAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show normalized values in the table
                    bulkCellAnalysisPanelController.setNormalizedAreaTableData((PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue());

                    //set charts panel to null
                    bulkCellAnalysisPanelController.getDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getCorrectedDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getAreaChartPanel().setChart(null);
                }
            }
        });

        /**
         * Show Delta Area Values
         */
        dataAnalysisPanel.getDeltaAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show delta area values in the table            
                    bulkCellAnalysisPanelController.setDeltaAreaTableData((PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue());

                    //set charts panel to null
                    bulkCellAnalysisPanelController.getDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getCorrectedDensityChartPanel().setChart(null);
                    bulkCellAnalysisPanelController.getAreaChartPanel().setChart(null);
                }
            }
        });

        /**
         * Show %Area increase values
         */
        dataAnalysisPanel.getPercentageAreaIncreaseButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //check that a condition is selected
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    //show %increments of area between two consecutive time frames and determine if a JUMP is present
                    bulkCellAnalysisPanelController.setAreaIncreaseTableData((PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue());
                    dataAnalysisPanel.getGraphicsParentPanel().remove(bulkCellAnalysisPanelController.getAreaChartPanel());
                    dataAnalysisPanel.getGraphicsParentPanel().revalidate();
                    dataAnalysisPanel.getGraphicsParentPanel().repaint();
                    //show density function for selected condition
                    bulkCellAnalysisPanelController.showRawDataDensityFunction();
                    bulkCellAnalysisPanelController.showCorrectedDataDensityFunction();
                }
            }
        });

        /**
         * show Corrected values for Area (corrected for JUMPS)
         */
        dataAnalysisPanel.getCorrectedAreaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataAnalysisPanel.getConditionsList().getSelectedIndex() != -1) {
                    bulkCellAnalysisPanelController.setCorrectedAreaTableData(bulkCellAnalysisPanelController.getDataTable(), (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue());
                    bulkCellAnalysisPanelController.showArea();
                    bulkCellAnalysisPanelController.setCorrectedAreaTableData(dataAnalysisPanel.getAreaTable(), (PlateCondition) dataAnalysisPanel.getConditionsList().getSelectedValue());
                    bulkCellAnalysisPanelController.showSlopesInTable(dataAnalysisPanel.getConditionsList().getSelectedIndex());
                }
            }
        });

        //***************************************************************************//
        dataAnalysisPanel.getShowBarsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bulkCellAnalysisPanelController.showVelocityBars();
            }
        });
    }

    /**
     * update conditions list for current experiment 
     */
    private void showConditions() {

        //set cell renderer for the List
        dataAnalysisPanel.getConditionsList().setCellRenderer(new ConditionsRenderer());
        ObservableList<PlateCondition> plateConditionBindingList = ObservableCollections.observableList(plateConditionList);
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, plateConditionBindingList, dataAnalysisPanel.getConditionsList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();
    }

    /**
     * once a plateCondition is selected get all time steps for that condition 
     * @param well (of the condition)
     */
    private void updateTimeStepList(PlateCondition plateCondition) {
        //clear the actual timeStepList
        if (!bulkCellAnalysisPanelController.getTimeStepBindingList().isEmpty()) {
            bulkCellAnalysisPanelController.getTimeStepBindingList().clear();
        }
        for (Well well : plateCondition.getWellCollection()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                Collection<TimeStep> timeStepCollection = wellHasImagingType.getTimeStepCollection();
                for (TimeStep timeStep : timeStepCollection) {
                    bulkCellAnalysisPanelController.getTimeStepBindingList().add(timeStep);
                }
            }
        }
    }

    /**
     * renderer for the Conditions JList
     */
    private class ConditionsRenderer extends DefaultListCellRenderer {

        /*
         *constructor
         */
        public ConditionsRenderer() {
            setOpaque(true);
            setIconTextGap(10);
        }

        //Overrides method from the DefaultListCellRenderer
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            int conditionIndex = plateConditionList.indexOf((PlateCondition) value);
            setIcon(new rectIcon(GuiUtils.getAvailableColors()[conditionIndex + 1]));
            return this;
        }
    }

    /**
     * rectangular icon for the Condition list
     */
    private class rectIcon implements Icon {

        private final Integer rectHeight = 10;
        private final Integer rectWidth = 25;
        private Color color;

        /**
         * constructor
         * @param color 
         */
        public rectIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            //loadDataPlatePanelController.getLoadDataPlatePanel().setGraphics(g2d);
            g2d.setColor(color);
            g2d.fillRect(x, y, rectWidth, rectHeight);
        }

        @Override
        public int getIconWidth() {
            return rectWidth;
        }

        @Override
        public int getIconHeight() {
            return rectHeight;
        }
    }
}
