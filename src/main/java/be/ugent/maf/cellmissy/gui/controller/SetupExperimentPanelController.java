/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.ExperimentStatus;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.entity.Magnification;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupPanel;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import be.ugent.maf.cellmissy.service.UserService;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class SetupExperimentPanelController {

    //model
    private ObservableList<Project> projectBindingList;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<Magnification> magnificationBindingList;
    private BindingGroup bindingGroup;
    //view
    private SetupExperimentPanel setupExperimentPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    private SetupPanel setupPanel;
    //parent controller
    private CellMissyController cellMissyController;
    //child controller
    private ConditionsPanelController conditionsPanelController;
    private SetupPlatePanelController setupPlatePanelController;
    //services
    private ProjectService projectService;
    private ExperimentService experimentService;
    private GridBagConstraints gridBagConstraints;

    /**
     * constructor
     * @param cellMissyController 
     */
    public SetupExperimentPanelController(CellMissyController cellMissyController) {
        this.cellMissyController = cellMissyController;

        setupExperimentPanel = new SetupExperimentPanel();
        experimentInfoPanel = new ExperimentInfoPanel();
        setupPanel = new SetupPanel();

        //init child controllers
        setupPlatePanelController = new SetupPlatePanelController(this);
        conditionsPanelController = new ConditionsPanelController(this);

        //init services
        projectService = (ProjectService) cellMissyController.getBeanByName("projectService");
        experimentService = (ExperimentService) cellMissyController.getBeanByName("experimentService");
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initExperimentInfoPanel();
    }

    /**
     * setters and getters
     *  
     */
    public SetupExperimentPanel getSetupExperimentPanel() {
        return setupExperimentPanel;
    }

    public CellMissyController getCellMissyController() {
        return cellMissyController;
    }

    public ConditionsPanelController getConditionsPanelController() {
        return conditionsPanelController;
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    /**
     * public methods
     */
    /**
     * 
     * if the user adds a new condition, add a new entry to the map: new condition-empty list of rectangles
     * @param newCondition added to the list
     */
    public void onNewConditionAdded(PlateCondition newCondition) {
        setupPlatePanelController.addNewRectangleEntry(newCondition);
    }

    /**
     * if the user removes a condition from the list, wells conditions are set back to null, rectangles are removed from the map and repaint is called
     * @param conditionToRemove 
     */
    public void onConditionToRemove(PlateCondition conditionToRemove) {
        //set back to null the condition of the wells selected 
        resetWellsCondition(conditionToRemove);
        //remove the rectangles from the map
        setupPlatePanelController.removeRectangleEntry(conditionToRemove);
        //repaint
        setupPlatePanelController.getSetupPlatePanel().repaint();
    }

    /**
     * get the current condition from the child controller
     * @return the current condition
     */
    public PlateCondition getCurrentCondition() {
        return conditionsPanelController.getCurrentCondition();
    }

    /**
     * get the setup plate panel from the child controller
     * @return setup plate panel
     */
    public SetupPlatePanel getSetupPlatePanel() {
        return setupPlatePanelController.getSetupPlatePanel();
    }

    /**
     * when the mouse is released and the rectangle has been drawn, this method is called:
     * set well collection of the current condition and set the condition of the selected wells
     * @param plateCondition, the current condition
     * @param rectangle, the just drawn rectangle
     * @return true if the selection of wells is valid, else show a message
     */
    public boolean updateWellCollection(PlateCondition plateCondition, Rectangle rectangle) {
        boolean isSelectionValid = true;
        Collection<Well> wellCollection = plateCondition.getWellCollection();
        outerloop:
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                //check if the collection already contains that well
                if (!wellCollection.contains(wellGui.getWell())) {
                    //the selection is valid if the wells do not have a condition yet
                    if (!hasCondition(wellGui)) {
                        //in this case, add the well to the collection and set the condition of the well
                        wellCollection.add(wellGui.getWell());
                        wellGui.getWell().setPlateCondition(plateCondition);
                    } else {
                        //if the wells do have a condition already, the selection is not valid
                        isSelectionValid = false;
                        //in this case, show a message through the main controller
                        cellMissyController.showMessage("Wells cannot have more than one condition\nPlease select again the wells", 1);
                        //exit from the outer loop
                        break outerloop;
                    }
                }
            }
        }
        return isSelectionValid;
    }

    /**
     * set back to null the condition of the wells selected (for a certain Condition)
     * @param plateCondition 
     */
    public void resetWellsCondition(PlateCondition plateCondition) {
        //set plate condition of wells again to null
        for (WellGui wellGui : setupPlatePanelController.getSetupPlatePanel().getWellGuiList()) {
            //get only the bigger default ellipse2D
            Ellipse2D ellipse = wellGui.getEllipsi().get(0);
            for (Rectangle rectangle : setupPlatePanelController.getSetupPlatePanel().getRectangles().get(plateCondition)) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight())) {
                    wellGui.getWell().setPlateCondition(null);
                }
            }
        }
    }

    /**
     * set back to null the conditions of all wells selected (for all conditions)
     */
    public void resetAllWellsCondition() {
        //set plate condition of all wells selected again to null
        for (PlateCondition plateCondition : conditionsPanelController.getPlateConditionBindingList()) {
            resetWellsCondition(plateCondition);
        }
    }

    /**
     * initializes the experiment info panel
     */
    private void initExperimentInfoPanel() {
        //init projectJList
        projectBindingList = ObservableCollections.observableList(projectService.findAll());
        JListBinding jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, projectBindingList, experimentInfoPanel.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        //init instrument combo box
        instrumentBindingList = ObservableCollections.observableList(experimentService.findAllInstruments());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, instrumentBindingList, experimentInfoPanel.getInstrumentComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        //init magnification combo box
        magnificationBindingList = ObservableCollections.observableList(experimentService.findAllMagnifications());
        jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, magnificationBindingList, experimentInfoPanel.getMagnificationComboBox());
        bindingGroup.addBinding(jComboBoxBinding);

        bindingGroup.bind();
        //add experimentInfoPanel to parent panel
        setupExperimentPanel.getTopPanel().add(experimentInfoPanel, gridBagConstraints);

        //select first project in the ProjectList
        experimentInfoPanel.getProjectJList().setSelectedIndex(0);

        //disable Next and Previous buttons
        setupExperimentPanel.getNextButton().setEnabled(false);
        setupExperimentPanel.getPreviousButton().setEnabled(false);
        //hide Report and Finish buttons
        setupExperimentPanel.getFinishButton().setVisible(false);
        setupExperimentPanel.getReportButton().setVisible(false);

        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and fill in experiment data");

        //date cannot be modified manually
        experimentInfoPanel.getDateChooser().getDateEditor().setEnabled(false);

        //get current date with Date()
        Date date = new Date();
        experimentInfoPanel.getDateChooser().setDate(date);

        /**
         * add action listeners
         */
        setupExperimentPanel.getNextButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //show the setupPanel and hide the experimentInfoPanel
                GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), setupPanel, experimentInfoPanel);
                cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Add conditions and select wells for each condition. Conditions details can be chosen in the right panel.");
                //enable the Previous Button
                setupExperimentPanel.getPreviousButton().setEnabled(true);
                setupExperimentPanel.getNextButton().setEnabled(false);
                setupExperimentPanel.getFinishButton().setVisible(true);
                setupExperimentPanel.getTopPanel().revalidate();
                setupExperimentPanel.getTopPanel().repaint();
            }
        });

        setupExperimentPanel.getPreviousButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), experimentInfoPanel, setupPanel);
                cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and fill in experiment data");
                setupExperimentPanel.getPreviousButton().setEnabled(false);
                setupExperimentPanel.getNextButton().setEnabled(true);
                setupExperimentPanel.getFinishButton().setVisible(false);
                setupExperimentPanel.getTopPanel().revalidate();
                setupExperimentPanel.getTopPanel().repaint();
            }
        });

        //click on Finish button: save the experiment
        setupExperimentPanel.getFinishButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //create a new experiment entity and set its fields
                Experiment experiment = new Experiment();
                experiment.setExperimentDate(experimentInfoPanel.getDateChooser().getDate());
                experiment.setInstrument((Instrument) experimentInfoPanel.getInstrumentComboBox().getSelectedItem());
                experiment.setMagnification((Magnification) experimentInfoPanel.getMagnificationComboBox().getSelectedItem());
                experiment.setExperimentNumber(Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()));
                experiment.setExperimentStatus(ExperimentStatus.IN_PROGRESS);
                experiment.setPurpose(experimentInfoPanel.getPurposeTextArea().getText());
                experiment.setProject((Project) experimentInfoPanel.getProjectJList().getSelectedValue());
                experiment.setPlateFormat((PlateFormat) setupPlatePanelController.getSetupPlatePanelGui().getPlateFormatComboBox().getSelectedItem());
                
                //need to set the user like this NOW, to be changed!!!=====================================================================================
                experiment.setUser(cellMissyController.getAUser());
                
                experiment.setPlateConditionCollection(conditionsPanelController.getPlateConditionBindingList());

                for(PlateCondition plateCondition : conditionsPanelController.getPlateConditionBindingList()){
                    plateCondition.setExperiment(experiment);
                }
                //save the new experiment to the DB
                experimentService.save(experiment);
            }
        });

        ExperimentListener experimentListener = new ExperimentListener(setupExperimentPanel.getNextButton());
        experimentListener.registerDoc(experimentInfoPanel.getNumberTextField().getDocument());
        experimentListener.registerDoc(experimentInfoPanel.getPurposeTextArea().getDocument());
        experimentListener.registerDoc(((JTextField) experimentInfoPanel.getDateChooser().getDateEditor().getUiComponent()).getDocument());
    }

    /*
     * private methods and classes
     */
    /**
     * check if a well already has a condition
     * @param wellGui
     * @return true if a well already has a condition assigned
     */
    private boolean hasCondition(WellGui wellGui) {
        boolean hasCondition = false;
        Ellipse2D ellipse = wellGui.getEllipsi().get(0);
        for (List<Rectangle> list : setupPlatePanelController.getSetupPlatePanel().getRectangles().values()) {
            for (Rectangle rectangle : list) {
                if (rectangle.contains(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight()) && wellGui.getWell().getPlateCondition() != null) {
                    hasCondition = true;
                }
            }
        }
        return hasCondition;
    }

    /**
     * document listener 
     */
    private class ExperimentListener implements DocumentListener {

        private List<Document> documentList = new ArrayList<>();
        private JButton button;

        public ExperimentListener(JButton button) {
            this.button = button;
        }

        public void registerDoc(Document document) {
            documentList.add(document);
            document.addDocumentListener(this);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }

        private void update() {
            //for each document check the lenght, when it's not zero enable the button
            for (Document document : documentList) {
                if (document.getLength() == 0) {
                    button.setEnabled(false);
                    return;
                }
            }
            button.setEnabled(true);
        }
    }
}
