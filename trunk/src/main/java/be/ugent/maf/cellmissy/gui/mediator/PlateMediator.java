/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.mediator;

import be.ugent.maf.cellmissy.gui.ButtonPanel;
import be.ugent.maf.cellmissy.gui.plate.PlatePanel;

/**
 *
 * @author Paola
 */
public interface PlateMediator {

    void setButtonPanel(ButtonPanel buttonPanel);

    void setPlatePanel(PlatePanel platePanel);

    void updateInfoMessage(String infoMessage);

    void onForward();

    void disableFinishButton();
    
    void enableFinishButton();
}
