package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.shape.impl.Feature;
import com.cgzz.mapbox.jturf.shape.impl.FeatureCollection;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

import java.util.Collections;

public class GeoJsonTest {

    public static void main(String[] args) {
        Polygon poly1 = Polygon.fromLngLats(new double[]{-81, 41, -81, 47, -72, 47, -72, 41, -81, 41});

        // System.out.println(poly1.toJson());

        String json = Feature.fromGeometry(poly1).toJson();
        // System.out.println(json);

        FeatureCollection<Polygon> features = FeatureCollection.fromFeatures(Collections.singletonList(Feature.fromJson(json, Polygon.class)));
        System.out.println(features);

        json = features.toJson();
        System.out.println(json);

        features = FeatureCollection.fromJson(json, Polygon.class);

        System.out.println(features.get(0).geometry());

        for (Feature<Polygon> pl : features) {

        }

//        System.out.println(Point.fromJson("{\"type\":\"Point\",\"coordinates\":[-77.0,44.0,100.0]}").toJson());
//
//        Double d = SegmentReduceHelper.segmentReduce(poly1, (previousValue, currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex) -> {
//            List<Point> coords = currentSegment.coordinates();
//            return previousValue + JTurfMeasurement.distance(coords.get(0), coords.get(1));
//        }, 0D);
//
//        System.out.println(d);
    }

}
