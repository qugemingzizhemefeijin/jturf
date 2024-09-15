package com.cgzz.mapbox.jturf.util.pkg.clipping.polygonclipping;

import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class PolygonClipping {

    private PolygonClipping() {
        throw new AssertionError("No Instances.");
    }

    @SafeVarargs
    public static List<List<List<Point>>> union(List<List<List<Point>>> geom, List<List<List<Point>>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }

        return Operation.run(PolygonClippingType.UNION, geom, moreGeoms);
    }

    @SafeVarargs
    public static List<List<List<Point>>> intersection(List<List<List<Point>>> geom, List<List<List<Point>>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }
        return Operation.run(PolygonClippingType.INTERSECT, geom, moreGeoms);
    }

    @SafeVarargs
    public static List<List<List<Point>>> xor(List<List<List<Point>>> geom, List<List<List<Point>>>... moreGeoms) {
        if (moreGeoms == null || moreGeoms.length == 0) {
            return geom;
        }
        return Operation.run(PolygonClippingType.XOR, geom, moreGeoms);
    }

    @SafeVarargs
    public static List<List<List<Point>>> difference(List<List<List<Point>>> subjectGeom, List<List<List<Point>>>... clippingGeoms) {
        if (clippingGeoms == null || clippingGeoms.length == 0) {
            return subjectGeom;
        }
        return Operation.run(PolygonClippingType.DIFFERENCE, subjectGeom, clippingGeoms);
    }

}
