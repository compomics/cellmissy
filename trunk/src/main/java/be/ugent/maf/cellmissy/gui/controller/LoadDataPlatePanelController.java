/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.plate.LoadDataPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.List;
import javax.swing.SwingWorker;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Paola Masuzzo
 */
public class LoadDataPlatePanelController {

    //model
    //view
    private LoadDataPlatePanel loadDataPlatePanel;
    //parent controller
    private LoadExperimentPanelController loadExperimentPanelController;
    //child controllers
    //services
    private ApplicationContext context;
    private PlateService plateService;
    private WellService wellService;
    private GridBagConstraints gridBagConstraints;
    private boolean isEnable;
    private boolean isFirtTime;

    /**
     * constructor (parent Controller)
     * @param loadExperimentPanelController 
     */
    public LoadDataPlatePanelController(LoadExperimentPanelController loadExperimentPanelController) {

        this.loadExperimentPanelController = loadExperimentPanelController;
        //init views
        loadDataPlatePanel = new LoadDataPlatePanel();

        //init services
        context = ApplicationContextProvider.getInstance().getApplicationContext();
        plateService = (PlateService) context.getBean("plateService");
        wellService = (WellService) context.getBean("wellService");
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //first time that data are processed
        isFirtTime = true;
        //disable mouse Listener
        isEnable = false;

        initLoadDataPlatePanel();
    }

    /**
     * getters and setters
     */
    public LoadDataPlatePanel getLoadDataPlatePanel() {
        return loadDataPlatePanel;
    }

    public void setIsFirtTime(boolean isFirtTime) {
        this.isFirtTime = isFirtTime;
    }

    /**
     * private methods and classes
     */
    private void initLoadDataPlatePanel() {

        //show as default a 96 plate format
        Dimension parentDimension = loadExperimentPanelController.getLoadExperimentPanel().getLoadDataPlateParentPanel().getSize();

        loadDataPlatePanel.initPanel(plateService.findByFormat(96), parentDimension);
        loadExperimentPanelController.getLoadExperimentPanel().getLoadDataPlateParentPanel().add(loadDataPlatePanel, gridBagConstraints);
        loadExperimentPanelController.getLoadExperimentPanel().getLoadDataPlateParentPanel().repaint();

        /**
         * add action listeners
         */
        //forward button
        loadExperimentPanelController.getLoadExperimentPanel().getForwardButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //add mouse listener to the plate
                isEnable = true;
                // process first Imaging Type data:
                // ImagingTypeList is null, create a new PlateWorker and execute it             
                if (loadDataPlatePanel.getImagingTypeList() == null) {
                    loadExperimentPanelController.getLoadExperimentPanel().getForwardButton().setEnabled(false);

                    loadExperimentPanelController.getLoadExperimentPanel().getjProgressBar1().setIndeterminate(true);
                    PlateWorker plateWorker = new PlateWorker();
                    //set cursor to wait
                    loadExperimentPanelController.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    plateWorker.execute();
                } else {
                    //check if data are being processed for the first time                    
                    if (isFirtTime) {
                        // forward to next Imaging Type
                        List<ImagingType> imagingTypeList = loadDataPlatePanel.getImagingTypeList();

                        // check if there are still more Imaging Types
                        if (loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType()) < imagingTypeList.size() - 1) {
                            // get next Imaging Type
                            ImagingType imagingType = imagingTypeList.get(loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType()) + 1);
                            loadDataPlatePanel.setCurrentImagingType(imagingType);
                            // update info Label
                            String message = "Select first well imaged with " + imagingType.getName() + " (imaging type " + (imagingTypeList.indexOf(imagingType) + 1) + "/" + imagingTypeList.size() + ")";
                            loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), message);
                            loadExperimentPanelController.getLoadExperimentPanel().getForwardButton().setEnabled(false);
                        }
                    } else {
                        //if not, a call to cancel Method has been done ! Need to process data from the beginning
                        //set as current imaging type the first one of the list
                        loadDataPlatePanel.setCurrentImagingType(loadDataPlatePanel.getImagingTypeList().get(0));
                        // ask the user to select first well for the imaging type
                        String message = "Select first well imaged with " + loadDataPlatePanel.getCurrentImagingType().getName() + " (imaging type " + (loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType()) + 1) + "/" + loadDataPlatePanel.getImagingTypeList().size() + ")";
                        loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), message);
                        isFirtTime = true;
                    }
                }
            }
        });

        /**
         * add mouse listeners
         */
        loadDataPlatePanel.addMouseListener(new MouseAdapter() {

            // if the mouse has been pressed and released on a wellGui, set it as firstWellGui and show imaged wells
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnable == true) {
                    WellGui firstWellGui = null;
                    for (WellGui wellGUI : loadDataPlatePanel.getWellGuiList()) {
                        List<Ellipse2D> ellipsi = wellGUI.getEllipsi();
                        if ((e.getButton() == 1) && ellipsi.get(0).contains(e.getX(), e.getY())) {
                            firstWellGui = wellGUI;
                            break;
                        }
                    }
                    showImagedWells(firstWellGui);
                    //check if there are more imaging types to process
                    if (loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType()) == loadDataPlatePanel.getImagingTypeList().size() - 1) {
                        // there are no more imaging types to process, the experiment can be saved to DB
                        loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), "Click <<Cancel>> to reset plate view or <<Finish>> to save the experiment");
                        //disable Forward button
                        loadExperimentPanelController.getLoadExperimentPanel().getForwardButton().setEnabled(false);
                        // enable Cancel and Finish buttons
                        loadExperimentPanelController.getLoadExperimentPanel().getCancelButton().setEnabled(true);
                        loadExperimentPanelController.getLoadExperimentPanel().getFinishButton().setEnabled(true);
                    } else {
                        // ask the user to click on Forward button to proceed with next imaging type
                        loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), "Click <<Forward>> to proceed with next imaging type.");
                        loadExperimentPanelController.getLoadExperimentPanel().getForwardButton().setEnabled(true);
                    }
                }
                isEnable = false;
            }
        });
    }

    /**
     * compute the concentric ellipsi to show all imaged wells according to imaging types found
     * @param firstWellGui 
     */
    private void showImagedWells(WellGui firstWellGui) {
        //update WellGuiList and show imaged wells positions on the plate
        wellService.updateWellGuiListWithImagingType(loadDataPlatePanel.getCurrentImagingType(), loadDataPlatePanel.getPlateFormat(), firstWellGui, loadDataPlatePanel.getWellGuiList());
        int currentImagingTypeIndex = loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType());

        if (currentImagingTypeIndex != 0) {
            for (WellGui wellGui : loadDataPlatePanel.getWellGuiList()) {
                if (containsImagingType(wellGui.getWell().getWellHasImagingTypeCollection(), loadDataPlatePanel.getCurrentImagingType())) {

                    List<Ellipse2D> ellipsi = wellGui.getEllipsi();
                    // get the bigger ellipsi and calculate factors for the new ones (concentric wells)
                    Ellipse2D ellipse2D = ellipsi.get(currentImagingTypeIndex - 1);
                    double size = ellipse2D.getHeight();
                    double newSize = (size / loadDataPlatePanel.getUniqueImagingTypes(wellGui.getWell().getWellHasImagingTypeCollection()).size());
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
        loadDataPlatePanel.repaint();
    }

    /**
     * check if a wellHasImagingType contains a certain imaging type
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
     * swing worker
     */
    private class PlateWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            //show progress bar
            loadExperimentPanelController.getLoadExperimentPanel().getjProgressBar1().setVisible(true);

            //init wellService: init also CellMiaData Service and MicroscopeData Service
            wellService.init(loadExperimentPanelController.getExperiment());
            // get the list of imaging types
            List<ImagingType> imagingTypes = wellService.getImagingTypes();
            loadDataPlatePanel.setImagingTypeList(imagingTypes);

            loadDataPlatePanel.setAlgoMap(wellService.getMap());

            return null;
        }

        @Override
        protected void done() {

            //set cursor back to normal
            loadExperimentPanelController.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            loadExperimentPanelController.getLoadExperimentPanel().getjProgressBar1().setVisible(false);
            // get first Imaging Type
            loadDataPlatePanel.setCurrentImagingType(loadDataPlatePanel.getImagingTypeList().get(0));
            // ask the user to select first well for the imaging type
            String message = "Select first well imaged with " + loadDataPlatePanel.getCurrentImagingType().getName() + " (imaging type " + (loadDataPlatePanel.getImagingTypeList().indexOf(loadDataPlatePanel.getCurrentImagingType()) + 1) + "/" + loadDataPlatePanel.getImagingTypeList().size() + ")";
            loadExperimentPanelController.updateInfoLabel(loadExperimentPanelController.getLoadExperimentPanel().getInfolabel(), message);
        }
    }
}
