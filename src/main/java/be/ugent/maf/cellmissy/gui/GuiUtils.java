/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import java.awt.GridBagConstraints;

/**
 *
 * @author Paola
 */
public class GuiUtils {
    
    public static GridBagConstraints getDefaultGridBagConstraints(){
        GridBagConstraints gridBagConstraints  = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        return gridBagConstraints;
    }
    
}
