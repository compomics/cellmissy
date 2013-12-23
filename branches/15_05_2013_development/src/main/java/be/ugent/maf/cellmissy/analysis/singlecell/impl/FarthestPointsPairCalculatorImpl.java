/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.FarthestPointsPairCalculator;
import be.ugent.maf.cellmissy.entity.result.singlecell.Point;
import be.ugent.maf.cellmissy.entity.Track;
import be.ugent.maf.cellmissy.entity.result.singlecell.FarthestPointsPair;
import be.ugent.maf.cellmissy.entity.result.singlecell.TrackDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("farthestPointsPairCalculator")
public class FarthestPointsPairCalculatorImpl implements FarthestPointsPairCalculator {

    @Autowired
    private GrahamScanAlgorithm grahamScanAlgorithm;
    private FarthestPointsPair farthestPointsPair;

    @Override
    public FarthestPointsPair findFarthestPoints(TrackDataHolder trackDataHolder) {
        Track track = trackDataHolder.getTrack();
        // compute first the convex hull
        Iterable<Point> convexHull = grahamScanAlgorithm.computeConvexHull(track);
        trackDataHolder.setConvexHull(convexHull);
        farthestPointsPair = new FarthestPointsPair(track, convexHull);
        return farthestPointsPair;
    }
}
