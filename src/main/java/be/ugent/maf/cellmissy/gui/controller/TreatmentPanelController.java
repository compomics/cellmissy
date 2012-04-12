/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.TreatmentType;

/**
 *
 * @author Paola
 */
public class TreatmentPanelController {

    //model
    //view
    //parent controller
    private ConditionsPanelController conditionsPanelController;

    public TreatmentPanelController(ConditionsPanelController conditionsPanelController) {
        this.conditionsPanelController = conditionsPanelController;

        //init views
        initTreatmentPanel();

    }

    private void initTreatmentPanel() {

        //fill in tratment type ComboBox
        conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().addItem(TreatmentType.DRUG);
        conditionsPanelController.getConditionsSetupPanel().getTreatmentTypeComboBox().addItem(TreatmentType.GENERAL_TREATMENT);

    }
}
