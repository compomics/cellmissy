/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.PaintScale;

/**
 *
 * @author Paola
 */
public class ColorScale1 implements PaintScale {

    private static final float H1 = 0f;
    private static final float H2 = 1f;
    private final double min;
    private final double max;

    public ColorScale1(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double getLowerBound() {
        return min;
    }

    @Override
    public double getUpperBound() {
        return max;
    }

    @Override
    public Paint getPaint(double value) {
        if (value < min || value > max) {
            return Color.BLACK;
        }

        float[] RGBtoHSB = Color.RGBtoHSB(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), null);
        float blueHue = RGBtoHSB[0];
        RGBtoHSB = Color.RGBtoHSB(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), null);
        float redHue = RGBtoHSB[0];
        float hue = (float) (blueHue + (redHue - blueHue) * (value - min) / (max - min));
        return new Color(Color.HSBtoRGB(hue, 0.85f, 0.9f));
    }
}
