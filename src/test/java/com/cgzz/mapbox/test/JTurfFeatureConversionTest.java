package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfFeatureConversion;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class JTurfFeatureConversionTest {

    @Test
    public void polygonToLineTest() {
        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[125,-30],[145,-30],[145,-20],[125,-20],[125,-30]]]}");

        Feature<Geometry> line = JTurfFeatureConversion.polygonToLine(poly);

        Feature<LineString> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[125,-30],[145,-30],[145,-20],[125,-20],[125,-30]]}}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(line, same));
    }

    @Test
    public void polygonToLine2Test() {
        MultiPolygon multiPolygon = MultiPolygon.fromJson("{\"type\":\"MultiPolygon\",\"coordinates\":[[[[125,-30],[145,-30],[145,-20],[125,-20],[125,-30]]]]}");

        FeatureCollection<Geometry> lineCollection = JTurfFeatureConversion.polygonToLine(multiPolygon);

        FeatureCollection<LineString> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[125,-30],[145,-30],[145,-20],[125,-20],[125,-30]]}}]}", LineString.class);

        assertTrue(JTurfBooleans.booleanEqual(lineCollection, same));
    }

    @Test
    public void flattenTest() {
        MultiPolygon multiPolygon = MultiPolygon.fromJson("{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102,2],[103,2],[103,3],[102,3],[102,2]]],[[[100,0],[101,0],[101,1],[100,1],[100,0]],[[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]]]]}");

        FeatureCollection<Geometry> flatten = JTurfFeatureConversion.flatten(multiPolygon);

        FeatureCollection<Polygon> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[102,2],[103,2],[103,3],[102,3],[102,2]]]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100,0],[101,0],[101,1],[100,1],[100,0]],[[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]]]}}]}", Polygon.class);

        assertTrue(JTurfBooleans.booleanEqual(flatten, same));
    }

    @Test
    public void explodeTest() {
        Polygon polygon = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[-81,41],[-88,36],[-84,31],[-80,33],[-77,39],[-81,41]]]}");

        List<Point> explode = JTurfFeatureConversion.explode(polygon);

        FeatureCollection<Point> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-81,41]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-88,36]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-84,31]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-80,33]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-77,39]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-81,41]}}]}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(FeatureCollection.fromFeatures(explode.stream().map(Feature::fromGeometry).collect(Collectors.toList())), same));
    }

    @Test
    public void combineTest() {
        FeatureCollection<Geometry> fc = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[19.026432,47.49134]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[19.074497,47.509548]}}]}");

        FeatureCollection<Geometry> combined = JTurfFeatureConversion.combine(fc);

        FeatureCollection<Geometry> same = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPoint\",\"coordinates\":[[19.026432,47.49134],[19.074497,47.509548]]}}]}");

        assertTrue(JTurfBooleans.booleanEqual(combined, same));
    }

}
