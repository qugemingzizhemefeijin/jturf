package com.cgzz.mapbox.jturf.util.classification;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfTransformation;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.Point;

import java.util.List;

public final class NearestPointHelper {

    private NearestPointHelper() {
        throw new AssertionError("NO Instance.");
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param points      点集合
     * @return Feature 返回距其最近的点
     */
    public static Feature<Point> nearestPoint(Point targetPoint, List<Point> points) {
        if (targetPoint == null) {
            throw new IllegalArgumentException("targetPoint is required");
        }
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("points is required");
        }

        int bestFeatureIndex = 0;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point point : points) {
            double distanceToPoint = JTurfMeasurement.distance(targetPoint, point);
            if (distanceToPoint < minDist) {
                minDist = distanceToPoint;
            }
        }
        Feature<Point> nearest = Feature.fromGeometry(JTurfTransformation.clone(points.get(bestFeatureIndex)));
        nearest.addProperty("featureIndex", 0);
        nearest.addProperty("distanceToPoint", minDist);

        return nearest;
    }

}
