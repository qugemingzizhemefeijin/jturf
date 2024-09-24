package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfRandom;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class JTurfRandomTest {

    @Test
    public void randomPointTest() {
        Feature<Point> f = JTurfRandom.randomPoint();
        assertNotNull(f.geometry());
    }

    @Test
    public void randomPoint2Test() {
        BoundingBox bbox = BoundingBox.fromLngLats(-100, -45, 100, 45);
        Point f = JTurfRandom.randomPoint(bbox).geometry();

        assertTrue(f.getX() >= -100 && f.getX() <= 100);
        assertTrue(f.getY() > -45 && f.getY() < 45);
    }

    @Test
    public void randomPoint3Test() {
        FeatureCollection<Point> points = JTurfRandom.randomPoint(2);

        assertEquals(points.size(), 2);
    }

    @Test
    public void randomLineStringTest() {
        Feature<LineString> line = JTurfRandom.randomLineString();
        assertNotNull(line.geometry());
    }

    @Test
    public void randomPolygonTest() {
        Feature<Polygon> polygon = JTurfRandom.randomPolygon();

        assertNotNull(polygon.geometry());
    }

}
