package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.joins.PointsWithinPolygonHelper;
import com.cgzz.mapbox.jturf.util.joins.TagHelper;

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

    /**
     * 接受一组 Point 和一组 Polygon | MultiPolygon，如果点在多边形内，则执行空间连接。
     *
     * @param points   一组点
     * @param polygons 一组Polygon 或 MultiPolygon
     * @param field    添加到连接的 Point 上
     * @param outField 用于存储 多边形 的连接属性
     * @return 具有 outField 属性的点包含 polygons 中的 field 属性值
     */
    public static <T extends Geometry> FeatureCollection<Point> tag(FeatureCollection<Point> points, FeatureCollection<T> polygons, String field, String outField) {
        return TagHelper.tag(points, polygons, field, outField);
    }

}
