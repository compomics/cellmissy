/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.experiment.analysis.singlecell;

import be.ugent.maf.cellmissy.gui.view.renderer.jfreechart.TrackXYLineAndShapeRenderer;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * This class keeps the right TrackXYLineAndShapeRenderer, according to user
 * selection through a plotSettingsMenuBar.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class PlotSettingsRendererGiver {

    // an index for the track to highlight
    private final int selectedTrackIndex;
    // a menu bar with the settings for th eplot
    private final PlotSettingsMenuBar plotSettingsMenuBar;
    // the end points for the tracks to show
    private final List<Integer> endPoints;

    /**
     * Constructor
     *
     * @param selectedTrackIndex: which track has to be highlighted?
     * @param plotSettingsMenuBar: the GUI through the user selects display
     * options.
     * @param endPoints: the list of end points to show in the plot.
     */
    public PlotSettingsRendererGiver(int selectedTrackIndex, PlotSettingsMenuBar plotSettingsMenuBar, List<Integer> endPoints) {
        this.selectedTrackIndex = selectedTrackIndex;
        this.plotSettingsMenuBar = plotSettingsMenuBar;
        this.endPoints = endPoints;
    }

    /**
     * Get the right renderer for the plot
     *
     * @param e: the event item from a menu item
     * @return the appropriate renderer for the plot
     */
    public TrackXYLineAndShapeRenderer getRenderer(ItemEvent e) {

        boolean plotLines = plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().isSelected();
        boolean plotPoints = plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().isSelected();
        boolean showEndPoints = plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().isSelected();
        Float selectedLineWidth = plotSettingsMenuBar.getSelectedLineWidth();

        TrackXYLineAndShapeRenderer trackXYLineAndShapeRenderer = null;
        String menuItemText = ((JMenuItem) e.getSource()).getText();
        if (menuItemText.equalsIgnoreCase("plot lines")) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, plotPoints, showEndPoints, endPoints, selectedTrackIndex, selectedLineWidth);
            } else {
                // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                if (!plotPoints) {
                    plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().setSelected(true);
                }
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(false, true, false, null, selectedTrackIndex, selectedLineWidth);
            }
        } else if (menuItemText.equalsIgnoreCase("plot points")) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (showEndPoints) {
                    plotSettingsMenuBar.getShowEndPointsCheckBoxMenuItem().setSelected(false);
                }
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, true, false, null, selectedTrackIndex, selectedLineWidth);
            } else {
                // if the checkbox is being deselected, check for the points checkbox, if it's deselected, select it
                if (!plotLines) {
                    plotSettingsMenuBar.getPlotLinesCheckBoxMenuItem().setSelected(true);
                }
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, false, showEndPoints, endPoints, selectedTrackIndex, selectedLineWidth);
            }
        } else if (menuItemText.equalsIgnoreCase("show endpoints")) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // need to show the endpoints
                if (plotPoints) { // first of all, to show the endpoints we need to have only lines and not points
                    plotSettingsMenuBar.getPlotPointsCheckBoxMenuItem().setSelected(false);
                }
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(true, plotPoints, true, endPoints, selectedTrackIndex, selectedLineWidth);
            } else {
                // need to hide the endpoints
                trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, false, null, selectedTrackIndex, selectedLineWidth);
            }
        } else {
            // line widths (floats): get the text, cast to float
            float lineWidth = Float.parseFloat(menuItemText);
            trackXYLineAndShapeRenderer = new TrackXYLineAndShapeRenderer(plotLines, plotPoints, showEndPoints, endPoints, selectedTrackIndex, lineWidth);
        }
        return trackXYLineAndShapeRenderer;
    }
}
