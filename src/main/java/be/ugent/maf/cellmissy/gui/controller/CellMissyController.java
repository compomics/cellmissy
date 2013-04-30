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
import be.ugent.maf.cellmissy.gui.project.NewProjectDialog;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main Controller Child Controllers: User Management, Setup Experiment, Load
 * Experiment, Data Analysis - controllers
 *
 * @author Paola
 */
@Controller("cellMissyController")
public class CellMissyController {

    private static final Logger LOG = Logger.getLogger(CellMissyController.class);
    // model
    private boolean firstSetup;
    private boolean firstDataAnalysis;
    private boolean firstLoadingFromCellMia;
    private boolean firstLoadingFromGenericInput;
    //view
    //main frame
    CellMissyFrame cellMissyFrame;
    // subviews
    private NewProjectDialog newProjectDialog;
    //child controllers
    @Autowired
    private LoginController loginController;
    @Autowired
    private OverviewController overviewController;
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
    // services

    /**
     * Get main frame
     *
     * @return
     */
    public CellMissyFrame getCellMissyFrame() {
        return cellMissyFrame;
    }

    /**
     * Get current user from the authentication bean
     *
     * @return
     */
    public User getCurrentUser() {
        return loginController.getCurrentUser();
    }

    /**
     * Initialize controller
     */
    public void init() {
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error(e.getMessage(), e);
                showMessage("Unexpected error: " + e.getMessage() + ", application will exit", "unexpected error", JOptionPane.ERROR_MESSAGE);
                // exit the application
                System.exit(1);
            }
        });

        //workaround for betterbeansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //create main frame and set its title
        cellMissyFrame = new CellMissyFrame();
        cellMissyFrame.setTitle("CellMissy");
        // at starter, show main panel with logo
        getCardLayout().first(cellMissyFrame.getBackgroundPanel());
        // init booleans to true
        firstSetup = true;
        firstDataAnalysis = true;
        firstLoadingFromCellMia = true;
        firstLoadingFromGenericInput = true;
        //init child controllers
        setupExperimentController.init();
        loadExperimentFromCellMiaController.init();
        loadExperimentFromGenericInputController.init();
        dataAnalysisController.init();
        overviewController.init();
        loginController.init();
        userManagementController.init();

        // initialize main frame
        initMainFrame();
    }

    /**
     * public classes and methods
     */
    /**
     * Show message to the user through the main frame
     *
     * @param message
     * @param title
     * @param messageType
     */
    public void showMessage(String message, String title, Integer messageType) {
        JOptionPane.showMessageDialog(cellMissyFrame.getContentPane(), message, title, messageType);
    }

    /**
     * update info message
     *
     * @param infoLabel
     * @param message
     */
    public void updateInfoLabel(JLabel infoLabel, String message) {
        infoLabel.setText(message);
    }

    /**
     * set cursor type
     *
     * @param cursor
     */
    public void setCursor(Cursor cursor) {
        cellMissyFrame.setCursor(cursor);
    }

    /**
     * validate User
     *
     * @param userToValidate
     * @return
     */
    public boolean validateUser(User userToValidate) {
        String message = "";
        boolean isValid = false;
        if (userManagementController.validateUser(userToValidate).isEmpty()) {
            isValid = true;
        } else {
            for (Iterator<String> it = userManagementController.validateUser(userToValidate).iterator(); it.hasNext();) {
                String string = it.next();
                message += string + "\n";
            }
            showMessage(message, "Error in user validation", JOptionPane.WARNING_MESSAGE);
        }
        return isValid;
    }

    /**
     * validate experiment info
     *
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
            showMessage(message, "Error in experiment validation", JOptionPane.WARNING_MESSAGE);
        }
        return isValid;
    }

    /**
     * Initialize section for ADMIN users
     */
    public void initAdminSection() {
        // menu item and create project item are not enabled for standand users
        cellMissyFrame.getUserMenuItem().setEnabled(true);
        cellMissyFrame.getNewProjectMenuItem().setEnabled(true);
        // user management
        cellMissyFrame.getUserMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userManagementController.showUserManagementDialog();
            }
        });
        // create a new  project
        cellMissyFrame.getNewProjectMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newProjectDialog.getProjectNumberTextField().setText("");
                newProjectDialog.getDescriptionTextArea().setText("");
                // show a newProjectDialog
                newProjectDialog.pack();
                newProjectDialog.setVisible(true);
            }
        });
    }

    /**
     * Disable ADMIN section for STANDARD users
     */
    public void disableAdminSection() {
        cellMissyFrame.getUserMenuItem().setEnabled(false);
        cellMissyFrame.getNewExperimentMenuItem().setEnabled(false);
        // disable actions on experiments for standard users
        overviewController.disableActionsOnExperiments();
    }

    /**
     * Make the frame visible and enter the application after user has logged in
     */
    public void enterTheApplication() {
        cellMissyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cellMissyFrame.setLocationRelativeTo(null);
        cellMissyFrame.setVisible(true);
    }

    /**
     * Initialize main frame
     */
    private void initMainFrame() {
        // do nothing on closing the main frame; ask user for the OK to proceed
        cellMissyFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // ask the user if he wants to actually exit from the application
        cellMissyFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                int option = JOptionPane.showConfirmDialog(cellMissyFrame, "Do you really want to close CellMissy?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (option) {
                    case JOptionPane.YES_OPTION:
                        System.exit(0);
                }
            }
        });
        // add action listeners to MenuBar
        ItemActionListener itemActionListener = new ItemActionListener();
        // experiment manager (set up)
        cellMissyFrame.getNewExperimentMenuItem().addActionListener(itemActionListener);
        // import data from cell mia
        cellMissyFrame.getCellMiaMenuItem().addActionListener(itemActionListener);
        // import data from generic input
        cellMissyFrame.getGenericInputMenuItem().addActionListener(itemActionListener);
        // data analysis
        cellMissyFrame.getDataAnalysisMenuItem().addActionListener(itemActionListener);
        // exit the application
        cellMissyFrame.getExitMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(cellMissyFrame, "Do you really want to close CellMissy?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (option) {
                    case JOptionPane.YES_OPTION:
                        System.exit(0);
                }
            }
        });

        // view all projects/experiments
        cellMissyFrame.getOverviewMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show a overviewProjectsDialog through child controller
                overviewController.showOverviewDialog();
            }
        });

        // customize dialog
        newProjectDialog = new NewProjectDialog(cellMissyFrame, true);
        //center the dialog on the main screen
        newProjectDialog.setLocationRelativeTo(cellMissyFrame);
        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        newProjectDialog.getInfoLabel().setIcon(scaledIcon);

        // create a new project
        newProjectDialog.getCreateProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create new project: save it to DB and create folder on the server
                if (!newProjectDialog.getProjectNumberTextField().getText().isEmpty()) {
                    try {
                        int projectNumber = Integer.parseInt(newProjectDialog.getProjectNumberTextField().getText());
                        //project description is not mandatory
                        String projectDescription = newProjectDialog.getDescriptionTextArea().getText();
                        setupExperimentController.createNewProject(projectNumber, projectDescription);
                        LOG.info("project " + projectNumber + " (" + projectDescription + ") " + "was created");
                        // creation of new project was successfull
                        showMessage("Project was created!", "Project created", JOptionPane.INFORMATION_MESSAGE);
                        newProjectDialog.getProjectNumberTextField().setText("");
                        newProjectDialog.getDescriptionTextArea().setText("");
                        // close the dialog
                        newProjectDialog.setVisible(false);
                    } catch (PersistenceException exception) {
                        showMessage("Project already present in the DB", "Error in persisting project", JOptionPane.WARNING_MESSAGE);
                        LOG.error(exception.getMessage());
                        newProjectDialog.getProjectNumberTextField().setText("");
                        newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                    } catch (NumberFormatException exception) {
                        showMessage("Please insert a valid number", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
                        LOG.error(exception.getMessage());
                        newProjectDialog.getProjectNumberTextField().setText("");
                        newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                    }
                } else {
                    showMessage("Please insert a number for the project you want to create", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
                    newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Action Listener for MenuItems
     */
    private class ItemActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String menuItemText = ((JMenuItem) e.getSource()).getText();
            if (menuItemText.equalsIgnoreCase("create experiment...") && switchCard(menuItemText)) {
                if (!firstSetup) {
                    setupExperimentController.resetAfterCardSwitch();
                }
                getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getSetupExperimentParentPanel().getName());
                firstSetup = false;
            } else if (menuItemText.equalsIgnoreCase("data analysis") && switchCard(menuItemText)) {
                if (!firstDataAnalysis) {
                    dataAnalysisController.resetAfterCardSwitch();
                }
                getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getAnalysisExperimentParentPanel().getName());
                firstDataAnalysis = false;
            } else if (menuItemText.equalsIgnoreCase("... from CELLMIA") && switchCard(menuItemText)) {
                if (!firstLoadingFromCellMia) {
                    loadExperimentFromCellMiaController.resetAfterCardSwitch();
                }
                getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromCellMiaParentPanel().getName());
                firstLoadingFromCellMia = false;
            } else if (menuItemText.equalsIgnoreCase("... from generic input") && switchCard(menuItemText)) {
                if (!firstLoadingFromGenericInput) {
                    loadExperimentFromGenericInputController.resetAfterCardSwitch();
                }
                getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromGenericInputParentPanel().getName());
                firstLoadingFromGenericInput = false;
            }
        }
    }

    /**
     * On card switch: if current data is not saved, ask the user if he wants to
     * change the view
     */
    private boolean switchCard(String menuItemText) {
        int showOptionDialog = 0;
        Object[] options = {"Yes", "No"};
        String currentCardName = GuiUtils.getCurrentCardName(cellMissyFrame.getBackgroundPanel());
        switch (currentCardName) {
            case "setupExperimentParentPanel":
                if (menuItemText.equalsIgnoreCase("create experiment...")) {
                    return false;
                } else if (!setupExperimentController.setupWasSaved()) {
                    showOptionDialog = JOptionPane.showOptionDialog(null, "Do you really want to leave this experimental set-up?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                } else {
                    return true;
                }
                break;
            case "loadFromCellMiaParentPanel":
                if (menuItemText.equalsIgnoreCase("... from CELLMIA")) {
                    return false;
                } else if (!loadExperimentFromCellMiaController.loadingWasSaved()) {
                    showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved! Continue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                } else {
                    return true;
                }
                break;
            case "loadFromGenericInputParentPanel":
                if (menuItemText.equalsIgnoreCase("... from generic input")) {
                    return false;
                } else if (!loadExperimentFromGenericInputController.loadingWasSaved()) {
                    showOptionDialog = JOptionPane.showOptionDialog(null, "Current data will not be saved! Continue?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                } else {
                    return true;
                }
                break;
            case "analysisExperimentParentPanel":
                if (menuItemText.equalsIgnoreCase("data analysis")) {
                    return false;
                } else if (dataAnalysisController.analysisWasStarted()) {
                    showOptionDialog = JOptionPane.showOptionDialog(null, "Do you really want to end this data analysis session?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                } else {
                    return true;
                }
                break;
        }
        if (showOptionDialog == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the card layout from the background panel of CellMissy frame
     *
     * @return
     */
    private CardLayout getCardLayout() {
        return (CardLayout) cellMissyFrame.getBackgroundPanel().getLayout();
    }
}
