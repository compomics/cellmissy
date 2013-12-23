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
     * Given a track, find the two farthest points that belong to this track,
     * making use of the Graham Scan Algorithm, which computes the convex hull
     * of the set of points of this track. The diameter of the convex hull is
     * the maximal displacement of the cell (between the two farthest points of
     * its path).
     *
     * @param track
     * @return
     */
    public List<Point> findFarthestPoints(Track track);
}
