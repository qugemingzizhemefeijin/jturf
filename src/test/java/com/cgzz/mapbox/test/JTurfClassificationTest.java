package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfBooleans;
import com.cgzz.mapbox.jturf.JTurfClassification;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JTurfClassificationTest {

    @Test
    public void nearestPointTest() {
        Point targetPoint = Point.fromLngLat(28.965797, 41.010086);
        FeatureCollection<Point> points = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[28.973865,41.011122]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[28.948459,41.024204]}},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[28.938674,41.013324]}}]}", Point.class);

        Feature<Point> nearest = JTurfClassification.nearestPoint(targetPoint, points);

        Feature<Point> same = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"featureIndex\":0,\"distanceToPoint\":0.6866892586431127},\"geometry\":{\"type\":\"Point\",\"coordinates\":[28.973865,41.011122]}}", Point.class);
        assertTrue(JTurfBooleans.booleanEqual(nearest, same));
    }

}
