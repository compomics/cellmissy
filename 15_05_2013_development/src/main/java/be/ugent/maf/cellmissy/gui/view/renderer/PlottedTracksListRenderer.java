/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class PlottedTracksListRenderer extends DefaultListCellRenderer {

    private LegendItemCollection legendItems;

    public PlottedTracksListRenderer(LegendItemCollection legendItems) {
        this.legendItems = legendItems;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, false, false);
        LegendItem legendItem = legendItems.get(index);
        Shape shape = legendItem.getShape();
        String description = legendItem.getDescription();
       // setIcon(new TrackIcon(shape));
        setText(description);
        return this;
    }

    /**
     *
     */
    private class TrackIcon implements Icon {

        private Shape shape;

        public TrackIcon(Shape shape) {
            this.shape = shape;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.draw(shape);
        }

        @Override
        public int getIconWidth() {
            Rectangle bounds = shape.getBounds();
            return bounds.width;
        }

        @Override
        public int getIconHeight() {
            Rectangle bounds = shape.getBounds();
            return bounds.height;
        }
    }
}
