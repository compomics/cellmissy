/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller;

import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.experiment.PlateSetupPanel;
import be.ugent.maf.cellmissy.gui.plate.PlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
    //private Rectangle currentRect;
    //private Rectangle rectToDraw;
    private Rectangle previousRectDrawn;
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
        //currentRect = null;
        //rectToDraw = null;
        previousRectDrawn = new Rectangle();

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
            //currentRect = new Rectangle(e.getX(), e.getY(), 0, 0);
            Rectangle currentRect = new Rectangle(e.getX(), e.getY(), 0, 0);
            platePanel.setCurrentRect(currentRect);
            
            updateDrawableRectangle(platePanel.getWidth(), platePanel.getHeight());
            //platePanel.drawRect(platePanel.getGraphics(), rectToDraw.x, rectToDraw.y, rectToDraw.width - 1, rectToDraw.height - 1);
            platePanel.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            updateRectSize(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateRectSize(e);
        }
    }

    private void updateDrawableRectangle(int compWidth, int compHeight) {

        //rectangle for the selection should not extend the plate panel area
        if ((platePanel.getCurrentRect().x + platePanel.getCurrentRect().width) > compWidth) {
            platePanel.getCurrentRect().width = compWidth - platePanel.getCurrentRect().x;
        }
        if ((platePanel.getCurrentRect().y + platePanel.getCurrentRect().height) > compHeight) {
            platePanel.getCurrentRect().height = compHeight - platePanel.getCurrentRect().y;
        }

        //update rectToDraw
        if (platePanel.getRectToDraw() != null) {
            previousRectDrawn.setBounds(platePanel.getRectToDraw().x, platePanel.getRectToDraw().y, platePanel.getRectToDraw().width, platePanel.getRectToDraw().height);
            platePanel.getRectToDraw().setBounds(platePanel.getCurrentRect().x, platePanel.getCurrentRect().y, platePanel.getCurrentRect().width, platePanel.getCurrentRect().height);
//        } else {
//            platePanel.getRectToDraw() = new Rectangle(platePanel.getCurrentRect().x, platePanel.getCurrentRect().y, platePanel.getCurrentRect().width, platePanel.getCurrentRect().height);
//        }
        }
    }

    private void updateRectSize(MouseEvent e) {
        platePanel.getCurrentRect().setSize(e.getX() - platePanel.getCurrentRect().x, e.getY() - platePanel.getCurrentRect().y);
        updateDrawableRectangle(platePanel.getWidth(), platePanel.getHeight());
        Rectangle finalRect = platePanel.getRectToDraw().union(previousRectDrawn);
        //platePanel.drawRect(platePanel.getGraphics(), finalRect.x, finalRect.y, finalRect.width, finalRect.height);
        platePanel.repaint(finalRect.x, finalRect.y, finalRect.width, finalRect.height);
    }
}
