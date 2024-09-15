package com.cgzz.mapbox.jturf.util.featureconversion;

import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class PolygonToLineHelper {

    private PolygonToLineHelper() {
        throw new AssertionError("No Instance.");
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param poly       多边形
     * @param properties 附属的属性
     * @return Feature的内部类型为LineString
     */
    public static Feature polygonToLine(Polygon poly, JsonObject properties) {
        return Feature.fromGeometry(coordsToLine(poly.coordinates()), properties);
    }

    /**
     * 多边形转换成多线<br><br>
     * <p>
     * 将多边形转换为(多)线段或将多面体转换为包含(多)线段
     *
     * @param multiPoly  组合多边形
     * @param properties 附属的属性
     * @return Feature的内部类型为LineString
     */
    public static FeatureCollection polygonToLine(MultiPolygon multiPoly, JsonObject properties) {
        List<List<List<Point>>> coords = multiPoly.coordinates();
        List<Feature> lines = new ArrayList<>(coords.size());

        for (List<List<Point>> coord : coords) {
            lines.add(Feature.fromGeometry(coordsToLine(coord), properties));
        }

        return FeatureCollection.fromFeatures(lines);
    }

    private static Geometry coordsToLine(List<List<Point>> coords) {
        if (coords.size() > 1) {
            return MultiLineString.fromLngLats(coords);
        } else {
            return LineString.fromLngLats(coords.get(0));
        }
    }

}
