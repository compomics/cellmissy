/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.PlateSetupPanel;
import be.ugent.maf.cellmissy.gui.plate.PlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import be.ugent.maf.cellmissy.service.PlateService;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.event.MouseInputAdapter;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author Paola
 */
public class PlateSetupPanelController {

    //model
    private ObservableList<PlateFormat> plateFormatBindingList;
    private BindingGroup bindingGroup;
    //view
    private PlateSetupPanel plateSetupPanel;
    private PlatePanel platePanel;
    //parent controller
    private ExperimentSetupPanelController experimentSetupPanelController;
    //services
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;
    private Rectangle rectangle;

    public PlateSetupPanelController(ExperimentSetupPanelController experimentSetupPanelController) {
        this.experimentSetupPanelController = experimentSetupPanelController;

        plateSetupPanel = new PlateSetupPanel();
        //init services
        plateService = (PlateService) experimentSetupPanelController.getCellMissyController().getBeanByName("plateService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();

        //init views
        initPlateSetupPanel();
    }

    private void initPlateSetupPanel() {
        //init plate panel and add it to the bottom panel 
        platePanel = new PlatePanel();
        plateSetupPanel.getBottomPanel().add(platePanel, gridBagConstraints);

        //init plateFormatJcombo
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JComboBoxBinding jComboBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, plateFormatBindingList, plateSetupPanel.getPlateFormatComboBox());
        bindingGroup.addBinding(jComboBoxBinding);
        bindingGroup.bind();

        //add mouse listener
        PlateListener plateListener = new PlateListener();
        platePanel.addMouseListener(plateListener);
        platePanel.addMouseMotionListener(plateListener);

        // add action listener
        plateSetupPanel.getPlateFormatComboBox().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PlateFormat selectedPlateFormat = plateFormatBindingList.get(plateSetupPanel.getPlateFormatComboBox().getSelectedIndex());
                Dimension parentDimension = plateSetupPanel.getBottomPanel().getSize();
                platePanel.initPanel(selectedPlateFormat, parentDimension);
                plateSetupPanel.getBottomPanel().repaint();
            }
        });

        // show 96 plate format as default
        // after adding the listener
        plateSetupPanel.getPlateFormatComboBox().setSelectedIndex(0);

        experimentSetupPanelController.getExperimentSetupPanel().getPlateSetupParentPanel().add(plateSetupPanel, gridBagConstraints);
    }

    private class PlateListener extends MouseInputAdapter {

        private int xMin;
        private int xMax;
        private int yMin;
        private int yMax;

        @Override
        public void mousePressed(MouseEvent e) {

            platePanel.setStartPoint(e.getPoint());
            platePanel.setEndPoint(platePanel.getStartPoint());
            xMin = platePanel.getStartPoint().x;
            xMax = platePanel.getStartPoint().x;
            yMin = platePanel.getStartPoint().y;
            yMax = platePanel.getStartPoint().y;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            platePanel.setEndPoint(e.getPoint());
            xMin = Math.min(xMin, platePanel.getEndPoint().x);
            xMax = Math.max(xMax, platePanel.getEndPoint().x);
            yMin = Math.min(yMin, platePanel.getEndPoint().y);
            yMax = Math.max(yMax, platePanel.getEndPoint().y);
            platePanel.repaint(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1);

        }

        @Override
        public void mouseReleased(MouseEvent e) {

            int x = Math.min(platePanel.getStartPoint().x, platePanel.getEndPoint().x);
            int y = Math.min(platePanel.getStartPoint().y, platePanel.getEndPoint().y);
            int width = Math.abs(platePanel.getStartPoint().x - platePanel.getEndPoint().x);
            int height = Math.abs(platePanel.getStartPoint().y - platePanel.getEndPoint().y);
            rectangle = new Rectangle(x, y, width, height);

            if (rectangle.width != 0 || rectangle.height != 0) {

                if (rectangle.x + rectangle.width > platePanel.getWidth()) {
                    rectangle.width = platePanel.getWidth() - rectangle.x;
                }

                if (rectangle.y + rectangle.height > platePanel.getHeight()) {
                    rectangle.height = platePanel.getHeight() - rectangle.y;
                }

                platePanel.getRectanglesToDrawList().add(rectangle);
            }

            platePanel.setStartPoint(null);
            colorSelectedWells();
            platePanel.repaint(rectangle);
        }
    }
    
    private void colorSelectedWells(){
        for(WellGui wellGui : platePanel.getWellGuiList()){
            Ellipse2D defaultWell = wellGui.getEllipsi().get(0);
            if(rectangle.contains(defaultWell.getX(), defaultWell.getY(), defaultWell.getWidth(), defaultWell.getHeight())){
               
                platePanel.repaint(rectangle);
            }
        }
    }
}
