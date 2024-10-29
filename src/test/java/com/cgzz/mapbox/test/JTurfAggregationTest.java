package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfAggregation;
import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JTurfAggregationTest {

    @Test
    public void clustersKmeansTest() {
        Feature<Point> p1 = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"marker-symbol\":\"circle\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,0]}}", Point.class);
        Feature<Point> p2 = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"marker-symbol\":\"star\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[2,4]}}", Point.class);
        Feature<Point> p3 = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"marker-symbol\":\"star\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[3,6]}}", Point.class);
        Feature<Point> p4 = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"marker-symbol\":\"square\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[5,1]}}", Point.class);
        Feature<Point> p5 = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"marker-symbol\":\"circle\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[4,2]}}", Point.class);

        FeatureCollection<Point> geojson = FeatureCollection.fromFeatures(p1, p2, p3, p4, p5);

        FeatureCollection<Point> clustered = JTurfAggregation.clustersKmeans(geojson);

        assertEquals(clustered.get(0).getPropertyAsString("cluster"), "0");
        assertEquals(clustered.get(1).getPropertyAsString("cluster"), "1");
        assertEquals(clustered.get(2).getPropertyAsString("cluster"), "1");
        assertEquals(clustered.get(3).getPropertyAsString("cluster"), "1");
        assertEquals(clustered.get(3).getPropertyAsString("cluster"), "1");

    }

}
