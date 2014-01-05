/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import be.ugent.maf.cellmissy.utils.JFreeChartUtils;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

/**
 * This class extends JMenuBar from the Swing library. It is a JMenuBar to be
 * added on top of chart panels, to let the user choose settings for current
 * plot.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class PlotSettingsMenuBar extends JMenuBar {

    //3 check box menu items: points, lines and endpoints
    private JCheckBoxMenuItem plotPointsCheckBoxMenuItem;
    private JCheckBoxMenuItem plotLinesCheckBoxMenuItem;
    private JCheckBoxMenuItem showEndPointsCheckBoxMenuItem;
    //a button group: a button for each line width of the jfreechart utils class
    private ButtonGroup linesButtonGroup;

    /**
     * Public Constructor
     */
    public PlotSettingsMenuBar() {
        constructMenuBar();
    }

    /**
     * Getters
     */
    public JCheckBoxMenuItem getPlotPointsCheckBoxMenuItem() {
        return plotPointsCheckBoxMenuItem;
    }

    public JCheckBoxMenuItem getPlotLinesCheckBoxMenuItem() {
        return plotLinesCheckBoxMenuItem;
    }

    public JCheckBoxMenuItem getShowEndPointsCheckBoxMenuItem() {
        return showEndPointsCheckBoxMenuItem;
    }

    public ButtonGroup getLinesButtonGroup() {
        return linesButtonGroup;
    }

    /**
     * Construct the MenuBar
     */
    private void constructMenuBar() {
        //main menu
        JMenu mainMenu = new JMenu("Plot Settings");
        mainMenu.setToolTipText("Click to choose plot settings");

        //first submenu: line width

        JMenu lineWidthMenu = new JMenu("Line Width");

        //now, one menu item for each line width in the JFreeChart Utils class
        List<Float> lineWidths = JFreeChartUtils.getLineWidths();
        linesButtonGroup = new ButtonGroup();
        for (Float lineWidth : lineWidths) {
            String value = "" + lineWidth.doubleValue();
            JRadioButtonMenuItem lineRadioButtonMenuItem = new JRadioButtonMenuItem(value);
            lineWidthMenu.add(lineRadioButtonMenuItem);
            //select 1.5 as default
            if (value.equals("1.5")) {
                lineRadioButtonMenuItem.setSelected(true);
            }
            linesButtonGroup.add(lineRadioButtonMenuItem);
        }

        mainMenu.add(lineWidthMenu);
        plotLinesCheckBoxMenuItem = new JCheckBoxMenuItem("Plot Lines");
        plotPointsCheckBoxMenuItem = new JCheckBoxMenuItem("Plot Points");
        showEndPointsCheckBoxMenuItem = new JCheckBoxMenuItem("Show Endpoints");
        //add the menu items to the main menu
        mainMenu.add(plotLinesCheckBoxMenuItem);
        mainMenu.add(plotPointsCheckBoxMenuItem);
        mainMenu.add(showEndPointsCheckBoxMenuItem);

        //by deafult lines and endpoints are shown (points are not shown)
        plotLinesCheckBoxMenuItem.setSelected(true);
        showEndPointsCheckBoxMenuItem.setSelected(true);

        //add the main menu to the bar
        this.add(mainMenu);
    }
}