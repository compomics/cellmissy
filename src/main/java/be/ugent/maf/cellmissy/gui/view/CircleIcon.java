/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 * Circular icon to show imaging types used
 * @author Paola Masuzzo
 */
public class CircleIcon implements Icon {

    private final Integer circleHeight = 10;
    private final Integer circleWidth = 10;
    private Color color;

    /**
     * Constructor with a color
     *
     * @param color
     */
    public CircleIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillOval(x, y, circleWidth, circleHeight);
    }

    @Override
    public int getIconWidth() {
        return circleWidth;
    }

    @Override
    public int getIconHeight() {
        return circleHeight;
    }
}
