package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfHelper;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 清除重复坐标点工具类
 */
public final class CleanCoordsHelper {

    private CleanCoordsHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 清除重复坐标点
     *
     * @param geometry 图形组件
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的图形组件
     */
    @SuppressWarnings("unchecked")
    public static <T extends Geometry> T cleanCoords(T geometry, boolean mutate) {
        GeometryType type = geometry.geometryType();
        if (type == GeometryType.POINT) {
            return geometry;
        } else if (type == GeometryType.FEATURE) {
            Geometry t = cleanCoords(Feature.fromGeometry(geometry).geometry(), mutate);
            if (!mutate) {
                return (T) Feature.fromGeometry(t);
            } else {
                return geometry;
            }
        }

        switch (type) {
            case LINE_STRING: {
                return (T) cleanCoords(LineString.lineString(geometry), mutate);
            }
            case MULTI_LINE_STRING: {
                return (T) cleanCoords(MultiLineString.multiLineString(geometry), mutate);
            }
            case POLYGON: {
                return (T) cleanCoords(Polygon.polygon(geometry), mutate);
            }
            case MULTI_POLYGON: {
                return (T) cleanCoords(MultiPolygon.multiPolygon(geometry), mutate);
            }
            default:
                throw new JTurfException(type + " geometry not supported");
        }
    }

    /**
     * 清除LineString的重复坐标点
     *
     * @param geometry 要处理的LineString对象
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的LineString
     */
    private static LineString cleanCoords(LineString geometry, boolean mutate) {
        List<Point> coordinates = cleanLine(geometry.coordinates(), geometry.geometryType());
        if (coordinates == null) {
            return null;
        }

        LineString newGeometry = JTurfHelper.getGeometryByMutate(geometry, mutate);
        newGeometry.coordinates(coordinates);

        return newGeometry;
    }

    /**
     * 清除MultiLineString的重复坐标点
     *
     * @param geometry 要处理的MultiLineString对象
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的MultiLineString
     */
    private static MultiLineString cleanCoords(MultiLineString geometry, boolean mutate) {
        List<List<Point>> coordinates = cleanLinePointListList(geometry.coordinates(), geometry.geometryType());
        if (coordinates.isEmpty()) {
            return null;
        }

        MultiLineString newGeometry = JTurfHelper.getGeometryByMutate(geometry, mutate);
        newGeometry.coordinates(coordinates);

        return newGeometry;
    }

    /**
     * 清除指定点集合的重复坐标点
     *
     * @param pointListList 要处理的点集合
     * @param geometryType  点集合所属的组件类型
     * @return 返回处理后的集合
     */
    private static List<List<Point>> cleanLinePointListList(List<List<Point>> pointListList, GeometryType geometryType) {
        List<List<Point>> newPoints = new ArrayList<>(pointListList.size());
        for (List<Point> points : pointListList) {
            List<Point> temp = cleanLine(points, geometryType);
            if (temp == null) {
                continue;
            }
            newPoints.add(temp);
        }

        return newPoints;
    }

    /**
     * 清除Polygon的重复坐标点
     *
     * @param geometry 要处理的Polygon对象
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的Polygon
     */
    private static Polygon cleanCoords(Polygon geometry, boolean mutate) {
        List<List<Point>> coordinates = cleanLinePointListList(geometry.coordinates(), geometry.geometryType());
        if (coordinates.isEmpty()) {
            return null;
        }

        Polygon newGeometry = JTurfHelper.getGeometryByMutate(geometry, mutate);
        newGeometry.coordinates(coordinates);

        return newGeometry;
    }

    /**
     * 清除MultiPolygon的重复坐标点
     *
     * @param geometry 要处理的MultiPolygon对象
     * @param mutate   是否影响原图形坐标
     * @return 返回处理后的MultiPolygon
     */
    private static MultiPolygon cleanCoords(MultiPolygon geometry, boolean mutate) {
        List<List<List<Point>>> coordinates = new ArrayList<>(geometry.coordinates().size());

        for (List<List<Point>> points : geometry.coordinates()) {
            List<List<Point>> newPoints = cleanLinePointListList(points, geometry.geometryType());
            if (newPoints.isEmpty()) {
                return null;
            }
            coordinates.add(newPoints);
        }
        if (coordinates.isEmpty()) {
            return null;
        }

        MultiPolygon newGeometry = JTurfHelper.getGeometryByMutate(geometry, mutate);
        newGeometry.coordinates(coordinates);

        return newGeometry;
    }

    /**
     * 处理带有线条性质的重复点集合
     *
     * @param points 点集合
     * @param type   当前处理的多边形类型
     * @return 处理完的新的集合
     */
    private static List<Point> cleanLine(List<Point> points, GeometryType type) {
        int size = points.size();
        if (size == 2 && !JTurfHelper.equals(points.get(0), points.get(1))) {
            return points;
        }

        List<Point> newPoints = new ArrayList<>();
        int secondToLast = points.size() - 1;
        int newPointsLength;

        newPoints.add(points.get(0));
        for (int i = 1; i < secondToLast; i++) {
            Point prevAddedPoint = newPoints.get(newPoints.size() - 1);
            Point currPoint = points.get(i);

            // 如果当前点与前一个点相同，则直接返回
            if (currPoint.longitude() == prevAddedPoint.longitude()
                    && currPoint.latitude() == prevAddedPoint.latitude()) {
                continue;
            }

            newPoints.add(currPoint);
            newPointsLength = newPoints.size();
            if (newPointsLength > 2) {
                // 如果最后第二个元素在最后第三个元素与最后一个元素的线上，则直接将其删除掉
                if (JTurfHelper.isPointOnLineSegment(newPoints.get(newPointsLength - 3),
                        newPoints.get(newPointsLength - 1),
                        newPoints.get(newPointsLength - 2))) {
                    newPoints.remove(newPointsLength - 2);
                }
            }
        }

        Point lastPoint = points.get(points.size() - 1);

        newPoints.add(lastPoint);
        newPointsLength = newPoints.size();

        // 如果第一个点与最后一个点相同，但是点却小于4个，则是一个错误的Polygon图形
        if ((type == GeometryType.POLYGON || type == GeometryType.MULTI_POLYGON) && JTurfHelper.equals(points.get(0), lastPoint) && newPointsLength < 4) {
            return null;
        }

        // 如果是线条类型，则直接返回
        if (type == GeometryType.LINE_STRING && newPointsLength < 3) {
            return newPoints;
        }

        // 如果最后第二个元素在最后第三个元素与最后一个元素的线上，则直接将其删除掉
        if (JTurfHelper.isPointOnLineSegment(newPoints.get(newPointsLength - 3),
                newPoints.get(newPointsLength - 1),
                newPoints.get(newPointsLength - 2))) {
            newPoints.remove(newPointsLength - 2);
        }

        return newPoints;
    }

}
