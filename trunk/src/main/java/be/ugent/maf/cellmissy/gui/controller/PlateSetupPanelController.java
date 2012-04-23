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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
    private Rectangle currentRectangle;
    //view
    private PlateSetupPanel plateSetupPanel;
    private PlatePanel platePanel;
    //parent controller
    private ExperimentSetupPanelController experimentSetupPanelController;
    //services
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    public PlateSetupPanelController(ExperimentSetupPanelController experimentSetupPanelController) {
        this.experimentSetupPanelController = experimentSetupPanelController;

        plateSetupPanel = new PlateSetupPanel();
        //init services
        plateService = (PlateService) experimentSetupPanelController.getCellMissyController().getBeanByName("plateService");

        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        currentRectangle = null;

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

        @Override
        public void mousePressed(MouseEvent e) {
            currentRectangle = new Rectangle(e.getX(), e.getY(), 0, 0);
            updateDrawableRectangle(platePanel.getWidth(), platePanel.getHeight());
            platePanel.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }

    private void updateDrawableRectangle(int compWidth, int compHeight) {

        int x = currentRectangle.x;
        int y = currentRectangle.y;
        int width = currentRectangle.width;
        int height = currentRectangle.height;

        if ((x + width) > compWidth) {
            width = compWidth - x;
        }
        if ((y + height) > compHeight) {
            height = compHeight - y;
        }
        
       
    }
}
