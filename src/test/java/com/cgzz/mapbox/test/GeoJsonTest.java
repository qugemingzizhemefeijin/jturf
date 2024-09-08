package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;
import com.cgzz.mapbox.jturf.util.metahelper.SegmentReduceHelper;

import java.util.List;

public class GeoJsonTest {

    public static void main(String[] args) {
        Polygon poly1 = Polygon.fromLngLats(new double[]{-81, 41, -81, 47, -72, 47, -72, 41, -81, 41});

        System.out.println(poly1.toJson());

        Point pt1 = Point.fromLngLat(-77, 44, 100);
        Point pt2 = Point.fromLngLat(-77, 38);

        System.out.println(pt1.toJson());
        System.out.println(pt2.toJson());

        System.out.println(Point.fromJson("{\"type\":\"Point\",\"coordinates\":[-77.0,44.0,100.0]}").toJson());

        Double d = SegmentReduceHelper.segmentReduce(poly1, (previousValue, currentSegment, featureIndex, multiFeatureIndex, geometryIndex, segmentIndex) -> {
            List<Point> coords = currentSegment.coordinates();
            return previousValue + JTurfMeasurement.distance(coords.get(0), coords.get(1));
        }, 0D);

        System.out.println(d);
    }

}
