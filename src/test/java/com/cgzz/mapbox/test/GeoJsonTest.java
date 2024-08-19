package com.cgzz.mapbox.test;

import com.cgzz.mapbox.jturf.shape.impl.Point;
import com.cgzz.mapbox.jturf.shape.impl.Polygon;

public class GeoJsonTest {

    public static void main(String[] args) {
        Polygon poly1 = Polygon.fromLngLats(new double[]{-81, 41, -81, 47, -72, 47, -72, 41, -81, 41});

        System.out.println(poly1.toJson());

        Point pt1 = Point.fromLngLat(-77, 44, 100);
        Point pt2 = Point.fromLngLat(-77, 38);

        System.out.println(pt1.toJson());
        System.out.println(pt2.toJson());

        System.out.println(Point.fromJson("{\"type\":\"Point\",\"coordinates\":[-77.0,44.0,100.0]}").toJson());
    }

}
