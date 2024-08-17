package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.util.ArrayList;
import java.util.List;

public final class MultiPoint implements CoordinateContainer<List<Point>> {

    private final List<Point> coordinates;

    MultiPoint(List<Point> coordinates) {
        if (coordinates == null) {
            throw new JTurfException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiPoint fromLngLats(List<Point> points) {
        return new MultiPoint(points);
    }

    public static MultiPoint fromLngLats(double[][] coordinates) {
        List<Point> points = new ArrayList<>(coordinates.length);

        for (double[] p : coordinates) {
            points.add(Point.fromLngLat(p));
        }

        return fromLngLats(points);
    }

    public static MultiPoint fromLngLats(double[] coordinates) {
        int len = coordinates.length;
        if (len < 2) {
            throw new JTurfException("coordinates length at least 2");
        }

        // 则必须为2个倍数
        if (coordinates.length % 2 == 1) {
            throw new JTurfException("coordinates length must be a multiple of 2");
        }

        List<Point> points = new ArrayList<>(len / 2);
        for (int i = 0; i < len; i = i + 2) {
            points.add(Point.fromLngLat(coordinates[i], coordinates[i + 1]));
        }

        return fromLngLats(points);
    }

    public static MultiPoint fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, MultiPoint.class);
    }

    public static MultiPoint multiPoint(Geometry g) {
        return (MultiPoint) g;
    }

    @Override
    public List<Point> coordinates() {
        return coordinates;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.MULTI_POINT;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (Point p : coordinates) {
            buf.append(p.toViewCoordsString());
        }
        return buf.toString();
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "MultiPoint{"
                + "coordinates=" + coordinates
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MultiPoint) {
            MultiPoint that = (MultiPoint) obj;
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
