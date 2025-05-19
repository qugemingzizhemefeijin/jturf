package com.cgzz.mapbox.jturf.util.other;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMisc;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.List;

/**
 * 判断有效性辅助类
 */
public class BooleanValidHelper {

    public BooleanValidHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断有效性<br>
     * booleanValid 检查几何体是否符合 OGC 简单特征规范的有效性。
     *
     * @param feature Feature图形组件
     * @return 有效的对象则返回true
     */
    public static <T extends Geometry> boolean booleanValid(Feature<T> feature) {
        return booleanValid(feature.geometry());
    }

    /**
     * 判断有效性<br>
     * booleanValid 检查几何体是否符合 OGC 简单特征规范的有效性。
     *
     * @param geometry 图形组件
     * @return 有效的对象则返回true
     */
    public static boolean booleanValid(Geometry geometry) {
        GeometryType type = geometry.geometryType();

        switch (type) {
            case POINT:
            case MULTI_POINT:
                return true;
            case LINE_STRING: {
                List<Point> pointList = LineString.lineString(geometry).coordinates();
                return pointList != null && pointList.size() > 1;
            }
            case MULTI_LINE_STRING:
                return booleanMultiLineStringValid(MultiLineString.multiLineString(geometry));
            case POLYGON:
                return booleanPolygonValid(Polygon.polygon(geometry));
            case MULTI_POLYGON:
                return booleanMultiPolygonValid(MultiPolygon.multiPolygon(geometry));
        }
        return false;
    }

    private static boolean booleanMultiLineStringValid(MultiLineString multiLineString) {
        List<List<Point>> coordinates = multiLineString.coordinates();
        if (coordinates == null || coordinates.isEmpty()) {
            return false;
        }

        for (List<Point> coords : coordinates) {
            if (coords == null || coords.size() < 2) {
                return false;
            }
        }

        return true;
    }

    private static boolean booleanPolygonValid(Polygon polygon) {
        List<List<Point>> coordinates = polygon.coordinates();
        if (coordinates == null || coordinates.isEmpty()) {
            return false;
        }

        for (int i = 0, size = coordinates.size(); i < size; i++) {
            List<Point> coords = coordinates.get(i);
            if (coords == null || coords.size() < 4) {
                return false;
            }
            if (!JTurfHelper.checkRingsClose(coords)) {
                return false;
            }
            if (JTurfHelper.checkRingsForSpikesPunctures(coords)) {
                return false;
            }
            if (i > 0) {
                FeatureCollection<Point> featureCollection = JTurfMisc.lineIntersect(Polygon.fromOuterInner(coordinates.get(0)), Polygon.fromOuterInner(coords));
                if (featureCollection == null) {
                    return false;
                }
                List<Feature<Point>> features = featureCollection.geometries();
                if (features != null && features.size() > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean booleanMultiPolygonValid(MultiPolygon multiPolygon) {
        List<List<List<Point>>> geom = multiPolygon.coordinates();
        if (geom == null || geom.isEmpty()) {
            return false;
        }

        for (int i = 0, s = geom.size(); i < s; i++) {
            List<List<Point>> coordinates = geom.get(i);
            if (coordinates == null || coordinates.isEmpty()) {
                return false;
            }

            for (int ii = 0, size = coordinates.size(); ii < size; ii++) {
                List<Point> coords = coordinates.get(ii);
                if (coords == null || coords.size() < 4) {
                    return false;
                }
                if (!JTurfHelper.checkRingsClose(coords)) {
                    return false;
                }
                if (JTurfHelper.checkRingsForSpikesPunctures(coords)) {
                    return false;
                }
                if (ii == 0) {
                    // 如果传入的点集合与另一个点组集合是否正常（不相交，但是又交叉）
                    if (!JTurfHelper.checkPolygonAgainstOthers(coordinates, multiPolygon.coordinates(), i)) {
                        return false;
                    }
                } else {
                    FeatureCollection<Point> featureCollection = JTurfMisc.lineIntersect(Polygon.fromOuterInner(coordinates.get(0)), Polygon.fromOuterInner(coords));
                    if (featureCollection == null) {
                        return false;
                    }
                    List<Feature<Point>> features = featureCollection.geometries();
                    if (features != null && features.size() > 1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
