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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
    // Available colors (for set-up the experiment)
    private static final Color[] availableColors = {new Color(0, 0, 139), new Color(255, 0, 0), new Color(34, 139, 34), new Color(148, 0, 211), new Color(255, 140, 0), new Color(30, 144, 255), new Color(255, 0, 255), new Color(0, 140, 140), new Color(128, 0, 0), new Color(128, 128, 0)};
    // Colors used for Imaging Type Rendering
    private static final Color[] imagingTypeColors = {new Color(138, 43, 226), new Color(135, 206, 250), new Color(255, 0, 255)};
    // Font for highlighted labels
    private static Font boldFont = new Font("Tahoma", Font.BOLD, 14);
    // Font for normal labels
    private static Font plainFont = new Font("Tahoma", Font.PLAIN, 12);
    // Color for highlighted labels
    private static Color highlightColor = new Color(72, 61, 169);

    /**
     * Getters
     *
     * @return
     */
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
     * Center the dialog on the parent frame.
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
     * Get a scaled icon starting from an icon. The rescaling is performed with
     * the specified integer scale. This is done through a buffered image.
     *
     * @param icon
     * @param scale
     * @return
     */
    public static ImageIcon getScaledIcon(Icon icon) {
        // get icon  size
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        // get the scaled sizes
        int scale = 2;
        int scaledIconWidth = iconWidth / scale;
        int scaledIconHeight = iconHeight / scale;
        // create the buffered image - 8-bit RGBA color components packed into integer pixels
        BufferedImage bufferedImage = new BufferedImage(scaledIconWidth, scaledIconHeight, BufferedImage.TYPE_INT_ARGB);
        // create graphics from the image and scale it
        Graphics2D graphics2D = bufferedImage.createGraphics();
        setGraphics(graphics2D);
        graphics2D.scale(0.5, 0.5);
        // draw the icon
        icon.paintIcon(null, graphics2D, 0, 0);
        // dispose of the graphics
        graphics2D.dispose();
        // create the actual image icon
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        return imageIcon;
    }

    /**
     * set graphics: implementing rendering process for a Graphics2D object
     *
     * @param g2d
     */
    public static void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }

    /**
     * Sets the preferred width of a column of a JTable specified by colIndex.
     * The column will be just wide enough to show the column head and the
     * widest cell in the column (the computation is done per row, then). Margin
     * pixels are added to the left and right (resulting in an additional width
     * of 2*margin pixels).
     *
     * @param table
     * @param colIndex
     * @param margin
     */
    public static void packColumn(JTable table, int colIndex, int margin) {
        // get column model and then column
        DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn column = columnModel.getColumn(colIndex);
        // initialize width to zero
        int width = 0;
        // get width of column header
        TableCellRenderer renderer = column.getHeaderRenderer();
        // if the header is null, just get the default one from the table
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        // get the component used to draw the cell -- for the header, row and coumn: zero
        Component component = renderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, 0);
        width = component.getPreferredSize().width;
        // get maximum width of column data
        // iterate through the rows
        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, colIndex);
            // get the component used to draw the cell -- for the current cell: row and coumn are index r and colIndex
            component = renderer.getTableCellRendererComponent(table, table.getValueAt(r, colIndex), false, false, r, colIndex);
            width = Math.max(width, component.getPreferredSize().width);
        }
        // add margin
        width += 2 * margin;
        // set the width
        column.setPreferredWidth(width);
    }

    /**
     * Highlight label (both color and size)
     *
     * @param label
     */
    public static void highlightLabel(JLabel label) {
        label.setFont(boldFont);
        label.setForeground(highlightColor);
    }

    /**
     * Reset label (both size and color)
     *
     * @param label
     */
    public static void resetLabel(JLabel label) {
        label.setFont(plainFont);
        label.setForeground(defaultColor);
    }
}
