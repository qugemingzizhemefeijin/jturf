package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

/**
 * 判断是否包含辅助类
 */
public final class BooleanContainsHelper {

    public BooleanContainsHelper() {
        throw new AssertionError("No Instances.");
    }

    public static boolean booleanContains(Geometry geometry1, Geometry geometry2) {
        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();
        switch (t1) {
            case POINT:
                if (t2 == GeometryType.POINT) {
                    return JTurfHelper.equals(Point.point(geometry1), Point.point(geometry2));
                }
                throw new JTurfException("geometry2 " + t2 + " not supported");
            case MULTI_POINT:
                switch (t2) {
                    case POINT:
                        return JTurfHelper.isPointInMultiPoint(Point.point(geometry2), MultiPoint.multiPoint(geometry1));
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointInMultiPoint(MultiPoint.multiPoint(geometry2), MultiPoint.multiPoint(geometry1));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case LINE_STRING:
                switch (t2) {
                    case POINT:
                        return JTurfHelper.isPointOnLine(Point.point(geometry2), LineString.lineString(geometry1));
                    case LINE_STRING:
                        return JTurfHelper.isLineOnLine(LineString.lineString(geometry2), LineString.lineString(geometry1));
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointOnLine(MultiPoint.multiPoint(geometry2), LineString.lineString(geometry1));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case POLYGON:
                switch (t2) {
                    case POINT:
                        return JTurfBooleans.booleanPointInPolygon(Point.point(geometry2), Polygon.polygon(geometry1), true);
                    case LINE_STRING:
                        return JTurfHelper.isLineInPolygon(LineString.lineString(geometry2), Polygon.polygon(geometry2));
                    case POLYGON:
                        return JTurfHelper.isPolygonInPolygon(Polygon.polygon(geometry2), Polygon.polygon(geometry1));
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointInPolygon(MultiPoint.multiPoint(geometry2), Polygon.polygon(geometry1));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            default:
                throw new JTurfException("geometry1 " + t1 + " not supported");
        }
    }

}
