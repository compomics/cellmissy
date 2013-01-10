/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.controller.setup.SetupExperimentController;
import be.ugent.maf.cellmissy.gui.controller.load.generic.LoadExperimentFromGenericInputController;
import be.ugent.maf.cellmissy.gui.controller.load.cellmia.LoadExperimentFromCellMiaController;
import be.ugent.maf.cellmissy.gui.controller.analysis.DataAnalysisController;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.MainPanel;
import be.ugent.maf.cellmissy.gui.project.NewProjectPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main Controller
 * Child Controllers: User Management, Setup Experiment, Load Experiment, Data Analysis - controllers
 * @author Paola
 */
@Controller("cellMissyController")
public class CellMissyController {

    //view
    //main frame
    CellMissyFrame cellMissyFrame;
    // subviews
    private MainPanel mainPanel;
    private JDialog dialog;
    private NewProjectPanel newProjectPanel;
    //child controllers
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private SetupExperimentController setupExperimentController;
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    @Autowired
    private DataAnalysisController dataAnalysisController;
    private GridBagConstraints gridBagConstraints;

    /**
     * getter
     * @return 
     */
    public CellMissyFrame getCellMissyFrame() {
        return cellMissyFrame;
    }

    /**
     * initialize controller
     */
    public void init() {

        //workaround for betterbeansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //create main frame and set its title
        cellMissyFrame = new CellMissyFrame();
        cellMissyFrame.setTitle("CellMissy");

        // add main panel with background
        mainPanel = new MainPanel();
        cellMissyFrame.getBackgroundPanel().add(mainPanel, gridBagConstraints);

        //init child controllers
        userManagementController.init();
        setupExperimentController.init();
        loadExperimentFromCellMiaController.init();
        loadExperimentFromGenericInputController.init();
        dataAnalysisController.init();

        // initialize main frame
        initMainFrame();
    }

    /**
     * public classes and methods 
     */
    /**
     * show message to the user through the main frame
     * @param message
     * @param messageType 
     */
    public void showMessage(String message, Integer messageType) {
        JOptionPane.showMessageDialog(cellMissyFrame, message, "", messageType);
    }

    /**
     * update info message
     * @param infoLabel
     * @param message 
     */
    public void updateInfoLabel(JLabel infoLabel, String message) {
        infoLabel.setText(message);
    }

    /**
     * set cursor type
     * @param cursor 
     */
    public void setCursor(Cursor cursor) {
        cellMissyFrame.setCursor(cursor);
    }

    /**
     * validate User
     * @return 
     */
    public boolean validateUser() {
        String message = "";
        boolean isValid = false;
        if (userManagementController.validateUser().isEmpty()) {
            isValid = true;
        } else {
            for (String string : userManagementController.validateUser()) {
                message += string + "\n";
            }
            showMessage(message, 2);
        }
        return isValid;
    }

    /**
     * temporary used to get user
     * @return 
     */
    public User getAUser() {
        return userManagementController.getUserBindingList().get(0);
    }

    /**
     * validate experiment info
     * @return 
     */
    public boolean validateExperimentInfo() {
        String message = "";
        boolean isValid = false;
        if (setupExperimentController.validateExperimentInfo().isEmpty()) {
            isValid = true;
        } else {
            for (String string : setupExperimentController.validateExperimentInfo()) {
                message += string + "\n";
            }
            showMessage(message, 2);
        }
        return isValid;
    }

    /**
     * Initialize main frame
     */
    private void initMainFrame() {
        // add action listeners to MenuBar
        ItemActionListener itemActionListener = new ItemActionListener();
        // user management
        cellMissyFrame.getUserMenuItem().addActionListener(itemActionListener);
        // experiment manager (set up)
        cellMissyFrame.getNewExperimentMenuItem().addActionListener(itemActionListener);
        // import data from cell mia
        cellMissyFrame.getCellMiaMenuItem().addActionListener(itemActionListener);
        // import data from generic input
        cellMissyFrame.getGenericInputMenuItem().addActionListener(itemActionListener);
        // data analysis
        cellMissyFrame.getDataAnalysisMenuItem().addActionListener(itemActionListener);
        // exit the application
        cellMissyFrame.getExitMenuItem().addActionListener(new ExitActionListener());
        // create a new  project
        cellMissyFrame.getNewProjectMenuItem().addActionListener(new NewProjectActionListener());
        // view all projects/experiments
        cellMissyFrame.getAllProjectsMenuItem().addActionListener(new OverviewActionListener());
        // customize dialog
        dialog = new JDialog();
        dialog.setAlwaysOnTop(false);
        dialog.setModal(true);
        dialog.getContentPane().setBackground(Color.white);
        dialog.getContentPane().setLayout(new GridBagLayout());
        //center the dialog on the main screen
        dialog.setLocationRelativeTo(null);
        // create a new panel for project creation
        newProjectPanel = new NewProjectPanel();
        newProjectPanel.getCreateProjectButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //create new project: save it to DB and create folder on the server
                if (!newProjectPanel.getProjectNumberTextField().getText().isEmpty()) {
                    try {
                        int projectNumber = Integer.parseInt(newProjectPanel.getProjectNumberTextField().getText());
                        //project description is not mandatory
                        String projectDescription = newProjectPanel.getDescriptionTextArea().getText();
                        setupExperimentController.createNewProject(projectNumber, projectDescription);
                        // creation of new project was successfull
                        showMessage("Project was created!", JOptionPane.INFORMATION_MESSAGE);
                        newProjectPanel.getProjectNumberTextField().setText("");
                    } catch (PersistenceException exception) {
                        showMessage("Project already present in the DB", JOptionPane.WARNING_MESSAGE);
                        newProjectPanel.getProjectNumberTextField().setText("");
                        newProjectPanel.getProjectNumberTextField().requestFocusInWindow();
                    } catch (NumberFormatException exception) {
                        showMessage("Please insert a valid number", JOptionPane.WARNING_MESSAGE);
                        newProjectPanel.getProjectNumberTextField().setText("");
                        newProjectPanel.getProjectNumberTextField().requestFocusInWindow();
                    }
                } else {
                    showMessage("Please insert a number for the project you want to create", JOptionPane.WARNING_MESSAGE);
                    newProjectPanel.getProjectNumberTextField().requestFocusInWindow();
                }
            }
        });

        // fit to screen
        cellMissyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cellMissyFrame.setLocationRelativeTo(null);
        cellMissyFrame.setVisible(true);
    }

    /**
     * Action Listener for MenuItems
     */
    private class ItemActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String menuItemText = ((JMenuItem) e.getSource()).getText();
            if (menuItemText.equalsIgnoreCase("user management")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), dataAnalysisController.getDataAnalysisPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), mainPanel);

            } else if (menuItemText.equalsIgnoreCase("new experiment")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), dataAnalysisController.getDataAnalysisPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), mainPanel);

            } else if (menuItemText.equalsIgnoreCase("data analysis")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getDataAnalysisPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getDataAnalysisPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getDataAnalysisPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getDataAnalysisPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getDataAnalysisPanel(), mainPanel);

            } else if (menuItemText.equalsIgnoreCase("... from CELLMIA")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel(), dataAnalysisController.getDataAnalysisPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel(), mainPanel);

            } else if (menuItemText.equalsIgnoreCase("... from generic input")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel(), loadExperimentFromCellMiaController.getLoadExperimentFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel(), dataAnalysisController.getDataAnalysisPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadExperimentFromGenericInputPanel(), mainPanel);
            }
            cellMissyFrame.getBackgroundPanel().repaint();
        }
    }

    /**
     * Action Listener for Exit MenuItem
     */
    private class ExitActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(cellMissyFrame, "Exit from application?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (option) {
                case JOptionPane.YES_OPTION:
                    System.exit(0);
                case JOptionPane.NO_OPTION:
                    break;
            }
        }
    }

    /**
     * Show a Dialog to create a new project
     */
    private class NewProjectActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.remove(setupExperimentController.getOverviewPanel());
            // set title
            dialog.setTitle("New Project");
            // add new panel
            dialog.getContentPane().add(newProjectPanel, gridBagConstraints);
            // show the dialog
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    /**
     * Show Overview: projects/experiments
     */
    private class OverviewActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.remove(newProjectPanel);
            // set title
            dialog.setTitle("Overview: Projects/Experiments");
            // add new panel
            dialog.add(setupExperimentController.getOverviewPanel(), gridBagConstraints);
            // show the dialog
            dialog.pack();
            dialog.setVisible(true);
        }
    }
}
