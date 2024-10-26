package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.models.IntHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JTurfMetaTest {

    @Test
    public void coordEachTest() {
        Feature<Point> p1 = Feature.fromGeometry(Point.fromLngLat(26, 37));
        p1.addProperty("foo", "bar");

        Feature<Point> p2 = Feature.fromGeometry(Point.fromLngLat(36, 53));
        p2.addProperty("hello", "world");

        FeatureCollection<Point> features = FeatureCollection.fromFeatures(p1, p2);

        Set<Point> points = new HashSet<>();
        JTurfMeta.coordEach(features, (point, coordIndex, featureIndex, multiFeatureIndex, geometryIndex) -> {
            points.add(point);
            return true;
        });

        assertEquals(new HashSet<>(features.toItemList()), points);
    }

    @Test
    public void coordListEachTest() {
        MultiPolygon multiPolygon = MultiPolygon.fromJson("{\"type\":\"MultiPolygon\",\"coordinates\":[[[[125,-30],[145,-30],[145,-20],[125,-20],[125,-30]]]]}");

        Set<Point> points = new HashSet<>();
        JTurfMeta.coordListEach(multiPolygon, (geometry, pointList, multiIndex, geomIndex) -> {
            points.addAll(pointList);

            return true;
        });

        Set<Point> originPoint = new HashSet<>();
        for (List<List<Point>> p1 : multiPolygon.coordinates()) {
            for (List<Point> p2 : p1) {
                originPoint.addAll(p2);
            }
        }

        assertEquals(originPoint, points);
    }

    @Test
    public void flattenEachTest() {
        Feature<Geometry> p = Feature.fromGeometry(Point.fromLngLat(26, 37));
        Feature<Geometry> mp = Feature.fromGeometry(MultiPoint.fromLngLats(new double[]{40, 30, 36, 53}));

        FeatureCollection<Geometry> features = FeatureCollection.fromFeatures(p, mp);

        Set<Geometry> points = new HashSet<>();
        JTurfMeta.flattenEach(features, (feature, featureIndex, multiFeatureIndex) -> {
            points.add(feature.geometry());

            return true;
        });

        Set<Geometry> originPoint = new HashSet<>();
        originPoint.add(p.geometry());
        originPoint.addAll(MultiPoint.multiPoint(mp.geometry()).coordinates());

        assertEquals(originPoint, points);
    }

    @Test
    public void segmentEachTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-50,5],[-40,-10],[-50,-10],[-40,5],[-50,5]]]}");

        IntHolder total = new IntHolder();
        JTurfMeta.segmentEach(polygon, (currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex) -> {
            total.value++;

            return true;
        });

        assertEquals(total.value, 4);
    }

    @Test
    public void geomEachTest() {
        Feature<Point> p1 = Feature.fromGeometry(Point.fromLngLat(26, 37), "abc");
        p1.addProperty("foo", "bar");

        Feature<Point> p2 = Feature.fromGeometry(Point.fromLngLat(36, 53), "def");
        p2.addProperty("hello", "world");

        FeatureCollection<Point> features = FeatureCollection.fromFeatures(p1, p2);

        Set<String> ids = new HashSet<>();
        JTurfMeta.geomEach(features, (currentGeometry, featureIndex, featureProperties, featureId) -> {
            ids.add(featureId);

            return true;
        });

        Set<String> originIds = new HashSet<>();
        originIds.add(p1.id());
        originIds.add(p2.id());

        assertEquals(ids, originIds);
    }

    @Test
    public void featureEachTest() {
        Feature<Point> p1 = Feature.fromGeometry(Point.fromLngLat(26, 37), "abc");
        p1.addProperty("foo", "bar");

        Feature<Point> p2 = Feature.fromGeometry(Point.fromLngLat(36, 53), "def");
        p2.addProperty("hello", "world");

        FeatureCollection<Point> features = FeatureCollection.fromFeatures(p1, p2);

        Set<String> ids = new HashSet<>();
        JTurfMeta.featureEach(features, (currentFeature, featureIndex) -> {
            ids.add(currentFeature.id());

           return true;
        });

        Set<String> originIds = new HashSet<>();
        originIds.add(p1.id());
        originIds.add(p2.id());

        assertEquals(ids, originIds);
    }

    @Test
    public void propEachTest() {
        FeatureCollection<Point> features = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"foo\":\"bar\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[26,37]}},{\"type\":\"Feature\",\"properties\":{\"hello\":\"world\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[36,53]}}]}", Point.class);

        StringBuilder buf = new StringBuilder();
        JTurfMeta.propEach(features, (currentProperties, featureIndex) -> {
            if (featureIndex == 0) {
                buf.append(currentProperties.get("foo").getAsString());
            } else {
                buf.append(currentProperties.get("hello").getAsString());
            }

            return true;
        });

        assertEquals(buf.toString(), "barworld");
    }

}
