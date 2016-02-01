package be.ugent.maf.cellmissy.gui.controller.analysis.singlecell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Component to control track interpolation logic.
 * Has the single cell pre-processing controller as parent controller.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("trackInterpolationController")
public class TrackInterpolationController {

    // model
    // view
    // parent controller
    @Autowired
    private SingleCellPreProcessingController singleCellPreProcessingController;
    // child controllers

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrackInterpolationController.class);

    /**
     * Initialize controller.
     */
    public void init() {

    }
}
