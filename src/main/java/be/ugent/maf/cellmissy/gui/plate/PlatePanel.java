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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 *
 *
 * @author Paola
 *
 */
public class PlatePanel extends JPanel implements MouseListener, MouseMotionListener {

    @Autowired
    private WellService wellService;
    private List<WellGUI> wellGUIList;
    private PlateFormat plateFormat;
    private static final int pixelsGrid = 5;
    private static final int pixelsBorders = 20;

    public PlatePanel() {

        //load applicationContext
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        wellService = (WellService) context.getBean("wellService");

        addMouseListener(this);
        addMouseMotionListener(this);
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
        Well firstWell = new Well();

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

                // first well
                firstWell.setColumnNumber(wellGUI.getColumnNumber() + 1);
                firstWell.setRowNumber(wellGUI.getRowNumber() + 1);
                wellGUI.setWell(firstWell);

                wellService.init();
                List<ImagingType> imagingTypeList = wellService.getImagingTypes();

                for (ImagingType imagingType : imagingTypeList) {
                    System.out.println("imaging type: " + imagingType);
                }

                //JOptionPane.showMessageDialog(this, imagingTypeList.size() + " imaging types were used.\n Please select first well for the first imaging type", "Imaging types info", JOptionPane.INFORMATION_MESSAGE);

                List<Well> wellList = wellService.positionWellsByImagingType(imagingTypeList.get(0), plateFormat, firstWell);
                for (Well well : wellList) {
                    if (wellGUI.getRowNumber() + 1 == well.getRowNumber() && wellGUI.getColumnNumber() + 1 == well.getColumnNumber()) {
                        g2d.fill(wellGUI.getWellShape());
                    }
                }


//                int confirm = JOptionPane.showConfirmDialog(this, "First well selected is (" + (firstWell.getRowNumber() + 1) + ", " + (firstWell.getColumnNumber() + 1) + ") \n Are you sure?", "First well selected message", JOptionPane.YES_NO_OPTION);
//
//                if (confirm == JOptionPane.NO_OPTION) {
//                    wellGUI.setWellColor(null);
//                    g2d.draw(wellGUI.getWellShape());
//                }
            }
        }
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

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        for (WellGUI wellGUI : wellGUIList) {
            if (wellGUI.getWellShape().contains(e.getX(), e.getY())) {
                int rowNumber = wellGUI.getRowNumber();
                int columnNumber = wellGUI.getColumnNumber();
                Graphics g = getGraphics();
                Graphics2D g2d = (Graphics2D) g;
                setGraphics(g2d);
                super.paint(g);
                g2d.drawString("(" + (rowNumber + 1) + ", " + (columnNumber + 1) + ")", e.getX(), e.getY());
                g.dispose();
            }

        }
    }

    // draw the wells for the first time
    public void drawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        for (int i = 0; i < plateFormat.getNumberOfCols(); i++) {
            for (int j = 0; j < plateFormat.getNumberOfRows(); j++) {

                int topLeftX = (int) Math.round(wellSize * i + (i + 1) * pixelsGrid + pixelsBorders);
                int topLeftY = (int) Math.round(wellSize * j + (j + 1) * pixelsGrid + pixelsBorders);

                WellGUI wellGUI = new WellGUI(i, j);
                Ellipse2D ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);
                // set the shape of the wellGUI and draw it
                wellGUI.setWellShape(ellipse2D);
                g2d.draw(ellipse2D);

                wellGUIList.add(wellGUI);

                if (i == 0 || j == 0) {
                    // draw the labels on the plate
                    drawPlateLabel(wellGUI, g2d, i, j);
                }
            }
        }
    }

    // re-draw the wells if rezise event occours (keep color)
    public void reDrawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        // a list of WellGUI objects is present
        for (WellGUI wellGUI : wellGUIList) {

            int topLeftX = (int) Math.round(wellSize * wellGUI.getRowNumber() + (wellGUI.getRowNumber() + 1) * pixelsGrid + pixelsBorders);
            int topLeftY = (int) Math.round(wellSize * wellGUI.getColumnNumber() + (wellGUI.getColumnNumber() + 1) * pixelsGrid + pixelsBorders);

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
            if (wellGUI.getRowNumber() == 0 || wellGUI.getColumnNumber() == 0) {
                drawPlateLabel(wellGUI, g2d, wellGUI.getRowNumber(), wellGUI.getColumnNumber());
            }
        }

    }

    // compute plate panel sizes according to JFrame resize
    public void doResize(Dimension parentDimension) {
        int minimumParentDimension = Math.min(parentDimension.height, parentDimension.width);

        if (plateFormat.getNumberOfCols() != 0 && plateFormat.getNumberOfRows() != 0) {
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

    // draw numbers on upper-side and left-side of the plate
    private void drawPlateLabel(WellGUI wellGUI, Graphics2D g2d, int columnNumber, int rowNumber) {
        Font font = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(font);
        if (rowNumber == 0) {
            String columnLabel = "" + (columnNumber + 1);
            if (columnLabel.length() > 1) {
                g2d.drawString(columnLabel, (int) Math.round(wellGUI.getWellShape().getCenterX()) - 5, pixelsBorders);
            } else {
                g2d.drawString(columnLabel, (int) Math.round(wellGUI.getWellShape().getCenterX()) - 3, pixelsBorders);
            }
        }
        if (columnNumber == 0) {
            String rowLabel = "" + (rowNumber + 1);
            if (rowLabel.length() > 1) {
                g2d.drawString(rowLabel, pixelsBorders - 12, (int) Math.round(wellGUI.getWellShape().getCenterY()) + 3);
            } else {
                g2d.drawString(rowLabel, pixelsBorders - 8, (int) Math.round(wellGUI.getWellShape().getCenterY()) + 3);
            }
        }
    }
}
