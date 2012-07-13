/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.User;
import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola
 */
public class CellMissyController {

    //view
    //main frame
    CellMissyFrame cellMissyFrame;
    //child controllers
    private UserPanelController userPanelController;
    private SetupExperimentPanelController setupExperimentPanelController;
    private LoadExperimentPanelController loadExperimentPanelController;
    private DataAnalysisPanelController dataAnalysisPanelController;
    
    //application context
    ApplicationContext context;
    private GridBagConstraints gridBagConstraints;

    public CellMissyController(CellMissyFrame cellMissyFrame) {
        this.cellMissyFrame = cellMissyFrame;

        //init child controllers
        userPanelController = new UserPanelController(this);
        setupExperimentPanelController = new SetupExperimentPanelController(this);
        loadExperimentPanelController = new LoadExperimentPanelController(this);
        dataAnalysisPanelController = new DataAnalysisPanelController(this);
        
        //load application context
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init view
        initFrame();
    }

    private void initFrame() {

        //add panel components to main frame
        cellMissyFrame.getUserParentPanel().add(userPanelController.getUserPanel(), gridBagConstraints);
        cellMissyFrame.getExperimentSetupParentPanel().add(setupExperimentPanelController.getSetupExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getLoadExperimentParentPanel().add(loadExperimentPanelController.getLoadExperimentPanel(), gridBagConstraints);
        cellMissyFrame.getDataAnalysisParentPanel().add(dataAnalysisPanelController.getDataAnalysisPanel(), gridBagConstraints);
    }

    public void showMessage(String message, Integer messageType) {
        JOptionPane.showMessageDialog(cellMissyFrame, message, "", messageType);
    }

    public void updateInfoLabel(JLabel infoLabel, String message) {
        infoLabel.setText(message);
    }
    
    public void setCursor(Cursor cursor){
        cellMissyFrame.setCursor(cursor);
    }

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

    public User getAUser() {
        return userPanelController.getUserBindingList().get(0);
    }

    public boolean validateExperimentInfo() {
        String message = "";
        boolean isValid = false;
        if(setupExperimentPanelController.validateExperimentInfo().isEmpty()){
            isValid = true;
        } else {
            for(String string : setupExperimentPanelController.validateExperimentInfo()){
                message += string + "\n";
            }
            showMessage(message, 2);
        }

        return isValid;
    }
}
