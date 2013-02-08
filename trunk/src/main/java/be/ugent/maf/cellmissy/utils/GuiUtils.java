/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Utility class for GUI
 *
 * @author Paola
 */
public class GuiUtils {

    //Available colors
    private static final Color[] availableColors = {Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.YELLOW, new Color(0, 140, 140), new Color(125, 0, 130), Color.GRAY, Color.DARK_GRAY};
    //Colors used for Imaging Type Rendering 
    private static final Color[] imagingTypeColors = {new Color(130, 70, 230), new Color(90, 200, 30), new Color(175, 238, 238)};

    /**
     * Get Default Grid Bag Constraints used for Grid Bag Layout GUI Structures
     *
     * @return GridBagConstraints
     */
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

    /**
     * Check if a parent component already contains a child component
     *
     * @param parentContainer
     * @param childComponent
     * @return boolean
     */
    public static boolean containsComponent(Container parentContainer, Component childComponent) {
        boolean containsComponent = false;
        for (Component component : parentContainer.getComponents()) {
            if (childComponent.equals(component)) {
                containsComponent = true;
            }
        }
        return containsComponent;
    }

    /**
     * Switch between two different panels keeping the same view
     *
     * @param parentPanel
     * @param panelToAdd
     * @param panelToRemove
     */
    public static void switchChildPanels(JPanel parentPanel, JPanel panelToAdd, JPanel panelToRemove) {
        if (!GuiUtils.containsComponent(parentPanel, panelToAdd)) {
            parentPanel.add(panelToAdd, getDefaultGridBagConstraints());
        }
        if (GuiUtils.containsComponent(parentPanel, panelToRemove)) {
            parentPanel.remove(panelToRemove);
        }
    }

    /**
     *
     * @param icon
     * @return
     */
    public static ImageIcon getScaledIcon(Icon icon) {
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        int scale = 2;
        BufferedImage bufferedImage = new BufferedImage(iconWidth / scale, iconHeight / scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        setGraphics(graphics2D);
        graphics2D.scale(0.5, 0.5);
        icon.paintIcon(null, graphics2D, 0, 0);
        graphics2D.dispose();
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        return imageIcon;
    }

    /**
     * set graphics: implementing rendering process
     *
     * @param g2d
     */
    public static void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }
}
