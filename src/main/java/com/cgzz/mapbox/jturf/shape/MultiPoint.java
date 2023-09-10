package com.cgzz.mapbox.jturf.shape;

import java.util.ArrayList;
import java.util.List;

public final class MultiPoint implements CoordinateContainer<List<Point>, MultiPoint> {

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

    public static MultiPoint fromLngLats(double[][] coordinates) {
        List<Point> points = new ArrayList<>(coordinates.length);

        for (double[] p : coordinates) {
            points.add(Point.fromLngLat(p));
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
        return fromLngLats(list);
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
