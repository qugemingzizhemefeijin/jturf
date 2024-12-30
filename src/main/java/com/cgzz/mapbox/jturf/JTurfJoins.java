package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.joins.PointsWithinPolygonHelper;

public final class JTurfJoins {

    private JTurfJoins() {
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
        return PointsWithinPolygonHelper.pointsWithinPolygon(point, polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param point    点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 Point
     */
    public static FeatureCollection<Point> pointsWithinPolygon(Feature<Point> point, Geometry polygons) {
        return PointsWithinPolygonHelper.pointsWithinPolygon(point, polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   点对象集合
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 Point
     */
    public static FeatureCollection<Point> pointsWithinPolygon(FeatureCollection<Point> points, Geometry polygons) {
        return PointsWithinPolygonHelper.pointsWithinPolygon(points, polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   多点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(MultiPoint points, Geometry polygons) {
        return PointsWithinPolygonHelper.multiPointsWithinPolygon(points, polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   多点对象
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(Feature<MultiPoint> points, Geometry polygons) {
        return PointsWithinPolygonHelper.multiPointsWithinPolygon(points, polygons);
    }

    /**
     * 查找落在 (多)多边形 内的 点 或 多点 坐标位置。
     *
     * @param points   多点对象集合
     * @param polygons FeatureCollection | Geometry | Feature | Polygon | MultiPolygon
     * @return 位置位于至少一个多边形内的 MultiPoint
     */
    public static FeatureCollection<MultiPoint> multiPointsWithinPolygon(FeatureCollection<MultiPoint> points, Geometry polygons) {
        return PointsWithinPolygonHelper.multiPointsWithinPolygon(points, polygons);
    }

}
