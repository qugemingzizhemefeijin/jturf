package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.shape.impl.LineString;
import com.cgzz.mapbox.jturf.shape.impl.MultiLineString;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JTurfBooleansTest {

    @Test
    public void booleanClockwiseTest() {
        LineString clockwiseRing = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[0,0],[1,1],[1,0],[0,0]]}");
        LineString counterClockwiseRing = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[0,0],[1,0],[1,1],[0,0]]}");

        assertTrue(JTurfBooleans.booleanClockwise(clockwiseRing));
        assertFalse(JTurfBooleans.booleanClockwise(counterClockwiseRing));
    }

    @Test
    public void booleanEqualTest() {
        Point pt1 = Point.fromLngLat(0, 0);
        Point pt2 = Point.fromLngLat(0, 0);
        Point pt3 = Point.fromLngLat(1, 1);

        assertTrue(JTurfBooleans.booleanEqual(pt1, pt2));
        assertFalse(JTurfBooleans.booleanEqual(pt2, pt3));
    }

    @Test
    public void booleanParallelTest() {
        LineString line1 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[0,0],[0,1]]}");
        LineString line2 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,0],[1,1]]}");

        assertTrue(JTurfBooleans.booleanParallel(line1, line2));
    }

    @Test
    public void booleanPointInPolygonTest() {
        Point pt = Point.fromLngLat(-81, 41);
        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-81,41],[-81,47],[-72,47],[-72,41],[-81,41]]]}");

        assertTrue(JTurfBooleans.booleanPointInPolygon(pt, poly));
    }

    @Test
    public void booleanPointInPolygon2Test() {
        Point pt = Point.fromLngLat(-81, 41);
        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-81,41],[-81,47],[-72,47],[-72,41],[-81,41]]]}");

        assertFalse(JTurfBooleans.booleanPointInPolygon(pt, poly, true));
    }

    @Test
    public void booleanPointOnLineTest() {
        Point pt = Point.fromLngLat(-1, -1);
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-1,-1],[1,1],[1.5,2.2]]}");

        assertTrue(JTurfBooleans.booleanPointOnLine(pt, line));
    }

    @Test
    public void booleanPointOnLine2Test() {
        Point pt = Point.fromLngLat(-1, -1);
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-1,-1],[1,1],[1.5,2.2]]}");

        assertFalse(JTurfBooleans.booleanPointOnLine(pt, line, true));
    }

    @Test
    public void booleanOverlapTest() {
        Polygon poly1 = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[0,5],[5,5],[5,0],[0,0]]]}");
        Polygon poly2 = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[1,1],[1,6],[6,6],[6,1],[1,1]]]}");
        Polygon poly3 = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[10,10],[10,15],[15,15],[15,10],[10,10]]]}");

        assertTrue(JTurfBooleans.booleanOverlap(poly1, poly2));
        assertFalse(JTurfBooleans.booleanOverlap(poly2, poly3));
    }

    @Test
    public void booleanWithinTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        Point point = Point.fromLngLat(1, 2);

        assertTrue(JTurfBooleans.booleanWithin(point, line));
    }

    @Test
    public void booleanCrossesTest() {
        LineString line1 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[-2,2],[4,2]]}");
        LineString line2 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");

        assertTrue(JTurfBooleans.booleanCrosses(line1, line2));
    }

    @Test
    public void booleanDisjointTest() {
        Point point = Point.fromLngLat(2, 2);
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");

        assertTrue(JTurfBooleans.booleanDisjoint(line, point));
    }

    @Test
    public void booleanContainsTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        Point point = Point.fromLngLat(1,2);
        assertTrue(JTurfBooleans.booleanContains(line, point));
    }

    @Test
    public void booleanValidTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");

        List<List<Point>> points = new ArrayList<>();
        points.add(null);

        MultiLineString multiLine = MultiLineString.fromLngLats(points);

        assertTrue(JTurfBooleans.booleanValid(line));
        assertFalse(JTurfBooleans.booleanValid(multiLine));
    }

    @Test
    public void booleanIntersectsTest() {
        Point point = Point.fromLngLat(1, 3);

        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        assertTrue(JTurfBooleans.booleanIntersects(line, point));

        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[0,5],[5,5],[5,0],[0,0]]]}");
        assertTrue(JTurfBooleans.booleanIntersects(poly, point));
    }

    @Test
    public void booleanConcaveTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[0,1],[1,1],[1,0],[0,0]]]}");

        assertFalse(JTurfBooleans.booleanConcave(polygon));
    }

}
