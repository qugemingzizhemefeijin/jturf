package com.cgzz.mapbox.jturf.util.polygonclipping;

import com.cgzz.mapbox.jturf.shape.Point;

import java.util.ArrayList;
import java.util.List;

public final class PolygonClipping {

    private PolygonClipping() {
        throw new AssertionError("No Instances.");
    }

    private static List<List<Point>> reduceDimension(List<List<List<Point>>> points) {
        if (points == null) {
            return null;
        }
        List<List<Point>> newPoints = new ArrayList<>();
        for (List<List<Point>> p1 : points) {
            newPoints.addAll(p1);
        }
        return newPoints;
    }

    @SafeVarargs
    public static List<List<Point>> union(List<List<Point>> geom, List<List<Point>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }

        return reduceDimension(Operation.run(PolygonClippingType.UNION, geom, moreGeoms));
    }

    @SafeVarargs
    public static List<List<Point>> intersection(List<List<Point>> geom, List<List<Point>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }
        return reduceDimension(Operation.run(PolygonClippingType.INTERSECT, geom, moreGeoms));
    }

    @SafeVarargs
    public static List<List<Point>> xor(List<List<Point>> geom, List<List<Point>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }
        return reduceDimension(Operation.run(PolygonClippingType.XOR, geom, moreGeoms));
    }

    @SafeVarargs
    public static List<List<Point>> difference(List<List<Point>> subjectGeom, List<List<Point>>... clippingGeoms) {
        if (clippingGeoms == null || clippingGeoms.length == 0) {
            return subjectGeom;
        }
        return reduceDimension(Operation.run(PolygonClippingType.DIFFERENCE, subjectGeom, clippingGeoms));
    }

}
