package com.cgzz.mapbox.jturf.util.joins;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.models.BooleanHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.ArrayList;
import java.util.List;

public final class PointsWithinPolygonHelper {

    private PointsWithinPolygonHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param point    点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 Point
     */
    public static FeatureCollection<Point> pointsWithinPolygon(Point point, Geometry polygons) {
        return pointsWithinPolygon(Feature.fromGeometry(point), polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param point    点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 Point
     */
    public static FeatureCollection<Point> pointsWithinPolygon(Feature<Point> point, Geometry polygons) {
        List<Feature<Point>> results = new ArrayList<>(1);

        if (booleanPointInPolygon(point.geometry(), polygons)) {
            results.add(point);
        }

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   点对象集合
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 Point
     */
    public static FeatureCollection<Point> pointsWithinPolygon(FeatureCollection<Point> points, Geometry polygons) {
        List<Feature<Point>> results = new ArrayList<>();

        JTurfMeta.featureEach(points, (currentFeature, featureIndex) -> {
            if (booleanPointInPolygon(currentFeature.geometry(), polygons)) {
                results.add(currentFeature);
            }

            return true;
        });

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   多点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(MultiPoint points, Geometry polygons) {
        return multiPointsWithinPolygon(Feature.fromGeometry(points), polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   多点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(Feature<MultiPoint> points, Geometry polygons) {
        List<Point> results = new ArrayList<>();

        for (Point p : points.geometry().coordinates()) {
            if (booleanPointInPolygon(p, polygons)) {
                results.add(p);
            }
        }

        return FeatureCollection.fromFeatures(Feature.fromGeometry(MultiPoint.fromLngLats(results), points.properties()));
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   点对象集合
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(FeatureCollection<MultiPoint> points, Geometry polygons) {
        List<Feature<MultiPoint>> results = new ArrayList<>();

        JTurfMeta.featureEach(points, (currentFeature, featureIndex) -> {
            List<Point> pointsWithin = new ArrayList<>();

            for (Point p : currentFeature.geometry().coordinates()) {
                if (booleanPointInPolygon(p, polygons)) {
                    pointsWithin.add(p);
                }
            }

            if (!pointsWithin.isEmpty()) {
                results.add(Feature.fromGeometry(MultiPoint.fromLngLats(pointsWithin)));
            }

            return true;
        });

        return FeatureCollection.fromFeatures(results);
    }

    /**
     * 判断点是否在所有的多边形内
     *
     * @param point    点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 如果
     */
    private static boolean booleanPointInPolygon(Point point, Geometry polygons) {
        BooleanHolder contained = new BooleanHolder();
        JTurfMeta.geomEach(polygons, (currentGeometry, featureIndex, featureProperties, featureId) -> {
            if (JTurfBooleans.booleanPointInPolygon(point, currentGeometry)) {
                contained.value = true;
                return false;
            }
            return true;
        });

        return contained.value;
    }

}
