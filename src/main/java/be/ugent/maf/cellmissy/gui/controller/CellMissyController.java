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
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola
 */
@Controller("cellMissyController")
public class CellMissyController {

    //view
    //main frame
    CellMissyFrame cellMissyFrame;
    //child controllers
    @Autowired
    private UserPanelController userPanelController;
    @Autowired
    private SetupExperimentPanelController setupExperimentPanelController;
    @Autowired
    private LoadExperimentPanelController loadExperimentPanelController;
    @Autowired
    private DataAnalysisPanelController dataAnalysisPanelController;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        //workaround for betterbeansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init child controllers
        userPanelController.init();
        setupExperimentPanelController.init();
        loadExperimentPanelController.init();
        dataAnalysisPanelController.init();

        //create main frame
        cellMissyFrame = new CellMissyFrame();
        cellMissyFrame.setVisible(true);

        //init sub views
        cellMissyFrame.getUserParentPanel().add(userPanelController.getUserPanel(), gridBagConstraints);
        cellMissyFrame.getExperimentSetupParentPanel().add(setupExperimentPanelController.getSetupExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getLoadExperimentParentPanel().add(loadExperimentPanelController.getLoadExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getDataAnalysisParentPanel().add(dataAnalysisPanelController.getDataAnalysisPanel(), gridBagConstraints);
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
        if (userPanelController.validateUser().isEmpty()) {
            isValid = true;
        } else {
            for (String string : userPanelController.validateUser()) {
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
        return userPanelController.getUserBindingList().get(0);
    }

    /**
     * validate experiment info
     * @return 
     */
    public boolean validateExperimentInfo() {
        String message = "";
        boolean isValid = false;
        if (setupExperimentPanelController.validateExperimentInfo().isEmpty()) {
            isValid = true;
        } else {
            for (String string : setupExperimentPanelController.validateExperimentInfo()) {
                message += string + "\n";
            }
            showMessage(message, 2);
        }

        return isValid;
    }
}
