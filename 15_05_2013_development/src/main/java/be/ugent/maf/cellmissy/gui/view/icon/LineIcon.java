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
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class LineIcon implements Icon {

    // the stroke-width property
    private Integer lineHeight;
    private Integer lineWidth;
    // the stroke-colour property
    private Color color;

    /**
     * Constructor: takes an integer for the line height, one per its width and
     * a color.
     *
     * @param color
     */
    public LineIcon(Integer lineHeight, Integer lineWidth, Color color) {
        this.lineHeight = lineHeight;
        this.lineWidth = lineWidth;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(x, y, lineHeight, lineWidth);
    }

    @Override
    public int getIconWidth() {
        return lineWidth;
    }

    @Override
    public int getIconHeight() {
        return lineHeight;
    }
}
