package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.misc.*;

public final class JTurfMisc {

    private JTurfMisc() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 返回自身的相交点
     *
     * @param geometry 图形，支持 POLYGON、LING_STRING、MULTI_LINE_STRING、MULTI_POLYGON
     * @return 返回自相交点集合
     */
    public static FeatureCollection<Point> kinks(Geometry geometry) {
        return KinksHelper.kinks(geometry);
    }

    /**
     * 多边型顶点连线，从一个(多)Line或(多)Polygon创建一个2-vertex线段的GeometryCollection
     *
     * @param geometry 支持 LineString|MultiLine|MultiPolygon|Polygon
     * @return 返回线段集合
     */
    public static FeatureCollection<LineString> lineSegment(Geometry geometry) {
        return LineSegmentHelper.lineSegment(geometry);
    }

    /**
     * 计算两个图形相交点
     *
     * @param geometry1 图形1，支持 LineString、Polygon、MultiLineString、MultiPolygon
     * @param geometry2 图形2，支持 LineString、Polygon、MultiLineString、MultiPolygon
     * @return 返回相交点集合
     */
    public static FeatureCollection<Point> lineIntersect(Geometry geometry1, Geometry geometry2) {
        return LineIntersectHelper.lineIntersect(geometry1, geometry2);
    }

    /**
     * 计算两个图形之间重叠的线，默认容差距离为0公里
     *
     * @param geometry1 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @return 返回两个图形之间重叠的线段集合
     */
    public static FeatureCollection<LineString> lineOverlap(Geometry geometry1, Geometry geometry2) {
        return lineOverlap(geometry1, geometry2, 0);
    }

    /**
     * 计算两个图形之间重叠的线
     *
     * @param geometry1 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形1，支持 Line、MultiLine、Polygon、MultiPolygon
     * @param tolerance 匹配重叠线段的容差距离（以公里为单位）
     * @return 返回两个图形之间重叠的线段集合
     */
    public static FeatureCollection<LineString> lineOverlap(Geometry geometry1, Geometry geometry2, Integer tolerance) {
        return LineOverlapHelper.lineOverlap(geometry1, geometry2, tolerance);
    }

    /**
     * 计算点到多线段的最短间距的点，默认距离单位：公里
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @return 返回线段上最短距离的点
     */
    public static Feature<Point> nearestPointOnLine(Geometry lines, Point pt) {
        return nearestPointOnLine(lines, pt, Units.KILOMETERS);
    }

    /**
     * 计算点到多线段的最短间距的点
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @param units 距离单位
     * @return 返回线段上最短距离的点
     */
    public static Feature<Point> nearestPointOnLine(Geometry lines, Point pt, Units units) {
        return NearestPointOnLineHelper.nearestPointOnLine(lines, pt, units);
    }

    /**
     * 计算点到多线段的最短间距的点，默认距离单位：公里
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @return 返回线段上最短距离的点
     */
    public static Feature<Point> nearestPointOnLine(Geometry lines, Feature<Point> pt) {
        return nearestPointOnLine(lines, pt, Units.KILOMETERS);
    }

    /**
     * 计算点到多线段的最短间距的点，默认距离单位：公里
     *
     * @param lines 要计算的线段，支持Line、MultiLine
     * @param pt    点
     * @return 返回线段上最短距离的点
     */
    public static Feature<Point> nearestPointOnLine(Geometry lines, Feature<Point> pt, Units units) {
        Geometry geometry = pt.geometry();
        if (geometry.geometryType() != GeometryType.POINT) {
            throw new JTurfException("pt type must Point");
        }

        return NearestPointOnLineHelper.nearestPointOnLine(lines, Point.point(geometry), units);
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     * <p>
     * 默认步长为64，距离单位 KILOMETERS
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @return 返回弧度的线条
     */
    public static Feature<LineString> lineArc(Point center, double radius, double bearing1, double bearing2) {
        return lineArc(center, radius, bearing1, bearing2, null, null);
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @param units    单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 返回弧度的线条
     */
    public static Feature<LineString> lineArc(Point center, double radius, double bearing1, double bearing2, Integer steps, Units units) {
        return LineArcHelper.lineArc(center, radius, bearing1, bearing2, steps, units, null);
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     * <p>
     * 默认步长为64，距离单位 KILOMETERS
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @return 返回弧度的线条
     */
    public static Feature<LineString> lineArc(Feature<Point> center, double radius, double bearing1, double bearing2) {
        return lineArc(center, radius, bearing1, bearing2, null, null);
    }

    /**
     * 创建圆弧<br>
     * <p>
     * 在bearing1和bearing2之间创建给定半径和圆心点的圆弧；0方位为中心点以北，顺时针正。
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 方位角1 圆弧第一半径的角度
     * @param bearing2 方位角2 圆弧第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @param units    单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 返回弧度的线条
     */
    public static Feature<LineString> lineArc(Feature<Point> center, double radius, double bearing1, double bearing2, Integer steps, Units units) {
        return LineArcHelper.lineArc(center.geometry(), radius, bearing1, bearing2, steps, units, center.properties());
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param line      线段
     * @param startDist 沿线到起点的起始距离（默认 KILOMETERS）
     * @param stopDist  沿线到终点的停靠点距离（默认 KILOMETERS）
     * @return 切片线段
     */
    public static Feature<LineString> lineSliceAlong(LineString line, double startDist, double stopDist) {
        return lineSliceAlong(line, startDist, stopDist, null);
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param line      线段
     * @param startDist 沿线到起点的起始距离
     * @param stopDist  沿线到终点的停靠点距离
     * @param units     距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 切片线段
     */
    public static Feature<LineString> lineSliceAlong(LineString line, double startDist, double stopDist, Units units) {
        return LineSliceAlongHelper.lineSliceAlong(line, startDist, stopDist, units, null);
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param feature   线段
     * @param startDist 沿线到起点的起始距离（默认 KILOMETERS）
     * @param stopDist  沿线到终点的停靠点距离（默认 KILOMETERS）
     * @return 切片线段
     */
    public static Feature<LineString> lineSliceAlong(Feature<LineString> feature, double startDist, double stopDist) {
        return lineSliceAlong(feature, startDist, stopDist, null);
    }

    /**
     * 根据距离截取多线段
     * <p>
     * 取一条线，沿该线到起始点的指定距离，以及沿该线到终止点的指定距离，并返回这些点之间的该线的分段。
     *
     * @param feature   线段
     * @param startDist 沿线到起点的起始距离
     * @param stopDist  沿线到终点的停靠点距离
     * @param units     距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 切片线段
     */
    public static Feature<LineString> lineSliceAlong(Feature<LineString> feature, double startDist, double stopDist, Units units) {
        return LineSliceAlongHelper.lineSliceAlong(feature.geometry(), startDist, stopDist, units, feature.properties());
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度，默认距离单位 KILOMETERS
     * @return 线段集合
     */
    public static FeatureCollection<LineString> lineChunk(Geometry geometry, double segmentLength) {
        return lineChunk(geometry, segmentLength, null, false);
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度
     * @param units         距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 线段集合
     */
    public static FeatureCollection<LineString> lineChunk(Geometry geometry, double segmentLength, Units units) {
        return lineChunk(geometry, segmentLength, units, false);
    }

    /**
     * 分割多线段<br>
     * <p>
     * 将一个Line分割成指定长度的块。如果Line比分隔段长度短，则返回原始Line
     *
     * @param geometry      线段，支持 Line、MultiLine
     * @param segmentLength 每个段的长度
     * @param units         距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @param reverse       反转坐标以在末尾开始第一个分块段
     * @return 线段集合
     */
    public static FeatureCollection<LineString> lineChunk(Geometry geometry, double segmentLength, Units units, boolean reverse) {
        return LineChunkHelper.lineChunk(geometry, segmentLength, units, reverse);
    }

    /**
     * 根据点截取多线段<br>
     * <p>
     * 获取一条线、起点和终点，并返回这些点之间的线段。起止点不需要正好落在直线上
     *
     * @param startPt 起点
     * @param stopPt  终点
     * @param feature 被截取的线段
     * @return 切片线
     */
    public static Feature<LineString> lineSlice(Point startPt, Point stopPt, Feature<LineString> feature) {
        if (feature.geometry().geometryType() != GeometryType.LINE_STRING) {
            throw new JTurfException("feature must LineString");
        }

        return LineSliceHelper.lineSlice(startPt, stopPt, feature.geometry(), feature.properties());
    }

    /**
     * 根据点截取多线段<br>
     * <p>
     * 获取一条线、起点和终点，并返回这些点之间的线段。起止点不需要正好落在直线上
     *
     * @param startPt 起点
     * @param stopPt  终点
     * @param line    被截取的线段
     * @return 切片线
     */
    public static Feature<LineString> lineSlice(Point startPt, Point stopPt, LineString line) {
        return LineSliceHelper.lineSlice(startPt, stopPt, line, null);
    }

    /**
     * 计算扇形多边形<br>
     * <p>
     * 创建给定半径和中心点的圆的一个扇形，位于(顺时针)bearing1和bearing2之间;0方位为中心点以北，顺时针正。默认步长64，默认距离为 KILOMETERS。
     *
     * @param center   中心点
     * @param radius   圆的半径，默认距离为 KILOMETERS
     * @param bearing1 扇区第一半径的角度
     * @param bearing2 扇区第二半径的角度
     * @return 扇区多边形
     */
    public static Polygon sector(Point center, int radius, double bearing1, double bearing2) {
        return sector(center, radius, bearing1, bearing2, null, null);
    }

    /**
     * 计算扇形多边形<br>
     * <p>
     * 创建给定半径和中心点的圆的一个扇形，位于(顺时针)bearing1和bearing2之间;0方位为中心点以北，顺时针正。默认距离为 KILOMETERS。
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 扇区第一半径的角度
     * @param bearing2 扇区第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @return 扇区多边形
     */
    public static Polygon sector(Point center, int radius, double bearing1, double bearing2, Integer steps) {
        return sector(center, radius, bearing1, bearing2, steps, null);
    }

    /**
     * 计算扇形多边形<br>
     * <p>
     * 创建给定半径和中心点的圆的一个扇形，位于(顺时针)bearing1和bearing2之间;0方位为中心点以北，顺时针正。
     *
     * @param center   中心点
     * @param radius   圆的半径
     * @param bearing1 扇区第一半径的角度
     * @param bearing2 扇区第二半径的角度
     * @param steps    步长，不传入则默认为64
     * @param units    距离单位，支持 KILOMETERS、MILES、DEGREES、RADIANS，不传入默认为 KILOMETERS
     * @return 扇区多边形
     */
    public static Polygon sector(Point center, int radius, double bearing1, double bearing2, Integer steps, Units units) {
        return SectorHelper.sector(center, radius, bearing1, bearing2, steps, units);
    }

}
