/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
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
    UserPanelController userPanelController;
    SetupExperimentPanelController setupExperimentPanelController;
    //application context
    ApplicationContext context;
    private GridBagConstraints gridBagConstraints;

    public CellMissyController(CellMissyFrame cellMissyFrame) {
        this.cellMissyFrame = cellMissyFrame;

        //init child controllers
        userPanelController = new UserPanelController(this);
        setupExperimentPanelController = new SetupExperimentPanelController(this);

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

    }

    public Object getBeanByName(String beanName) {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        return context.getBean(beanName);
    }

    public void showMessage(String message, Integer messageType) {
        JOptionPane.showMessageDialog(cellMissyFrame, message, "", messageType);
    }

    public void updateInfoLabel(JLabel infoLabel, String message) {
        infoLabel.setText(message);
    }

    public boolean validateUser() {

        boolean isValid = false;
        if (userPanelController.validateUser().isEmpty()) {
            isValid = true;
        }
        return isValid;

    }
}
