package com.cgzz.mapbox.jturf.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultiLine extends GeometryProperties implements CoordinateContainer<List<List<Point>>, MultiLine> {

    private List<List<Point>> coordinates;

    MultiLine(List<List<Point>> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiLine fromLine(List<Line> lines) {
        List<List<Point>> coordinates = new ArrayList<>(lines.size());
        for (Line line : lines) {
            coordinates.add(line.coordinates());
        }
        return new MultiLine(coordinates);
    }

    public static MultiLine fromLine(Line line) {
        List<List<Point>> coordinates = Collections.singletonList(line.coordinates());
        return new MultiLine(coordinates);
    }

    public static MultiLine fromLngLats(List<List<Point>> points) {
        return new MultiLine(points);
    }

    public static MultiLine fromLngLats(double[][] coordinates) {
        List<Point> line = new ArrayList<>(coordinates.length);
        for (double[] p : coordinates) {
            line.add(Point.fromLngLat(p));
        }

        return new MultiLine(Collections.singletonList(line));
    }

    public static MultiLine fromLngLats(double[][][] coordinates) {
        List<List<Point>> multiLine = new ArrayList<>(coordinates.length);
        for (double[][] coordinate : coordinates) {
            List<Point> line = new ArrayList<>(coordinate.length);
            for (double[] p : coordinate) {
                line.add(Point.fromLngLat(p));
            }
            multiLine.add(line);
        }

        return new MultiLine(multiLine);
    }

    public static MultiLine multiLine(Geometry geometry) {
        return (MultiLine) geometry;
    }

    @Override
    public List<List<Point>> coordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(List<List<Point>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public MultiLine deepClone() {
        List<List<Point>> newList = new ArrayList<>(coordinates.size());
        for (List<Point> coordinate : coordinates) {
            List<Point> newCoordinate = new ArrayList<>(coordinate.size());
            for (Point p : coordinate) {
                newCoordinate.add(p.deepClone());
            }
            newList.add(newCoordinate);
        }
        MultiLine m = fromLngLats(newList);
        m.properties = cloneProperties();

        return m;
    }

    @Override
    public GeometryType type() {
        return GeometryType.MULTI_LINE;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(type()).append("─────┤").append("\n");
        if (coordinates != null) {
            for (List<Point> pointList : coordinates) {
                buf.append("[");
                for (Point point : pointList) {
                    buf.append("[").append(point.getX()).append(",").append(point.getY()).append("]").append("\n");
                }
                buf.append("]");
            }
            return buf.toString();
        }
        return "";
    }

    @Override
    public String toString() {
        return "MultiLine{" +
                "coordinates=" + coordinates +
                '}';
    }
}
