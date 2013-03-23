/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

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
        int length = conditionName.length();
        CharSequence subSequence = conditionName.subSequence(10, length);
        int conditionIndex = Integer.parseInt(subSequence.toString());
        return GuiUtils.getAvailableColors()[conditionIndex];
    }

    @Override
    public double getMaximumBarWidth() {
        return 0.05;
    }
}
