/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.GridBagConstraints;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola
 */
public class CellMissyController {

    //main frame
    CellMissyFrame cellMissyFrame;
    //child controllers
    UserPanelController userPanelController;
    ExperimentSetupPanelController experimentSetupPanelController;
    ConditionsSetupPanelController conditionsSetupPanelController;
    //application context
    ApplicationContext context;
    private GridBagConstraints gridBagConstraints;

    public CellMissyController(CellMissyFrame cellMissyFrame) {
        this.cellMissyFrame = cellMissyFrame;
        
        //controllers
        userPanelController = new UserPanelController(this);
        experimentSetupPanelController = new ExperimentSetupPanelController(this);
        conditionsSetupPanelController = new ConditionsSetupPanelController(experimentSetupPanelController);
        
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        initFrame();
    }

    private void initFrame() {

        //add panel components to main frame
        cellMissyFrame.getUserParentPanel().add(userPanelController.getUserPanel(), gridBagConstraints);
        cellMissyFrame.getExperimentSetupParentPanel().add(experimentSetupPanelController.getExperimentSetupPanel(), gridBagConstraints);

    }

    public Object getBeanByName(String beanName) {
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        return context.getBean(beanName);
    }
}
