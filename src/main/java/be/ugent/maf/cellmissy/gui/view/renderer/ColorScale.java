/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer;

import java.awt.Color;
import java.awt.Container;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Paola
 */
public class ColorScale {

    private final double min;
    private final double max;

    public ColorScale(double min, double max) {
        this.min = min;
        this.max = max;
    }

//    public void show(JPanel view) {
//        Image colorScaleImage = createColorScaleImage(view.getWidth(), view.getHeight(), Orientation.HORIZONTAL);
//        ImageView imageView = new ImageView(colorScaleImage);
//
//        final SwingNode swingNode = new SwingNode();
//
//        Pane pane = new Pane(imageView);
//        pane.getChildren().add(swingNode); // Adding swing node
//
//        Stage stage = new Stage();
//        stage.setScene(new Scene(pane));
//        stage.show();
//
//    }

    private Image createColorScaleImage(int width, int height, Orientation orientation) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        if (orientation == Orientation.HORIZONTAL) {
            for (int x = 0; x < width; x++) {
                double value = min + (max - min) * x / width;
                Color color = getColorForValue(value);
                javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), 0.8);
                for (int y = 0; y < height; y++) {
                    pixelWriter.setColor(x, y, fxColor);
                }
            }
        } else {
            for (int y = 0; y < height; y++) {
                double value = max - (max - min) * y / height;
                Color color = getColorForValue(value);
                javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), 0.8);
                for (int x = 0; x < width; x++) {
                    pixelWriter.setColor(x, y, fxColor);
                }
            }
        }
        return image;
    }

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

    private Scene initScene(JPanel view) {
        Group root = new Group();
        Scene scene = new Scene(root);
        Image colorScaleImage = createColorScaleImage(view.getWidth(), view.getHeight(), Orientation.HORIZONTAL);
        ImageView imageView = new ImageView(colorScaleImage);

        root.getChildren().add(imageView);
        return (scene);
    }

    public JPanel initialize(JPanel view) {

        final JFXPanel fxPanel = new JFXPanel();
        JPanel jp = new JPanel();
        
        jp.add(fxPanel);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, view);
            }
        });

        return jp;
    }

    private void initFX(JFXPanel fxPanel, JPanel view) {
        Scene scene = initScene(view);
        fxPanel.setScene(scene);
    }
}
