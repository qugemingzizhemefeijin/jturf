package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfJoins;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.MultiPoint;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JTurfJoinsTest {

    @Test
    public void pointsWithinPolygonTest() {
        Feature<Point> p1 = Feature.fromGeometry(Point.fromLngLat(-46.6318, -23.5523));
        Feature<Point> p2 = Feature.fromGeometry(Point.fromLngLat(-46.6246, -23.5325));
        Feature<Point> p3 = Feature.fromGeometry(Point.fromLngLat(-46.6062, -23.5513));
        Feature<Point> p4 = Feature.fromGeometry(Point.fromLngLat(-46.663, -23.554));
        Feature<Point> p5 = Feature.fromGeometry(Point.fromLngLat(-46.643, -23.557));

        FeatureCollection<Point> points = FeatureCollection.fromFeatures(p1, p2, p3, p4, p5);

        Feature<Point> searchWithin = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-46.653,-23.543],[-46.634,-23.5346],[-46.613,-23.543],[-46.614,-23.559],[-46.631,-23.567],[-46.653,-23.56],[-46.653,-23.543]]]}}", Point.class);

        FeatureCollection<Point> ptsWithin = JTurfJoins.pointsWithinPolygon(points, searchWithin);

        FeatureCollection<Point> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-46.6318,-23.5523]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-46.643,-23.557]}}]}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(ptsWithin, same));
    }

    @Test
    public void multiPointsWithinPolygonTest() {
        Point p1 = Point.fromLngLat(-46.6318, -23.5523);
        Point p2 = Point.fromLngLat(-46.643, -23.557);
        Point p3 = Point.fromLngLat(-46.6246, -23.5325);
        Point p4 = Point.fromLngLat(-46.6062, -23.5513);
        Point p5 = Point.fromLngLat(-46.663, -23.554);

        Feature<MultiPoint> m1 = Feature.fromGeometry(MultiPoint.fromLngLats(p1, p2));
        Feature<MultiPoint> m2 = Feature.fromGeometry(MultiPoint.fromLngLats(p3, p4));
        Feature<MultiPoint> m3 = Feature.fromGeometry(MultiPoint.fromLngLats(p5));

        FeatureCollection<MultiPoint> points = FeatureCollection.fromFeatures(m1, m2, m3);

        Feature<Point> searchWithin = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-46.653,-23.543],[-46.634,-23.5346],[-46.613,-23.543],[-46.614,-23.559],[-46.631,-23.567],[-46.653,-23.56],[-46.653,-23.543]]]}}", Point.class);

        FeatureCollection<MultiPoint> ptsWithin = JTurfJoins.multiPointsWithinPolygon(points, searchWithin);

        FeatureCollection<MultiPoint> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[-46.6318,-23.5523],[-46.643,-23.557]]}}]}", MultiPoint.class);

        assertTrue(JTurfBooleans.booleanEqual(ptsWithin, same));
    }

}
