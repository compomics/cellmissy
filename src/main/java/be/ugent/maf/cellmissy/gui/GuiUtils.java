/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;

/**
 *
 * @author Paola
 */
public class GuiUtils {

    private static final Color[] availableColors = {Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.YELLOW, new Color(0, 140, 140), new Color(125, 0, 130), Color.GRAY, Color.DARK_GRAY};
    
    private static final Color[] imagingTypeColors = {new Color(130, 70, 230), new Color(90, 200, 30)};

    public static GridBagConstraints getDefaultGridBagConstraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        return gridBagConstraints;
    }

    public static Color[] getAvailableColors() {
        return availableColors;
    }

    public static Color[] getImagingTypeColors() {
        return imagingTypeColors;
    }

    public static boolean containsComponent(Container parentContainer, Component childComponent) {
        boolean containsComponent = false;
        for (Component component : parentContainer.getComponents()) {
            if (childComponent.equals(component)) {
                containsComponent = true;
            }
        }
        return containsComponent;
    }

    public static void switchChildPanels(JPanel parentPanel, JPanel panelToAdd, JPanel panelToRemove) {
        if (!GuiUtils.containsComponent(parentPanel, panelToAdd)) {
            parentPanel.add(panelToAdd, getDefaultGridBagConstraints());
        }
        if (GuiUtils.containsComponent(parentPanel, panelToRemove)) {
            parentPanel.remove(panelToRemove);
        }
    }
}
