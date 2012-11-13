/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.util.logging.Level;
import javax.swing.JLabel;
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
    //child controllers
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private SetupExperimentController setupExperimentController;
    @Autowired
    private LoadExperimentController loadExperimentController;
    @Autowired
    private DataAnalysisController dataAnalysisController;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        //workaround for betterbeansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init child controllers
        userManagementController.init();
        setupExperimentController.init();
        loadExperimentController.init();
        dataAnalysisController.init();

        //create main frame
        cellMissyFrame = new CellMissyFrame();
        cellMissyFrame.setTitle("CellMissy");
        cellMissyFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        cellMissyFrame.setVisible(true);

        //init sub views
        cellMissyFrame.getUserParentPanel().add(userManagementController.getUserPanel(), gridBagConstraints);
        cellMissyFrame.getExperimentSetupParentPanel().add(setupExperimentController.getSetupExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getLoadExperimentParentPanel().add(loadExperimentController.getLoadExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getDataAnalysisParentPanel().add(dataAnalysisController.getDataAnalysisPanel(), gridBagConstraints);
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
}
