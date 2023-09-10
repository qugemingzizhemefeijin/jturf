package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.util.*;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.IntHolder;

import java.util.List;

public final class JTurfBooleans {

    private JTurfBooleans() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断一条线是顺时针还是逆时针
     *
     * @param line 线图形
     * @return true是顺时针, false逆时针
     */
    public static boolean booleanClockwise(Line line) {
        return booleanClockwise(line.coordinates());
    }

    /**
     * 判断坐标集合是顺时针还是逆时针
     *
     * @param points 坐标点集合
     * @return true是顺时针, false逆时针
     */
    public static boolean booleanClockwise(List<Point> points) {
        int sum = 0, i = 1, length = points.size();
        Point cur = points.get(0);
        Point prev;

        while (i < length) {
            prev = cur;
            cur = points.get(i);
            sum += (cur.getLongitude() - prev.getLongitude()) * (cur.getLatitude() + prev.getLatitude());
            i++;
        }
        return sum > 0;
    }

    /**
     * 确定相同类型的两个几何图形是否具有相同的X、Y坐标值。（注意：这里x,y坐标的小数默认为6位相同）
     *
     * @param g1 图形组件
     * @param g2 图形组件
     * @return 如果对象相等，则返回true,否则返回false
     */
    public static boolean booleanEqual(Geometry g1, Geometry g2) {
        return Equality.compare(g1, g2, 6);
    }

    /**
     * 确定相同类型的两个几何图形是否具有相同的X、Y坐标值。
     *
     * @param g1        图形组件
     * @param g2        图形组件
     * @param precision 坐标的小数精度（超出精度四舍五入）
     * @return 如果对象相等，则返回true,否则返回false
     */
    public static boolean booleanEqual(Geometry g1, Geometry g2, int precision) {
        return Equality.compare(g1, g2, precision);
    }

    /**
     * 如果line1的每个线段与line2的对应线段平行，则返回True。
     *
     * @param line1 线段1
     * @param line2 线段2
     * @return 当线段平行，则返回true
     */
    public static boolean booleanParallel(Line line1, Line line2) {
        if (line1 == null) {
            throw new JTurfException("line1 is required");
        }
        if (line2 == null) {
            throw new JTurfException("line2 is required");
        }

        List<Line> segments1 = JTurfMisc.lineSegment(JTurfCoordinateMutation.cleanCoords(line1));
        List<Line> segments2 = JTurfMisc.lineSegment(JTurfCoordinateMutation.cleanCoords(line2));

        for (int i = 0, size1 = segments1.size(), size2 = segments2.size(); i < size1; i++) {
            if (i >= size2) {
                break;
            }
            if (!isParallel(segments1.get(i), segments2.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个线条的斜率并返回结果
     *
     * @param line1 线段1
     * @param line2 线段2
     * @return 如果斜率相等返回true
     */
    private static boolean isParallel(Line line1, Line line2) {
        double slope1 = JTurfHelper.bearingToAzimuth(JTurfMeasurement.rhumbBearing(line1.coordinates().get(0), line1.coordinates().get(1)));
        double slope2 = JTurfHelper.bearingToAzimuth(JTurfMeasurement.rhumbBearing(line2.coordinates().get(0), line2.coordinates().get(1)));

        return slope1 == slope2;
    }

    /**
     * 判断点是否在多边形内，如果点在多边形的边界上，也算在内。
     *
     * @param point    要判断的点
     * @param geometry 多边形，支持 Polygon、MultiPolygon
     * @return 如果点在多边形内，则返回true;否则返回false
     */
    public static boolean booleanPointInPolygon(Point point, Geometry geometry) {
        return booleanPointInPolygon(point, geometry, false);
    }

    /**
     * 判断点是否在多边形内
     *
     * @param point          要判断的点
     * @param geometry       多边形，支持 Polygon、MultiPolygon
     * @param ignoreBoundary 是否忽略多边形边界（true如果点在多边形的边界上不算，false则也算在多边形内）
     * @return 如果点在多边形内，则返回true;否则返回false
     */
    public static boolean booleanPointInPolygon(Point point, Geometry geometry, boolean ignoreBoundary) {
        if (point == null) {
            throw new JTurfException("point is required");
        }
        if (geometry == null) {
            throw new JTurfException("polygon is required");
        }
        if (geometry.type() != GeometryType.POLYGON && geometry.type() != GeometryType.MULTI_POLYGON) {
            throw new JTurfException("geometry only support polygon or multiPolygon");
        }

        BoundingBox bbox = JTurfMeasurement.bbox(geometry);
        if (!JTurfHelper.inBBox(point, bbox)) {
            return false;
        }

        if (geometry instanceof Polygon) {
            int polyResult = JTurfPointInPolygonHelper.pointInPolygon(point, Polygon.polygon(geometry));
            if (polyResult == 0) {
                return !ignoreBoundary;
            } else {
                return polyResult == 1;
            }
        } else {
            MultiPolygon multiPolygon = MultiPolygon.multiPolygon(geometry);

            // FIXME 此代码在turf.js的master下有BUG。可以有机会提一个isuse
            boolean result = false;
            for (Polygon polygon : multiPolygon.polygons()) {
                int polyResult = JTurfPointInPolygonHelper.pointInPolygon(point, polygon);
                if (polyResult == 0) {
                    if (ignoreBoundary) { // 如果在边界上，并且在边界上不算的话，则直接返回
                        return false;
                    } else {
                        result = true;
                    }
                } else if (polyResult == 1) { // 如果在边界内
                    result = true;
                } else {
                    return false;
                }
            }

            return result;
        }
    }

    /**
     * 如果点位于直线上，则返回 true。默认不忽略忽略线段的起始和终止顶点.
     *
     * @param point 要判断的点
     * @param line  线
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, Line line) {
        return booleanPointOnLine(point, line, false, null);
    }

    /**
     * 如果点位于直线上，则返回 true。接受可选参数以忽略线串的开始和结束顶点。
     *
     * @param point             要判断的点
     * @param line              线
     * @param ignoreEndVertices 是否忽略线段的起始和终止顶点
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, Line line, boolean ignoreEndVertices) {
        return booleanPointOnLine(point, line, ignoreEndVertices, null);
    }

    /**
     * 如果点位于直线上，则返回 true。接受可选参数以忽略线串的开始和结束顶点。
     *
     * @param point             要判断的点
     * @param line              线
     * @param ignoreEndVertices 是否忽略线段的起始和终止顶点
     * @param epsilon           要与交叉乘积结果进行比较的小数。用于处理浮点，例如经纬度点
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, Line line, boolean ignoreEndVertices, Number epsilon) {
        if (point == null) {
            throw new JTurfException("point is required");
        }
        if (line == null) {
            throw new JTurfException("line is required");
        }

        List<Point> pointList = line.coordinates();
        if (pointList == null || pointList.isEmpty()) {
            return false;
        }

        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            int ignoreBoundary = 0;
            if (ignoreEndVertices) {
                if (i == 0) {
                    ignoreBoundary = 1; // start
                }
                if (i == size - 2) {
                    ignoreBoundary = 2; // end
                }
                if (i == 0 && i + 1 == size - 1) {
                    ignoreBoundary = 3; // both
                }
            }

            if (JTurfHelper.isPointOnLineSegment(pointList.get(i), pointList.get(i + 1), point, ignoreBoundary, epsilon)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否重叠。<br>
     * <br>
     * 比较相同维度的两个几何图形，如果它们的交集集产生的几何图形与两个几何图形不同，但维度相同，则返回true。
     *
     * @param geometry1 图形1，支持Line、MultiLine、Polygon、MultiPolygon
     * @param geometry2 图形2，支持Line、MultiLine、Polygon、MultiPolygon
     * @return 如果重叠则返回true
     */
    public static boolean booleanOverlap(Geometry geometry1, Geometry geometry2) {
        GeometryType type1 = geometry1.type(), type2 = geometry2.type();

        if ((type1 == GeometryType.MULTI_POINT && type2 != GeometryType.MULTI_POINT) ||
                ((type1 == GeometryType.LINE || type1 == GeometryType.MULTI_LINE)
                        && type2 != GeometryType.LINE && type2 != GeometryType.MULTI_LINE) ||
                ((type1 == GeometryType.POLYGON || type1 == GeometryType.MULTI_POLYGON) && type2 != GeometryType.POLYGON
                        && type2 != GeometryType.MULTI_POLYGON)
        ) {
            throw new JTurfException("features must be of the same type");
        }
        if (type1 == GeometryType.POINT) {
            throw new JTurfException("Point geometry not supported");
        }

        // must be not equal 不知道为什么要有这一段？？
        if (Equality.compare(geometry1, geometry2, 6)) {
            return false;
        }

        IntHolder overlap = new IntHolder();
        switch (type1) {
            case MULTI_POINT:
                List<Point> pointList1 = MultiPoint.multiPoint(geometry1).coordinates();
                List<Point> pointList2 = MultiPoint.multiPoint(geometry2).coordinates();
                for (Point p1 : pointList1) {
                    for (Point p2 : pointList2) {
                        if (JTurfHelper.equals(p1, p2)) {
                            return true;
                        }
                    }
                }
                return false;
            case LINE:
            case MULTI_LINE:
                JTurfMeta.segmentEach(geometry1, (currentSegment1, geometryIndex1, multiIndex1, segmentIndex1) -> {
                    JTurfMeta.segmentEach(geometry2, (currentSegment2, geometryIndex2, multiIndex2, segmentIndex2) -> {
                        List<Line> overlapLines = JTurfMisc.lineOverlap(currentSegment1, currentSegment2);
                        if (overlapLines.size() > 0) {
                            overlap.value++;
                        }
                        return true;
                    });

                    return true;
                });
                break;
            case POLYGON:
            case MULTI_POLYGON:
                JTurfMeta.segmentEach(geometry1, (currentSegment1, geometryIndex1, multiIndex1, segmentIndex1) -> {
                    JTurfMeta.segmentEach(geometry2, (currentSegment2, geometryIndex2, multiIndex2, segmentIndex2) -> {
                        List<Point> intersectLines = JTurfMisc.lineIntersect(currentSegment1, currentSegment2);
                        if (intersectLines != null && intersectLines.size() > 0) {
                            overlap.value++;
                        }
                        return true;
                    });

                    return true;
                });
                break;
        }

        return overlap.value > 0;
    }

    /**
     * 判断第一个图形是否完全在第二个图形内 <br><br>
     * <p>
     * 如果第一个几何图形完全在第二个几何图形内，则返回true。两个几何图形的内部必须相交，
     * 并且，主几何图形(几何a)的内部和边界不能相交于次几何图形(几何b)的外部。
     *
     * <br><br>
     * 注意：这里有几个问题：比如判断两个线，实际只是判断线1的所有点是否在线2中，注入此类。
     *
     * @param geometry1 图形组件1
     * @param geometry2 图形组件2
     * @return 第一个图形在第二个图形内，则返回true
     */
    public static boolean booleanWithin(Geometry geometry1, Geometry geometry2) {
        GeometryType type1 = geometry1.type(), type2 = geometry2.type();

        switch (type1) {
            case POINT:
                switch (type2) {
                    case MULTI_POINT:
                        return JTurfHelper.isPointInMultiPoint(Point.point(geometry1), MultiPoint.multiPoint(geometry2));
                    case LINE:
                        return JTurfBooleans.booleanPointOnLine(Point.point(geometry1), Line.line(geometry2), true);
                    case POLYGON:
                    case MULTI_POLYGON:
                        return JTurfBooleans.booleanPointInPolygon(Point.point(geometry1), geometry2, true);
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case MULTI_POINT:
                switch (type2) {
                    case MULTI_POINT:
                        return JTurfHelper.isMultiPointInMultiPoint(MultiPoint.multiPoint(geometry1), MultiPoint.multiPoint(geometry2));
                    case LINE:
                        return JTurfHelper.isMultiPointOnLine(MultiPoint.multiPoint(geometry1), Line.line(geometry2));
                    case POLYGON:
                        return JTurfHelper.isMultiPointInPolygon(MultiPoint.multiPoint(geometry1), Polygon.polygon(geometry2));
                    case MULTI_POLYGON:
                        return JTurfHelper.isMultiPointInPolygon(MultiPoint.multiPoint(geometry1), MultiPolygon.multiPolygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case LINE:
                switch (type2) {
                    case LINE:
                        return JTurfHelper.isLineOnLine(Line.line(geometry1), Line.line(geometry2));
                    case POLYGON:
                        return JTurfHelper.isLineInPolygon(Line.line(geometry1), Polygon.polygon(geometry2));
                    case MULTI_POINT:
                        return JTurfHelper.isLineInPolygon(Line.line(geometry1), MultiPolygon.multiPolygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            case POLYGON:
                switch (type2) {
                    case POLYGON:
                    case MULTI_POLYGON:
                        return JTurfHelper.isPolygonInPolygon(Polygon.polygon(geometry1), geometry2);
                    default:
                        throw new JTurfException("geometry2 " + type2 + " geometry not supported");
                }
            default:
                throw new JTurfException("geometry1 " + type1 + " geometry not supported");
        }
    }

    /**
     * 判断是否交叉<br><br>
     * <p>
     * 如果交集产生的几何图形的维数比两个源几何图形的最大维数小1，并且交集集位于两个源几何图形的内部，则返回True。
     *
     * @param geometry1 图形1，支持 MULTI_POINT、LINE、POLYGON
     * @param geometry2 图形2，支持 MULTI_POINT、LINE、POLYGON
     * @return 如果交叉则返回true
     */
    public static boolean booleanCrosses(Geometry geometry1, Geometry geometry2) {
        if (geometry1 == null) {
            throw new JTurfException("geometry1 is required");
        }
        if (geometry2 == null) {
            throw new JTurfException("geometry2 is required");
        }

        GeometryType t1 = geometry1.type(), t2 = geometry2.type();

        switch (t1) {
            case MULTI_POINT:
                switch (t2) {
                    case LINE:
                        return BooleanCrossesHelper.doMultiPointAndLineStringCross(MultiPoint.multiPoint(geometry1), Line.line(geometry2));
                    case POLYGON:
                        return BooleanCrossesHelper.doesMultiPointCrossPoly(MultiPoint.multiPoint(geometry1), Polygon.polygon(geometry2));
                    default:
                        throw new JTurfException("geometry2 " + t2 + " not supported");
                }
            case LINE:
                switch (t2) {
                    case MULTI_POINT:
                        return BooleanCrossesHelper.doMultiPointAndLineStringCross(MultiPoint.multiPoint(geometry2), Line.line(geometry1));
                    case LINE:
                        return BooleanCrossesHelper.doLineStringsCross(Line.line(geometry1), Line.line(geometry2));
                    case POLYGON:
                        return BooleanCrossesHelper.doLineStringAndPolygonCross(Line.line(geometry1), Polygon.polygon(geometry2));
                    default:
                        throw new JTurfException("geometry1 " + t2 + " not supported");
                }
            case POLYGON:
                switch (t2) {
                    case MULTI_POINT:
                        return BooleanCrossesHelper.doesMultiPointCrossPoly(MultiPoint.multiPoint(geometry2), Polygon.polygon(geometry1));
                    case LINE:
                        return BooleanCrossesHelper.doLineStringAndPolygonCross(Line.line(geometry2), Polygon.polygon(geometry1));
                    default:
                        throw new JTurfException("geometry1 " + t2 + " not supported");
                }
            default:
                throw new JTurfException("geometry1 " + t1 + " not supported");
        }
    }

    /**
     * 判断是否不相交 <br><br>
     * <p>
     * 如果两个几何图形的交集为空集，则返回(TRUE)。
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果图形不相交，则返回true
     */
    public static boolean booleanDisjoint(Geometry geometry1, Geometry geometry2) {
        BooleanHolder bool = new BooleanHolder(true);
        JTurfMeta.flattenEach(geometry1, (g1, m1) -> {
            JTurfMeta.flattenEach(geometry2, (g2, m2) -> {
                if (!bool.value) {
                    return false;
                }
                bool.value = BooleanDisjointHelper.disjoint(g1, g2);
                return true;
            });

            return true;
        });

        return bool.value;
    }

    /**
     * 判断是否包含<br><br>
     * <p>
     * 如果第二个几何图形完全包含在第一个几何图形中，则Boolean-contains返回True。
     * 两个几何图形的内部必须相交，次要图形的内部和边界(几何图形b)不能与主要图形的外部(几何图形a)相交。
     * Boolean-contains返回与@turf/boolean-within完全相反的结果。
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果图形1包含图形2则返回true
     */
    public static boolean booleanContains(Geometry geometry1, Geometry geometry2) {
        return BooleanContainsHelper.booleanContains(geometry1, geometry2);
    }

    /**
     * 检查图形是否是有效的
     *
     * @param geometry 图形
     * @return 如果图形有效则返回true
     */
    public static boolean booleanValid(Geometry geometry) {
        return BooleanValidHelper.booleanValid(geometry);
    }

    /**
     * 判断两个图形是否有交集（就是循环迭代图形，并对 booleanDisjoint 取反）
     *
     * @param geometry1 图形1
     * @param geometry2 图形2
     * @return 如果两个图形有交集则返回true
     */
    public static boolean booleanIntersects(Geometry geometry1, Geometry geometry2) {
        BooleanHolder bool = new BooleanHolder();

        JTurfMeta.flattenEach(geometry1, (g1, multiIndex1) -> {
            JTurfMeta.flattenEach(geometry2, (g2, multiIndex2) -> {
                boolean b = !JTurfBooleans.booleanDisjoint(g1, g2);
                if (b) {
                    bool.value = true;
                    return false; // 只要有相交，则跳出循环
                }
                return true;
            });

            return !bool.value; // 只要有相交，则跳出循环
        });

        return bool.value;
    }

}
