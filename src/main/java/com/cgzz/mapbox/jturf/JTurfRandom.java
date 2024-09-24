package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.random.RandomHelper;

public final class JTurfRandom {

    private JTurfRandom() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 返回一个随机的点
     *
     * @return GeoJSON Feature of point
     */
    public static Feature<Point> randomPoint() {
        return RandomHelper.randomPoint(null);
    }

    /**
     * 返回一个随机的点
     *
     * @param bbox 指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON Feature of point
     */
    public static Feature<Point> randomPoint(BoundingBox bbox) {
        return RandomHelper.randomPoint(bbox);
    }

    /**
     * 返回一组随机的点
     *
     * @param count 指定生成随机点的数量
     * @return GeoJSON FeatureCollection of points
     */
    public static FeatureCollection<Point> randomPoint(int count) {
        return RandomHelper.randomPoint(count, null);
    }

    /**
     * 返回一组随机的点
     *
     * @param count 指定生成随机点的数量
     * @param bbox  指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON FeatureCollection of points
     */
    public static FeatureCollection<Point> randomPoint(int count, BoundingBox bbox) {
        return RandomHelper.randomPoint(count, bbox);
    }

    /**
     * 返回随机的线段
     *
     * @return GeoJSON Feature of linestring
     */
    public static Feature<LineString> randomLineString() {
        return RandomHelper.randomLineString(null, null, null, null);
    }

    /**
     * 返回随机的线段
     *
     * @param bbox 指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON Feature of linestring
     */
    public static Feature<LineString> randomLineString(BoundingBox bbox) {
        return RandomHelper.randomLineString(bbox, null, null, null);
    }

    /**
     * 返回随机的线段
     *
     * @param bbox        指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices 每个 LineString 将包含的坐标数。（默认 10）
     * @param maxLength   点与其前驱点之间可以相差的最大水平距离（默认为 0.0001）
     * @param maxRotation 线段可以从上一条线段转动的最大弧度数。（默认为 Math.PI/8）
     * @return GeoJSON Feature of linestring
     */
    public static Feature<LineString> randomLineString(BoundingBox bbox, Integer numVertices, Double maxLength, Double maxRotation) {
        return RandomHelper.randomLineString(bbox, null, null, null);
    }

    /**
     * 返回一组随机的线段
     *
     * @param count 指定返回的数量
     * @return GeoJSON FeatureCollection of linestrings
     */
    public static FeatureCollection<LineString> randomLineString(int count) {
        return RandomHelper.randomLineString(count, null, null, null, null);
    }

    /**
     * 返回随机的线段
     *
     * @param count 指定返回的数量
     * @param bbox  指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON FeatureCollection of linestrings
     */
    public static FeatureCollection<LineString> randomLineString(int count, BoundingBox bbox) {
        return RandomHelper.randomLineString(count, bbox, null, null, null);
    }

    /**
     * 返回随机的线段
     *
     * @param count       指定返回的数量
     * @param bbox        指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices 每个 LineString 将包含的坐标数。（默认 10）
     * @param maxLength   点与其前驱点之间可以相差的最大水平距离（默认为 0.0001）
     * @param maxRotation 线段可以从上一条线段转动的最大弧度数。（默认为 Math.PI/8）
     * @return GeoJSON FeatureCollection of linestrings
     */
    public static FeatureCollection<LineString> randomLineString(int count, BoundingBox bbox, Integer numVertices, Double maxLength, Double maxRotation) {
        return RandomHelper.randomLineString(count, bbox, numVertices, maxLength, maxRotation);
    }

    /**
     * 返回随机的多边形
     *
     * @return GeoJSON Feature of polygon
     */
    public static Feature<Polygon> randomPolygon() {
        return RandomHelper.randomPolygon(null, null, null);
    }

    /**
     * 返回随机的多边形
     *
     * @param bbox 指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON Feature of polygon
     */
    public static Feature<Polygon> randomPolygon(BoundingBox bbox) {
        return RandomHelper.randomPolygon(bbox, null, null);
    }

    /**
     * 返回随机的多边形
     *
     * @param bbox            指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices     每个 Polygon 将包含的坐标数。（默认 10）
     * @param maxRadialLength 点到达多边形中心的最大的纬度或经度数。（默认 10）
     * @return GeoJSON Feature of polygon
     */
    public static Feature<Polygon> randomPolygon(BoundingBox bbox, Integer numVertices, Double maxRadialLength) {
        return RandomHelper.randomPolygon(bbox, numVertices, maxRadialLength);
    }

    /**
     * 返回一组随机的多边形
     *
     * @param count 指定返回的数量
     * @return GeoJSON FeatureCollection of polygons
     */
    public static FeatureCollection<Polygon> randomPolygon(int count) {
        return RandomHelper.randomPolygon(count, null, null, null);
    }

    /**
     * 返回一组随机的多边形
     *
     * @param count 指定返回的数量
     * @param bbox  指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @return GeoJSON FeatureCollection of polygons
     */
    public static FeatureCollection<Polygon> randomPolygon(int count, BoundingBox bbox) {
        return RandomHelper.randomPolygon(count, bbox, null, null);
    }

    /**
     * 返回一组随机的多边形
     *
     * @param count           指定返回的数量
     * @param bbox            指定的随机边界框，默认在[-180, -90, 180, 90]范围
     * @param numVertices     每个 Polygon 将包含的坐标数。（默认 10）
     * @param maxRadialLength 点到达多边形中心的最大的纬度或经度数。（默认 10）
     * @return GeoJSON FeatureCollection of polygons
     */
    public static FeatureCollection<Polygon> randomPolygon(int count, BoundingBox bbox, Integer numVertices, Double maxRadialLength) {
        return RandomHelper.randomPolygon(count, bbox, numVertices, maxRadialLength);
    }

}
