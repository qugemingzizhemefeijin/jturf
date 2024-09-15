package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.Collections;
import java.util.List;

/**
 * 检查图形是否是有效的辅助类
 */
public final class BooleanValidHelper {

    public BooleanValidHelper() {
        throw new AssertionError("No Instances.");
    }

    public static boolean booleanValid(Geometry geometry) {
        geometry = JTurfMeta.getGeom(geometry);
        GeometryType t = geometry.geometryType();
        switch (t) {
            case POINT:
                return true;
            case MULTI_POINT:
                return booleanValid(MultiPoint.multiPoint(geometry));
            case LINE_STRING:
                return booleanValid(LineString.lineString(geometry));
            case MULTI_LINE_STRING:
                return booleanValid(MultiLineString.multiLineString(geometry));
            case POLYGON:
                return booleanValid(Polygon.polygon(geometry));
            case MULTI_POLYGON:
                return booleanValid(MultiPolygon.multiPolygon(geometry));
            default:
                return false;
        }
    }

    private static boolean booleanValid(MultiPoint multiPoint) {
        List<Point> pointList = multiPoint.coordinates();

        return pointList != null && !pointList.isEmpty();
    }

    private static boolean booleanValid(LineString line) {
        List<Point> pointList = line.coordinates();

        return pointList != null && pointList.size() >= 2;
    }

    private static boolean booleanValid(MultiLineString multiLineString) {
        List<List<Point>> coordinates = multiLineString.coordinates();
        if (coordinates == null || coordinates.isEmpty()) {
            return false;
        }
        for (List<Point> pointList : coordinates) {
            if (pointList == null || pointList.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean booleanValid(Polygon polygon) {
        return booleanValidPolygonPoints(polygon.coordinates());
    }

    private static boolean booleanValid(MultiPolygon multiPolygon) {
        List<List<List<Point>>> multiPointList = multiPolygon.coordinates();
        if (multiPointList == null || multiPointList.isEmpty()) {
            return false;
        }

        for (int i = 0, s = multiPointList.size(); i < s; i++) {
            List<List<Point>> pointLists = multiPointList.get(i);

            Polygon fstPolygon = pointLists.size() > 1 ? Polygon.fromLngLats(Collections.singletonList(pointLists.get(0))) : null;
            for (int ii = 0, size = pointLists.size(); ii < size; ii++) {
                if (!booleanValidPoints(pointLists, fstPolygon, ii)) {
                    return false;
                }

                if (ii == 0) {
                    // 如果传入的点集合与另一个点组集合是否正常（不相交，但是又交叉）
                    if (!JTurfHelper.checkPolygonAgainstOthers(pointLists, multiPointList, i)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean booleanValidPolygonPoints(List<List<Point>> pointList) {
        if (pointList == null || pointList.isEmpty()) {
            return false;
        }

        Polygon fstPolygon = pointList.size() > 1 ? Polygon.fromLngLats(Collections.singletonList(pointList.get(0))) : null;
        for (int i = 0, size = pointList.size(); i < size; i++) {
            if (!booleanValidPoints(pointList, fstPolygon, i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean booleanValidPoints(List<List<Point>> pointList, Polygon fstPolygon, int idx) {
        List<Point> coords = pointList.get(idx);

        if (coords == null || coords.size() < 4) {
            return false;
        }
        if (!JTurfHelper.checkRingsClose(coords)) {
            return false;
        }
        if (JTurfHelper.checkRingsForSpikesPunctures(coords)) {
            return false;
        }

        if (idx > 0) {
            FeatureCollection lineIntersectPoints = JTurfMisc.lineIntersect(fstPolygon, Polygon.fromLngLats(Collections.singletonList(pointList.get(idx))));
            if (lineIntersectPoints != null && lineIntersectPoints.size() > 1) {
                return false;
            }
        }

        return true;
    }

}
