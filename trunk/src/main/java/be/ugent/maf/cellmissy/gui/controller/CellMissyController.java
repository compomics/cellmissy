/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.gui.CellMissyFrame;
import java.awt.GridBagConstraints;

/**
 *
 * @author Paola
 */
public class CellMissyController {
    
    //main frame
    CellMissyFrame cellMissyFrame;
    
    //child controllers
    UserPanelController userPanelController;
    
    public CellMissyController(CellMissyFrame cellMissyFrame){
        this.cellMissyFrame = cellMissyFrame;
        userPanelController = new UserPanelController(this);
        initFrame();
    }
    
    private void initFrame(){
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        
        //add panel components to main frame
        cellMissyFrame.getUserParentPanel().add(userPanelController.getUserPanel(), gridBagConstraints);
    }
    
}
