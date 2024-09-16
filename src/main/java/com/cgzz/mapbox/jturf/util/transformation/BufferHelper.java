package com.cgzz.mapbox.jturf.util.transformation;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.JTurfMeasurement;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.pkg.d3geo.projection.AzimuthalEqualArea;
import com.cgzz.mapbox.jturf.util.pkg.d3geo.projection.Projection;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
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
     * 计算缓冲区（辐射区）<br>
     * 计算组件在给定半径的缓冲区。支持的单位有英里、公里和度数。<br>
     * 使用负半径时，生成的几何图形可能无效，如果与半径大小相比，它太小了。如果是集合类组件，输出集合的成员可能少于输入，甚至为空。
     *
     * @param geometry 要计算的组件
     * @param radius   绘制缓冲区的距离（允许负值）
     * @param units    距离单位，默认 KILOMETERS
     * @param steps    频数，默认为 8
     * @return 缓冲区的图形组件
     */
    public static Geometry buffer(Geometry geometry, int radius, Units units, Integer steps) {
        if (geometry == null) {
            throw new JTurfException("geometry is required");
        }
        if (units == null) {
            units = Units.KILOMETERS;
        }
        if (steps == null) {
            steps = 8;
        }

        final Units finalUnits = units;
        final int finalSteps = steps;

        GeometryType type = geometry.geometryType();
        if (type == GeometryType.GEOMETRY_COLLECTION) {
            List<Geometry> geometryList = new ArrayList<>();

            JTurfMeta.geomEach(geometry, (g, featureIndex, featureProperties, featureId) -> {
                Geometry buffered = bufferFeature(g, radius, finalUnits, finalSteps);
                if (buffered != null) {
                    geometryList.add(buffered);
                }
                return true;
            });

            return geometryList.isEmpty() ? null : GeometryCollection.fromGeometries(geometryList);
        } else {
            return bufferFeature(geometry, radius, finalUnits, finalSteps);
        }
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
    private static Geometry bufferFeature(Geometry geometry, int radius, Units units, int steps) {
        JsonObject properties = null;
        if (geometry instanceof Feature) {
            Feature<Geometry> feature = Feature.feature(geometry);
            properties = feature.properties();
            geometry = feature.geometry();
        }

        if (geometry.geometryType() == GeometryType.GEOMETRY_COLLECTION) {
            List<Geometry> results = new ArrayList<>();
            JTurfMeta.geomEach(geometry, (g, featureIndex, featureProperties, featureId) -> {
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
        return Feature.fromGeometry(unTransformation(buffered, projection), properties);
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
                return com.cgzz.mapbox.jturf.shape.impl.LineString.fromLngLats(unprojectCoords(geometry.getCoordinates(), proj));
            }
            case "MultiLineString": {
                int size = geometry.getNumGeometries();
                List<List<Point>> allPointList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    allPointList.add(unprojectCoords(geometry.getGeometryN(i).getCoordinates(), proj));
                }
                return MultiLineString.fromLngLats(allPointList);
            }
            case "Polygon": {
                return Polygon.fromLngLats(unprojectPolygonCoords(geometry, proj));
            }
            case "MultiPolygon": {
                int size = geometry.getNumGeometries();
                List<List<List<Point>>> allPointList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    allPointList.add(unprojectPolygonCoords(geometry.getGeometryN(i), proj));
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

        GeometryType type = geometry.geometryType();
        switch (type) {
            case POINT: {
                Point p = Point.point(geometry);
                return geometryFactory.createPoint(projectCoords(p, proj));
            }
            case MULTI_POINT: {
                MultiPoint multiPoint = MultiPoint.multiPoint(geometry);
                return geometryFactory.createMultiPoint(projectCoords(multiPoint.coordinates(), proj));
            }
            case LINE_STRING: {
                LineString line = LineString.lineString(geometry);
                return geometryFactory.createLineString(projectCoords(line.coordinates(), proj));
            }
            case MULTI_LINE_STRING: {
                MultiLineString multiLine = MultiLineString.multiLineString(geometry);
                return geometryFactory.createMultiLineString(
                        multiLine.coordinates()
                                .stream()
                                .map(pointList -> geometryFactory.createLineString(projectCoords(pointList, proj)))
                                .toArray(com.vividsolutions.jts.geom.LineString[]::new)
                );
            }
            case POLYGON: {
                Polygon polygon = Polygon.polygon(geometry);
                return createJtsPolygon(geometryFactory, polygon.coordinates(), proj);
            }
            case MULTI_POLYGON: {
                MultiPolygon multiPolygon = MultiPolygon.multiPolygon(geometry);
                return geometryFactory.createMultiPolygon(
                        multiPolygon.coordinates()
                                .stream()
                                .map(pointList -> createJtsPolygon(geometryFactory, pointList, proj))
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
     * 投影坐标并返回JTL的{@link com.vividsolutions.jts.geom.Polygon}类型
     *
     * @param geometryFactory JTL工厂
     * @param coordinates     JTurf的Polygon坐标集合
     * @param proj            D3地理投影
     * @return com.vividsolutions.jts.geom.Polygon
     */
    private static com.vividsolutions.jts.geom.Polygon createJtsPolygon(GeometryFactory geometryFactory, List<List<Point>> coordinates, Projection proj) {
        int size = coordinates.size();
        if (size == 1) {
            return geometryFactory.createPolygon(projectCoords(coordinates.get(0), proj));
        } else {
            LinearRing shell = geometryFactory.createLinearRing(projectCoords(coordinates.get(0), proj));
            LinearRing[] holes = new LinearRing[size - 1];
            for (int i = 1; i < size; i++) {
                holes[i - 1] = geometryFactory.createLinearRing(projectCoords(coordinates.get(i), proj));
            }

            return geometryFactory.createPolygon(shell, holes);
        }
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
     * 针对 {@link com.vividsolutions.jts.geom.Polygon} 返回坐标
     *
     * @param geometry JTS的Geometry
     * @param proj     D3地理投影
     * @return 取消后的坐标集合
     */
    private static List<List<Point>> unprojectPolygonCoords(com.vividsolutions.jts.geom.Geometry geometry, Projection proj) {
        com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) geometry;

        int holes = polygon.getNumInteriorRing();
        List<List<Point>> pointLists = new ArrayList<>(holes + 1);

        pointLists.add(unprojectCoords(polygon.getExteriorRing().getCoordinates(), proj));

        if (holes > 0) {
            for (int i = 0; i < holes; i++) {
                pointLists.add(unprojectCoords(polygon.getInteriorRingN(i).getCoordinates(), proj));
            }
        }

        return pointLists;
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
