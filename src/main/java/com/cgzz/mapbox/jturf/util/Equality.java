package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Equality {

    /**
     * 默认比较点的小数精度
     */
    public static final int PRECISION = 17;

    /**
     * 如果是比较多边形，否是必须方向一致
     */
    public static final boolean DIRECTION = false;

    private Equality() {
        throw new AssertionError("No Instances.");
    }

    public static boolean compare(Geometry g1, Geometry g2) {
        return compare(g1, g2, PRECISION, DIRECTION);
    }

    public static boolean compare(Geometry g1, Geometry g2, int precision) {
        return compare(g1, g2, precision, DIRECTION);
    }

    public static boolean compare(Geometry g1, Geometry g2, boolean direction) {
        return compare(g1, g2, PRECISION, direction);
    }

    public static boolean compare(Geometry g1, Geometry g2, int precision, boolean direction) {
        GeometryType type = g1.type();
        // 类型一致并且维护的点集合长度要一致
        if (type != g2.type() || !sameLength(g1, g2)) {
            return false;
        }

        switch (type) {
            case POINT:
                return comparePoint((Point) g1, (Point) g2, precision);
            case LINE:
                return compareLine((Line) g1, (Line) g2, precision);
            case POLYGON:
                return comparePolygon((Polygon) g1, (Polygon) g2, precision, direction);
            case GEOMETRY_COLLECTION:
                return compareGeometryCollection((GeometryCollection) g1, (GeometryCollection) g2, precision, direction);
            case MULTI_POINT:
                return comparePoints(((MultiPoint) g1).coordinates(), ((MultiPoint) g2).coordinates(), 0, false, precision, false);
            case MULTI_LINE:
                return compareMultiLine((MultiLine) g1, (MultiLine) g2, precision);
            case MULTI_POLYGON:
                return compareMultiPolygon((MultiPolygon) g1, (MultiPolygon) g2, precision, direction);
            default:
                throw new JTurfException("Unknown Geometry Type");
        }
    }

    /**
     * 比较两个图形组件的点集合长度是否一致
     *
     * @param g1 图形1
     * @param g2 图形2
     * @return true代表一致或无法比较, false代表不一致
     */
    @SuppressWarnings({"all"})
    private static boolean sameLength(Geometry g1, Geometry g2) {
        if (g1 instanceof CoordinateContainer) {
            Object o1 = ((CoordinateContainer) g1).coordinates();
            Object o2 = ((CoordinateContainer) g1).coordinates();

            if (o1 instanceof List) {
                List l1 = (List) o1;
                List l2 = (List) o2;

                return l1.size() == l2.size();
            } else {
                // 跑这里就是Point类型的
                return true;
            }
        } else {
            List<Geometry> l1 = ((CollectionContainer) g1).geometries();
            List<Geometry> l2 = ((CollectionContainer) g2).geometries();

            return l1.size() == l2.size();
        }
    }

    /**
     * 比较两个点是否一致
     *
     * @param g1        Point
     * @param g2        Point
     * @param precision 小数精度
     * @return 一致返回true
     */
    private static boolean comparePoint(Point g1, Point g2, int precision) {
        if (g1 == null && g2 == null) {
            return true;
        } else if (g1 == null || g2 == null) {
            return false;
        }

        return JTurfHelper.round(g1.getX(), precision) == JTurfHelper.round(g2.getX(), precision)
                && JTurfHelper.round(g1.getY(), precision) == JTurfHelper.round(g2.getY(), precision);
    }

    /**
     * 比较两条线是否一致
     *
     * @param line1     Line
     * @param line2     Line
     * @param precision 小数精度
     * @return 一致返回true
     */
    private static boolean compareLine(Line line1, Line line2, int precision) {
        return comparePoints(line1.coordinates(), line2.coordinates(), 0, false, precision, false);
    }

    /**
     * 比较两个多边形是否一致
     *
     * @param polygon1  Polygon
     * @param polygon2  Polygon
     * @param precision 小数精度
     * @param direction 是否需要方向一致
     * @return 一致返回true
     */
    private static boolean comparePolygon(Polygon polygon1, Polygon polygon2, int precision, boolean direction) {
        return comparePoints(polygon1.coordinates(), polygon2.coordinates(), 1, true, precision, direction);
    }

    /**
     * 比较两个图形集合是否一致
     *
     * @param g1        GeometryCollection
     * @param g2        GeometryCollection
     * @param precision 小数精度
     * @param direction 如果图形集合中存在多边形，则多边形的方向是否要一致
     * @return 一致返回true
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean compareGeometryCollection(GeometryCollection g1, GeometryCollection g2, int precision, boolean direction) {
        List<Geometry> l1 = ((CollectionContainer) g1).geometries();
        List<Geometry> l2 = ((CollectionContainer) g2).geometries();

        for (int i = 0, size = l1.size(); i < size; i++) {
            if (!compare(l1.get(i), l2.get(i), precision, direction)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个组合线组是否一致
     *
     * @param multiLine1 MultiLine
     * @param multiLine2 MultiLine
     * @param precision  小数精度
     * @return 一致返回true
     */
    private static boolean compareMultiLine(MultiLine multiLine1, MultiLine multiLine2, int precision) {
        List<List<Point>> g1s = multiLine1.coordinates(), g2s = multiLine2.coordinates();

        for (int i = 0, size = g1s.size(); i < size; i++) {
            if (!comparePoints(g1s.get(i), g2s.get(i), 0, false, precision, false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个组合多边形是否一致
     *
     * @param multiPolygon1 MultiPolygon
     * @param multiPolygon2 MultiPolygon
     * @param precision     小数精度
     * @param direction     是否需要方向一致
     * @return 一致返回true
     */
    private static boolean compareMultiPolygon(MultiPolygon multiPolygon1, MultiPolygon multiPolygon2, int precision, boolean direction) {
        List<List<Point>> g1s = multiPolygon1.coordinates(), g2s = multiPolygon2.coordinates();

        for (int i = 0, size = g1s.size(); i < size; i++) {
            if (!comparePoints(g1s.get(i), g2s.get(i), 1, true, precision, direction)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个Path点集合是否一致
     *
     * @param points1    List<Point>
     * @param points2    List<Point>
     * @param startIndex 起始比较的点索引位置
     * @param isPolygon  是否是图形比较
     * @param precision  小数精度
     * @param direction  是否比较方向
     * @return 一致返回true
     */
    private static boolean comparePoints(List<Point> points1, List<Point> points2, int startIndex, boolean isPolygon, int precision, boolean direction) {
        if (isPolygon && !comparePoint(points1.get(0), points2.get(0), precision)) {
            // fix start index of both to same point
            points2 = fixStartIndex(points2, points1, precision);
            if (points2 == null) {
                return false;
            }
        }

        // for line startIndex =0 and for polygon startIndex =1 (line肯定是一个方向的，只有polygon的方向可能不一致)
        boolean sameDirection = comparePoint(points1.get(startIndex), points2.get(startIndex), precision);
        if (direction || sameDirection) {
            return comparePath(points1, points2, precision);
        } else {
            // 如果两个点集合的方向是反着的，则需要将其中的点位置方向倒一下
            if (comparePoint(points1.get(startIndex), points2.get(points2.size() - (1 + startIndex)), precision)) {
                // 坐标点反转（不能影响到原有的元素坐标）
                points1 = new ArrayList<>(points1);
                Collections.reverse(points1);

                return comparePath(points1, points2, precision);
            }
            return false;
        }
    }

    /**
     * 计算 sourcePath 中与 targetPath 第一个点的相同位置，并返回按照此点位在第一个位置的点集合
     *
     * @param sourcePath 源Path
     * @param targetPath 需比较第一个点的Path
     * @param precision  小数精度
     * @return 返回新的从 sourcePath 中组装的点集合，如果未比较到，则返回 null。
     */
    private static List<Point> fixStartIndex(List<Point> sourcePath, List<Point> targetPath, int precision) {
        // make sourcePath first point same as of targetPath
        List<Point> correctPath = null;
        int idx = -1;

        // 查找 sourcePath 中与 targetPath 的第一个位置相同的点
        for (int i = 0, size = sourcePath.size(); i < size; i++) {
            if (comparePoint(sourcePath.get(i), targetPath.get(0), precision)) {
                idx = i;
                break;
            }
        }

        if (idx >= 0) {
            correctPath = new ArrayList<>(sourcePath.size());
            sourcePath.addAll(sourcePath.subList(idx, sourcePath.size()));
            sourcePath.addAll(sourcePath.subList(1, idx + 1));
        }

        return correctPath;
    }

    /**
     * 判断点集合的线性方向是否完全一致
     *
     * @param path1     点集合1
     * @param path2     点集合2
     * @param precision 小数精度
     * @return 一致返回true
     */
    private static boolean comparePath(List<Point> path1, List<Point> path2, int precision) {
        int size = path1.size();
        for (int i = 0; i < size; i++) {
            if (!comparePoint(path1.get(i), path2.get(i), precision)) {
                return false;
            }
        }
        return true;
    }

}
