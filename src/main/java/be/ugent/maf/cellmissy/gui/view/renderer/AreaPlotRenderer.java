/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * XY Line and Shape Renderer for Area Plot
 * @author Paola Masuzzo
 */
public class AreaPlotRenderer extends XYLineAndShapeRenderer {

    @Override
    public Paint getSeriesPaint(int series) {
        return GuiUtils.getAvailableColors()[series + 1];
    }

    @Override
    public Stroke getSeriesStroke(int series) {
        BasicStroke wideLine = new BasicStroke(1.3f);
        return wideLine;
    }

    @Override
    public boolean getItemShapeVisible(int series, int item) {
        return false;
    }
}
