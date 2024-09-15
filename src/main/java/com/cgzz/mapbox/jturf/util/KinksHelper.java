package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.models.IntersectsResult;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.List;

public final class KinksHelper {

    private KinksHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 返回自身的相交点
     *
     * @param geometry 图形，支持 POLYGON、LINE、MULTI_LINE、MULTI_POLYGON（Feature也不也需要这个类型）
     * @return 返回自相交点集合
     */
    public static FeatureCollection kinks(Geometry geometry) {
        if (geometry instanceof Feature) {
            geometry = Feature.feature(geometry).geometry();
        }

        switch (geometry.geometryType()) {
            case POLYGON:
                return kinksMulti(((Polygon) geometry).coordinates());
            case LINE_STRING:
                return kinks(((LineString) geometry).coordinates());
            case MULTI_LINE_STRING:
                return kinksMulti(((MultiLineString) geometry).coordinates());
            case MULTI_POLYGON:
                return kinksMulti(((MultiPolygon) geometry).decreaseOneCoordinates());
        }

        return null;
    }

    private static FeatureCollection kinksMulti(List<List<Point>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        List<Feature> pointList = new ArrayList<>();
        for (List<Point> c : coordinates) {
            if (c == null || c.isEmpty()) {
                continue;
            }

            kinks(c, pointList);
        }

        return pointList.isEmpty() ? null : FeatureCollection.fromFeatures(pointList);
    }

    private static FeatureCollection kinks(List<Point> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        List<Feature> pointList = new ArrayList<>();

        kinks(coordinates, pointList);

        return pointList.isEmpty() ? null : FeatureCollection.fromFeatures(pointList);
    }

    private static void kinks(List<Point> coordinates, List<Feature> pointList) {
        int len = coordinates.size();
        for (int i = 0; i < len - 1; i++) {
            for (int k = i; k < len - 1; k++) {
                // segments are adjacent and always share a vertex, not a kink
                if (Math.abs(i - k) == 1) {
                    continue;
                }
                // first and last segment in a closed lineString or ring always share a vertex, not a kink
                if (i == 0
                        && k == len - 2
                        && coordinates.get(i).longitude() == coordinates.get(len - 1).longitude()
                        && coordinates.get(i).latitude() == coordinates.get(len - 1).latitude()) {
                    continue;
                }

                IntersectsResult result = lineIntersects(coordinates.get(i), coordinates.get(i + 1), coordinates.get(k), coordinates.get(k + 1));
                if (result != null) {
                    pointList.add(Feature.fromGeometry(Point.fromLngLat(result.getX(), result.getY())));
                }
            }
        }
    }

    private static IntersectsResult lineIntersects(Point line1Start, Point line1End, Point line2Start, Point line2End) {
        return lineIntersects(line1Start.longitude(), line1Start.latitude(),
                line1End.longitude(), line1End.latitude(),
                line2Start.longitude(), line2Start.latitude(),
                line2End.longitude(), line2End.latitude());
    }

    private static IntersectsResult lineIntersects(double line1StartX, double line1StartY,
                                                   double line1EndX, double line1EndY,
                                                   double line2StartX, double line2StartY,
                                                   double line2EndX, double line2EndY) {
        double denominator, a, b, numerator1, numerator2;
        IntersectsResult result = new IntersectsResult();
        denominator = (line2EndY - line2StartY) * (line1EndX - line1StartX) - (line2EndX - line2StartX) * (line1EndY - line1StartY);
        if (denominator == 0) {
            if (result.getX() != null && result.getY() != null) {
                return result;
            } else {
                return null;
            }
        }

        a = line1StartY - line2StartY;
        b = line1StartX - line2StartX;
        numerator1 = (line2EndX - line2StartX) * a - (line2EndY - line2StartY) * b;
        numerator2 = (line1EndX - line1StartX) * a - (line1EndY - line1StartY) * b;
        a = numerator1 / denominator;
        b = numerator2 / denominator;

        result.setX(line1StartX + a * (line1EndX - line1StartX));
        result.setY(line1StartY + a * (line1EndY - line1StartY));

        if (a >= 0 && a <= 1) {
            result.setOnLine1(true);
        }
        if (b >= 0 && b <= 1) {
            result.setOnLine2(true);
        }

        if (result.isOnLine1() && result.isOnLine2()) {
            return result;
        } else {
            return null;
        }
    }

}
