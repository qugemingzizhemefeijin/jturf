package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.MultiPoint;
import com.cgzz.mapbox.jturf.shape.Point;
import com.cgzz.mapbox.jturf.util.JTurfHelper;

import java.util.ArrayList;
import java.util.List;

public final class JTurfJoins {

    private JTurfJoins() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 返回在多边形内的点<br>
     * <p>
     * 找到落在(多个)多边形内的点。
     *
     * @param points  输入的点集合
     * @param polygon 检查点是否在多边形内，只支持 POLYGON、MULTI_POLYGON、GEOMETRY_COLLECTION（仅包含POLYGON、MULTI_POLYGON）
     * @return 位于至少一个多边形内的点或多点
     */
    public static List<Point> pointsWithinPolygon(List<Point> points, Geometry polygon) {
        if (points == null || points.isEmpty()) {
            throw new JTurfException("points is required");
        }
        if (polygon == null) {
            throw new JTurfException("polygon is required");
        }

        List<Point> results = new ArrayList<>();
        for (Point point : points) {
            JTurfMeta.geomEach(point, (geometry, parent, geomIndex) -> {
                if (JTurfBooleans.booleanPointInPolygon(point, polygon)) {
                    results.add(point);
                }
                return true;
            });
        }

        return results;
    }

    /**
     * 返回在多边形内的点<br>
     * <p>
     * 找到落在(多个)多边形内的点。
     *
     * @param points  多点组合
     * @param polygon 检查点是否在多边形内，只支持 POLYGON、MULTI_POLYGON、GEOMETRY_COLLECTION（仅包含POLYGON、MULTI_POLYGON）
     * @return 位于至少一个多边形内的点或多点
     */
    public static List<Point> pointsWithinPolygon(MultiPoint points, Geometry polygon) {
        if (points == null) {
            throw new JTurfException("points is required");
        }
        return pointsWithinPolygon(points.coordinates(), polygon);
    }

    /**
     * 空间连接<br>
     * <p>
     * 取一组点和一组多边形，并执行空间连接。<br>
     * 如果点在多边形为，并且点的 [outField] 不存在，则将多边形的 [field] 属性赋值给点的 [outField] 属性
     *
     * @param points   点集合
     * @param polygons 多边形集合
     * @param field    要添加到连接点元素的 Polygon 中的字段属性
     * @param outField 点中的 outField 属性，用于存储来自 Polygon 的连接属性
     * @return 返回点集合，注意：此集合是克隆自points
     */
    public static List<Point> tag(List<Point> points, List<Geometry> polygons, String field, String outField) {
        // deep clone
        points = JTurfHelper.deepCloneList(points);
        polygons = JTurfHelper.deepCloneList(polygons);

        for (Point pt : points) {
            for (Geometry poly : polygons) {
                if (!pt.hasProperty(outField)) {
                    if (JTurfBooleans.booleanPointInPolygon(pt, poly)) {
                        pt.addProperty(outField, poly.getProperty(field));
                    }
                }
            }
        }

        return points;
    }

}
