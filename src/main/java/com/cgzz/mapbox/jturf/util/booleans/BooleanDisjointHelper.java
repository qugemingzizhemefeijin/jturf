package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.*;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.omg.CORBA.BooleanHolder;

import java.util.List;

public final class BooleanDisjointHelper {

    private BooleanDisjointHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断是否不相交 <br><br>
     * <p>
     * 如果两个几何图形的交集为空集，则返回(TRUE)。
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果图形不相交，则返回true
     */
    public static boolean booleanDisjoint(Geometry geometry1, Geometry geometry2) {
        BooleanHolder bool = new BooleanHolder(true);
        JTurfMeta.flattenEach(geometry1, (f1, featureIndex1,  multiFeatureIndex1) -> {
            JTurfMeta.flattenEach(geometry2, (f2, featureIndex2,  multiFeatureIndex2) -> {
                if (!bool.value) {
                    return false;
                }
                bool.value = disjoint(f1, f2);
                return true;
            });

            return true;
        });

        return bool.value;
    }

    /**
     * 简单几何图形的不相交操作（点/线字符串/多边形）
     *
     * @param feature1 图形1，只支持 Line、POINT、POLYGON
     * @param feature2 图形2，只支持 Line、POINT、POLYGON
     * @return 如果不相交则返回true
     */
    private static boolean disjoint(Feature<Geometry> feature1, Feature<Geometry> feature2) {
        Geometry geometry1 = feature1.geometry();
        Geometry geometry2 = feature2.geometry();

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();
        switch (t1) {
            case POINT:
                switch (t2) {
                    case POINT:
                        return !JTurfHelper.equals(Point.point(geometry1), Point.point(geometry2));
                    case LINE_STRING:
                        return !JTurfHelper.isPointOnLine(Point.point(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return !JTurfBooleans.booleanPointInPolygon(Point.point(geometry1), geometry2);
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case LINE_STRING:
                switch (t2) {
                    case POINT:
                        return !JTurfHelper.isPointOnLine(Point.point(geometry2), LineString.lineString((geometry1)));
                    case LINE_STRING:
                        return !isLineOnLine(LineString.lineString(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return !isLineInPoly(LineString.lineString(geometry1), Polygon.polygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case POLYGON:
                switch (t2) {
                    case POINT:
                        return !JTurfBooleans.booleanPointInPolygon(Point.point(geometry1), Polygon.polygon(geometry1));
                    case LINE_STRING:
                        return !isLineInPoly(LineString.lineString(geometry2), Polygon.polygon(geometry1));
                    case POLYGON:
                        return !isPolyInPoly(Polygon.polygon(geometry2), Polygon.polygon(geometry1));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            default:
                throw new JTurfException("geometry1 " + t1 + " not supported");
        }
    }

    private static boolean isLineOnLine(LineString line1, LineString line2) {
        FeatureCollection<Point> doLinesIntersect = JTurfMisc.lineIntersect(line1, line2);
        return doLinesIntersect != null && !doLinesIntersect.isEmpty();
    }

    private static boolean isLineInPoly(LineString line, Polygon polygon) {
        List<Point> linePoints = line.coordinates();
        for (Point pt : linePoints) {
            if (JTurfBooleans.booleanPointInPolygon(pt, polygon)) {
                return true;
            }
        }

        FeatureCollection<Point> doLinesIntersect = JTurfMisc.lineIntersect(line, JTurfFeatureConversion.polygonToLine(polygon));
        return doLinesIntersect != null && !doLinesIntersect.isEmpty();
    }

    private static boolean isPolyInPoly(Polygon polygon1, Polygon polygon2) {
        for (Point pt : polygon1.coordinates().get(0)) {
            if (JTurfBooleans.booleanPointInPolygon(pt, polygon2)) {
                return true;
            }
        }
        for (Point pt : polygon2.coordinates().get(0)) {
            if (JTurfBooleans.booleanPointInPolygon(pt, polygon1)) {
                return true;
            }
        }

        FeatureCollection<Point> doLinesIntersect = JTurfMisc.lineIntersect(JTurfFeatureConversion.polygonToLine(polygon1),
                JTurfFeatureConversion.polygonToLine(polygon2));
        return doLinesIntersect != null && !doLinesIntersect.isEmpty();
    }

}
