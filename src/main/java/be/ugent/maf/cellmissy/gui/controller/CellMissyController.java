/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.Project;
import be.ugent.maf.cellmissy.gui.controller.management.InstrumentManagementController;
import be.ugent.maf.cellmissy.gui.controller.management.UserManagementController;
import be.ugent.maf.cellmissy.gui.controller.setup.SetupExperimentController;
import be.ugent.maf.cellmissy.gui.controller.load.generic.LoadExperimentFromGenericInputController;
import be.ugent.maf.cellmissy.gui.controller.load.cellmia.LoadExperimentFromCellMiaController;
import be.ugent.maf.cellmissy.gui.controller.analysis.area.AreaMainController;
import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.AboutDialog;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.HelpDialog;
import be.ugent.maf.cellmissy.gui.StartupDialog;
import be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse.generic.GenericDoseResponseController;
import be.ugent.maf.cellmissy.gui.controller.analysis.singlecell.SingleCellMainController;
import be.ugent.maf.cellmissy.gui.controller.management.AssayManagementController;
import be.ugent.maf.cellmissy.gui.controller.management.PlateManagementController;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import com.compomics.util.examples.BareBonesBrowserLaunch;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Main Controller. Child Controllers: User Management, Setup Experiment, Load
 * Experiment, Data Analysis - controllers
 *
 * @author Paola
 */
@Controller("cellMissyController")
public class CellMissyController {

    private static final Logger LOG = Logger.getLogger(CellMissyController.class);
    // model
    private boolean genericArea;
    private boolean firstSetup;
    private boolean firstAreaAnalysis;
    private boolean firstSingleCellAnalysis;
    private boolean firstDoseResponseAnalysis;
    private boolean firstLoadingFromCellMia;
    private boolean firstLoadingFromGenericInput;
    //view
    //main frame
    private CellMissyFrame cellMissyFrame;
    // subviews
    // startup dialog with functionalities of the software
    private StartupDialog startupDialog;
    private HelpDialog helpDialog;
    private AboutDialog aboutDialog;
    //child controllers
    @Autowired
    private LoginController loginController;
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private InstrumentManagementController instrumentManagementController;
    @Autowired
    private PlateManagementController plateManagementController;
    @Autowired
    private AssayManagementController assayManagementController;
    @Autowired
    private OverviewController overviewController;
    @Autowired
    private SetupExperimentController setupExperimentController;
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    @Autowired
    private LoadExperimentFromGenericInputController loadExperimentFromGenericInputController;
    @Autowired
    private AreaMainController areaMainController;
    @Autowired
    private SingleCellMainController singleCellMainController;
    @Autowired
    private GenericDoseResponseController genericDoseResponseController;
    @Autowired
    private ImportExportController importExportController;
    @Autowired
    private TracksWriterController tracksWriterController;

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
        GuiUtils.centerDialogOnFrame(cellMissyFrame, startupDialog);
        startupDialog.setVisible(true);
    }

    public void addNewProjectToList(Project project) {
        overviewController.getProjectBindingList().add(project);
    }

    public boolean isGenericArea() {
        return genericArea;
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
        aboutDialog = new AboutDialog(cellMissyFrame);
        helpDialog = new HelpDialog(cellMissyFrame);
        // at starter, show main panel with logo
        getCardLayout().first(cellMissyFrame.getBackgroundPanel());
        // init booleans to true
        firstSetup = true;
        firstAreaAnalysis = true;
        firstSingleCellAnalysis = true;
        firstDoseResponseAnalysis = true;
        firstLoadingFromCellMia = true;
        firstLoadingFromGenericInput = true;
        //init child controllers
        setupExperimentController.init();
        loadExperimentFromCellMiaController.init();
        loadExperimentFromGenericInputController.init();
        areaMainController.init();
        singleCellMainController.init();
        overviewController.init();
        loginController.init();
        userManagementController.init();
        instrumentManagementController.init();
        plateManagementController.init();
        assayManagementController.init();
        importExportController.init();
        tracksWriterController.init();
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
            for (String string : userManagementController.validateUser(userToValidate)) {
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
        // enable all the management sections
        cellMissyFrame.getUserMenuItem().setEnabled(true);
        cellMissyFrame.getAssayMenuItem().setEnabled(true);
        cellMissyFrame.getInstrumentMenuItem().setEnabled(true);
        cellMissyFrame.getPlateMenuItem().setEnabled(true);
        setupExperimentController.getExperimentInfoPanel().getNewProjectButton().setEnabled(true);
        // user management
        cellMissyFrame.getUserMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userManagementController.showUserManagementDialog();
            }
        });
        // instrument management
        cellMissyFrame.getInstrumentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentManagementController.showInstrumentManagementDialog();
            }
        });
        // plate management
        cellMissyFrame.getPlateMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plateManagementController.showPlateManagementDialog();
            }
        });
        // assay management
        cellMissyFrame.getAssayMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assayManagementController.showAssayManagementDialog();
            }
        });
    }

    /**
     * Disable ADMIN section for STANDARD users
     */
    public void disableAdminSection() {
        // disable all the management sections
        cellMissyFrame.getUserMenuItem().setEnabled(false);
        cellMissyFrame.getAssayMenuItem().setEnabled(false);
        cellMissyFrame.getInstrumentMenuItem().setEnabled(false);
        cellMissyFrame.getPlateMenuItem().setEnabled(false);
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
        // we disable these menu items, will be enabled only under ceratin conditions in the setup phase
        cellMissyFrame.getImportSettingsMenuItem().setEnabled(false);
        cellMissyFrame.getImportTemplateMenuItem().setEnabled(false);

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
        // import area data from generic input
        cellMissyFrame.getAreaGenericMenuItem().addActionListener(itemActionListener);
        // import tracks (single cell data) from generic input
        cellMissyFrame.getSingleCellGenericMenuItem().addActionListener(itemActionListener);
        // area analysis
        cellMissyFrame.getAreaAnalysisMenuItem().addActionListener(itemActionListener);
        // single cell analysis
        cellMissyFrame.getSingleCellAnalysisMenuItem().addActionListener(itemActionListener);
        //generic dose-response analysis
        cellMissyFrame.getDoseResponseMenuItem().addActionListener(itemActionListener);
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

        // export experiment to XML file
        cellMissyFrame.getExportExperimentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExportExperiment();
            }
        });

        // import experiment from XML file
        cellMissyFrame.getImportExperimentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onImportExperiment();
            }
        });

        // export experiment template to XML file
        cellMissyFrame.getExportTemplateMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExportTemplate();
            }
        });

        // import setup settings
        cellMissyFrame.getImportSettingsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onImportSettings();
            }
        });

        // import setup template
        cellMissyFrame.getImportTemplateMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onImportTemplate();
            }
        });

        // add the hyperlink events
        aboutDialog.getAboutEditorPane().addHyperlinkListener(new LinkListener(aboutDialog.getAboutEditorPane()));
        helpDialog.getHelpEditorPane().addHyperlinkListener(new LinkListener(helpDialog.getHelpEditorPane()));
        aboutDialog.getAboutEditorPane().setCaretPosition(0);
        helpDialog.getHelpEditorPane().setCaretPosition(0);
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

        // load area from generic input
        startupDialog.getLoadAreaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onLoadingFromGenericInput();
            }
        });

        // load tracks from generic input
        startupDialog.getLoadTracksButton().addActionListener(new ActionListener() {
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

        // area analysis
        startupDialog.getAreaAnalysisButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onAreaAnalysis();
            }
        });

        // single cell analysis
        startupDialog.getSingleCellAnalysisButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startupDialog.setVisible(false);
                onSingleCellAnalysis();
            }
        });

        //generic dose-response analysis
        startupDialog.getDoseResponseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDoseResponse();
            }
        });

        // overview
        startupDialog.getOverviewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOverview();
            }
        });

        // write cell tracks data to file
        startupDialog.getWriteCellTracksToFileButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onWriteTracksToFile();
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
            } else if (menuItemText.equalsIgnoreCase("... area") && switchCard(menuItemText)) {
                onAreaAnalysis();
            } else if (menuItemText.equalsIgnoreCase("... single cell") && switchCard(menuItemText)) {
                onSingleCellAnalysis();
            } else if (menuItemText.equalsIgnoreCase("... data from CELLMIA") && switchCard(menuItemText)) {
                onLoadingFromCellMia();
                // we keep it general for the generic input: can have both area and tracks importing
            } else if (menuItemText.contains("generic input") && switchCard(menuItemText)) {
                onLoadingFromGenericInput();
                genericArea = menuItemText.equalsIgnoreCase("... area from generic input");
            } else if (menuItemText.equalsIgnoreCase("... dose-response") && switchCard(menuItemText)) {
                onDoseResponse();
            }
        }
    }

    /**
     * Action performed on creating a new experiment.
     */
    private void onCreateExperiment() {
        if (!firstSetup) {
            setupExperimentController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getSetupExperimentParentPanel().getName());
        firstSetup = false;
    }

    /**
     * Action performed on loading data from generic input.
     */
    private void onLoadingFromGenericInput() {
        if (!firstLoadingFromGenericInput) {
            loadExperimentFromGenericInputController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromGenericInputParentPanel().getName());
        firstLoadingFromGenericInput = false;
    }

    /**
     * Action performed on loading data from CELLMIA.
     */
    private void onLoadingFromCellMia() {
        if (!firstLoadingFromCellMia) {
            loadExperimentFromCellMiaController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getLoadFromCellMiaParentPanel().getName());
        firstLoadingFromCellMia = false;
    }

    /**
     * Action performed on area analysis.
     */
    private void onAreaAnalysis() {
        if (!firstAreaAnalysis) {
            areaMainController.resetAfterCardSwitch();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getAreaAnalysisParentPanel().getName());
        firstAreaAnalysis = false;
    }

    /**
     * Action performed on single cell analysis.
     */
    private void onSingleCellAnalysis() {
        if (!firstSingleCellAnalysis) {
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getSingleCellAnalysisParentPanel().getName());
        firstSingleCellAnalysis = false;
    }

    /**
     * Action performed on generic dose-response analysis.
     */
    private void onDoseResponse() {
        if (!firstDoseResponseAnalysis) {
            genericDoseResponseController.resetOnCancel();
        }
        getCardLayout().show(cellMissyFrame.getBackgroundPanel(), cellMissyFrame.getDoseResponseAnalysisParentPanel().getName());
        firstDoseResponseAnalysis = false;
    }

    /**
     * Action performed on getting the overview projects/experiments.
     */
    private void onOverview() {
        // show a overviewProjectsDialog through child controller
        overviewController.showOverviewDialog();
    }

    /**
     * Action performed on about dialog.
     */
    private void onAbout() {
        aboutDialog.setLocationRelativeTo(cellMissyFrame);
        aboutDialog.setVisible(true);
    }

    /**
     * Action performed on help dialog.
     */
    private void onHelp() {
        helpDialog.setLocationRelativeTo(cellMissyFrame);
        helpDialog.setVisible(true);
    }

    /**
     * Action performed on export experiment feature.
     */
    private void onExportExperiment() {
        importExportController.showExportExperimentDialog();
    }

    /**
     * Action performed on import experiment from external file.
     */
    private void onImportExperiment() {
        importExportController.showImportExperimentDialog();
    }

    /**
     * Action performed on write tracks data to file.
     */
    private void onWriteTracksToFile() {
        startupDialog.setVisible(false);
        tracksWriterController.showTracksWriterDialog();
    }

    /**
     * Action performed on export experiment template to file. If we are setting
     * up an experiment, the current template will be exported. If the user is
     * performing other tasks in CellMissy, a specific dialog will be shown,
     * through the import-export controller. In this last case, the user has to
     * select an experiment and export the template from it.
     */
    private void onExportTemplate() {
        String currentCardName = GuiUtils.getCurrentCardName(cellMissyFrame.getBackgroundPanel());
        if (currentCardName.equalsIgnoreCase("setupExperimentParentPanel")) {
            setupExperimentController.onExportTemplateForCurrentExperiment();
        } else {
            // in any other case, use a dialog: select an experiment to export the template from
            importExportController.showExportTemplateDialog();
        }
    }

    /**
     * Action performed on importing settings from another experiment.
     */
    private void onImportSettings() {
        setupExperimentController.onImportSettings();
    }

    /**
     * Action performed on importing a template from another experiment to the
     * one that is currently being set up.
     */
    private void onImportTemplate() {
        setupExperimentController.onImportTemplateToCurrentExperiment();
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
                } else if (areaMainController.analysisWasStarted()) {
                    showOptionDialog = JOptionPane.showOptionDialog(null, "Do you really want to end this data analysis session?", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                } else {
                    return true;
                }
                break;
        }
        return showOptionDialog == 0;
    }

    /**
     * Get the card layout from the background panel of CellMissy frame
     *
     * @return
     */
    private CardLayout getCardLayout() {
        return (CardLayout) cellMissyFrame.getBackgroundPanel().getLayout();
    }

    /**
     * This private class implements the
     */
    private class LinkListener implements HyperlinkListener {

        private final JEditorPane editorPane;

        public LinkListener(JEditorPane editorPane) {
            this.editorPane = editorPane;
        }

        @Override
        public void hyperlinkUpdate(HyperlinkEvent evt) {
            if (evt.getEventType().toString().equalsIgnoreCase(EventType.ENTERED.toString())) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (evt.getEventType().toString().equalsIgnoreCase(EventType.EXITED.toString())) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else if (evt.getEventType().toString().equalsIgnoreCase(EventType.ACTIVATED.toString())) {
                if (evt.getDescription().startsWith("#")) {
                    editorPane.scrollToReference(evt.getDescription());
                } else {
                    editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    BareBonesBrowserLaunch.openURL(evt.getDescription());
                    editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }
}
