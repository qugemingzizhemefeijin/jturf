package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.ArrayList;
import java.util.List;

public final class Line extends GeometryProperties implements CoordinateContainer<List<Point>, Line> {

    private List<Point> coordinates;

    Line(List<Point> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static Line fromLngLats(List<Point> points) {
        if (points == null) {
            throw new JTurfException("points can not be null");
        }
        int len = points.size();
        if (len < 2) {
            throw new JTurfException("points size at least 3");
        }

        return new Line(points);
    }

    public static Line fromLngLatsShallowCopy(List<Point> points) {
        if (points == null) {
            throw new JTurfException("points can not be null");
        }
        int len = points.size();
        if (len < 2) {
            throw new JTurfException("points size at least 3");
        }

        return new Line(new ArrayList<>(points));
    }

    public static Line fromLngLats(Point start, Point end) {
        List<Point> points = new ArrayList<>(2);
        points.add(start);
        points.add(end);

        return fromLngLats(points);
    }

    public static Line fromLngLats(double x1, double y1, double x2, double y2) {
        return fromLngLats(Point.fromLngLat(x1, y1), Point.fromLngLat(x2, y2));
    }

    public static Line fromLngLats(double[][] coordinates) {
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

    public static Line fromLngLats(double[] coordinates) {
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

        return new Line(points);
    }

    public static Line line(Geometry g) {
        return (Line)g;
    }

    @Override
    public List<Point> coordinates() {
        return coordinates;
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
    public void setCoordinates(List<Point> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public Line deepClone() {
        List<Point> list = new ArrayList<>(coordinates.size());
        for (Point point : coordinates) {
            list.add(point.deepClone());
        }
        return fromLngLats(list);
    }

    @Override
    public GeometryType type() {
        return GeometryType.LINE;
    }

    @Override
    public String toString() {
        return "Line{" +
                "coordinates=" + coordinates +
                '}';
    }
}
