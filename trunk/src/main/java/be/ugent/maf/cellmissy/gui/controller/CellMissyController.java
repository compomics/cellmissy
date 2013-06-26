/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.controller.setup.SetupExperimentController;
import be.ugent.maf.cellmissy.gui.controller.load.generic.LoadExperimentFromGenericInputController;
import be.ugent.maf.cellmissy.gui.controller.load.cellmia.LoadExperimentFromCellMiaController;
import be.ugent.maf.cellmissy.gui.controller.analysis.AreaController;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.AboutDialog;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.HelpDialog;
import be.ugent.maf.cellmissy.gui.StartupDialog;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
    // startup dialog with functionalities of the software
    private StartupDialog startupDialog;
    private HelpDialog helpDialog;
    private AboutDialog aboutDialog;
    // subviews
    //child controllers
    @Autowired
    private LoginController loginController;
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private OverviewController overviewController;
    @Autowired
    private SetupExperimentController setupExperimentController;
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    @Autowired
    private AreaController areaController;

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

    public void onStartup() {
        startupDialog.setVisible(true);
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
        cellMissyFrame.setTitle("CellMissy - Cell Migration Invasion Storage System");
        // create a new startup dialog
        startupDialog = new StartupDialog(cellMissyFrame, true);
        aboutDialog = new AboutDialog(cellMissyFrame, true);
        helpDialog = new HelpDialog(cellMissyFrame, true);
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
        areaController.init();
        overviewController.init();
        loginController.init();
        userManagementController.init();

        // initialize main frame
        initMainFrame();
        // initialize start up dialog
        initStartupDialog();
        // show login dialog
        loginController.showLoginDialog();
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
     * Handle unexpected error: show an error message and exit the application.
     *
     * @param ex
     */
    public void handleUnexpectedError(Exception ex) {
        JOptionPane.showMessageDialog(cellMissyFrame.getContentPane(), "Unexpected error occured: " + ex.getMessage() + ", please try to restart the application.", "Unexpected error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
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
        setupExperimentController.getExperimentInfoPanel().getNewProjectButton().setEnabled(true);
        // user management
        cellMissyFrame.getUserMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userManagementController.showUserManagementDialog();
            }
        });
    }

    /**
     * Disable ADMIN section for STANDARD users
     */
    public void disableAdminSection() {
        cellMissyFrame.getUserMenuItem().setEnabled(false);
        // disable actions on experiments for standard users
        overviewController.disableAdminSection();
        setupExperimentController.disableAdminSection();
    }

    /**
     * Make the frame visible and enter the application after user has logged in
     */
    public void enterTheApplication() {
        cellMissyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cellMissyFrame.setLocationRelativeTo(null);
        // we show the main frame
        cellMissyFrame.setVisible(true);
        GuiUtils.centerDialogOnFrame(cellMissyFrame, startupDialog);
        // on top of this, we show the startup dialog
        startupDialog.setVisible(true);
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
                int option = JOptionPane.showConfirmDialog(cellMissyFrame, "Do you really want to close CellMissy?", "exit CellMissy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
                int option = JOptionPane.showConfirmDialog(cellMissyFrame, "Do you really want to close CellMissy?", "exit CellMissy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (option) {
                    case JOptionPane.YES_OPTION:
                        System.exit(0);
                }
            }
        });
        // get an overview of the projects
        cellMissyFrame.getOverviewMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOverview();
            }
        });

        // open an about dialog
        cellMissyFrame.getAboutMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAbout();
            }
        });

        // open an help dialog
        cellMissyFrame.getHelpMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onHelp();
            }
        });

        // format texts for the about and the help dialogs
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = aboutDialog.getAboutTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        aboutDialog.getAboutTextPane().setCaretPosition(0);
        helpDialog.getHelpTextPane().setCaretPosition(0);
        styledDocument = helpDialog.getHelpTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        Image helpImage = new ImageIcon(getClass().getResource("/icons/helpIcon.png")).getImage();
        helpDialog.setIconImage(helpImage);
        Image aboutImage = new ImageIcon(getClass().getResource("/icons/informationIcon.png")).getImage();
        aboutDialog.setIconImage(aboutImage);
    }

    /**
     * Initialize start up dialog
     */
    private void initStartupDialog() {
        /**
         * add action listeners
         */
        // create experiment
        startupDialog.getCreateExpButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onCreateExperiment();
            }
        });

        // load data from generic input
        startupDialog.getGenericInputButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onLoadingFromGenericInput();
            }
        });

        // load data from CELLMIA
        startupDialog.getCellMiaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onLoadingFromCellMia();
            }
        });

        // data analysis
        startupDialog.getDataAnalysisButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onDataAnalysis();
            }
        });
        // overview
        startupDialog.getOverviewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOverview();
            }
        });

        // about CellMissy
        startupDialog.getAboutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAbout();
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
            if (menuItemText.equalsIgnoreCase("create experiment") && switchCard(menuItemText)) {
                onCreateExperiment();
            } else if (menuItemText.equalsIgnoreCase("data analysis") && switchCard(menuItemText)) {
                onDataAnalysis();
            } else if (menuItemText.equalsIgnoreCase("... from CELLMIA") && switchCard(menuItemText)) {
                onLoadingFromCellMia();
            } else if (menuItemText.equalsIgnoreCase("... from generic input") && switchCard(menuItemText)) {
                onLoadingFromGenericInput();
            }
        }
    }

    /**
     * Action performed on creating a new experiment
     */
    private void onCreateExperiment() {
        if (!firstSetup) {
            setupExperimentController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getSetupExperimentParentPanel().getName());
        firstSetup = false;
    }

    /**
     * Action performed on loading data from generic input
     */
    private void onLoadingFromGenericInput() {
        if (!firstLoadingFromGenericInput) {
            loadExperimentFromGenericInputController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromGenericInputParentPanel().getName());
        firstLoadingFromGenericInput = false;
        loadExperimentFromGenericInputController.setExpListRenderer(getCurrentUser());
    }

    /**
     * Action performed on loading data from CELLMIA
     */
    private void onLoadingFromCellMia() {
        if (!firstLoadingFromCellMia) {
            loadExperimentFromCellMiaController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromCellMiaParentPanel().getName());
        firstLoadingFromCellMia = false;
        loadExperimentFromCellMiaController.setExpListRenderer(getCurrentUser());
    }

    /**
     * Action performed on data analysis
     */
    private void onDataAnalysis() {
        if (!firstDataAnalysis) {
            areaController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getAnalysisExperimentParentPanel().getName());
        firstDataAnalysis = false;
        areaController.setExpListRenderer(getCurrentUser());
    }

    /**
     * Action performed on getting the overview projects/experiments
     */
    private void onOverview() {
        // show a overviewProjectsDialog through child controller
        overviewController.showOverviewDialog();
    }

    /**
     * Action performed on getting about information
     */
    private void onAbout() {
        aboutDialog.setLocationRelativeTo(cellMissyFrame);
        aboutDialog.setVisible(true);
    }

    /**
     * Action performed on getting help information
     */
    private void onHelp() {
        helpDialog.setLocationRelativeTo(cellMissyFrame);
        helpDialog.setVisible(true);
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
                } else if (areaController.analysisWasStarted()) {
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
