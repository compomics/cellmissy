/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.mediator;

import javax.swing.JLabel;

/**
 *
 * @author Paola
 */
public interface PlateMediator {

    void setInfoLabel(JLabel infoLabel);

    void setUpdatedInfo(String updatedInfo);

    void updateInfoLabel();
}
