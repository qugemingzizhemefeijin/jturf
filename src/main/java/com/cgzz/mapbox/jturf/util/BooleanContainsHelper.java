package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;

/**
 * 判断是否包含辅助类
 */
public final class BooleanContainsHelper {

    public BooleanContainsHelper() {
        throw new AssertionError("No Instances.");
    }

    public static boolean booleanContains(Geometry geometry1, Geometry geometry2) {
        GeometryType t1 = geometry1.type(), t2 = geometry2.type();
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
            case LINE:
                switch (t2) {
                    case POINT:
                        return JTurfHelper.isPointOnLine(Point.point(geometry2), Line.line(geometry1));
                    case LINE:
                        return JTurfHelper.isLineOnLine(Line.line(geometry2), Line.line(geometry1));
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointOnLine(MultiPoint.multiPoint(geometry2), Line.line(geometry1));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case POLYGON:
                switch (t2) {
                    case POINT:
                        return JTurfBooleans.booleanPointInPolygon(Point.point(geometry2), Polygon.polygon(geometry1), true);
                    case LINE:
                        return JTurfHelper.isLineInPolygon(Line.line(geometry2), Polygon.polygon(geometry2));
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
