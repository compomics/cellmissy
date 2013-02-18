/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.cellmia;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.WellService;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Imaged Plate Controller: show conditions and highlight imaged wells on plate view Parent Controller: Load Experiment Controller
 *
 * @author Paola Masuzzo
 */
@Controller("imagedPlateController")
public class CellMiaImagedPlateController {

    //model
    //view
    private ImagedPlatePanel imagedPlatePanel;
    //parent controller
    @Autowired
    private LoadExperimentFromCellMiaController loadExperimentFromCellMiaController;
    //child controllers
    //services
    @Autowired
    private PlateService plateService;
    @Autowired
    private WellService wellService;
    private GridBagConstraints gridBagConstraints;
    private boolean isEnable;
    private boolean isFirtTime;

    /**
     * initialize controller
     */
    public void init() {
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        //create panels
        imagedPlatePanel = new ImagedPlatePanel();
        //first time that data are processed
        isFirtTime = true;
        //disable mouse Listener
        isEnable = false;
        //init views
        initLoadDataPlatePanel();
    }

    /**
     * getters and setters
     *
     * @return
     */
    public ImagedPlatePanel getImagedPlatePanel() {
        return imagedPlatePanel;
    }

    public void setIsFirtTime(boolean isFirtTime) {
        this.isFirtTime = isFirtTime;
    }

    /**
     * Compute number of samples that need to be stored
     * @return
     */
    public int getNumberOfSamples() {
        return wellService.getNumberOfSamples();
    }

    /**
     * private methods and classes
     */
    private void initLoadDataPlatePanel() {

        //show as default a 96 plate format
        Dimension parentDimension = loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getPlateViewParentPanel().getSize();
        imagedPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getPlateViewParentPanel().add(imagedPlatePanel, gridBagConstraints);
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getPlateViewParentPanel().repaint();

        /**
         * add action listeners
         */
        //forward button
        loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add mouse listener to the plate
                isEnable = true;
                // process first Imaging Type data:
                // ImagingTypeList is null, create a new PlateWorker and execute it             
                if (imagedPlatePanel.getImagingTypeList() == null) {
                    loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().setEnabled(false);
                    loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getSaveDataProgressBar().setIndeterminate(true);
                    PlateWorker plateWorker = new PlateWorker();
                    //set cursor to wait
                    loadExperimentFromCellMiaController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    plateWorker.execute();
                } else {
                    //check if data are being processed for the first time                    
                    if (isFirtTime) {
                        // forward to next Imaging Type
                        List<ImagingType> imagingTypeList = imagedPlatePanel.getImagingTypeList();

                        // check if there are still more Imaging Types
                        if (imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) < imagingTypeList.size() - 1) {
                            // get next Imaging Type
                            ImagingType imagingType = imagingTypeList.get(imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) + 1);
                            imagedPlatePanel.setCurrentImagingType(imagingType);
                            // update info Label
                            String message = "Please select first well imaged with " + imagingType.getName() + " (imaging type " + (imagingTypeList.indexOf(imagingType) + 1) + "/" + imagingTypeList.size() + ")" + "\nExposure time: " + imagingType.getExposureTime() + " " + imagingType.getExposureTimeUnit() + ", Light intensity: " + imagingType.getLightIntensity() + " V";
                            loadExperimentFromCellMiaController.showMessage(message, 1);
                            message = "Select first well imaged.";
                            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), message);
                            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().setEnabled(false);
                        }
                    } else {
                        //if not, a call to cancel Method has been done ! Need to process data from the beginning
                        //set as current imaging type the first one of the list
                        imagedPlatePanel.setCurrentImagingType(imagedPlatePanel.getImagingTypeList().get(0));
                        // ask the user to select first well for the imaging type
                        String message = "Please select first well imaged with " + imagedPlatePanel.getCurrentImagingType().getName() + " (imaging type " + (imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) + 1) + "/" + imagedPlatePanel.getImagingTypeList().size() + ")" + "\nExposure time: " + imagedPlatePanel.getCurrentImagingType().getExposureTime() + " " + imagedPlatePanel.getCurrentImagingType().getExposureTimeUnit() + ", Light intensity: " + imagedPlatePanel.getCurrentImagingType().getLightIntensity() + " V";
                        loadExperimentFromCellMiaController.showMessage(message, 1);
                        message = "Select first well imaged.";
                        loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), message);
                        isFirtTime = true;
                    }
                }
            }
        });

        /**
         * add mouse listeners
         */
        imagedPlatePanel.addMouseListener(new MouseAdapter() {
            // if the mouse has been pressed and released on a wellGui, set it as firstWellGui and show imaged wells
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnable == true) {
                    WellGui firstWellGui = null;
                    for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
                        List<Ellipse2D> ellipsi = wellGui.getEllipsi();
                        if ((e.getButton() == 1) && ellipsi.get(0).contains(e.getX(), e.getY())) {
                            firstWellGui = wellGui;
                            break;
                        }
                    }
                    showImagedWells(firstWellGui);
                    //check if selection is valid
                    if (validateSelection()) {
                        onForward();
                    } else {
                        //show a warning message
                        String message = "Some wells chosen do not have a condition; these wells will not be stored. Confirm this selection?";
                        String title = "Error in Selection!";
                        int showConfirmDialog = loadExperimentFromCellMiaController.showConfirmDialog(message, title, JOptionPane.YES_NO_OPTION);
                        if (showConfirmDialog == JOptionPane.YES_OPTION) {
                            //selection was confirmed: proceed
                            onForward();
                        } else {
                            //selection was not confirmed: cancel it
                            onCancel();
                            message = "Select again first well imaged with " + imagedPlatePanel.getCurrentImagingType().getName() + " (imaging type " + (imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) + 1) + "/" + imagedPlatePanel.getImagingTypeList().size() + ")" + "\nExposure time: " + imagedPlatePanel.getCurrentImagingType().getExposureTime() + " " + imagedPlatePanel.getCurrentImagingType().getExposureTimeUnit() + ", Light intensity: " + imagedPlatePanel.getCurrentImagingType().getLightIntensity() + " V";
                            loadExperimentFromCellMiaController.showMessage(message, 1);
                        }
                    }
                }
            }
        });
    }

    /**
     * action performed on Forward button: proceed with next imaging type or save the experiment to DB
     */
    private void onForward() {
        //check if there are more imaging types to process
        if (imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) == imagedPlatePanel.getImagingTypeList().size() - 1) {
            // there are no more imaging types to process, the experiment can be saved to DB
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), "Click <<Cancel>> to reset plate view or <<Finish>> to save the experiment");
            //disable Forward button
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().setEnabled(false);
            // enable Cancel and Finish buttons
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getCancelButton().setEnabled(true);
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getFinishButton().setEnabled(true);
        } else {
            // ask the user to click on Forward button to proceed with next imaging type
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), "Click <<Forward>> to proceed with next imaging type.");
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getForwardButton().setEnabled(true);
        }
        isEnable = false;
    }

    /**
     * if a selection is not validated, reset plate view (ONLY for current imaging type)
     */
    private void onCancel() {
        for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
            //empty the collection of WellHasImagingType (so color is set to default) but ONLY for current Imaging Type
            Iterator<WellHasImagingType> iterator = wellGui.getWell().getWellHasImagingTypeCollection().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getImagingType().equals(imagedPlatePanel.getCurrentImagingType())) {
                    iterator.remove();
                }
            }
            //remove ellipse2D for current imaging type
            List<Ellipse2D> ellipse2DList = new ArrayList<>();

            for (Ellipse2D ellipse2D : wellGui.getEllipsi()) {
                if (wellGui.getEllipsi().indexOf(ellipse2D) == imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) && wellGui.getEllipsi().indexOf(ellipse2D) > 0) {
                    ellipse2DList.add(ellipse2D);
                }
            }
            wellGui.getEllipsi().removeAll(ellipse2DList);
        }
        imagedPlatePanel.repaint();
    }

    /**
     * compute the concentric ellipsi to show all imaged wells according to imaging types found
     *
     * @param firstWellGui
     */
    private void showImagedWells(WellGui firstWellGui) {
        //update WellGuiList and show imaged wells positions on the plate
        wellService.updateWellGuiListWithImagingType(imagedPlatePanel.getCurrentImagingType(), imagedPlatePanel.getPlateFormat(), firstWellGui, imagedPlatePanel.getWellGuiList());
        int currentImagingTypeIndex = imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType());

        if (currentImagingTypeIndex != 0) {
            for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
                if (containsImagingType(wellGui.getWell().getWellHasImagingTypeCollection(), imagedPlatePanel.getCurrentImagingType())) {

                    List<Ellipse2D> ellipsi = wellGui.getEllipsi();
                    // get the bigger ellipsi and calculate factors for the new ones (concentric wells)
                    Ellipse2D ellipse2D = ellipsi.get(currentImagingTypeIndex - 1);
                    double size = ellipse2D.getHeight();
                    double newSize = (size / imagedPlatePanel.getUniqueImagingTypes(wellGui.getWell().getWellHasImagingTypeCollection()).size());
                    double newTopLeftX = ellipse2D.getCenterX() - (newSize / 2);
                    double newTopLeftY = ellipse2D.getCenterY() - (newSize / 2);

                    if (newSize != size) {
                        Ellipse2D newEllipse2D = new Ellipse2D.Double(newTopLeftX, newTopLeftY, newSize, newSize);
                        // add the new Ellipse2D to the ellipsi List
                        ellipsi.add(newEllipse2D);
                    }
                }
            }
        }
        // this calls the paintComponent method
        imagedPlatePanel.repaint();
    }

    /**
     * this method validates the selection on the plate
     *
     * @return true if the selection is valid
     */
    private boolean validateSelection() {
        boolean isSelectionValid = true;

        for (WellGui wellGui : imagedPlatePanel.getWellGuiList()) {
            //check if the wellGui was imaged
            if (!wellGui.getWell().getWellHasImagingTypeCollection().isEmpty()) {
                //check if the imaged wellGui has a condition
                if (wellGui.getRectangle() == null) {
                    isSelectionValid = false;
                    break;
                }
            }
        }
        return isSelectionValid;
    }

    /**
     * check if a wellHasImagingType contains a certain imaging type
     *
     * @param wellHasImagingTypes
     * @param imagingType
     * @return
     */
    private boolean containsImagingType(Collection<WellHasImagingType> wellHasImagingTypes, ImagingType imagingType) {
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (wellHasImagingType.getImagingType().equals(imagingType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Swing worker to parse all samples files
     */
    private class PlateWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            //show progress bar
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getSaveDataProgressBar().setVisible(true);
            //init wellService: init also CellMiaData Service and MicroscopeData Service
            wellService.init(loadExperimentFromCellMiaController.getExperiment());
            int numberOfSamples = getNumberOfSamples();
            String message = numberOfSamples + " samples are being processed. Please wait.";
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), message);
            // get the list of imaging types
            List<ImagingType> imagingTypes = wellService.getImagingTypes();
            imagedPlatePanel.setImagingTypeList(imagingTypes);
            Map<Algorithm, Map<ImagingType, List<WellHasImagingType>>> algoMap = wellService.getMap();
            imagedPlatePanel.setAlgoMap(algoMap);
            return null;
        }

        @Override
        protected void done() {
            //set cursor back to normal
            loadExperimentFromCellMiaController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getSaveDataProgressBar().setVisible(false);
            // get first Imaging Type
            imagedPlatePanel.setCurrentImagingType(imagedPlatePanel.getImagingTypeList().get(0));
            // ask the user to select first well for the imaging type
            String message = "";
            List<String> list = new ArrayList<>();

            String string = "Imaging data was successfully processed";
            list.add(string);
            string = wellService.getImagingTypes().size() + " imaging type(s) and " + imagedPlatePanel.getAlgoMap().keySet().size() + " algorithm(s) were found";
            list.add(string);
            string = "Please select first well imaged with " + imagedPlatePanel.getCurrentImagingType().getName() + " (imaging type " + (imagedPlatePanel.getImagingTypeList().indexOf(imagedPlatePanel.getCurrentImagingType()) + 1) + "/" + imagedPlatePanel.getImagingTypeList().size() + ")" + "\nExposure time: " + imagedPlatePanel.getCurrentImagingType().getExposureTime() + " " + imagedPlatePanel.getCurrentImagingType().getExposureTimeUnit() + ", Light intensity: " + imagedPlatePanel.getCurrentImagingType().getLightIntensity() + " V";
            list.add(string);

            for (String s : list) {
                message += s + "\n";
            }
            loadExperimentFromCellMiaController.showMessage(message, JOptionPane.INFORMATION_MESSAGE);
            message = "Select first well imaged.";
            loadExperimentFromCellMiaController.updateInfoLabel(loadExperimentFromCellMiaController.getLoadFromCellMiaPanel().getInfolabel(), message);
        }
    }
}
