/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.Point;
import be.ugent.maf.cellmissy.entity.Track;
import java.util.List;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface FarthestPointsPairCalculator {

    /**
     *
     * @param track
     * @return
     */
    public List<Point> findFarthestPoints(Track track);
}
