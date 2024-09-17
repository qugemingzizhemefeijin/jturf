package com.cgzz.mapbox.jturf.util.meta;

import com.cgzz.mapbox.jturf.util.meta.func.FlattenEachFunc;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

public final class FlattenEachHelper {

    private FlattenEachHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 迭代任何对象中的扁平特性，类似于Array.forEach，会将Multi(Point|Line|Polygon)拆分成单个图形
     *
     * @param geometry 要迭代的元素
     * @param func     回调处理函数
     */
    public static void flattenEach(Geometry geometry, FlattenEachFunc func) {
        GeomEachHelper.geomEach(geometry, (g, featureIndex, properties, id) -> {
            // Callback for single geometry
            if (g == null) {
                return func.accept(null, featureIndex, 0);
            }

            GeometryType type = g.geometryType();
            switch (type) {
                case POINT:
                case LINE_STRING:
                case POLYGON:
                    return func.accept(Feature.fromGeometry(g, properties, id), featureIndex, 0);
            }

            // Callback for multi-geometry
            switch (type) {
                case MULTI_POINT: {
                    List<Point> coordinates = MultiPoint.multiPoint(g).coordinates();
                    for (int i = 0, size = coordinates.size(); i < size; i++) {
                        if (!func.accept(Feature.fromGeometry(coordinates.get(i), properties, id), featureIndex, i)) {
                            return false;
                        }
                    }
                    break;
                }
                case MULTI_LINE_STRING: {
                    List<List<Point>> coordinates = MultiLineString.multiLineString(g).coordinates();
                    for (int i = 0, size = coordinates.size(); i < size; i++) {
                        if (!func.accept(Feature.fromGeometry(LineString.fromLngLats(coordinates.get(i)), properties, id), featureIndex, i)) {
                            return false;
                        }
                    }
                    break;
                }
                case MULTI_POLYGON: {
                    List<List<List<Point>>> coordinates = MultiPolygon.multiPolygon(g).coordinates();
                    for (int i = 0, size = coordinates.size(); i < size; i++) {
                        if (!func.accept(Feature.fromGeometry(Polygon.fromLngLats(coordinates.get(i)), properties, id), featureIndex, i)) {
                            return false;
                        }
                    }
                    break;
                }
            }

            return true;
        });
    }

}
