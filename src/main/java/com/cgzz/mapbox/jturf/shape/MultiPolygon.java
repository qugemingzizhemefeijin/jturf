package com.cgzz.mapbox.jturf.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultiPolygon extends GeometryProperties implements CoordinateContainer<List<List<Point>>, MultiPolygon> {

    private List<List<Point>> coordinates;

    MultiPolygon(List<List<Point>> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static MultiPolygon fromPolygons(List<Polygon> polygons) {
        List<List<Point>> coordinates = new ArrayList<>(polygons.size());
        for (Polygon polygon : polygons) {
            coordinates.add(polygon.coordinates());
        }
        return new MultiPolygon(coordinates);
    }

    public static MultiPolygon fromPolygon(Polygon polygon) {
        List<List<Point>> coordinates = Collections.singletonList(polygon.coordinates());
        return new MultiPolygon(coordinates);
    }

    public static MultiPolygon fromLngLats(List<List<Point>> points) {
        return new MultiPolygon(points);
    }

    public static MultiPolygon fromLngLats(double[][][] coordinates) {
        List<List<Point>> converted = new ArrayList<>(coordinates.length);

        for (double[][] coordinate : coordinates) {
            List<Point> innerOneList = new ArrayList<>(coordinate.length);
            for (double[] doubles : coordinate) {
                innerOneList.add(Point.fromLngLat(doubles));
            }
            converted.add(innerOneList);
        }

        return new MultiPolygon(converted);
    }

    public static MultiPolygon multiPolygon(Geometry geometry) {
        return (MultiPolygon) geometry;
    }

    public List<Polygon> polygons() {
        List<List<Point>> coordinates = coordinates();
        List<Polygon> polygons = new ArrayList<>(coordinates.size());
        for (List<Point> points : coordinates) {
            polygons.add(Polygon.fromLngLats(points));
        }
        return polygons;
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
    public MultiPolygon deepClone() {
        List<List<Point>> newList = new ArrayList<>(coordinates.size());
        for (List<Point> coordinate : coordinates) {
            List<Point> newCoordinate = new ArrayList<>(coordinate.size());
            for (Point p : coordinate) {
                newCoordinate.add(p.deepClone());
            }
            newList.add(newCoordinate);
        }
        return fromLngLats(newList);
    }

    @Override
    public GeometryType type() {
        return GeometryType.MULTI_POLYGON;
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
        return "MultiPolygon{" +
                "coordinates=" + coordinates +
                '}';
    }
}
