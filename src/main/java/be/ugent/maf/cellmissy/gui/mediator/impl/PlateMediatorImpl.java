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
import be.ugent.maf.cellmissy.repository.WellRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Paola
 */
public class PlateMediatorImpl implements PlateMediator {

    private ButtonPanel buttonPanel;
    private PlatePanel platePanel;

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
        // if ImagingTypeList is null, create a new PlateWorker and execute it             
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
        for (WellGui wellGui : wellGuiList) {
            Well well = wellGui.getWell();
            if (!well.getWellHasImagingTypeCollection().isEmpty()) {
                platePanel.getWellService().save(wellGui.getWell());
            }
        }
    }
}
