package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.MultiPoint;
import com.cgzz.mapbox.jturf.shape.Point;

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

}
