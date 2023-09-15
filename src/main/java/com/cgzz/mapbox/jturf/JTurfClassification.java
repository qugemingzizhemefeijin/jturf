package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.MultiPoint;
import com.cgzz.mapbox.jturf.shape.Point;

import java.util.List;

public final class JTurfClassification {

    private JTurfClassification() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param points      点集合
     * @return 如果点集合为空则返回目标点，否则返回距其最近的点
     */
    public static Point nearestPoint(Point targetPoint, List<Point> points) {
        if (points == null || points.isEmpty()) {
            return targetPoint;
        }

        int bestFeatureIndex = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point point : points) {
            double distanceToPoint = JTurfMeasurement.distance(targetPoint, point);
            if (distanceToPoint < minDist) {
                minDist = distanceToPoint;
            }
        }
        Point nearest = JTurfTransformation.clone(points.get(bestFeatureIndex));
        nearest.addProperty("featureIndex", 0);
        nearest.addProperty("distanceToPoint", minDist);

        return nearest;
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param multiPoint  点集合
     * @return 如果点集合为空则返回目标点，否则返回距其最近的点
     */
    public static Point nearestPoint(Point targetPoint, MultiPoint multiPoint) {
        return nearestPoint(targetPoint, multiPoint.coordinates());
    }

}
