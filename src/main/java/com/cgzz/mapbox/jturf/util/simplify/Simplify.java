package com.cgzz.mapbox.jturf.util.simplify;

import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

/*
 (c) 2013, Vladimir Agafonkin
 Simplify.js, a high-performance JS polyline simplification library
 mourner.github.io/simplify-js
*/

// to suit your point format, run search/replace for '.x' and '.y';
// for 3D version, see 3d branch (configurability would draw significant performance overhead)
// https://github.com/Turfjs/turf/blob/v6.5.0/packages/turf-simplify/lib/simplify.js
final class Simplify {

    private Simplify() {
        throw new AssertionError("No Instances.");
    }

    public static List<Point> simplify(List<Point> points, double tolerance, boolean highestQuality) {
        if (points.size() <= 2) {
            return points;
        }

        double sqTolerance = tolerance * tolerance;

        points = highestQuality ? points : simplifyRadialDist(points, sqTolerance);
        points = simplifyDouglasPeucker(points, sqTolerance);

        return points;
    }

    // square distance between 2 points
    private static double getSqDist(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX(), dy = p1.getY() - p2.getY();

        return dx * dx + dy * dy;
    }

    // square distance from a point to a segment
    private static double getSqSegDist(Point p, Point p1, Point p2) {
        double x = p1.getX(), y = p1.getY(), dx = p2.getX() - x, dy = p2.getY() - y;

        if (dx != 0 || dy != 0) {
            double t = ((p.getX() - x) * dx + (p.getY() - y) * dy) / (dx * dx + dy * dy);

            if (t > 1) {
                x = p2.getX();
                y = p2.getY();
            } else if (t > 0) {
                x += dx * t;
                y += dy * t;
            }
        }

        dx = p.getX() - x;
        dy = p.getY() - y;

        return dx * dx + dy * dy;
    }

    // basic distance-based simplification
    private static List<Point> simplifyRadialDist(List<Point> points, double sqTolerance) {
        Point prevPoint = points.get(0), point = null;
        List<Point> newPoints = new ArrayList<>(points.size());
        newPoints.add(prevPoint);

        for (int i = 1, len = points.size(); i < len; i++) {
            point = points.get(i);

            if (getSqDist(point, prevPoint) > sqTolerance) {
                newPoints.add(point);
                prevPoint = point;
            }
        }

        if (point != null && prevPoint != point) {
            newPoints.add(point);
        }

        return newPoints;
    }

    private static void simplifyDPStep(List<Point> points, int first, int last, double sqTolerance, List<Point> simplified) {
        int index = 0;
        double maxSqDist = sqTolerance;

        for (int i = first + 1; i < last; i++) {
            double sqDist = getSqSegDist(points.get(i), points.get(first), points.get(last));

            if (sqDist > maxSqDist) {
                index = i;
                maxSqDist = sqDist;
            }
        }

        if (maxSqDist > sqTolerance) {
            if (index - first > 1)
                simplifyDPStep(points, first, index, sqTolerance, simplified);
            simplified.add(points.get(index));
            if (last - index > 1)
                simplifyDPStep(points, index, last, sqTolerance, simplified);
        }
    }

    // simplification using Ramer-Douglas-Peucker algorithm
    private static List<Point> simplifyDouglasPeucker(List<Point> points, double sqTolerance) {
        int last = points.size() - 1;

        List<Point> simplified = new ArrayList<>();
        simplified.add(points.get(0));

        simplifyDPStep(points, 0, last, sqTolerance, simplified);
        simplified.add(points.get(last));

        return simplified;
    }

}
