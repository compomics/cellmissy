/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell;

import be.ugent.maf.cellmissy.entity.result.singlecell.FarthestPointsPair;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public interface FarthestPointsPairCalculator {

    /**
     * Given a track data holder (and thus a track), find the two farthest
     * points that belong to this track, making use of the Graham Scan
     * Algorithm, which computes the convex hull of the set of points of this
     * track. The diameter of the convex hull is the maximal displacement of the
     * cell (between the two farthest points of its path).
     *
     * @param trackDataHolder 
     * @return
     */
    public FarthestPointsPair findFarthestPoints(TrackDataHolder trackDataHolder);
}
