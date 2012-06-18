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
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.SetupReport;
import be.ugent.maf.cellmissy.gui.ValidationUtils;
import be.ugent.maf.cellmissy.gui.experiment.ExperimentInfoPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupExperimentPanel;
import be.ugent.maf.cellmissy.gui.experiment.SetupPanel;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.ExperimentService;
import be.ugent.maf.cellmissy.service.ProjectService;
import com.compomics.util.Export;
import com.compomics.util.enumeration.ImageType;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.apache.batik.transcoder.TranscoderException;
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
    private Experiment experiment;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<Magnification> magnificationBindingList;
    private BindingGroup bindingGroup;
    private SetupReport setupReport;
    //view
    private SetupExperimentPanel setupExperimentPanel;
    private ExperimentInfoPanel experimentInfoPanel;
    private SetupPanel setupPanel;
    private JFrame frame;
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
        initSetupExperimentPanel();
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
     * show a message through the main frame
     */
    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    /**
     * add mouse listener to setup plate panel (Only when a condition is selected)
     */
    public void addMouseListener() {
        setupPlatePanelController.addMouseListener();
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
                        cellMissyController.showMessage("Some wells already have a condition\nPlease make another selection", 1);
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
     * this method checks experiment Info
     * @return messages to show if validation was not successful 
     */
    public List<String> validateExperimentInfo() {
        List<String> messages = new ArrayList<>();
        try {
            //if the selected project does not have already the current experiment number, set the experiment number
            if (!projectHasExperiment(((Project) experimentInfoPanel.getProjectJList().getSelectedValue()).getProjectid(), Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()))) {
                experiment.setExperimentNumber(Integer.parseInt(experimentInfoPanel.getNumberTextField().getText()));
            } else {
                String message = "Experiment number " + experimentInfoPanel.getNumberTextField().getText() + " already exists for this project";
                messages.add(message);
                experimentInfoPanel.getNumberTextField().requestFocusInWindow();
            }

        } catch (NumberFormatException e) {
            messages.add("Please insert a valid Experiment Number");
            experimentInfoPanel.getNumberTextField().requestFocusInWindow();
        }
        if (messages.isEmpty()) {
            messages.addAll(ValidationUtils.validateObject(experiment));
        }
        return messages;
    }

    /**
     * validate PlateCondition, if PlateCondition is not valid, go back to the previous one
     * @param plateCondition
     * @return 
     */
    public boolean validateCondition(PlateCondition plateCondition) {
        boolean isValid = false;

        if (conditionsPanelController.validateCondition(plateCondition).isEmpty()) {
            isValid = true;
        } else {
            String message = "";
            for (String string : conditionsPanelController.validateCondition(plateCondition)) {
                message += string + "\n";
            }
            cellMissyController.showMessage(message, 2);
            conditionsPanelController.getConditionsPanel().getConditionsJList().setSelectedIndex(conditionsPanelController.getPreviousConditionIndex());
        }
        return isValid;
    }

    /*
     * private methods and classes
     */
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

        //date cannot be modified manually
        experimentInfoPanel.getDateChooser().getDateEditor().setEnabled(false);

        //get current date with Date()
        Date date = new Date();
        experimentInfoPanel.getDateChooser().setDate(date);

        ExperimentListener experimentListener = new ExperimentListener(setupExperimentPanel.getNextButton());
        experimentListener.registerDoc(experimentInfoPanel.getNumberTextField().getDocument());
        experimentListener.registerDoc(experimentInfoPanel.getPurposeTextArea().getDocument());
        experimentListener.registerDoc(((JTextField) experimentInfoPanel.getDateChooser().getDateEditor().getUiComponent()).getDocument());
    }

    private void initSetupExperimentPanel() {
        //disable Next and Previous buttons
        setupExperimentPanel.getNextButton().setEnabled(false);
        setupExperimentPanel.getPreviousButton().setEnabled(false);
        //hide Report and Finish buttons
        setupExperimentPanel.getFinishButton().setVisible(false);
        setupExperimentPanel.getFinishButton().setEnabled(false);
        setupExperimentPanel.getReportButton().setVisible(false);

        cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and fill in experiment data");

        /**
         * add action listeners
         */
        setupExperimentPanel.getNextButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create a new experiment (in progress) and set its fields
                experiment = new Experiment();
                experiment.setExperimentStatus(ExperimentStatus.IN_PROGRESS);
                //set the User of the experiment
                //need to set the user like this NOW, to be changed!!!=====================================================================================
                experiment.setUser(cellMissyController.getAUser());
                experiment.setProject((Project) experimentInfoPanel.getProjectJList().getSelectedValue());
                experiment.setInstrument((Instrument) experimentInfoPanel.getInstrumentComboBox().getSelectedItem());
                experiment.setMagnification((Magnification) experimentInfoPanel.getMagnificationComboBox().getSelectedItem());
                experiment.setExperimentDate(experimentInfoPanel.getDateChooser().getDate());
                experiment.setPurpose(experimentInfoPanel.getPurposeTextArea().getText());
                experiment.setPlateFormat((PlateFormat) setupPlatePanelController.getSetupPlatePanelGui().getPlateFormatComboBox().getSelectedItem());
                //check if the info was filled in properly
                if (cellMissyController.validateExperimentInfo()) {
                    //show the setupPanel and hide the experimentInfoPanel
                    GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), setupPanel, experimentInfoPanel);
                    cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Add conditions and select wells for each condition. Conditions details can be chosen in the right panel.");
                    //enable the Previous Button
                    setupExperimentPanel.getPreviousButton().setEnabled(true);
                    setupExperimentPanel.getNextButton().setEnabled(false);
                    setupExperimentPanel.getFinishButton().setVisible(true);
                    if (setupExperimentPanel.getFinishButton().isEnabled()) {
                        setupExperimentPanel.getFinishButton().setEnabled(true);
                    } else {
                        setupExperimentPanel.getFinishButton().setEnabled(false);
                    }
                    setupExperimentPanel.getReportButton().setVisible(true);
                    setupExperimentPanel.getTopPanel().revalidate();
                    setupExperimentPanel.getTopPanel().repaint();
                }
            }
        });

        setupExperimentPanel.getPreviousButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.switchChildPanels(setupExperimentPanel.getTopPanel(), experimentInfoPanel, setupPanel);
                cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Please select a project from the list and provide microscope/experiment data");
                setupExperimentPanel.getPreviousButton().setEnabled(false);
                setupExperimentPanel.getNextButton().setEnabled(true);
                setupExperimentPanel.getFinishButton().setVisible(false);
                setupExperimentPanel.getReportButton().setVisible(false);
                setupExperimentPanel.getTopPanel().revalidate();
                setupExperimentPanel.getTopPanel().repaint();
            }
        });

        //create a pdf from the plate panel (ONLY if experiment set up is OK)
        setupExperimentPanel.getReportButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (validateCondition(conditionsPanelController.getCurrentCondition())) {

                    //update last condition of the experiment
                    updateLastCondition();

                    //set the experiment for each plate condition in the List
                    for (PlateCondition plateCondition : conditionsPanelController.getPlateConditionBindingList()) {
                        plateCondition.setExperiment(experiment);
                    }

                    //set the condition's collection of the experiment
                    experiment.setPlateConditionCollection(conditionsPanelController.getPlateConditionBindingList());

                    //create PDF report, execute SwingWorker
                    SetupReportWorker setupReportWorker = new SetupReportWorker();
                    setupReportWorker.execute();
                    //update info label (>>next step: save the experiment)
                    cellMissyController.updateInfoLabel(setupExperimentPanel.getInfolabel(), "Pdf report was successfully created. Click on Finish to save the Experiment");
                    setupExperimentPanel.getFinishButton().setEnabled(true);
                }
            }
        });

        //click on Finish button: save the experiment
        setupExperimentPanel.getFinishButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //save the new experiment to the DB
                experimentService.save(experiment);
            }
        });
    }

    /**
     * this method checks if a well already has a condition
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
     * this class extends a document listener 
     * on "next" button
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

    /**
     * this method checks if a project already has a certain experiment
     * @param projectId
     * @param experimentNumber
     * @return 
     */
    private boolean projectHasExperiment(Integer projectId, Integer experimentNumber) {
        boolean hasExperiment = false;
        for (Integer number : experimentService.findExperimentNumbersByProjectId(projectId)) {
            if (number == experimentNumber) {
                hasExperiment = true;
            }
        }
        return hasExperiment;
    }

    /**
     * update last condition before creating the PDf report and saving the experiment
     */
    private void updateLastCondition() {
        conditionsPanelController.updateCondition(conditionsPanelController.getPlateConditionBindingList().size() - 1);
    }

    /**
     * SwingWorker to create PDF file (REPORT)
     */
    private class SetupReportWorker extends SwingWorker<Object, Object> {

        private File file;
        private JPanel reportPanel;

        @Override
        protected Void doInBackground() throws Exception {
            //create a new instance of SetupReport (with the current setupPlatePanel, conditionsList and experiment)
            setupReport = new SetupReport(setupPlatePanelController.getSetupPlatePanel(), conditionsPanelController.getConditionsPanel().getConditionsJList(), experiment);
            //create JPanel for the report
            reportPanel = setupReport.createReportPanel();
            //create a new frame, set the size and add the report panel to it.
            frame = new JFrame();
            Dimension reportDimension = new Dimension(1200, 700);
            frame.setSize(reportDimension);
            frame.add(reportPanel);
            //set the frame to visible
            frame.setVisible(true);
            //export Panel to Pdf
            exportPanelToPdf();
            //hide the frame and dispose all its resources
            frame.setVisible(false);
            frame.dispose();
            //if export to PDF was successfull, open the PDF file from the desktop
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                showMessage(ex.getMessage(), 1);
            }
            return null;
        }

        @Override
        protected void done() {

            //add back the two components to the panel
            conditionsPanelController.getConditionsPanel().getjScrollPane1().setViewportView(conditionsPanelController.getConditionsPanel().getConditionsJList());
            setupPlatePanelController.getSetupPlatePanelGui().getBottomPanel().add(setupPlatePanelController.getSetupPlatePanel(), gridBagConstraints);

            cellMissyController.cellMissyFrame.getContentPane().revalidate();
            cellMissyController.cellMissyFrame.getContentPane().repaint();

        }

        //print to PDF (Export class from COmpomics Utilities)
        private void exportPanelToPdf() {
            //file to which export the panel
            file = new File("Experiment " + experiment.getExperimentNumber() + " - Project " + experiment.getProject().getProjectNumber() + ".pdf");
            try {
                Export.exportComponent(reportPanel, reportPanel.getBounds(), file, ImageType.PDF);
            } catch (IOException | TranscoderException ex) {
                showMessage(ex.getMessage(), 1);
            }
        }
    }
}
