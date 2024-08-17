package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.util.ArrayList;
import java.util.List;

public final class MultiLineString implements CoordinateContainer<List<List<Point>>> {

    private final List<List<Point>> coordinates;

    MultiLineString(List<List<Point>> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiLineString fromLine(List<LineString> lines) {
        List<List<Point>> coordinates = new ArrayList<>(lines.size());
        for (LineString line : lines) {
            coordinates.add(line.coordinates());
        }
        return new MultiLineString(coordinates);
    }

    public static MultiLineString fromLine(LineString line) {
        List<List<Point>> coordinates = new ArrayList<>();
        coordinates.add(line.coordinates());

        return new MultiLineString(coordinates);
    }

    public static MultiLineString fromLngLats(List<List<Point>> points) {
        return new MultiLineString(points);
    }

    public static MultiLineString fromLngLats(double[][] coordinates) {
        List<Point> pointList = new ArrayList<>(coordinates.length);
        for (double[] p : coordinates) {
            pointList.add(Point.fromLngLat(p));
        }

        List<List<Point>> ppList = new ArrayList<>(1);
        ppList.add(pointList);

        return new MultiLineString(ppList);
    }

    public static MultiLineString fromLngLats(double[][][] coordinates) {
        List<List<Point>> multiLine = new ArrayList<>(coordinates.length);
        for (double[][] coordinate : coordinates) {
            List<Point> line = new ArrayList<>(coordinate.length);
            for (double[] p : coordinate) {
                line.add(Point.fromLngLat(p));
            }
            multiLine.add(line);
        }

        return new MultiLineString(multiLine);
    }

    public static MultiLineString fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, MultiLineString.class);
    }

    public static MultiLineString multiLineString(Geometry geometry) {
        return (MultiLineString) geometry;
    }

    @Override
    public List<List<Point>> coordinates() {
        return coordinates;
    }

    public List<LineString> lineStrings() {
        List<List<Point>> coordinates = coordinates();
        List<LineString> lineStrings = new ArrayList<>(coordinates.size());
        for (List<Point> points : coordinates) {
            lineStrings.add(LineString.fromLngLats(points));
        }
        return lineStrings;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.MULTI_LINE_STRING;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (List<Point> pointList : coordinates) {
            buf.append("[");
            for (Point point : pointList) {
                buf.append(point.toViewCoordsString());
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
        return "MultiLineString{" +
                "coordinates=" + coordinates +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MultiLineString) {
            MultiLineString that = (MultiLineString) obj;
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
