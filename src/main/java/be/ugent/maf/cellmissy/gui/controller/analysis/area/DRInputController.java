/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.area;

import be.ugent.maf.cellmissy.gui.experiment.analysis.area.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.GridBagConstraints;
import org.jdesktop.beansbinding.BindingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for input panel of dose-response analysis
 * 
 * @author Gwendolien
 */
@Controller("dRInputController")
public class DRInputController {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DRInputController.class);
    //model
    BindingGroup bindingGroup;
    //view
    private DRInputPanel dRInputPanel;
    // parent controller
    @Autowired
    private DoseResponseController doseResponseController;
    // services
    private GridBagConstraints gridBagConstraints;
    
    
    
    /**
     * Initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
    }
    
    
    
    /**
     * getters and setters
     *
     * @return
     */
    public DRInputPanel getdRInputPanel() {
        return dRInputPanel;
    }

    
    
}
