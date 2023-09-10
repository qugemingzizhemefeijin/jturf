package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.TesselateResult;
import com.cgzz.mapbox.jturf.shape.*;

import java.util.ArrayList;
import java.util.List;

public final class TesselateHelper {

    private TesselateHelper() {
        throw new AssertionError("No Instances.");
    }

    public static List<Polygon> tesselate(Geometry geometry) {
        GeometryType type = geometry.type();
        if (type != GeometryType.POLYGON && type != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("input must be a Polygon or MultiPolygon");
        }

        List<Polygon> fc;
        if (type == GeometryType.POLYGON) {
            fc = processPolygon(Polygon.polygon(geometry).coordinates());
        } else {
            fc = new ArrayList<>();
            for (List<Point> coordinates : MultiPolygon.multiPolygon(geometry).coordinates()) {
                fc.addAll(processPolygon(coordinates));
            }
        }

        return fc;
    }

    private static List<Polygon> processPolygon(List<Point> coordinates) {
        TesselateResult data = flattenCoords(coordinates);
        List<Double> vertices = data.getVertices();
        int dim = 2;

        List<Integer> result = Earcut.earcut(data.getVertices(), data.getHoles(), dim);

        List<Point> points = new ArrayList<>();
        for (Integer index : result) {
            points.add(Point.fromLngLat(vertices.get(index * dim), vertices.get(index * dim + 1)));
        }

        int size = points.size();
        List<Polygon> polygonList = new ArrayList<>(size / 3);
        for (int i = 0; i < size; i += 3) {
            List<Point> coords = new ArrayList<>(4);
            coords.add(points.get(i));
            coords.add(points.get(i + 1));
            coords.add(points.get(i + 2));
            coords.add(points.get(i));

            polygonList.add(Polygon.fromLngLats(coords));
        }

        return polygonList;
    }

    private static TesselateResult flattenCoords(List<Point> coordinates) {
        TesselateResult result = new TesselateResult(2);

        for (Point p : coordinates) {
            result.pushVertices(p.getX());
            result.pushVertices(p.getY());
        }

        return result;
    }

}
