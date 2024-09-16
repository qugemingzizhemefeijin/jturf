package com.cgzz.mapbox.jturf.util.measurement;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

public final class LengthHelper {

    private LengthHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 获取多边形并以指定单位测量其周长。
     *
     * @param geometry 支持 LINE、POLYGON、MULTI_LINE、MULTI_POLYGON
     * @param units    距离单位
     * @return 以指定单位表示的输入面的总周长
     */
    @SuppressWarnings("unchecked")
    public static double length(Geometry geometry, Units units) {
        geometry = JTurfMeta.getGeom(geometry);

        switch (geometry.geometryType()) {
            case LINE_STRING:
                return length(((LineString) geometry).coordinates(), units);
            case POLYGON:
                return lengthList(((Polygon) geometry).coordinates(), units);
            case MULTI_LINE_STRING:
                return lengthList(((MultiLineString) geometry).coordinates(), units);
            case MULTI_POLYGON:
                return lengthListList(((MultiPolygon) geometry).coordinates(), units);
            case FEATURE_COLLECTION: {
                double total = 0;
                for (Feature<Geometry> feature : ((FeatureCollection<Geometry>) geometry).geometries()) {
                    total += length(feature.geometry(), units);
                }
                return total;
            }
            case GEOMETRY_COLLECTION: {
                double total = 0;
                for (Geometry singleGeometry : ((GeometryCollection) geometry).geometries()) {
                    total += length(singleGeometry, units);
                }
                return total;
            }
            default:
                throw new JTurfException("Unknown Geometry Type");
        }
    }

    /**
     * 获取多点集合并以指定单位测量其周长
     *
     * @param coordinates 多点集合
     * @param units       距离单位
     * @return 以指定单位表示的输入面的总周长
     */
    public static double lengthListList(List<List<List<Point>>> coordinates, Units units) {
        double total = 0;
        for (List<List<Point>> coords : coordinates) {
            total += lengthList(coords, units);
        }
        return total;
    }

    /**
     * 获取多点集合并以指定单位测量其周长
     *
     * @param coordinates 多点集合
     * @param units       距离单位
     * @return 以指定单位表示的输入面的总周长
     */
    public static double lengthList(List<List<Point>> coordinates, Units units) {
        double total = 0;
        for (List<Point> coords : coordinates) {
            total += length(coords, units);
        }
        return total;
    }

    /**
     * 获取点集合并以指定的单位测量其长度。
     *
     * @param coords 点集合
     * @param units  距离单位
     * @return 以指定单位表示的长度
     */
    public static double length(List<Point> coords, Units units) {
        double travelled = 0;
        Point prevCoords = coords.get(0);
        Point curCoords;
        for (int i = 1; i < coords.size(); i++) {
            curCoords = coords.get(i);
            travelled += JTurfMeasurement.distance(prevCoords, curCoords, units);
            prevCoords = curCoords;
        }
        return travelled;
    }

}
