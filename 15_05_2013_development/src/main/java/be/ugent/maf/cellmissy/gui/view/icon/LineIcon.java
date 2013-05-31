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
public class LineIcon implements Icon{
    
    // the stroke-width property
    private final Integer lineHeigth = 10;
    private final Integer lineWidth = 15;
    // the stroke-colour property
    private Color color;

    /**
     * Constructor with a color
     *
     * @param color
     */
    public LineIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(x, y, lineHeigth, lineWidth);
    }

    @Override
    public int getIconWidth() {
        return lineWidth;
    }

    @Override
    public int getIconHeight() {
        return lineHeigth;
    }
    
}
