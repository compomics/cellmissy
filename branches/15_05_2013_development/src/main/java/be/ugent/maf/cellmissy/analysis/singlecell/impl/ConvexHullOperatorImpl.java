/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.analysis.singlecell.impl;

import be.ugent.maf.cellmissy.analysis.singlecell.ConvexHullOperator;
import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import org.springframework.stereotype.Component;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Component("convexHullOperator")
public class ConvexHullOperatorImpl implements ConvexHullOperator {

    @Override
    public void computePerimeter(ConvexHull convexHull) {
        GeometricPoint[] closedPolygonFromHull = getClosedPolygonFromHull(convexHull);
        //init perimeter to zer
        double perimeter = 0;
        //sum up the length of each edge of the polygon
        for (int i = 0; i < closedPolygonFromHull.length - 1; i++) {
            perimeter += closedPolygonFromHull[i].euclideanDistanceTo(closedPolygonFromHull[i + 1]);
        }
        convexHull.setPerimeter(perimeter);
    }

    @Override
    public void computeArea(ConvexHull convexHull) {
        GeometricPoint[] closedPolygonFromHull = getClosedPolygonFromHull(convexHull);
        //init area to zero
        double area = 0;
        // sum up the cross products around each vertex of the hull
        for (int i = 0; i < closedPolygonFromHull.length - 1; i++) {
            double xUp = closedPolygonFromHull[i].getX();
            double yDown = closedPolygonFromHull[i + 1].getY();
            double yUp = closedPolygonFromHull[i].getY();
            double xDown = closedPolygonFromHull[i + 1].getX();
            area += (xUp * yDown) - (yUp * xDown);
        }
        convexHull.setArea(area / 2);
    }

    @Override
    public void computeAcircularity(ConvexHull convexHull) {
        double perimeter = convexHull.getPerimeter();
        double area = convexHull.getArea();
        double acircularity = Math.pow(perimeter, 2) / (4 * Math.PI * area);
        convexHull.setAcircularity(acircularity);
    }

    @Override
    public void computeDirectionality(ConvexHull convexHull) {
        double directionality = convexHull.getMostDistantPointsPair().getMaxSpan() / convexHull.getArea();
        convexHull.setDirectionality(directionality);
    }

    /**
     *
     * @param convexHull
     * @return
     */
    private GeometricPoint[] getClosedPolygonFromHull(ConvexHull convexHull) {
        Iterable<GeometricPoint> hull = convexHull.getHull();
        // get the size of the hull: number of vertices of polygon
        int n = convexHull.getHullSize();
        // we take n + 1
        GeometricPoint[] geometricPoints = new GeometricPoint[n + 1];
        int j = 0;
        for (GeometricPoint geometricPoint : hull) {
            geometricPoints[j++] = geometricPoint;
        }
        // close the polygon: last point of polygon has to be equal to first point
        geometricPoints[n] = geometricPoints[0];
        return geometricPoints;
    }
}
