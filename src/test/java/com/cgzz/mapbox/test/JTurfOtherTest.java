package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfOther;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class JTurfOtherTest {

    @Test
    public void booleanIntersectsTest() {
        Point point = Point.fromLngLat(1, 3);

        LineString line = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        assertTrue(JTurfOther.booleanIntersects(line, point));

        Polygon poly = Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[0,5],[5,5],[5,0],[0,0]]]}");
        assertTrue(JTurfOther.booleanIntersects(poly, point));
    }

    @Test
    public void booleanValidTest() {
        LineString line1 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,2],[1,3],[1,4]]}");
        LineString line2 = LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":[[1,1],[1,1]]}");
        line2.coordinates(null);

        assertTrue(JTurfOther.booleanValid(line1));
        assertFalse(JTurfOther.booleanValid(line2));
    }

    @Test
    public void centerMeanTest() {
        FeatureCollection<Geometry> features = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"value\":10},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.522259,35.4691]}},{\"type\":\"Feature\",\"properties\":{\"value\":3},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.502754,35.463455]}},{\"type\":\"Feature\",\"properties\":{\"value\":5},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.508269,35.463245]}}]}");

        JsonObject options = new JsonObject();
        options.addProperty("weight", "value");
        options.addProperty("id", "123");

        JsonObject properties = new JsonObject();
        properties.addProperty("aaa", "123");
        options.add("properties", properties);

        Feature<Point> mean = JTurfOther.centerMean(features, options);
        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"id\":\"123\",\"properties\":{\"aaa\":\"123\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.51512205555557,35.46653277777778]}}", Point.class);

        assertTrue(JTurfBooleans.booleanEqual(mean, same));
        assertEquals(mean.id(), same.id());
        assertEquals(mean.properties(), properties);
    }

}
