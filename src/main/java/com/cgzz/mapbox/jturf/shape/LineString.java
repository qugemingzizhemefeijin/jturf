package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;

import java.util.ArrayList;
import java.util.List;

public final class LineString implements CoordinateContainer<List<Point>> {

    private final List<Point> coordinates;

    LineString(List<Point> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static LineString fromLngLats(List<Point> points) {
        if (points == null) {
            throw new JTurfException("points can not be null");
        }
        int len = points.size();
        if (len < 2) {
            throw new JTurfException("points size at least 3");
        }

        return new LineString(points);
    }

    public static LineString fromLngLatsShallowCopy(List<Point> points) {
        if (points == null) {
            throw new JTurfException("points can not be null");
        }
        int len = points.size();
        if (len < 2) {
            throw new JTurfException("points size at least 3");
        }

        return new LineString(new ArrayList<>(points));
    }

    public static LineString fromLngLats(Point start, Point end) {
        List<Point> points = new ArrayList<>(2);
        points.add(start);
        points.add(end);

        return fromLngLats(points);
    }

    public static LineString fromLngLats(double x1, double y1, double x2, double y2) {
        return fromLngLats(Point.fromLngLat(x1, y1), Point.fromLngLat(x2, y2));
    }

    public static LineString fromLngLats(double[][] coordinates) {
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = coordinates.length;
        if (len < 2) {
            throw new JTurfException("coordinates length at least 2");
        }

        List<Point> points = new ArrayList<>(coordinates.length);

        for (double[] p : coordinates) {
            points.add(Point.fromLngLat(p));
        }

        return fromLngLats(points);
    }

    public static LineString fromLngLats(double[] coordinates) {
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = coordinates.length;
        if (len < 4) {
            throw new JTurfException("coordinates length at least 4");
        }

        // 则必须为2个倍数
        if (coordinates.length % 2 == 1) {
            throw new JTurfException("coordinates length must be a multiple of 2");
        }

        List<Point> points = new ArrayList<>(len / 2);
        for (int i = 0; i < len; i = i + 2) {
            points.add(Point.fromLngLat(coordinates[i], coordinates[i + 1]));
        }

        return new LineString(points);
    }

    public static LineString fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, LineString.class);
    }

    public static LineString LineString(Geometry g) {
        return (LineString) g;
    }

    @Override
    public List<Point> coordinates() {
        return coordinates;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.LINE_STRING;
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
        return "LineString{"
                + "coordinates=" + coordinates
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LineString) {
            LineString that = (LineString) obj;
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
