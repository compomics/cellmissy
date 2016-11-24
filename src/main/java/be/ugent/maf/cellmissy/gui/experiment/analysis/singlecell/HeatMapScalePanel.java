/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import be.ugent.maf.cellmissy.utils.AnalysisUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * This class extends a JPanel to render a heat map color scale.
 *
 * @author Paola
 */
public class HeatMapScalePanel extends JPanel {

    private final double min;
    private final double max;
    private final List<Double> values = new ArrayList<>();

    public HeatMapScalePanel(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();

        BufferedImage createColorScaleImage = createColorScaleImage(w, h);
        g2d.drawImage(createColorScaleImage, 0, (h / 2), this);

        g2d.setFont(new Font("Tahoma", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();

        int position = 1; // +1 pixel
        Double offset = (double) w / 15; // want just 15 labels
        int intValue = 0;
        for (int i = 0; i < values.size(); i++) {
            if ((i + intValue) < values.size()) {
                Double twoDec = AnalysisUtils.roundTwoDecimals(values.get(i + intValue));
                String s = "" + twoDec;
                int x = position + intValue;
                int y = fm.getHeight() + 2;

                g2d.drawString(s, x, y);
                intValue += offset.intValue() + fm.stringWidth(s);

                g2d.drawLine(x + (fm.stringWidth(s) / 2), 0, x + (fm.stringWidth(s) / 2), y - fm.getHeight());
            }
        }

    }

    /**
     *
     * @param value
     * @return
     */
    private Color getColorForValue(double value) {
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

    /**
     *
     * @param width
     * @param height
     * @return
     */
    private BufferedImage createColorScaleImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();

        for (int x = 0; x < width; x++) {
            double value = min + (max - min) * x / width;

            values.add(value);

            Color color = getColorForValue(value);

            for (int y = 0; y < height; y++) {

                raster.setSample(x, y, 0, color.getRed());
                raster.setSample(x, y, 1, color.getGreen());
                raster.setSample(x, y, 2, color.getBlue());

            }
        }
        return image;
    }
}
