/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.experiment.load.generic.LoadExperimentFromGenericInputPanel;
import be.ugent.maf.cellmissy.gui.plate.CellMiaImagedPlatePanel;
import java.awt.Dimension;
import javax.swing.JLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Paola Masuzzo
 */
@Controller("loadExperimentFromGenericInputController")
public class LoadExperimentFromGenericInputController {

    //model
    private Experiment experiment;
    //view
    private LoadExperimentFromGenericInputPanel loadExperimentFromGenericInputPanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //child controllers
    @Autowired
    private GenericImagedPlateController genericImagedPlateController;
    @Autowired
    private GenericExperimentDataController genericExperimentDataController;

    //services
    /**
     * Initialize controller
     */
    public void init() {
        // init main view
        loadExperimentFromGenericInputPanel = new LoadExperimentFromGenericInputPanel();
        initMainPanel();
        //init child controllers
        genericExperimentDataController.init();
        genericImagedPlateController.init();
    }

    /**
     * getters and setters
     * @return 
     */
    public LoadExperimentFromGenericInputPanel getLoadExperimentFromGenericInputPanel() {
        return loadExperimentFromGenericInputPanel;
    }

    public void showMessage(String message, Integer messageType) {
        cellMissyController.showMessage(message, messageType);
    }

    public void updateInfoLabel(JLabel label, String message) {
        cellMissyController.updateInfoLabel(label, message);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public CellMiaImagedPlatePanel getCellMiaImagedPlatePanel() {
        return genericImagedPlateController.getCellMiaImagedPlatePanel();
    }

    public void initPlatePanel(PlateFormat plateFormat, Dimension dimension) {
        genericImagedPlateController.getCellMiaImagedPlatePanel().initPanel(plateFormat, dimension);
    }

    /**
     * 
     */
    private void initMainPanel() {
        //update info message
        cellMissyController.updateInfoLabel(loadExperimentFromGenericInputPanel.getInfolabel(), "Select a project and then an experiment in progress to load motility data.");

    }
}
