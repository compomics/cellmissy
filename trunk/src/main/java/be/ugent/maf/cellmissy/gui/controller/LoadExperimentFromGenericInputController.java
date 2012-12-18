/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.experiment.load.LoadDataFromGenericInputPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromGenericInputController")
public class LoadExperimentFromGenericInputController {

    //model
    //view
    private LoadDataFromGenericInputPanel loadDataFromGenericInputPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private ExperimentMetadataController experimentMetadataController;
    //services

    /**
     * Initialize controller
     */
    public void init() {
        // init main view
        loadDataFromGenericInputPanel = new LoadDataFromGenericInputPanel();
        initMainPanel();
        experimentMetadataController.init();
    }

    /**
     * 
     */
    private void initMainPanel() {
        //update info message
        cellMissyController.updateInfoLabel(loadDataFromGenericInputPanel.getInfolabel(), "Select a project and then an experiment in progress to load motility data.");
    
    }
}
