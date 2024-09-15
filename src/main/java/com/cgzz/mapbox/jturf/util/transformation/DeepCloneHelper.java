package com.cgzz.mapbox.jturf.util.transformation;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 深度克隆GEO组件工具类
 *
 */
public final class DeepCloneHelper {

    private DeepCloneHelper() {
        throw new AssertionError("No Instances.");
    }

    public static Geometry deepClone(Geometry geometry) {
        geometry = JTurfMeta.getGeom(geometry);
        GeometryType geometryType = geometry.geometryType();

        switch (geometryType) {
            case POINT:
                return deepClone(Point.point(geometry));
            case LINE_STRING:
                return deepClone(LineString.lineString(geometry));
            case POLYGON:
                return deepClone(Polygon.polygon(geometry));
            case MULTI_POLYGON:
                return deepClone(MultiPolygon.multiPolygon(geometry));
            case MULTI_POINT:
                return deepClone(MultiPoint.multiPoint(geometry));
            case MULTI_LINE_STRING:
                return deepClone(MultiLineString.multiLineString(geometry));
            case FEATURE_COLLECTION:
                return deepClone(FeatureCollection.featureCollection(geometry));
            case GEOMETRY_COLLECTION:
                return deepClone(GeometryCollection.geometryCollection(geometry));
            default:
                throw new JTurfException("geometry not support deepClone");
        }
    }

    /**
     * 深度克隆一个Point对象
     *
     * @param point 要克隆的Point
     * @return 克隆后的Point
     */
    private static Point deepClone(Point point) {
        return Point.fromLngLat(point);
    }

    /**
     * 深度克隆一个LineString对象
     *
     * @param lineString 要克隆的LineString
     * @return 克隆后的LineString
     */
    private static LineString deepClone(LineString lineString) {
        return LineString.fromLngLats(deepClonePointList(lineString.coordinates()));
    }

    /**
     * 深度克隆一个Point的List对象
     *
     * @param originPointList 要克隆的Point的List
     * @return 克隆后的集合
     */
    private static List<Point> deepClonePointList(List<Point> originPointList) {
        List<Point> newPointList = new ArrayList<>(originPointList.size());

        for (Point p : originPointList) {
            newPointList.add(deepClone(p));
        }

        return newPointList;
    }

    /**
     * 深度克隆一个Polygon对象
     *
     * @param polygon 要克隆的Polygon
     * @return 克隆后的Polygon
     */
    private static Polygon deepClone(Polygon polygon) {
        return Polygon.fromLngLats(deepClonePointListList(polygon.coordinates()));
    }

    /**
     * 深度克隆一个Point的List的List对象
     *
     * @param originPointListList 要克隆的Point的List的List
     * @return 克隆后的集合
     */
    private static List<List<Point>> deepClonePointListList(List<List<Point>> originPointListList) {
        List<List<Point>> newPointPintList = new ArrayList<>(originPointListList.size());

        for (List<Point> originPointList : originPointListList) {
            newPointPintList.add(deepClonePointList(originPointList));
        }

        return newPointPintList;
    }

    /**
     * 深度克隆一个MultiPoint对象
     *
     * @param multiPoint 要克隆的MultiPoint
     * @return 返回克隆后的MultiPoint
     */
    private static MultiPoint deepClone(MultiPoint multiPoint) {
        return MultiPoint.fromLngLats(deepClonePointList(multiPoint.coordinates()));
    }

    /**
     * 深度克隆一个MultiLineString对象
     *
     * @param multiLineString 要克隆的MultiLineString
     * @return 返回克隆后的MultiLineString
     */
    private static MultiLineString deepClone(MultiLineString multiLineString) {
        return MultiLineString.fromLngLats(deepClonePointListList(multiLineString.coordinates()));
    }

    /**
     * 深度克隆一个MultiPolygon对象
     *
     * @param multiPolygon 要克隆的MultiPolygon
     * @return 返回克隆后的MultiPolygon
     */
    private static MultiPolygon deepClone(MultiPolygon multiPolygon) {
        List<List<List<Point>>> originPointListListList = multiPolygon.coordinates();
        List<List<List<Point>>> newPointListListList = new ArrayList<>(originPointListListList.size());

        for (List<List<Point>> originPointListList : originPointListListList) {
            newPointListListList.add(deepClonePointListList(originPointListList));
        }

        return MultiPolygon.fromLngLats(newPointListListList);
    }

    /**
     * 深度克隆一个Feature对象
     *
     * @param feature 要克隆的Feature
     * @return 返回克隆后的Feature
     */
    private static Feature deepClone(Feature feature) {
        Geometry oldGeometry = feature.geometry();
        Geometry newGeometry = deepClone(oldGeometry);

        return Feature.fromGeometry(newGeometry, feature.deepCopyProperties());
    }

    /**
     * 深度克隆一个FeatureCollection对象
     *
     * @param featureCollection 要克隆的FeatureCollection
     * @return 返回克隆后的FeatureCollection
     */
    private static FeatureCollection deepClone(FeatureCollection featureCollection) {
        List<Feature> oldFeatures = featureCollection.geometries();
        List<Feature> newFeatures = new ArrayList<>(oldFeatures.size());

        for (Feature feature : oldFeatures) {
            newFeatures.add(deepClone(feature));
        }

        return FeatureCollection.fromFeatures(newFeatures);
    }

    /**
     * 深度克隆一个GeometryCollection对象
     *
     * @param geometryCollection 要克隆的GeometryCollection
     * @return 返回克隆后的GeometryCollection
     */
    private static GeometryCollection deepClone(GeometryCollection geometryCollection) {
        List<Geometry> oldGeometries = geometryCollection.geometries();
        List<Geometry> newGeometries = new ArrayList<>(oldGeometries.size());

        for (Geometry geometry : oldGeometries) {
            newGeometries.add(deepClone(geometry));
        }

        return GeometryCollection.fromGeometries(newGeometries);
    }

}
