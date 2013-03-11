/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.bean.AuthenticationBean;
import be.ugent.maf.cellmissy.entity.Role;
import be.ugent.maf.cellmissy.gui.controller.setup.SetupExperimentController;
import be.ugent.maf.cellmissy.gui.controller.load.generic.LoadExperimentFromGenericInputController;
import be.ugent.maf.cellmissy.gui.controller.load.cellmia.LoadExperimentFromCellMiaController;
import be.ugent.maf.cellmissy.gui.controller.analysis.DataAnalysisController;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.HomePanel;
import be.ugent.maf.cellmissy.gui.LoginDialog;
import be.ugent.maf.cellmissy.gui.project.NewProjectDialog;
import be.ugent.maf.cellmissy.gui.project.OverviewProjectsDialog;
import be.ugent.maf.cellmissy.gui.view.renderer.ExperimentsListRenderer;
import be.ugent.maf.cellmissy.service.UserService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main Controller Child Controllers: User Management, Setup Experiment, Load Experiment, Data Analysis - controllers
 *
 * @author Paola
 */
@Controller("cellMissyController")
public class CellMissyController {

    private static final Logger LOG = Logger.getLogger(CellMissyController.class);
    // model
    @Autowired
    private AuthenticationBean authenticationBean;
    //view
    //main frame
    CellMissyFrame cellMissyFrame;
    // subviews
    private HomePanel homePanel;
    private LoginDialog loginDialog;
    private NewProjectDialog newProjectDialog;
    private OverviewProjectsDialog overviewProjectsDialog;
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
    private BindingGroup bindingGroup;
    // services
    @Autowired
    private UserService userService;

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
        return authenticationBean.getCurrentUser();
    }

    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error(e.getMessage(), e);
                showMessage("Fatal error " + e.getMessage() + ", application will exit", "Fatal error", JOptionPane.ERROR_MESSAGE);
                // exit the application
                System.exit(1);
            }
        });

        //workaround for betterbeansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //create main frame and set its title
        cellMissyFrame = new CellMissyFrame();
        cellMissyFrame.setTitle("CellMissy");

        //init login view
        loginDialog = new LoginDialog(cellMissyFrame, true);
        // add main panel with background
        homePanel = new HomePanel();
        cellMissyFrame.getBackgroundPanel().add(homePanel, gridBagConstraints);

        //init child controllers
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
     * Initialize main frame
     */
    private void initMainFrame() {
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
        cellMissyFrame.getExitMenuItem().addActionListener(new ExitActionListener());
        // view all projects/experiments
        cellMissyFrame.getAllProjectsMenuItem().addActionListener(new OverviewActionListener());
        // customize dialog
        newProjectDialog = new NewProjectDialog(cellMissyFrame, true);
        overviewProjectsDialog = new OverviewProjectsDialog(cellMissyFrame, true);
        //center the dialog on the main screen
        newProjectDialog.setLocationRelativeTo(cellMissyFrame);
        overviewProjectsDialog.setLocationRelativeTo(cellMissyFrame);
        // set cell renderer for experiments list
        overviewProjectsDialog.getExperimentJList().setCellRenderer(new ExperimentsListRenderer());

        // set icon for info label
        Icon icon = UIManager.getIcon("OptionPane.informationIcon");
        ImageIcon scaledIcon = GuiUtils.getScaledIcon(icon);
        newProjectDialog.getInfoLabel().setIcon(scaledIcon);
        // set icon for info label
        overviewProjectsDialog.getInfoLabel().setIcon(scaledIcon);

        loginDialog.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(loginDialog.getUserNameTextField().getText().isEmpty() && loginDialog.getPasswordTextField().getPassword().length == 0)) {
                    onLogin();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Please provide an user name and a password", "login validation fail", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        //show login dialog
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);

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
                        // creation of new project was successfull
                        showMessage("Project was created!", "Project created", JOptionPane.INFORMATION_MESSAGE);
                        newProjectDialog.getProjectNumberTextField().setText("");
                    } catch (PersistenceException exception) {
                        showMessage("Project already present in the DB", "Error in persisting project", JOptionPane.WARNING_MESSAGE);
                        newProjectDialog.getProjectNumberTextField().setText("");
                        newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                    } catch (NumberFormatException exception) {
                        showMessage("Please insert a valid number", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
                        newProjectDialog.getProjectNumberTextField().setText("");
                        newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                    }
                } else {
                    showMessage("Please insert a number for the project you want to create", "Error while creating new project", JOptionPane.WARNING_MESSAGE);
                    newProjectDialog.getProjectNumberTextField().requestFocusInWindow();
                }
            }
        });

        // see overview
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, setupExperimentController.getProjectBindingList(), overviewProjectsDialog.getProjectJList());
        bindingGroup.addBinding(jListBinding);
        bindingGroup.bind();

        //show experiments for the project selected
        overviewProjectsDialog.getProjectJList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //init experimentJList
                int locationToIndex = overviewProjectsDialog.getProjectJList().locationToIndex(e.getPoint());
                if (setupExperimentController.findExperimentNumbersByProjectId(setupExperimentController.getProjectBindingList().get(locationToIndex).getProjectid()) != null) {
                    setupExperimentController.setExperimentBindingList(ObservableCollections.observableList(setupExperimentController.findExperimentsByProjectId(setupExperimentController.getProjectBindingList().get(locationToIndex).getProjectid())));
                    JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, setupExperimentController.getExperimentBindingList(), overviewProjectsDialog.getExperimentJList());
                    bindingGroup.addBinding(jListBinding);
                    bindingGroup.bind();
                } else {
                    showMessage("There are no experiments yet for this project!", "", JOptionPane.INFORMATION_MESSAGE);
                    if (setupExperimentController.getExperimentBindingList() != null && !setupExperimentController.getExperimentBindingList().isEmpty()) {
                        setupExperimentController.getExperimentBindingList().clear();
                    }
                }
            }
        });
    }

    /**
     * On login
     */
    private void onLogin() {
        //check if a user with given user name and password is found in the db    
        LOG.info("Login attempt with user name: " + loginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(loginDialog.getUserNameTextField().getText(), String.valueOf(loginDialog.getPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOG.info("User " + loginDialog.getUserNameTextField().getText() + " successfully logged in.");
            loginDialog.setVisible(false);

            //check if the current user has Role.ADMIN.
            //If so, init the admin section
            if (currentUser.getRole().equals(Role.ADMIN_USER)) {
                initAdminSection();
            } else {
                cellMissyFrame.getUserMenuItem().setEnabled(false);
                cellMissyFrame.getNewProjectMenuItem().setEnabled(false);
            }
            //set current user in authentication bean    
            authenticationBean.setCurrentUser(currentUser);
            // fit to screen
            cellMissyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            cellMissyFrame.setLocationRelativeTo(null);
            cellMissyFrame.setVisible(true);
        } else {
            LOG.error("Login validation failed");
            JOptionPane.showMessageDialog(loginDialog, "No user with the given credentials could be found, please try again.", "login fail", JOptionPane.ERROR_MESSAGE);
            loginDialog.getUserNameTextField().setText("");
            loginDialog.getPasswordTextField().setText("");
        }
    }

    /**
     * Initialize section for ADMIN user
     */
    private void initAdminSection() {
        // menu item and create project item are not enabled for standand users
        cellMissyFrame.getUserMenuItem().setEnabled(true);
        cellMissyFrame.getNewProjectMenuItem().setEnabled(true);
        userManagementController.init();
        // user management
        cellMissyFrame.getUserMenuItem().addActionListener(new ItemActionListener());
        // create a new  project
        cellMissyFrame.getNewProjectMenuItem().addActionListener(new NewProjectActionListener());
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
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), dataAnalysisController.getAnalysisExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), userManagementController.getUserPanel(), homePanel);

            } else if (menuItemText.equalsIgnoreCase("create experiment...")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), dataAnalysisController.getAnalysisExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), setupExperimentController.getSetupExperimentPanel(), homePanel);

            } else if (menuItemText.equalsIgnoreCase("data analysis")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getAnalysisExperimentPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getAnalysisExperimentPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getAnalysisExperimentPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getAnalysisExperimentPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), dataAnalysisController.getAnalysisExperimentPanel(), homePanel);

            } else if (menuItemText.equalsIgnoreCase("... from CELLMIA")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel(), dataAnalysisController.getAnalysisExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel(), homePanel);

            } else if (menuItemText.equalsIgnoreCase("... from generic input")) {
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel(), loadExperimentFromCellMiaController.getLoadFromCellMiaPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel(), setupExperimentController.getSetupExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel(), dataAnalysisController.getAnalysisExperimentPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel(), userManagementController.getUserPanel());
                GuiUtils.switchChildPanels(cellMissyFrame.getBackgroundPanel(), loadExperimentFromGenericInputController.getLoadFromGenericInputPanel(), homePanel);
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
            // show a newProjectDialog
            newProjectDialog.pack();
            newProjectDialog.setVisible(true);
        }
    }

    /**
     * Show Overview: projects/experiments
     */
    private class OverviewActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // show a overviewProjectsDialog
            overviewProjectsDialog.pack();
            overviewProjectsDialog.setVisible(true);
        }
    }
}
