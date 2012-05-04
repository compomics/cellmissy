/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.GuiUtils;
import be.ugent.maf.cellmissy.gui.controller.PlateMediator;
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
import java.util.Collection;
import javax.swing.JPanel;
import java.util.List;
import javax.swing.SwingWorker;
import org.springframework.context.ApplicationContext;

/**
 *
 *
 *
 * @author Paola
 *
 */
public class PlatePanelOld extends JPanel implements MouseListener {

    private WellService wellService;
    private PlateMediator plateMediator;
    private List<WellGui> wellGuiList;
    private PlateFormat plateFormat;
    private List<ImagingType> imagingTypeList;
    private static final int pixelsGrid = 5;
    private static final int pixelsBorders = 30;
    private ImagingType currentImagingType;

    public List<ImagingType> getImagingTypeList() {
        return imagingTypeList;
    }

    public ImagingType getCurrentImagingType() {
        return currentImagingType;
    }

    public void setCurrentImagingType(ImagingType currentImagingType) {
        this.currentImagingType = currentImagingType;
    }

    public WellService getWellService() {
        return wellService;
    }

    public List<WellGui> getWellGuiList() {
        return wellGuiList;
    }

    public PlatePanelOld(PlateMediator plateMediator) {

        this.plateMediator = plateMediator;

        //load applicationContext
        ApplicationContext context = ApplicationContextProvider.getInstance().getApplicationContext();
        wellService = (WellService) context.getBean("wellService");

        addMouseListener(this);
    }

    public void initPanel(PlateFormat plateFormat, Dimension parentDimension) {
        this.plateFormat = plateFormat;
        wellGuiList = new ArrayList<>();

        doResize(parentDimension);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // width and heigth of squares around wells (wellSize)
        int wellSize = (int) ((double) (this.getWidth()) - ((plateFormat.getNumberOfCols() - 1) * pixelsGrid) - (2 * pixelsBorders)) / plateFormat.getNumberOfCols();

        if (wellGuiList.isEmpty()) {
            drawWells(wellSize, g);
        } else {
            reDrawWells(g);
        }
    }

    // if the mouse has been pressed and released on a wellGui, set it as firstWellGui and show imaged wells
    @Override
    public void mouseClicked(MouseEvent e) {
        WellGui firstWellGui = null;
        for (WellGui wellGUI : wellGuiList) {
            List<Ellipse2D> ellipsi = wellGUI.getEllipsi();
            if ((e.getButton() == 1) && ellipsi.get(0).contains(e.getX(), e.getY())) {
                firstWellGui = wellGUI;
                break;
            }
        }

        showImagedWells(firstWellGui);

        // update info label
        if (imagingTypeList.indexOf(currentImagingType) == imagingTypeList.size() - 1) {
            // there are no more imaging types to process, save the wells to the DB
            plateMediator.updateInfoMessage("Click on Finish to save the wells");
            // enable Finish button
            plateMediator.enableFinishButton();
        } else {
            // ask the user to click on Forward button to proceed with next imaging type
            plateMediator.updateInfoMessage("Click on Forward to proceed with next imaging type");
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

    // draw the wells for the first time - wells color is set to default
    public void drawWells(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        for (int i = 0; i < plateFormat.getNumberOfRows(); i++) {
            for (int j = 0; j < plateFormat.getNumberOfCols(); j++) {

                int topLeftX = (int) Math.round(wellSize * j + (j + 1) * pixelsGrid + pixelsBorders);
                int topLeftY = (int) Math.round(wellSize * i + (i + 1) * pixelsGrid + pixelsBorders);

                List<Ellipse2D> ellipsi = new ArrayList<>();

                // create new WellGui object (rowNumber, columnNumber)
                WellGui wellGui = new WellGui(i + 1, j + 1);
                // create new Ellipse2D object to draw the well
                Ellipse2D ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);

                ellipsi.add(ellipse2D);
                wellGui.setEllipsi(ellipsi);
                // wells drawn for the first time are always shown in default color
                // the default color is the first object of the WellGui's AvailableWellColors()List
                Color defaultColor = GuiUtils.getAvailableColors()[0];
                g2d.setColor(defaultColor);
                g2d.fill(ellipse2D);

                wellGuiList.add(wellGui);

                if (i == 0 || j == 0) {
                    // draw the labels on the plate
                    drawPlateLabel(ellipse2D, g2d, j + 1, i + 1);
                }
            }
        }
    }

    // this method takes into account resize event, using wellSize variable ----- to be changed
    public void reDrawWells2(int wellSize, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);

        // a list of WellGUI objects is present, iterate through them
        for (WellGui wellGUI : wellGuiList) {
            List<Ellipse2D> ellipsi = wellGUI.getEllipsi();

            for (int i = 0; i < ellipsi.size(); i++) {
                Ellipse2D ellipse2D = ellipsi.get(i);
                int topLeftX = (int) Math.round(wellSize * (wellGUI.getColumnNumber() - 1) + wellGUI.getColumnNumber() * pixelsGrid + pixelsBorders);
                int topLeftY = (int) Math.round(wellSize * (wellGUI.getRowNumber() - 1) + wellGUI.getRowNumber() * pixelsGrid + pixelsBorders);
                ellipse2D = new Ellipse2D.Double(topLeftX, topLeftY, wellSize, wellSize);
                g2d.draw(ellipse2D);

                // if a color of a wellGui has been changed, keep track of it when resizing
                g2d.setColor(GuiUtils.getAvailableColors()[i]);
                g2d.fill(ellipse2D);
            }

            // draw the labels on the plate
            if (wellGUI.getRowNumber() == 1 || wellGUI.getColumnNumber() == 1) {
                drawPlateLabel(ellipsi.get(0), g2d, wellGUI.getColumnNumber(), wellGUI.getRowNumber());
            }
        }

    }

    // re-draw the wells if rezise event occours (keep color(s) of the wells)
    public void reDrawWells(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setGraphics(g2d);
        // a list of WellGUI objects is present, iterate through it
        for (WellGui wellGui : wellGuiList) {
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();

            for (int i = 0; i < ellipsi.size(); i++) {
                Ellipse2D ellipse2D = ellipsi.get(i);
                g2d.draw(ellipse2D);

                // if a color of a wellGui has been changed, keep track of it when resizing
                // if a well was not imaged, set its color to the default one
                if (wellGui.getWell().getWellHasImagingTypeCollection().isEmpty()) {
                    g2d.setColor(GuiUtils.getAvailableColors()[0]);
                } else {
                    // if it has been imaged, set its color to a different one
                    g2d.setColor(GuiUtils.getAvailableColors()[i + 1]);
                }

                g2d.fill(ellipse2D);
            }

            // draw the labels on the plate
            if (wellGui.getRowNumber() == 1 || wellGui.getColumnNumber() == 1) {
                drawPlateLabel(ellipsi.get(0), g2d, wellGui.getColumnNumber(), wellGui.getRowNumber());
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

    // set Graphics (implement Rendering process)
    private void setGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BasicStroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);
    }

    // draw numbers (plate labels) on upper-side and left-side of the plate
    private void drawPlateLabel(Ellipse2D ellipse2D, Graphics2D g2d, int columnNumber, int rowNumber) {
        Font font = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(font);
        String columnLabel = "" + columnNumber;
        if (columnLabel.length() > 1) {
            g2d.drawString(columnLabel, (int) Math.round(ellipse2D.getCenterX()) - 5, pixelsBorders);
        } else {
            g2d.drawString(columnLabel, (int) Math.round(ellipse2D.getCenterX()) - 3, pixelsBorders);
        }

        String rowLabel = "" + rowNumber;
        if (rowLabel.length() > 1) {
            g2d.drawString(rowLabel, pixelsBorders - 12, (int) Math.round(ellipse2D.getCenterY()) + 3);
        } else {
            g2d.drawString(rowLabel, pixelsBorders - 8, (int) Math.round(ellipse2D.getCenterY()) + 3);
        }
    }

    private void showImagedWells(WellGui firstWellGUI) {
        // update WellGuiList and show imaged wells positions on the plate
        wellService.updateWellGuiListWithImagingType(currentImagingType, plateFormat, firstWellGUI, wellGuiList);
        int currentImagingTypeIndex = imagingTypeList.indexOf(currentImagingType);
        for (WellGui wellGui : wellGuiList) {
            if (currentImagingTypeIndex != 0) {
                if (containsImagingType(wellGui.getWell().getWellHasImagingTypeCollection(), currentImagingType)) {

                    List<Ellipse2D> ellipsi = wellGui.getEllipsi();
                    // get the bigger ellipsi and calculate factors for the new ones (concentric wells)
                    Ellipse2D ellipse2D = ellipsi.get(currentImagingTypeIndex - 1);
                    double size = ellipse2D.getHeight();
                    double newSize = (size / imagingTypeList.size()) * (imagingTypeList.size() - currentImagingTypeIndex);
                    double newTopLeftX = ellipse2D.getCenterX() - (newSize / 2);
                    double newTopLeftY = ellipse2D.getCenterY() - (newSize / 2);

                    Ellipse2D newEllipse2D = new Ellipse2D.Double(newTopLeftX, newTopLeftY, newSize, newSize);
                    // add the new Ellipse2D to the ellipsi List
                    ellipsi.add(newEllipse2D);
                }
            }
        }
        // this calls the paintComponent method
        this.repaint();
    }

    private boolean containsImagingType(Collection<WellHasImagingType> wellHasImagingTypes, ImagingType imagingType) {
        for (WellHasImagingType wellHasImagingType : wellHasImagingTypes) {
            if (wellHasImagingType.getImagingType().equals(imagingType)) {
                return true;
            }
        }
        return false;
    }

    public class PlateWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            plateMediator.showInitProgressBar();
            // initializes wellService: CellMiaDataService and MicroscopeDataService are also inizialized
            // Imaging Types of the current Experiment are retrieved and CellMia data are processed (txt files are read and collections are set)
            wellService.init();
            // get the list of imaging types
            imagingTypeList = wellService.getImagingTypes();
            return null;
        }

        // SwingWorker calls this method after the doInBackground method finishes
        @Override
        protected void done() {
            plateMediator.hideInitProgressBar();
            // get first Imaging Type
            currentImagingType = imagingTypeList.get(0);
            // ask the user to select first well for the imaging type
            plateMediator.updateInfoMessage("Select first well for " + currentImagingType.getName() + " (imaging type " + (imagingTypeList.indexOf(currentImagingType) + 1) + "/" + imagingTypeList.size() + ")");
        }
    }
}
