package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.BoundingBox;
import com.cgzz.mapbox.jturf.shape.impl.MultiPolygon;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

import java.util.Collections;
import java.util.List;

public final class BooleanPointInPolygonHelper {

    private BooleanPointInPolygonHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断点是否在多边形内
     *
     * @param point          要判断的点
     * @param polygon        多边形，支持 Polygon、MultiPolygon
     * @param ignoreBoundary 是否忽略多边形边界（true如果点在多边形的边界上不算，false则也算在多边形内）
     * @return 如果点在多边形内，则返回true;否则返回false
     */
    public static boolean booleanPointInPolygon(Point point, Geometry polygon, boolean ignoreBoundary) {
        if (point == null) {
            throw new JTurfException("point is required");
        }
        if (polygon == null) {
            throw new JTurfException("polygon is required");
        }

        polygon = JTurfMeta.getGeom(polygon);
        GeometryType type = polygon.geometryType();

        if (type != GeometryType.POLYGON && type != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("polygon only support polygon or multiPolygon");
        }

        // Quick elimination if point is not inside bbox
        BoundingBox bbox = JTurfMeasurement.bbox(polygon);
        if (!JTurfHelper.inBBox(point, bbox)) {
            return false;
        }

        List<List<List<Point>>> coordinates;
        if (type == GeometryType.POLYGON) {
            coordinates = Collections.singletonList(Polygon.polygon(polygon).coordinates());
        } else {
            coordinates = MultiPolygon.multiPolygon(polygon).coordinates();
        }

        boolean insidePoly = false;
        for (int i = 0, size = coordinates.size(); i < size && !insidePoly; i++) {
            List<List<Point>> coords = coordinates.get(i);
            // check if it is in the outer ring first
            if (inRing(point, coords.get(0), ignoreBoundary)) {
                boolean inHole = false;
                int k = 1, s = coords.size();
                // check for the point in any of the holes
                while(k < s && !inHole) {
                    if (inRing(point, coords.get(k), !ignoreBoundary)) {
                        inHole = true;
                    }
                    k++;
                }
                if (!inHole) {
                    insidePoly = true;
                }
            }
        }
        return insidePoly;
    }

    private static boolean inRing(Point pt, List<Point> pointList, boolean ignoreBoundary) {
        boolean isInside = false;
        Point[] ring = pointList.toArray(new Point[0]);

        // 判断第一个是否与最后一个点一致，如果一致，则循环的时候不要循环到最后一个点
        int eachLimit = ring.length;
        if (JTurfHelper.equals(ring[0], ring[eachLimit - 1])) {
            eachLimit--;
        }

        for (int i = 0, j = eachLimit - 1; i < eachLimit; j = i++) {
            double xi = ring[i].getX();
            double yi = ring[i].getY();
            double xj = ring[j].getX();
            double yj = ring[j].getY();

            double px = pt.getX(), py = pt.getY();

            boolean onBoundary = py * (xi - xj) + yi * (xj - px) + yj * (px - xi) == 0
                            && (xi - px) * (xj - px) <= 0
                            && (yi - py) * (yj - py) <= 0;
            if (onBoundary) {
                return !ignoreBoundary;
            }

            boolean intersect = yi > py != yj > py && px < ((xj - xi) * (py - yi)) / (yj - yi) + xi;
            if (intersect) {
                isInside = !isInside;
            }
        }

        return isInside;
    }

}
