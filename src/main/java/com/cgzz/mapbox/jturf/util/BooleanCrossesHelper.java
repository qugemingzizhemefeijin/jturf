package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.*;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

public final class BooleanCrossesHelper {

    private BooleanCrossesHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断是否交叉<br><br>
     * <p>
     * 如果交集产生的几何图形的维数比两个源几何图形的最大维数小1，并且交集集位于两个源几何图形的内部，则返回True。
     *
     * @param geometry1 图形1，支持 MULTI_POINT、LINE、POLYGON
     * @param geometry2 图形2，支持 MULTI_POINT、LINE、POLYGON
     * @return 如果交叉则返回true
     */
    public static boolean booleanCrosses(Geometry geometry1, Geometry geometry2) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();

        switch (t1) {
            case MULTI_POINT:
                switch (t2) {
                    case LINE_STRING:
                        return doMultiPointAndLineStringCross(MultiPoint.multiPoint(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return doesMultiPointCrossPoly(MultiPoint.multiPoint(geometry1), Polygon.polygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case LINE_STRING:
                switch (t2) {
                    case MULTI_POINT:
                        return doMultiPointAndLineStringCross(MultiPoint.multiPoint(geometry2), LineString.lineString(geometry1));
                    case LINE_STRING:
                        return doLineStringsCross(LineString.lineString(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return doLineStringAndPolygonCross(LineString.lineString(geometry1), Polygon.polygon(geometry2));
                    default:
                        throw new JTurfException("geometry1 " + t2 + " not supported");
                }
            case POLYGON:
                switch (t2) {
                    case MULTI_POINT:
                        return doesMultiPointCrossPoly(MultiPoint.multiPoint(geometry2), Polygon.polygon(geometry1));
                    case LINE_STRING:
                        return doLineStringAndPolygonCross(LineString.lineString(geometry2), Polygon.polygon(geometry1));
                    default:
                        throw new JTurfException("geometry1 " + t2 + " not supported");
                }
            default:
                throw new JTurfException("geometry1 " + t1 + " not supported");
        }
    }

    private static boolean doMultiPointAndLineStringCross(MultiPoint multiPoint, LineString line) {
        boolean foundIntPoint = false, foundExtPoint = false;
        List<Point> multiPointList = multiPoint.coordinates();
        List<Point> linePointList = line.coordinates();
        int pointLength = multiPointList.size(), lineLength = linePointList.size();
        int i = 0;

        while (i < pointLength && !foundIntPoint && !foundExtPoint) {
            for (int i2 = 0; i2 < lineLength - 1; i2++) {
                boolean incEndVertices = true;
                if (i2 == 0 || i2 == lineLength - 2) {
                    incEndVertices = false;
                }
                if (JTurfHelper.isPointOnLineSegment(linePointList.get(i2), linePointList.get(i2 + 1), multiPointList.get(i), incEndVertices)) {
                    foundIntPoint = true;
                } else {
                    foundExtPoint = true;
                }
            }
            i++;
        }

        return foundIntPoint && foundExtPoint;
    }

    private static boolean doesMultiPointCrossPoly(MultiPoint multiPoint, Polygon polygon) {
        boolean foundIntPoint = false, foundExtPoint = false;
        List<Point> points = multiPoint.coordinates();
        for (int i = 0, pointLength = points.size(); i < pointLength && (!foundIntPoint || !foundExtPoint); i++) {
            if (JTurfBooleans.booleanPointInPolygon(points.get(i), polygon)) {
                foundIntPoint = true;
            } else {
                foundExtPoint = true;
            }
        }

        return foundExtPoint && foundIntPoint;
    }

    private static boolean doLineStringsCross(LineString line1, LineString line2) {
        // 获取交叉点
        FeatureCollection doLinesIntersect = JTurfMisc.lineIntersect(line1, line2);
        if (doLinesIntersect == null || doLinesIntersect.isEmpty()) {
            return false;
        }
        List<Point> linePointList1 = line1.coordinates(), linePointList2 = line2.coordinates();
        int size1 = linePointList1.size(), size2 = linePointList2.size();

        for (int i = 0; i < size1 - 1; i++) {
            for (int i2 = 0; i2 < size2 - 1; i2++) {
                boolean incEndVertices = true;
                if (i2 == 0 || i2 == size2 - 2) {
                    incEndVertices = false;
                }
                if (JTurfHelper.isPointOnLineSegment(linePointList1.get(i), linePointList1.get(i + 1), linePointList2.get(i2), incEndVertices)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean doLineStringAndPolygonCross(LineString line, Polygon polygon) {
        Feature polygonToLine = JTurfFeatureConversion.polygonToLine(polygon);
        FeatureCollection doLinesIntersect = JTurfMisc.lineIntersect(line, polygonToLine);

        return doLinesIntersect != null && !doLinesIntersect.isEmpty();
    }

}
