/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.analysis.doseresponse;

import be.ugent.maf.cellmissy.entity.result.doseresponse.DoseResponseAnalysisGroup;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.ChooseTreatmentDialog;
import be.ugent.maf.cellmissy.gui.experiment.analysis.doseresponse.DRInputPanel;
import be.ugent.maf.cellmissy.gui.view.table.model.NonEditableTableModel;
import javax.swing.JTable;
import org.jdesktop.beansbinding.BindingGroup;

/**
 *
 * @author Gwendolien Sergeant
 */
public abstract class DRInputController {
    
    //model
    protected NonEditableTableModel sharedTableModel;
    protected JTable slopesTable;
    protected BindingGroup bindingGroup;
    //view
    protected DRInputPanel dRInputPanel;
    protected ChooseTreatmentDialog chooseTreatmentDialog;
    
    
    /**
     * Getters and setters
     *
     * @return
     */
    public DRInputPanel getdRInputPanel() {
        return dRInputPanel;
    }

    public ChooseTreatmentDialog getChooseTreatmentDialog() {
        return chooseTreatmentDialog;
    }

    public NonEditableTableModel getTableModel() {
        return sharedTableModel;
    }

    protected void setTableModel(NonEditableTableModel tableModel) {
        this.sharedTableModel = tableModel;
    }
    
    public void init() {
        bindingGroup = new BindingGroup();
        sharedTableModel = new NonEditableTableModel();
        //init view
        initDRInputPanel();
    }
    
    /**
     * Abstract methods
     */
    
    public abstract void initDRInputData();
    
    protected abstract void initDRInputPanel();
    
    protected abstract void addToDRAnalysis();
    
    protected abstract void removeFromDRAnalysis();
    
}
