/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 * Rectangular icon to show conditions in a list
 * @author Paola Masuzzo
 */
public class RectIcon implements Icon {

    private final Integer rectHeight = 10;
    private final Integer rectWidth = 25;
    private final Color color;

    /**
     * Constructor with a color
     *
     * @param color
     */
    public RectIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(x, y, rectWidth, rectHeight);
    }

    @Override
    public int getIconWidth() {
        return rectWidth;
    }

    @Override
    public int getIconHeight() {
        return rectHeight;
    }
}
