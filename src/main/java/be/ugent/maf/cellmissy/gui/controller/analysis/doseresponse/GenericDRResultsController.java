/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRResultsPanel;
import java.awt.GridBagConstraints;
import org.springframework.stereotype.Controller;

/**
 *
 * @author CompOmics Gwen
 */
@Controller("genericDRResultsController")
public class GenericDRResultsController {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericDRResultsController.class);
    
    //model: all in super class
    //view
    private DRResultsPanel dRResultsPanel;
    
    
}
