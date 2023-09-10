package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.d3geo.projection.AzimuthalEqualArea;
import com.cgzz.mapbox.jturf.util.d3geo.projection.Projection;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BufferHelper {

    private BufferHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 计算缓冲区单个组件
     *
     * @param geometry 要计算缓存区的组件
     * @param radius   绘制缓冲区的距离（允许负值）
     * @param units    距离单位
     * @param steps    频数
     * @return 计算后的组件
     */
    public static Geometry buffer(Geometry geometry, int radius, Units units, int steps) {
        if (geometry.type() == GeometryType.GEOMETRY_COLLECTION) {
            List<Geometry> results = new ArrayList<>();
            JTurfMeta.geomEach(geometry, (g, parent, geomIndex) -> {
                Geometry buffered = buffer(g, radius, units, steps);
                if (buffered != null) {
                    results.add(buffered);
                }
                return true;
            });

            return results.isEmpty() ? null : GeometryCollection.fromGeometries(results);
        }

        // Project GeoJSON to Azimuthal Equidistant projection (convert to Meters)
        Projection projection = defineProjection(geometry);

        // JSTS buffer operation
        double distance = JTurfHelper.radiansToLength(JTurfHelper.lengthToRadians(radius, units), Units.METERS);

        // 这里可以直接做一层中转，以后可以实现GeoJSON，就不需要这么麻烦咯。。。
        com.vividsolutions.jts.geom.Geometry buffered = BufferOp.bufferOp(transformation(geometry, projection), distance, steps);

        // Detect if empty geometries
        if (coordsIsNaN(buffered.getCoordinates())) {
            return null;
        }

        // Unproject coordinates (convert to Degrees)
        return unTransformation(buffered, projection);
    }

    /**
     * 将 JST的Geometry 转成对应的 Geometry
     *
     * @param geometry 要转换的JST组件
     * @param proj     坐标转换
     * @return Geometry组建
     */
    private static Geometry unTransformation(com.vividsolutions.jts.geom.Geometry geometry, Projection proj) {
        String type = geometry.getGeometryType();
        switch (type) {
            case "Point": {
                return Point.fromLngLat(unprojectCoords(geometry.getCoordinate(), proj));
            }
            case "MultiPoint": {
                return MultiPoint.fromLngLats(unprojectCoords(geometry.getCoordinates(), proj));
            }
            case "LineString": {
                return Line.fromLngLats(unprojectCoords(geometry.getCoordinates(), proj));
            }
            case "MultiLineString": {
                int size = geometry.getNumGeometries();
                List<List<Point>> allPointList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    allPointList.add(unprojectCoords(geometry.getGeometryN(i).getCoordinates(), proj));
                }
                return MultiLine.fromLngLats(allPointList);
            }
            case "Polygon": {
                return Polygon.fromLngLats(unprojectCoords(geometry.getCoordinates(), proj));
            }
            case "MultiPolygon": {
                int size = geometry.getNumGeometries();
                List<List<Point>> allPointList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    allPointList.add(unprojectCoords(geometry.getGeometryN(i).getCoordinates(), proj));
                }
                return MultiPolygon.fromLngLats(allPointList);
            }
            default:
                throw new JTurfException("JST " + type + " geometry not supported");
        }
    }

    /**
     * 将 Geometry 转成对应的 JTS的Geometry
     *
     * @param geometry 要转换的组件
     * @param proj     坐标转换
     * @return JTS Geometry 组件
     */
    private static com.vividsolutions.jts.geom.Geometry transformation(Geometry geometry, Projection proj) {
        GeometryFactory geometryFactory = new GeometryFactory();

        GeometryType type = geometry.type();
        switch (type) {
            case POINT: {
                Point p = Point.point(geometry);
                return geometryFactory.createPoint(projectCoords(p, proj));
            }
            case MULTI_POINT: {
                MultiPoint multiPoint = MultiPoint.multiPoint(geometry);
                return geometryFactory.createMultiPoint(projectCoords(multiPoint.coordinates(), proj));
            }
            case LINE: {
                Line line = Line.line(geometry);
                return geometryFactory.createLineString(projectCoords(line.coordinates(), proj));
            }
            case MULTI_LINE: {
                MultiLine multiLine = MultiLine.multiLine(geometry);
                return geometryFactory.createMultiLineString(
                        multiLine.coordinates()
                                .stream()
                                .map(pointList -> geometryFactory.createLineString(projectCoords(pointList, proj)))
                                .toArray(LineString[]::new)
                );
            }
            case POLYGON: {
                Polygon polygon = Polygon.polygon(geometry);
                return geometryFactory.createPolygon(projectCoords(polygon.coordinates(), proj));
            }
            case MULTI_POLYGON: {
                MultiPolygon multiPolygon = MultiPolygon.multiPolygon(geometry);
                return geometryFactory.createMultiPolygon(
                        multiPolygon.coordinates()
                                .stream()
                                .map(pointList -> geometryFactory.createPolygon(projectCoords(pointList, proj)))
                                .toArray(com.vividsolutions.jts.geom.Polygon[]::new)
                );
            }
            default:
                throw new JTurfException(type + " geometry not supported");
        }
    }

    public static void main(String[] args) {
        Point point = Point.fromLngLat(-90.548630, 14.616599);
        Projection projection = BufferHelper.defineProjection(point);

        System.out.println(Arrays.toString(projection.projection(new double[]{-90.54863, 14.616599})));
        System.out.println(Arrays.toString(projection.invert(new double[]{480.0, 250.0})));

        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Geometry buffered = BufferOp.bufferOp(geometryFactory.createPoint(new Coordinate(480, 250)), 804672, 8);
        System.out.println(buffered);

        System.out.println(Arrays.toString(projection.invert(new double[]{805152, 250})));
    }

    /**
     * 判断坐标中的值是否是 NaN
     *
     * @param coords 坐标集合
     * @return 如果 NaN 存在返回true，否则false
     */
    private static boolean coordsIsNaN(Coordinate[] coords) {
        if (coords == null || coords.length == 0) {
            return false;
        }
        for (Coordinate coord : coords) {
            if (Double.isNaN(coord.x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 投影坐标
     *
     * @param coords 要计算的坐标
     * @param proj   D3地理投影
     * @return 投影后的坐标
     */
    private static Coordinate projectCoords(Point coords, Projection proj) {
        double[] p = proj.projection(new double[]{coords.getX(), coords.getY()});
        return new Coordinate(p[0], p[1]);
    }

    /**
     * 投影坐标
     *
     * @param coords 要计算的坐标集合
     * @param proj   D3地理投影
     * @return 投影后的坐标集合
     */
    private static Coordinate[] projectCoords(List<Point> coords, Projection proj) {
        return coords.stream().map(p -> projectCoords(p, proj)).toArray(Coordinate[]::new);
    }

    /**
     * 将坐标取消投影
     *
     * @param coords 要取消的坐标
     * @param proj   D3地理投影
     * @return 取消后的坐标
     */
    private static Point unprojectCoords(Coordinate coords, Projection proj) {
        double[] p = proj.invert(new double[]{coords.x, coords.y});
        return Point.fromLngLat(p);
    }

    /**
     * 将坐标取消投影
     *
     * @param coords 要取消的坐标集合
     * @param proj   D3地理投影
     * @return 取消后的坐标集合
     */
    private static List<Point> unprojectCoords(Coordinate[] coords, Projection proj) {
        return Stream.of(coords).map(p -> unprojectCoords(p, proj)).collect(Collectors.toList());
    }

    /**
     * 定义方位等距投影
     *
     * @param geometry 图形组件
     * @return D3 地理方位等距投影
     */
    private static Projection defineProjection(Geometry geometry) {
        Point coords = JTurfMeasurement.center(geometry);
        double[] rotation = new double[]{-coords.getX(), -coords.getY()};

        return AzimuthalEqualArea.geoAzimuthalEqualArea().rotate(rotation).scale(JTurfHelper.EARTH_RADIUS);
    }

}
