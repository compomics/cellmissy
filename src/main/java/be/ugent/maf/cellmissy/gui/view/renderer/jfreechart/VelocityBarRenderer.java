/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.jfreechart;

import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Paint;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;

/**
 * Statistical Bar Renderer for Velocity Bar Charts
 *
 * @author Paola Masuzzo
 */
public class VelocityBarRenderer extends StatisticalBarRenderer {

    @Override
    public Paint getItemPaint(final int row, final int column) {
        String conditionName = (String) getPlot().getDataset().getColumnKey(column);
        int lenght = GuiUtils.getAvailableColors().length;
        String subString = conditionName.substring(10);
        int conditionIndex = Integer.parseInt(subString) - 1;
        int indexOfColor = conditionIndex % lenght;
        return GuiUtils.getAvailableColors()[indexOfColor];
    }

    @Override
    public double getMaximumBarWidth() {
        return 0.05;
    }
}
