package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.ArrayList;
import java.util.List;

public final class MultiPoint extends GeometryProperties implements CoordinateContainer<List<Point>, MultiPoint> {

    private List<Point> coordinates;

    MultiPoint(List<Point> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiPoint fromLngLats(List<Point> points) {
        return new MultiPoint(points);
    }

    public static MultiPoint fromLngLats(Point p1, Point ...p2) {
        if (p2 == null && p1 == null) {
            throw new JTurfException("point can not be null");
        }

        List<Point> points;
        if (p2 == null) {
            points = new ArrayList<>(1);
            points.add(p1);
        } else {
            points = new ArrayList<>();
            points.add(p1);
            for (Point p : p2) {
                points.add(p);
            }
        }
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
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
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

    public static MultiPoint multiPoint(Geometry g) {
        return (MultiPoint)g;
    }

    @Override
    public List<Point> coordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(List<Point> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int coordsSize() {
        return coordinates != null ? coordinates.size() : 0;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(type()).append("─────┤").append("\n");
        if (coordinates != null) {
            for (Point p : coordinates) {
                buf.append("[").append(p.getX()).append(",").append(p.getY()).append("]").append("\n");
            }
            return buf.toString();
        }
        return "";
    }

    @Override
    public MultiPoint deepClone() {
        List<Point> list = new ArrayList<>(coordinates.size());
        for (Point point : coordinates) {
            list.add(point.deepClone());
        }
        MultiPoint mp = fromLngLats(list);
        mp.properties = cloneProperties();

        return mp;
    }

    @Override
    public GeometryType type() {
        return GeometryType.MULTI_POINT;
    }

    @Override
    public String toString() {
        return "MultiPoint{" +
                "coordinates=" + coordinates +
                '}';
    }
}
