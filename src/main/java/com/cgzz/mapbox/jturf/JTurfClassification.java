package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.util.classification.NearestPointHelper;

import java.util.List;

public final class JTurfClassification {

    private JTurfClassification() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param multiPoint  点集合
     * @return Point 返回距其最近的点
     */
    public static Feature<Point> nearestPoint(Point targetPoint, MultiPoint multiPoint) {
        return nearestPoint(targetPoint, multiPoint.coordinates());
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param points      点集合
     * @return Point 返回距其最近的点
     */
    public static Feature<Point> nearestPoint(Point targetPoint, FeatureCollection<Point> points) {
        return nearestPoint(targetPoint, points.toItemList());
    }

    /**
     * 根据目标点计算出在点集合中距离此目标点最近的点
     *
     * @param targetPoint 目标点
     * @param points      点集合
     * @return Feature 返回距其最近的点
     */
    public static Feature<Point> nearestPoint(Point targetPoint, List<Point> points) {
        return NearestPointHelper.nearestPoint(targetPoint, points);
    }

}
