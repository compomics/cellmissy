/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.mediator.impl;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.gui.ButtonPanel;
import be.ugent.maf.cellmissy.gui.mediator.PlateMediator;
import be.ugent.maf.cellmissy.gui.plate.PlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Paola
 */
public class PlateMediatorImpl implements PlateMediator {
    
    private ButtonPanel buttonPanel;
    private PlatePanel platePanel;
    private static final Logger LOG = Logger.getLogger(PlateMediator.class);
    
    public void setButtonPanel(ButtonPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }
    
    @Override
    public void setPlatePanel(PlatePanel platePanel) {
        this.platePanel = platePanel;
    }
    
    @Override
    public void updateInfoMessage(String infoMessage) {
        buttonPanel.getInfoLabel().setText(infoMessage);
    }
    
    @Override
    public void onForward() {
        // process first Imaging Type data:
        // ImagingTypeList is null, create a new PlateWorker and execute it             
        if (platePanel.getImagingTypeList() == null) {
            PlatePanel.PlateWorker plateWorker = platePanel.new PlateWorker();
            plateWorker.execute();
        } else {
            // forward to next Imaging Type
            List<ImagingType> imagingTypeList = platePanel.getImagingTypeList();
            int currentImagingTypeIndex = imagingTypeList.indexOf(platePanel.getCurrentImagingType());
            // check if there are still more Imaging Types
            if (currentImagingTypeIndex < imagingTypeList.size() - 1) {
                // get next Imaging Type
                ImagingType currentImagingType = imagingTypeList.get(currentImagingTypeIndex + 1);
                platePanel.setCurrentImagingType(currentImagingType);
                // update info Label
                buttonPanel.getInfoLabel().setText("Select first well for " + currentImagingType.getName() + " (imaging type " + (imagingTypeList.indexOf(currentImagingType) + 1) + "/" + imagingTypeList.size() + ")");
            }
        }
    }
    
    @Override
    public void enableFinishButton() {
        buttonPanel.getFinishButton().setEnabled(true);
    }
    
    @Override
    public void disableFinishButton() {
        buttonPanel.getFinishButton().setEnabled(false);
    }
    
    @Override
    public void saveWells() {
        List<WellGui> wellGuiList = platePanel.getWellGuiList();
        long currentTimeMillis = System.currentTimeMillis();
        for (WellGui wellGui : wellGuiList) {
            Well well = wellGui.getWell();
            // if the well was imaged, save it to DB
            if (!well.getWellHasImagingTypeCollection().isEmpty()) {
                platePanel.getWellService().save(well);
            }
        }
        long currentTimeMillis1 = System.currentTimeMillis();
        LOG.debug("Time to save wells: " + ((currentTimeMillis1 - currentTimeMillis) / 1000) + " s");
    }
    
    @Override
    public void showProgressBar() {
        buttonPanel.getjProgressBar1().setVisible(true);
    }
    
    @Override
    public void hideProgressBar() {
        buttonPanel.getjProgressBar1().setVisible(false);
    }
}
