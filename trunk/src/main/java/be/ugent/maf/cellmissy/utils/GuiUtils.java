/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Utility class for GUI
 *
 * @author Paola
 */
public class GuiUtils {

    // default color: Black
    private static final Color defaultColor = new Color(0, 0, 0);
    // color for not-imaged wells
    private static final Color nonImagedColor = new Color(169, 169, 169);
    //Available colors (for set-up the experiment)
    private static final Color[] availableColors = {new Color(0, 0, 139), new Color(255, 0, 0), new Color(34, 139, 34), new Color(148, 0, 211), new Color(255, 140, 0), new Color(30, 144, 255), new Color(255, 0, 255), new Color(0, 140, 140), new Color(128, 0, 0), new Color(128, 128, 0)};
    //Colors used for Imaging Type Rendering 
    private static final Color[] imagingTypeColors = {new Color(173, 255, 47), new Color(175, 238, 238), new Color(219, 112, 147)};

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

    // getters
    public static Color[] getAvailableColors() {
        return availableColors;
    }

    public static Color getDefaultColor() {
        return defaultColor;
    }

    public static Color getNonImagedColor() {
        return nonImagedColor;
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
     * Centers the dialog on the parent frame.
     *
     * @param parentFrame the parent frame
     * @param dialog the dialog
     */
    public static void centerDialogOnFrame(JFrame parentFrame, JDialog dialog) {
        Point topLeft = parentFrame.getLocationOnScreen();
        Dimension parentSize = parentFrame.getSize();
        Dimension dialogSize = dialog.getSize();
        int x = 0;
        int y = 0;
        if (parentSize.width > dialogSize.width) {
            x = ((parentSize.width - dialogSize.width) / 2) + topLeft.x;
        } else {
            x = topLeft.x;
        }
        if (parentSize.height > dialogSize.height) {
            y = ((parentSize.height - dialogSize.height) / 2) + topLeft.y;
        } else {
            y = topLeft.y;
        }
        dialog.setLocation(x, y);
    }

    /**
     * Gets the name of the component currently visible in the card layout.
     *
     * @param parentContainer the parent container
     * @return the component name
     */
    public static String getCurrentCardName(Container parentContainer) {
        CardLayout cardLayout = (CardLayout) parentContainer.getLayout();
        if (cardLayout == null) {
            throw new IllegalArgumentException("The layout of the parent container is no card layout.");
        }
        JPanel card = null;
        for (Component component : parentContainer.getComponents()) {
            if (component.isVisible()) {
                card = (JPanel) component;
                break;
            }
        }
        return card.getName();
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
        BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }
}
