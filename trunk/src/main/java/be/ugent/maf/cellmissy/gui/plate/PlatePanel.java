/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.Well;
import be.ugent.maf.cellmissy.service.WellService;
import be.ugent.maf.cellmissy.spring.ApplicationContextProvider;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.List;
import javax.swing.SwingWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 *
 *
 * @author Paola
 *
 */
public class PlatePanel extends JPanel implements MouseListener {

    @Autowired
    private WellService wellService;
    private List<WellGUI> wellGUIList;
    private PlateFormat plateFormat;
    private Well firstWell;
    private static final int pixelsGrid = 5;
    private static final int pixelsBorders = 20;

    public PlatePanel() {

        //load applicationContext
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        wellService = (WellService) context.getBean("wellService");

        addMouseListener(this);
    }

    public void initPanel(PlateFormat plateFormat, Dimension parentDimension) {
        this.plateFormat = plateFormat;

        wellGUIList = new ArrayList<WellGUI>();
        doResize(parentDimension);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // width and heigth of squares around wells
        int wellSize = (int) ((double) (this.getWidth()) - ((plateFormat.getNumberOfCols() - 1) * pixelsGrid) - (2 * pixelsBorders)) / plateFormat.getNumberOfCols();

        if (wellGUIList.isEmpty()) {
            drawWells(wellSize, g);

        } else {
            reDrawWells(wellSize, g);
        }
    }

    // if the mouse has been pressed and released on a well, change its color (first well has been selected) and show imaged wells
    @Override
    public void mouseClicked(MouseEvent e) {
        firstWell = new Well();
        for (WellGUI wellGUI : wellGUIList) {
            if ((e.getButton() == 1) && wellGUI.getWellShape().contains(e.getX(), e.getY())) {
                Graphics g = getGraphics();
                Graphics2D g2d = (Graphics2D) g;
                setGraphics(g2d);

                // set color of graphics to fill the wellGUI shape
                g2d.setColor(Color.BLACK);
                g2d.fill(wellGUI.getWellShape());

                // set wellGUI color
                wellGUI.setWellColor(g2d.getColor());
                g.dispose();

                // first well used by the "wellService"
                firstWell.setColumnNumber(wellGUI.getColumnNumber());
                firstWell.setRowNumber(wellGUI.getRowNumber());
                wellGUI.setWell(firstWell);
            }
        }
        showImagedWells();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // draw the wells for the first time
    public void drawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        for (int i = 0; i < plateFormat.getNumberOfRows(); i++) {
            for (int j = 0; j < plateFormat.getNumberOfCols(); j++) {

                int topLeftX = (int) Math.round(wellSize * j + (j + 1) * pixelsGrid + pixelsBorders);
                int topLeftY = (int) Math.round(wellSize * i + (i + 1) * pixelsGrid + pixelsBorders);

                WellGUI wellGUI = new WellGUI(i + 1, j + 1);
                Ellipse2D ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);
                // set the shape of the wellGUI and draw it
                wellGUI.setWellShape(ellipse2D);
                g2d.draw(ellipse2D);

                wellGUIList.add(wellGUI);

                if (i == 0 || j == 0) {
                    // draw the labels on the plate
                    drawPlateLabel(wellGUI, g2d, j + 1, i + 1);
                }
            }
        }
    }

    // re-draw the wells if rezise event occours (keep color)
    public void reDrawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        // a list of WellGUI objects is present, iterate through it
        for (WellGUI wellGUI : wellGUIList) {

            int topLeftX = (int) Math.round(wellSize * (wellGUI.getColumnNumber() - 1) + wellGUI.getColumnNumber() * pixelsGrid + pixelsBorders);
            int topLeftY = (int) Math.round(wellSize * (wellGUI.getRowNumber() - 1) + wellGUI.getRowNumber() * pixelsGrid + pixelsBorders);

            Ellipse2D ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);
            wellGUI.setWellShape(ellipse2D);
            g2d.draw(ellipse2D);

            // if a color of a wellGUI has been changed, keep track of it when resizing
            if (wellGUI.getWellColor() != null) {
                g2d.setColor(wellGUI.getWellColor());
                g2d.fill(wellGUI.getWellShape());
                g2d.setColor(Color.BLACK);
            }

            // draw the labels on the plate
            if (wellGUI.getRowNumber() == 1 || wellGUI.getColumnNumber() == 1) {
                drawPlateLabel(wellGUI, g2d, wellGUI.getColumnNumber(), wellGUI.getRowNumber());
            }
        }

    }

    // compute plate panel sizes according to JFrame resize
    public void doResize(Dimension parentDimension) {
        int minimumParentDimension = Math.min(parentDimension.height, parentDimension.width);

        if (plateFormat != null) {
            int panelHeight = parentDimension.height;
            int panelWidth = parentDimension.width;
            if (plateFormat.getNumberOfCols() >= plateFormat.getNumberOfRows()) {
                if (minimumParentDimension == parentDimension.width) {
                    panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                } else {
                    if ((int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows())) < panelWidth) {
                        panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                    } else {
                        panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                    }
                }
            } else {
                if (minimumParentDimension == parentDimension.width) {
                    if ((int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols())) < panelHeight) {
                        panelHeight = (int) (Math.round((double) panelWidth * plateFormat.getNumberOfRows() / plateFormat.getNumberOfCols()));
                    } else {
                        panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                    }
                } else {
                    panelWidth = (int) (Math.round((double) panelHeight * plateFormat.getNumberOfCols() / plateFormat.getNumberOfRows()));
                }
            }
            this.setSize(panelWidth, panelHeight);
        }
    }

    // set Graphics
    private void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }

    // draw numbers (labels) on upper-side and left-side of the plate
    private void drawPlateLabel(WellGUI wellGUI, Graphics2D g2d, int columnNumber, int rowNumber) {
        Font font = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(font);
        String columnLabel = "" + columnNumber;
        if (columnLabel.length() > 1) {
            g2d.drawString(columnLabel, (int) Math.round(wellGUI.getWellShape().getCenterX()) - 5, pixelsBorders);
        } else {
            g2d.drawString(columnLabel, (int) Math.round(wellGUI.getWellShape().getCenterX()) - 3, pixelsBorders);
        }

        String rowLabel = "" + rowNumber;
        if (rowLabel.length() > 1) {
            g2d.drawString(rowLabel, pixelsBorders - 12, (int) Math.round(wellGUI.getWellShape().getCenterY()) + 3);
        } else {
            g2d.drawString(rowLabel, pixelsBorders - 8, (int) Math.round(wellGUI.getWellShape().getCenterY()) + 3);
        }

    }

    private void showImagedWells() {

        // inizialites wellService
        wellService.init();

        // get List of imaging types
        List<ImagingType> imagingTypeList = wellService.getImagingTypes();

        // position the wells for each imaging types: get a list of Wells
        for (ImagingType imagingType : imagingTypeList) {
            List<Well> wellList = wellService.positionWellsByImagingType(imagingType, plateFormat, firstWell);
            for (Well well : wellList) {
                for (WellGUI wellGUI : wellGUIList) {
                    if (wellGUI.getRowNumber() == well.getRowNumber() && wellGUI.getColumnNumber() == well.getColumnNumber()) {
                        Graphics g = getGraphics();
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(Color.BLACK);
                        g2d.fill(wellGUI.getWellShape());

                        // set wellGUI color
                        wellGUI.setWellColor(g2d.getColor());
                        g.dispose();
                    }
                }
            }
            //int confirm = JOptionPane.showConfirmDialog(this, "Sure?", "Confirm wells message", JOptionPane.YES_NO_OPTION);
        }
    }
}
