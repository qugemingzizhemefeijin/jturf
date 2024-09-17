package com.cgzz.mapbox.jturf.util.booleans;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public final class GeoJsonEquality {

    /**
     * 默认比较点的小数精度
     */
    public static final int PRECISION = 17;

    /**
     * 如果是比较多边形，否是必须方向一致
     */
    public static final boolean DIRECTION = false;

    private GeoJsonEquality() {
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

    @SuppressWarnings("unchecked")
    public static boolean compare(Geometry g1, Geometry g2, int precision, boolean direction) {
        g1 = JTurfMeta.getGeom(g1);
        g2 = JTurfMeta.getGeom(g2);
        GeometryType type = g1.geometryType();
        // 类型一致并且维护的点集合长度要一致
        if (type != g2.geometryType() || !sameLength(g1, g2)) {
            return false;
        }

        switch (type) {
            case POINT:
                return comparePoint((Point) g1, (Point) g2, precision);
            case LINE_STRING:
                return compareLineString((LineString) g1, (LineString) g2, precision);
            case POLYGON:
                return comparePolygon((Polygon) g1, (Polygon) g2, precision, direction);
            case FEATURE_COLLECTION:
                return compareFeatureCollection((FeatureCollection<Geometry>) g1, (FeatureCollection<Geometry>) g2, precision, direction);
            case MULTI_POINT:
                return comparePoints(((MultiPoint) g1).coordinates(), ((MultiPoint) g2).coordinates(), 0, false, precision, false);
            case MULTI_LINE_STRING:
                return compareMultiLine((MultiLineString) g1, (MultiLineString) g2, precision);
            case MULTI_POLYGON:
                return compareMultiPolygon((MultiPolygon) g1, (MultiPolygon) g2, precision, direction);
            case GEOMETRY_COLLECTION:
                return compareGeometryCollection((GeometryCollection) g1, (GeometryCollection) g2, precision, direction);
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
    @SuppressWarnings({"unchecked", "all"})
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

        boolean b = JTurfHelper.round(g1.getX(), precision) == JTurfHelper.round(g2.getX(), precision)
                && JTurfHelper.round(g1.getY(), precision) == JTurfHelper.round(g2.getY(), precision);

        if (b) {
            // 判断是否有z坐标
            double z1 = g1.altitude(), z2 = g2.altitude();
            // 都没有z坐标则直接返回true
            if (Double.isNaN(z1) && Double.isNaN(z2)) {
                return true;
            }
            if (z1 == z2) {
                return JTurfHelper.round(z1, precision) == JTurfHelper.round(z2, precision);
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 比较两条线是否一致
     *
     * @param line1     Line
     * @param line2     Line
     * @param precision 小数精度
     * @return 一致返回true
     */
    private static boolean compareLineString(LineString line1, LineString line2, int precision) {
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
        List<List<Point>> coordinates1 = polygon1.coordinates(), coordinates2 = polygon2.coordinates();

        return comparePolygonCoordinates(coordinates1, coordinates2, precision, direction);
    }

    /**
     * 比较多边形的点
     *
     * @param coordinates1 点集合
     * @param coordinates2 点集合
     * @param precision    小数精度
     * @param direction    是否需要方向一致
     * @return 一致返回true
     */
    private static boolean comparePolygonCoordinates(List<List<Point>> coordinates1, List<List<Point>> coordinates2, int precision, boolean direction) {
        // 先比较外圈
        if (!comparePoints(coordinates1.get(0), coordinates2.get(0), 1, true, precision, direction)) {
            return false;
        }

        // 如果有内圈的话，则再比较内圈
        for (int i1 = 1, size1 = coordinates1.size(); i1 < size1; i1++) {
            List<Point> c1 = coordinates1.get(i1);

            boolean b = false;
            for (int i2 = 1, size2 = coordinates2.size(); i2 < size2; i2++) {
                List<Point> c2 = coordinates2.get(i2);

                // 内圈需要两个循环比较
                if (comparePoints(c1, c2, 1, true, precision, direction)) {
                    b = true;
                    break;
                }
            }

            if (!b) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较两个Feature是否一致
     *
     * @param feature1  Feature
     * @param feature2  Feature
     * @param precision 小数精度
     * @param direction 是否需要方向一致
     * @return 一致返回true
     */
    private static boolean compareFeature(Feature<Geometry> feature1, Feature<Geometry> feature2, int precision, boolean direction) {
        if (!Objects.equals(feature1.id(), feature2.id())
                || !compareFeatureProperties(feature1.properties(), feature2.properties())) {
            return false;
        }

        return compare(feature1.geometry(), feature2.geometry(), precision, direction);
    }

    /**
     * 比较两个FeatureCollection是否一致
     *
     * @param featureCollection1 FeatureCollection
     * @param featureCollection2 FeatureCollection
     * @param precision          小数精度
     * @param direction          是否需要方向一致
     * @return 一致返回true
     */
    private static boolean compareFeatureCollection(FeatureCollection<Geometry> featureCollection1, FeatureCollection<Geometry> featureCollection2, int precision, boolean direction) {
        List<Feature<Geometry>> features1 = featureCollection1.geometries(), features2 = featureCollection2.geometries();

        for (Feature<Geometry> feature1 : features1) {
            boolean b = false;
            for (Feature<Geometry> feature : features2) {
                if (compareFeature(feature1, feature, precision, direction)) {
                    b = true;
                    break;
                }
            }

            if (!b) {
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
     * 比较两个组合线组是否一致
     *
     * @param multiLineString1 MultiLine
     * @param multiLineString2 MultiLine
     * @param precision        小数精度
     * @return 一致返回true
     */
    private static boolean compareMultiLine(MultiLineString multiLineString1, MultiLineString multiLineString2, int precision) {
        List<List<Point>> g1s = multiLineString1.coordinates(), g2s = multiLineString2.coordinates();

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
        List<List<List<Point>>> g1s = multiPolygon1.coordinates(), g2s = multiPolygon2.coordinates();

        for (int i = 0, size = g1s.size(); i < size; i++) {
            if (!comparePolygonCoordinates(g1s.get(i), g2s.get(i), precision, direction)) {
                return false;
            }
        }
        return true;
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

    /**
     * 比较Feature的属性
     *
     * @param properties1 JsonObject
     * @param properties2 JsonObject
     * @return 完全一致则返回true
     */
    private static boolean compareFeatureProperties(JsonObject properties1, JsonObject properties2) {
        if (properties1 == properties2) {
            return true;
        } else if (properties1 == null || properties2 == null) {
            return false;
        }

        if (properties1.size() != properties2.size()) {
            return false;
        }

        for (Map.Entry<String, JsonElement> me : properties1.entrySet()) {
            String key = me.getKey();
            JsonElement element1 = me.getValue();
            JsonElement element2 = properties2.get(key);

            if (element1 == element2) {
                continue;
            }
            if (element1 == null || element2 == null) {
                return false;
            }

            if (!element1.equals(element2)) {
                return false;
            }
        }

        return true;
    }

}
