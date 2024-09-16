package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.CoordinateContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Polygon implements CoordinateContainer<List<List<Point>>> {

    private List<List<Point>> coordinates;

    Polygon(List<List<Point>> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("Null coordinates");
        }
        this.coordinates = coordinates;
    }

    public static Polygon fromLngLats(List<List<Point>> coordinates) {
        // 修正数据
        for (List<Point> pointList : coordinates) {
            corrective(pointList);
        }

        return new Polygon(coordinates);
    }

    public static Polygon fromLngLats(Point... coordinates) {
        List<Point> pointList = new ArrayList<>(coordinates.length + 1);
        pointList.addAll(Arrays.asList(coordinates));
        // 修正数据
        corrective(pointList);

        List<List<Point>> singlePointList = new ArrayList<>(4);
        singlePointList.add(pointList);

        return new Polygon(singlePointList);
    }

    public static Polygon fromLngLats(double[][][] coordinates) {
        List<List<Point>> converted = new ArrayList<>(coordinates.length);
        for (double[][] coordinate : coordinates) {
            List<Point> innerList = new ArrayList<>(coordinate.length);
            for (double[] pointCoordinate : coordinate) {
                innerList.add(Point.fromLngLat(pointCoordinate));
            }
            corrective(innerList);
            converted.add(innerList);
        }
        return new Polygon(converted);
    }

    public static Polygon fromLngLats(double[][] outer) {
        List<Point> innerList = new ArrayList<>(outer.length);
        for (double[] pointCoordinate : outer) {
            innerList.add(Point.fromLngLat(pointCoordinate));
        }
        corrective(innerList);

        List<List<Point>> coordinates = new ArrayList<>(1);
        coordinates.add(innerList);

        return new Polygon(coordinates);
    }

    @SafeVarargs
    public static Polygon fromOuterInner(List<Point> outer, List<Point>... inner) {
        if (inner == null) {
            corrective(outer);

            List<List<Point>> coordinates = new ArrayList<>(1);
            coordinates.add(outer);

            return new Polygon(coordinates);
        }

        List<List<Point>> coordinates = new ArrayList<>(inner.length + 1);
        corrective(outer);
        coordinates.add(outer);

        for (List<Point> i : inner) {
            corrective(i);
            coordinates.add(i);
        }

        return new Polygon(coordinates);
    }

    public static Polygon fromOuterInner(LineString outer, LineString... inner) {
        isLinearRing(outer);
        if (inner == null) {
            List<List<Point>> coordinates = new ArrayList<>(1);
            coordinates.add(outer.coordinates());

            return new Polygon(coordinates);
        }

        List<List<Point>> coordinates = new ArrayList<>(inner.length + 1);
        coordinates.add(outer.coordinates());

        for (LineString lineString : inner) {
            isLinearRing(lineString);
            coordinates.add(lineString.coordinates());
        }
        return new Polygon(coordinates);
    }

    public static Polygon fromOuterInner(LineString outer, List<LineString> inner) {
        isLinearRing(outer);
        if (inner == null || inner.isEmpty()) {
            List<List<Point>> coordinates = new ArrayList<>(1);
            coordinates.add(outer.coordinates());

            return new Polygon(coordinates);
        }

        List<List<Point>> coordinates = new ArrayList<>();
        coordinates.add(outer.coordinates());

        for (LineString lineString : inner) {
            isLinearRing(lineString);
            coordinates.add(lineString.coordinates());
        }
        return new Polygon(coordinates);
    }

    public static Polygon fromLngLats(double[] outer) {
        if (outer == null) {
            throw new JTurfException("coordinates can not be null");
        }
        int len = outer.length;
        if (len < 6) {
            throw new JTurfException("coordinates length at least 6");
        }

        // 判断尾巴是否与头部相同
        boolean tail = outer[0] == outer[len - 2] && outer[1] == outer[len - 1];

        // 则必须为2个倍数
        if (outer.length % 2 == 1) {
            throw new JTurfException("coordinates length must be a multiple of 2");
        }

        int ps = len / 2;
        List<Point> converted = new ArrayList<>(tail ? ps : (ps + 1));
        for (int i = 0; i < len; i = i + 2) {
            converted.add(Point.fromLngLat(outer[i], outer[i + 1]));
        }
        if (!tail) {
            converted.add(Point.fromLngLat(outer[0], outer[1]));
        }

        List<List<Point>> coordinates = new ArrayList<>(1);
        coordinates.add(converted);

        return new Polygon(coordinates);
    }

    private static void corrective(List<Point> pointList) {
        int len = pointList.size();
        if (len < 3) {
            throw new JTurfException("coordinates length at least 3");
        }

        Point tailPt = pointList.get(len - 1);
        Point headPt = pointList.get(0);

        // 判断尾巴是否与头部相同
        boolean tail = JTurfHelper.equals(headPt, tailPt);

        if (len == 3 && tail) {
            throw new JTurfException("when size = 3 then head can not equals tail");
        }

        if (!tail) {
            pointList.add(Point.fromLngLat(headPt));
        }
    }

    public static Polygon fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, Polygon.class);
    }

    public static Polygon polygon(Geometry g) {
        return (Polygon) g;
    }

    private static void isLinearRing(LineString lineString) {
        List<Point> pointList = lineString.coordinates();
        int size = pointList.size();

        if (size < 4) {
            throw new JTurfException("LinearRings need to be made up of 4 or more coordinates.");
        }
        if (!(pointList.get(0).equals(pointList.get(size - 1)))) {
            throw new JTurfException("LinearRings require first and last coordinate to be identical.");
        }
    }

    public LineString outer() {
        return LineString.fromLngLats(coordinates().get(0));
    }

    public List<LineString> inner() {
        List<List<Point>> coordinates = coordinates();
        if (coordinates.size() <= 1) {
            return new ArrayList<>(0);
        }
        List<LineString> inner = new ArrayList<>(coordinates.size() - 1);
        for (int i = 1, size = coordinates.size(); i < size; i++) {
            inner.add(LineString.fromLngLats(coordinates.get(i)));
        }
        return inner;
    }

    @Override
    public List<List<Point>> coordinates() {
        return coordinates;
    }

    @Override
    public void coordinates(List<List<Point>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.POLYGON;
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
        return "Polygon{" +
                "coordinates=" + coordinates +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Polygon) {
            Polygon that = (Polygon) obj;
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
