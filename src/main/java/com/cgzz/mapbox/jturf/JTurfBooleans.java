package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.models.BooleanHolder;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.cgzz.mapbox.jturf.util.booleans.*;

import java.util.List;

public final class JTurfBooleans {

    private JTurfBooleans() {
        throw new AssertionError("No Instances.");
    }

    /**
     * 判断一条线是顺时针还是逆时针
     *
     * @param lineString 线图形
     * @return true是顺时针, false逆时针
     */
    public static boolean booleanClockwise(LineString lineString) {
        return booleanClockwise(lineString.coordinates());
    }

    /**
     * 判断坐标集合是顺时针还是逆时针
     *
     * @param coordinates 坐标点集合
     * @return true是顺时针, false逆时针
     */
    public static boolean booleanClockwise(List<Point> coordinates) {
        int sum = 0, i = 1, length = coordinates.size();
        Point cur = coordinates.get(0);
        Point prev;

        while (i < length) {
            prev = cur;
            cur = coordinates.get(i);
            sum += (cur.longitude() - prev.longitude()) * (cur.latitude() + prev.latitude());
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
        return GeoJsonEquality.compare(g1, g2, 6);
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
        return GeoJsonEquality.compare(g1, g2, precision);
    }

    /**
     * 如果line1的每个线段与line2的对应线段平行，则返回True。
     *
     * @param line1 线段1
     * @param line2 线段2
     * @return 当线段平行，则返回true
     */
    public static boolean booleanParallel(LineString line1, LineString line2) {
        if (line1 == null) {
            throw new JTurfException("line1 is required");
        }
        if (line2 == null) {
            throw new JTurfException("line2 is required");
        }

        FeatureCollection<LineString> segments1 = JTurfMisc.lineSegment(JTurfCoordinateMutation.cleanCoords(line1));
        FeatureCollection<LineString> segments2 = JTurfMisc.lineSegment(JTurfCoordinateMutation.cleanCoords(line2));

        for (int i = 0, size1 = segments1.size(), size2 = segments2.size(); i < size1; i++) {
            if (i >= size2) {
                break;
            }
            if (!JTurfHelper.isParallel(segments1.get(i), segments2.get(i))) {
                return false;
            }
        }
        return true;
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
        return BooleanPointInPolygonHelper.booleanPointInPolygon(point, geometry, ignoreBoundary);
    }

    /**
     * 如果点位于直线上，则返回 true。默认不忽略忽略线段的起始和终止顶点.
     *
     * @param point       要判断的点
     * @param lineString  线
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, LineString lineString) {
        return booleanPointOnLine(point, lineString, false, null);
    }

    /**
     * 如果点位于直线上，则返回 true。接受可选参数以忽略线串的开始和结束顶点。
     *
     * @param point             要判断的点
     * @param lineString        线
     * @param ignoreEndVertices 是否忽略线段的起始和终止顶点
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, LineString lineString, boolean ignoreEndVertices) {
        return booleanPointOnLine(point, lineString, ignoreEndVertices, null);
    }

    /**
     * 如果点位于直线上，则返回 true。接受可选参数以忽略线串的开始和结束顶点。
     *
     * @param point             要判断的点
     * @param lineString        线
     * @param ignoreEndVertices 是否忽略线段的起始和终止顶点
     * @param epsilon           要与交叉乘积结果进行比较的小数。用于处理浮点，例如经纬度点
     * @return boolean
     */
    public static boolean booleanPointOnLine(Point point, LineString lineString, boolean ignoreEndVertices, Number epsilon) {
        return BooleanPointOnLineHelper.booleanPointOnLine(point, lineString, ignoreEndVertices, epsilon);
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
        return BooleanOverlapHelper.booleanOverlap(geometry1, geometry2);
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
        return BooleanWithinHelper.booleanWithin(geometry1, geometry2);
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
        return BooleanCrossesHelper.booleanCrosses(geometry1, geometry2);
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
        return BooleanDisjointHelper.booleanDisjoint(geometry1, geometry2);
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

        JTurfMeta.flattenEach(geometry1, (f1, featureIndex1, multiFeatureIndex1) -> {
            JTurfMeta.flattenEach(geometry2, (f2, featureIndex2, multiFeatureIndex2) -> {
                boolean b = !JTurfBooleans.booleanDisjoint(f1, f2);
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

    /**
     * 判断是否为凹多边形
     *
     * @param polygon 要判断的多边形
     * @return 如果为凹多边形则返回true
     */
    public static boolean booleanConcave(Feature<Polygon> polygon) {
        return booleanConcave(polygon.geometry());
    }

    /**
     * 判断是否为凹多边形
     *
     * @param polygon 要判断的多边形
     * @return 如果为凹多边形则返回true
     */
    public static boolean booleanConcave(Polygon polygon) {
        boolean sign = false;

        List<Point> outer = polygon.coordinates().get(0);
        int n = outer.size() - 1;
        for (int i = 0; i < n; i++) {
            Point p1 = outer.get((i + 2) % n), p2 = outer.get((i + 1) % n), p3 = outer.get(i);

            double dx1 = p1.getX() - p2.getX();
            double dy1 = p1.getY() - p2.getY();
            double dx2 = p3.getX() - p2.getX();
            double dy2 = p3.getY() - p2.getY();
            double crossProduct = dx1 * dy2 - dy1 * dx2;
            if (i == 0) {
                sign = crossProduct > 0;
            } else if (sign != crossProduct > 0) {
                return true;
            }
        }
        return false;
    }

}
