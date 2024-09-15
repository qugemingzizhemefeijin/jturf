package com.cgzz.mapbox.jturf.util.clipping;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.MultiPolygon;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class PolygonClippingHelper {

    private PolygonClippingHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 调用 PolygonClipping 工具类，计算对应的多边形交集、差集、并集以及补集。
     *
     * @param geometry1        仅支持 Polygon和MultiPolygon
     * @param geometry2        仅支持 Polygon和MultiPolygon
     * @param clippingFunction 执行 PolygonClipping 的工具方法
     * @return 返回 Polygon、MultiPolygon、null
     */
    public static Geometry polygonClipping(Geometry geometry1, Geometry geometry2, BiFunction<List<List<List<Point>>>, List<List<List<Point>>>, List<List<List<Point>>>> clippingFunction) {
        if (geometry1 == null && geometry2 == null) {
            return null;
        } else if (geometry1 == null) {
            return geometry2;
        } else if (geometry2 == null) {
            return geometry1;
        }

        geometry1 = JTurfMeta.getGeom(geometry1);
        geometry2 = JTurfMeta.getGeom(geometry2);

        GeometryType t1 = geometry1.geometryType(), t2 = geometry2.geometryType();
        if (t1 != GeometryType.POLYGON && t1 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry1 " + t1 + " not supported");
        }
        if (t2 != GeometryType.POLYGON && t2 != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry2 " + t2 + " not supported");
        }

        // 转换成集合，交给 PolygonClipping 工具
        List<List<List<Point>>> points1 = t1 == GeometryType.POLYGON ? Collections.singletonList(Polygon.polygon(geometry1).coordinates()) : MultiPolygon.multiPolygon(geometry1).coordinates();
        List<List<List<Point>>> points2 = t2 == GeometryType.POLYGON ? Collections.singletonList(Polygon.polygon(geometry2).coordinates()) : MultiPolygon.multiPolygon(geometry2).coordinates();

        List<List<List<Point>>> retPoints = clippingFunction.apply(points1, points2);

        if (retPoints == null || retPoints.isEmpty()) {
            return null;
        } else if (retPoints.size() == 1) {
            return Polygon.fromLngLats(retPoints.get(0));
        } else {
            return MultiPolygon.fromLngLats(retPoints);
        }
    }

}
