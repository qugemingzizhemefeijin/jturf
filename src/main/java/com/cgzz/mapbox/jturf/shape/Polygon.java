package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.util.JTurfHelper;

import java.util.ArrayList;
import java.util.List;

public final class Polygon extends GeometryProperties implements CoordinateContainer<List<Point>, Polygon> {

    private List<Point> coordinates;

    Polygon(List<Point> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static Polygon fromLngLats(List<Point> coordinates) {
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = coordinates.size();
        if (len < 3) {
            throw new JTurfException("coordinates length at least 3");
        }

        Point tailPt = coordinates.get(len - 1);
        Point headPt = coordinates.get(0);

        // 判断尾巴是否与头部相同
        boolean tail = JTurfHelper.equals(headPt, tailPt);

        if (!tail) {
            coordinates.add(Point.fromLngLat(headPt));
        }

        return new Polygon(coordinates);
    }

    public static Polygon fromLngLats(double[][] coordinates) {
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = coordinates.length;
        if (len < 3) {
            throw new JTurfException("coordinates length at least 3");
        }

        double[] tailPt = coordinates[len - 1];

        // 判断尾巴是否与头部相同
        boolean tail = coordinates[0][0] == tailPt[0] && coordinates[0][1] == tailPt[1];

        List<Point> converted = new ArrayList<>(tail ? len : (len + 1));
        for (double[] coordinate : coordinates) {
            converted.add(Point.fromLngLat(coordinate));
        }

        if (!tail) {
            converted.add(Point.fromLngLat(coordinates[0]));
        }

        return new Polygon(converted);
    }

    public static Polygon fromLngLats(double[] coordinates) {
        if (coordinates == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = coordinates.length;
        if (len < 6) {
            throw new JTurfException("coordinates length at least 6");
        }

        // 判断尾巴是否与头部相同
        boolean tail = coordinates[0] == coordinates[len - 2] && coordinates[1] == coordinates[len - 1];

        // 则必须为2个倍数
        if (coordinates.length % 2 == 1) {
            throw new JTurfException("coordinates length must be a multiple of 2");
        }

        int ps = len / 2;
        List<Point> converted = new ArrayList<>(tail ? ps : (ps + 1));
        for (int i = 0; i < len; i = i + 2) {
            converted.add(Point.fromLngLat(coordinates[i], coordinates[i + 1]));
        }
        if (!tail) {
            converted.add(Point.fromLngLat(coordinates[0], coordinates[1]));
        }

        return new Polygon(converted);
    }

    public static Polygon polygon(Geometry g) {
        return (Polygon) g;
    }

    @Override
    public List<Point> coordinates() {
        return coordinates;
    }

    public List<List<Point>> multiCoordinates() {
        List<List<Point>> m = new ArrayList<>(1);
        m.add(coordinates);

        return m;
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
    public Polygon deepClone() {
        List<Point> list = new ArrayList<>(coordinates.size());
        for (Point point : coordinates) {
            list.add(point.deepClone());
        }
        return fromLngLats(list);
    }

    @Override
    public GeometryType type() {
        return GeometryType.POLYGON;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "coordinates=" + coordinates +
                '}';
    }
}
