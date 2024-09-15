package com.cgzz.mapbox.jturf.util.measurement;

import com.cgzz.mapbox.jturf.JTurfClassification;
import com.cgzz.mapbox.jturf.JTurfFeatureConversion;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.MultiPolygon;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

import java.util.ArrayList;
import java.util.List;

public final class PolygonTangentsHelper {

    private PolygonTangentsHelper() {
        throw new AssertionError("No Instances.");
    }

    public static List<Point> polygonTangents(Point pt, Polygon polygon) {
        Point nearest = nearestPoint(pt, polygon);
        List<Point> polyCoords = polygon.coordinates().get(0);

        Point rtan = polyCoords.get(0), ltan = polyCoords.get(0);
        if (nearest != null) {
            if (nearest.getY() < pt.getY()) {
                ltan = polyCoords.get(0);
            }
        }

        double eprev = isLeft(
                polyCoords.get(0),
                polyCoords.get(polyCoords.size() - 1),
                pt);

        Point[] out = processPolygon(polyCoords, pt, eprev, rtan, ltan);
        rtan = out[0];
        ltan = out[1];

        List<Point> retList = new ArrayList<>(2);
        retList.add(rtan);
        retList.add(ltan);

        return retList;
    }

    // 这个方法看起来是有问题的。。。。以后再优化吧。
    public static List<Point> polygonTangents(Point pt, MultiPolygon multiPolygon) {
        Point nearest = nearestPoint(pt, multiPolygon);
        List<List<Point>> polyCoords = multiPolygon.coordinates().get(0);

        Point rtan = polyCoords.get(0).get(0);
        Point ltan = polyCoords.get(0).get(0);

        double eprev = isLeft(
                polyCoords.get(0).get(0),
                polyCoords.get(0).get(polyCoords.get(0).size() - 1),
                pt);

        for (List<Point> pointList : polyCoords) {
            Point[] out = processPolygon(pointList, pt, eprev, rtan, ltan);
            rtan = out[0];
            ltan = out[1];
        }

        List<Point> retList = new ArrayList<>(2);
        retList.add(rtan);
        retList.add(ltan);

        return retList;
    }

    private static Point[] processPolygon(List<Point> polygonCoords, Point ptCoords, double eprev, Point rtan, Point ltan) {
        for (int i = 0, size = polygonCoords.size(); i < size; i++) {
            Point currentCoords = polygonCoords.get(i);
            Point nextCoordPair;
            if (i == size - 1) {
                nextCoordPair = polygonCoords.get(0);
            } else {
                nextCoordPair = polygonCoords.get(i + 1);
            }
            double enext = isLeft(currentCoords, nextCoordPair, ptCoords);
            if (eprev <= 0 && enext > 0) {
                if (!isBelow(ptCoords, currentCoords, rtan)) {
                    rtan = currentCoords;
                }
            } else if (eprev > 0 && enext <= 0) {
                if (!isAbove(ptCoords, currentCoords, ltan)) {
                    ltan = currentCoords;
                }
            }
            eprev = enext;
        }
        return new Point[]{rtan, ltan};
    }

    private static boolean isAbove(Point point1, Point point2, Point point3) {
        return isLeft(point1, point2, point3) > 0;
    }

    private static boolean isBelow(Point point1, Point point2, Point point3) {
        return isLeft(point1, point2, point3) < 0;
    }

    private static double isLeft(Point point1, Point point2, Point point3) {
        return (point2.getX() - point1.getX()) * (point3.getY() - point1.getY()) - (point3.getX() - point1.getX()) * (point2.getY() - point1.getY());
    }

    /**
     * 计算组件中离指定点最近的点
     *
     * @param pt       切点
     * @param geometry 组件
     * @return 最近的点
     */
    private static Point nearestPoint(Point pt, Geometry geometry) {
        // If the point lies inside the polygon bbox then we need to be a bit trickier
        // otherwise points lying inside reflex angles on concave polys can have issues
        double[] bbox = JTurfMeasurement.bbox(geometry).bbox();
        if (pt.getX() > bbox[0] && pt.getX() < bbox[2] && pt.getY() > bbox[1] && pt.getY() < bbox[3]) {
            return (Point)JTurfClassification.nearestPoint(pt, JTurfFeatureConversion.explode(geometry)).geometry();
        }
        return null;
    }

}
