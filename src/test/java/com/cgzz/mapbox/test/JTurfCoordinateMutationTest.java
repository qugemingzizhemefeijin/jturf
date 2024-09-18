package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfCoordinateMutation;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class JTurfCoordinateMutationTest {

    @Test
    public void flipTest() {
        Point serbia = Point.fromLngLat(20.566406, 43.421008);

        Point saudiArabia = JTurfCoordinateMutation.flip(serbia);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[43.421008,20.566406]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(saudiArabia, same.geometry()));
    }

    @Test
    public void flip2Test() {
        Point serbia = Point.fromLngLat(20.566406, 43.421008);
        JTurfCoordinateMutation.flip(serbia, true);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[43.421008,20.566406]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(serbia, same.geometry()));
    }

    @Test
    public void truncateTest() {
        Point point = Point.fromLngLat(70.46923055566859, 58.11088890802906, 1508);
        Point truncated = JTurfCoordinateMutation.truncate(point, 3, 2);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[70.469,58.111]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(truncated, same.geometry()));
    }

    @Test
    public void cleanCoordsTest() {
        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[0,0],[0,2],[0,5],[0,8],[0,8],[0,10]]}");
        MultiPoint multiPoint = MultiPoint.fromJson("{\"type\":\"MultiPoint\",\"coordinates\":[[0,0],[0,0],[2,2]]}");

        LineString newLine = JTurfCoordinateMutation.cleanCoords(line);
        MultiPoint newMultiPoint = JTurfCoordinateMutation.cleanCoords(multiPoint);

        Feature<LineString> newLineSame = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[0,0],[0,10]]}}", LineString.class);
        Feature<MultiPoint> newMultiPointSame = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[0,0],[2,2]]}}", MultiPoint.class);

        assertTrue(JTurfBooleans.booleanEqual(newLine, newLineSame.geometry()));
        assertTrue(JTurfBooleans.booleanEqual(newMultiPoint, newMultiPointSame.geometry()));
    }

    @Test
    public void rewindTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[121,-29],[138,-29],[138,-18],[121,-18],[121,-29]]]}");

        Polygon rewind = JTurfCoordinateMutation.rewind(polygon);

        Feature<Polygon> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[121,-29],[138,-29],[138,-18],[121,-18],[121,-29]]]}}", Polygon.class);

        assertTrue(JTurfBooleans.booleanEqual(rewind, same.geometry()));
    }

}
