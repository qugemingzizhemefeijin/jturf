package com.cgzz.mapbox.jturf.util.transformation;

import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.TesselateResult;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TesselateHelper {

    private TesselateHelper() {
        throw new AssertionError("No Instances.");
    }

    public static FeatureCollection<Polygon> tesselate(Geometry geometry) {
        geometry = JTurfMeta.getGeom(geometry);
        GeometryType type = geometry.geometryType();
        if (type != GeometryType.POLYGON && type != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("input must be a Polygon or MultiPolygon");
        }

        List<Feature<Polygon>> fc;
        if (type == GeometryType.POLYGON) {
            fc = processPolygon(Polygon.polygon(geometry).coordinates());
        } else {
            fc = new ArrayList<>();
            for (List<List<Point>> coordinates : MultiPolygon.multiPolygon(geometry).coordinates()) {
                fc.addAll(processPolygon(coordinates));
            }
        }

        return FeatureCollection.fromFeatures(fc);
    }

    private static List<Feature<Polygon>> processPolygon(List<List<Point>> coordinates) {
        TesselateResult data = flattenCoords(coordinates);
        List<Double> vertices = data.getVertices();
        int dim = 2;

        List<Integer> result = Earcut.earcut(data.getVertices(), data.getHoles(), dim);

        List<Point> points = new ArrayList<>();
        for (Integer index : result) {
            points.add(Point.fromLngLat(vertices.get(index * dim), vertices.get(index * dim + 1)));
        }

        int size = points.size();
        List<Feature<Polygon>> polygonList = new ArrayList<>(size / 3);
        for (int i = 0; i < size; i += 3) {
            List<Point> coords = new ArrayList<>(4);
            coords.add(points.get(i));
            coords.add(points.get(i + 1));
            coords.add(points.get(i + 2));
            coords.add(points.get(i));

            polygonList.add(Feature.fromGeometry(Polygon.fromLngLats(Collections.singletonList(coords))));
        }

        return polygonList;
    }

    private static TesselateResult flattenCoords(List<List<Point>> coordinates) {
        boolean hasAltitude = coordinates.get(0).get(0).hasAltitude();
        int dim = hasAltitude ? 3 : 2;
        int holeIndex = 0;

        TesselateResult result = new TesselateResult(dim);

        for (int i = 0, is = coordinates.size(); i < is; i++) {
            List<Point> coords = coordinates.get(i);

            for (Point p : coords) {
                result.pushVertices(p.getX());
                result.pushVertices(p.getY());
                if (hasAltitude) {
                    result.pushVertices(p.getZ());
                }
            }

            if (i > 0) {
                holeIndex += coordinates.get(i - 1).size();
                result.pushHoles(holeIndex);
            }
        }

        return result;
    }

}
