package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.util.ArrayList;
import java.util.List;

public final class MultiPolygon implements CoordinateContainer<List<List<List<Point>>>> {

    private final List<List<List<Point>>> coordinates;

    MultiPolygon(List<List<List<Point>>> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiPolygon fromPolygons(List<Polygon> polygons) {
        List<List<List<Point>>> coordinates = new ArrayList<>(polygons.size());
        for (Polygon polygon : polygons) {
            coordinates.add(polygon.coordinates());
        }
        return new MultiPolygon(coordinates);
    }

    public static MultiPolygon fromPolygon(Polygon polygon) {
        List<List<List<Point>>> coordinates = new ArrayList<>(1);
        coordinates.add(polygon.coordinates());

        return new MultiPolygon(coordinates);
    }

    public static MultiPolygon fromLngLats(List<List<List<Point>>> points) {
        return new MultiPolygon(points);
    }

    public static MultiPolygon fromLngLats(double[][][][] coordinates) {
        List<List<List<Point>>> converted = new ArrayList<>(coordinates.length);
        for (double[][][] coordinate : coordinates) {
            converted.add(toListListPoint(coordinate));
        }

        return new MultiPolygon(converted);
    }

    public static MultiPolygon fromLngLats(double[][][] coordinates) {
        List<List<List<Point>>> ppList = new ArrayList<>(1);
        ppList.add(toListListPoint(coordinates));

        return new MultiPolygon(ppList);
    }

    private static List<List<Point>> toListListPoint(double[][][] coordinates) {
        List<List<Point>> converted = new ArrayList<>(coordinates.length);

        for (double[][] coordinate : coordinates) {
            List<Point> innerOneList = new ArrayList<>(coordinate.length);
            for (double[] doubles : coordinate) {
                innerOneList.add(Point.fromLngLat(doubles));
            }
            converted.add(innerOneList);
        }

        return converted;
    }

    public static MultiPolygon fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, MultiPolygon.class);
    }

    public static MultiPolygon multiPolygon(Geometry geometry) {
        return (MultiPolygon) geometry;
    }

    public List<Polygon> polygons() {
        List<List<List<Point>>> coordinates = coordinates();
        List<Polygon> polygons = new ArrayList<>(coordinates.size());
        for (List<List<Point>> points : coordinates) {
            polygons.add(Polygon.fromLngLats(points));
        }
        return polygons;
    }

    @Override
    public List<List<List<Point>>> coordinates() {
        return coordinates;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.MULTI_POLYGON;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (List<List<Point>> coordinate : coordinates) {
            buf.append("[");
            for (List<Point> pointList : coordinate) {
                buf.append("[");
                for (Point point : pointList) {
                    buf.append(point.toViewCoordsString());
                }
                buf.append("]");
            }
            buf.append("]");
        }

        return buf.toString();
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "MultiPolygon{" +
                "coordinates=" + coordinates +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MultiPolygon) {
            MultiPolygon that = (MultiPolygon) obj;
            return this.coordinates.equals(that.coordinates());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= coordinates.hashCode();
        return hashCode;
    }

}
