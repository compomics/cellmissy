/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.ButtonPanel;
import be.ugent.maf.cellmissy.gui.plate.PlatePanelOld;

/**
 *
 * @author Paola
 */
public interface PlateMediator {

    void setButtonPanel(ButtonPanel buttonPanel);

    void setPlatePanel(PlatePanelOld platePanel);

    void updateInfoMessage(String infoMessage);

    void onForward();

    void disableFinishButton();
    
    void enableFinishButton();
        
    void saveWells();
    
    void showInitProgressBar();
    
    void hideInitProgressBar();
}
