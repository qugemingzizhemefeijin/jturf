package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

public final class BooleanWithinHelper {

    private BooleanWithinHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 判断第一个图形是否完全在第二个图形内 <br><br>
     * <p>
     * 如果第一个几何图形完全在第二个几何图形内，则返回true。两个几何图形的内部必须相交，
     * 并且，主几何图形(几何a)的内部和边界不能相交于次几何图形(几何b)的外部。
     *
     * <br><br>
     * 注意：这里有几个问题：比如判断两个线，实际只是判断线1的所有点是否在线2中，注入此类。
     *
     * @param geometry1 图形组件1
     * @param geometry2 图形组件2
     * @return 第一个图形在第二个图形内，则返回true
     */
    public static boolean booleanWithin(Geometry geometry1, Geometry geometry2) {
        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType type1 = geometry1.geometryType(), type2 = geometry2.geometryType();

        switch (type1) {
            case POINT:
                switch (type2) {
                    case MULTI_POINT:
                        return JTurfHelper.isPointInMultiPoint(Point.point(geometry1), MultiPoint.multiPoint(geometry2));
                    case LINE_STRING:
                        return JTurfBooleans.booleanPointOnLine(Point.point(geometry1), LineString.lineString(geometry2), true);
                    case POLYGON:
                    case MULTI_POLYGON:
                        return JTurfBooleans.booleanPointInPolygon(Point.point(geometry1), geometry2, true);
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case MULTI_POINT:
                switch (type2) {
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointInMultiPoint(MultiPoint.multiPoint(geometry1), MultiPoint.multiPoint(geometry2));
                    case LINE_STRING:
                        return JTurfHelper.isMultiPointOnLine(MultiPoint.multiPoint(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return JTurfHelper.isMultiPointInPolygon(MultiPoint.multiPoint(geometry1), Polygon.polygon(geometry2));
                    case MULTI_POLYGON:
                        return JTurfHelper.isMultiPointInPolygon(MultiPoint.multiPoint(geometry1), MultiPolygon.multiPolygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case LINE_STRING:
                switch (type2) {
                    case LINE_STRING:
                        return JTurfHelper.isLineOnLine(LineString.lineString(geometry1), LineString.lineString(geometry2));
                    case POLYGON:
                        return JTurfHelper.isLineInPolygon(LineString.lineString(geometry1), Polygon.polygon(geometry2));
                    case MULTI_POINT:
                        return JTurfHelper.isLineInPolygon(LineString.lineString(geometry1), MultiPolygon.multiPolygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case POLYGON:
                switch (type2) {
                    case POLYGON:
                    case MULTI_POLYGON:
                        return JTurfHelper.isPolygonInPolygon(Polygon.polygon(geometry1), geometry2);
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            default:
                throw new JTurfException("geometry1 " + type1 + " geometry not supported");
        }
    }

}
