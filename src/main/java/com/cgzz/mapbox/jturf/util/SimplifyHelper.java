package com.cgzz.mapbox.jturf.util;

import com.cgzz.mapbox.jturf.JTurfCoordinateMutation;
import com.cgzz.mapbox.jturf.JTurfMeta;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;

import java.util.List;
import java.util.stream.Collectors;

public final class SimplifyHelper {

    private SimplifyHelper() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 简化多边形，POINT和MULTI_POINT不会做任何处理。<br>
     * 获取对象并返回简化版本。内部使用 simplify-js 使用 Ramer–Douglas–Peucker 算法执行简化。
     *
     * @param geometry    要处理的图形组件
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     * @return 返回简化后的图形
     */
    public static <T extends Geometry> T simplify(T geometry, double tolerance, boolean highQuality) {
        JTurfMeta.geomEach(geometry, (geom, parent, geomIndex) -> {
            simplifyGeom(geom, tolerance, highQuality);
            return true;
        });

        return geometry;
    }

    /**
     * 简化要素的坐标
     *
     * @param geometry    要处理的图形组件
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     */
    private static <T extends Geometry> void simplifyGeom(T geometry, double tolerance, boolean highQuality) {
        GeometryType type = geometry.type();
        if (type == GeometryType.POINT || type == GeometryType.MULTI_POINT) {
            return;
        }

        // 删除任何额外的坐标
        JTurfCoordinateMutation.cleanCoords(geometry, true);

        switch (type) {
            case LINE: {
                Line line = Line.line(geometry);
                line.setCoordinates(simplifyLine(line.coordinates(), tolerance, highQuality));
                break;
            }
            case MULTI_LINE: {
                MultiLine multiLine = MultiLine.multiLine(geometry);
                multiLine.setCoordinates(multiLine
                        .coordinates()
                        .stream()
                        .map(pointList -> SimplifyHelper.simplifyLine(pointList, tolerance, highQuality))
                        .collect(Collectors.toList()));
                break;
            }
            case POLYGON: {
                Polygon polygon = Polygon.polygon(geometry);
                polygon.setCoordinates(simplifyPolygon(polygon.coordinates(), tolerance, highQuality));
                break;
            }
            case MULTI_POLYGON: {
                MultiPolygon multiPolygon = MultiPolygon.multiPolygon(geometry);
                multiPolygon.setCoordinates(multiPolygon
                        .coordinates()
                        .stream()
                        .map(pointList -> SimplifyHelper.simplifyPolygon(pointList, tolerance, highQuality))
                        .collect(Collectors.toList()));
                break;
            }
        }
    }

    /**
     * 使用 simplify-js 简化 Line 的坐标
     *
     * @param pointList   要处理的坐标
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     * @return 返回处理后的坐标
     */
    private static List<Point> simplifyLine(List<Point> pointList, double tolerance, boolean highQuality) {
        return Simplify.simplify(pointList, tolerance, highQuality);
    }

    /**
     * 使用 simplify-js 简化 Polygon 的坐标
     *
     * @param pointList   要处理的坐标
     * @param tolerance   简化公差，不能为负数
     * @param highQuality 是否花更多时间使用不同的算法创建更高质量的简化
     * @return 返回处理后的坐标
     */
    private static List<Point> simplifyPolygon(List<Point> pointList, double tolerance, boolean highQuality) {
        if (pointList.size() < 4) {
            throw new JTurfException("invalid polygon");
        }
        List<Point> simpleRing = Simplify.simplify(pointList, tolerance, highQuality);
        //remove 1 percent of tolerance until enough points to make a triangle
        while (!checkValidity(simpleRing)) {
            tolerance -= tolerance * 0.01;
            simpleRing = Simplify.simplify(pointList, tolerance, highQuality);
        }

        Point last = simpleRing.get(simpleRing.size() - 1), first = simpleRing.get(0);
        if (last.getX() != first.getX() || last.getY() != first.getY()) {
            simpleRing.add(Point.fromLngLat(first));
        }

        return simpleRing;
    }

    /**
     * 多边形至少有3个坐标，并且如果为3个坐标的话，则第一个坐标与最后一个坐标不相同，则返回 true。
     *
     * @param ring
     * @return
     */
    private static boolean checkValidity(List<Point> ring) {
        if (ring.size() < 3) {
            return false;
        }
        //  1if the last point is the same as the first, it's not a triangle
        return !(ring.size() == 3 && ring.get(2).getX() == ring.get(0).getX() && ring.get(2).getY() == ring.get(0).getY());
    }

}
