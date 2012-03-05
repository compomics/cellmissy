/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.mediator.impl;

import be.ugent.maf.cellmissy.gui.mediator.PlateMediator;
import javax.swing.JLabel;

/**
 *
 * @author Paola
 */
public class PlateMediatorImpl implements PlateMediator {
    
    private JLabel infoLabel;
    private String updatedInfo;

    @Override
    public void updateInfoLabel() {
        infoLabel.setText(updatedInfo);
    }    
}
