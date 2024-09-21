package com.cgzz.mapbox.jturf;

import com.cgzz.mapbox.jturf.enums.Units;
import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.impl.*;

import java.util.*;

public final class JTurfHelper {

    private JTurfHelper() {
        throw new AssertionError("No Instances.");
    }

    public static final double EARTH_RADIUS = 6371008.8;

    private static final Map<Units, Double> FACTORS = new HashMap<>();

    static {
        FACTORS.put(Units.CENTIMETERS, EARTH_RADIUS * 100);
        FACTORS.put(Units.CENTIMETRES, EARTH_RADIUS * 100);
        FACTORS.put(Units.DEGREES, EARTH_RADIUS / 111325);
        FACTORS.put(Units.FEET, EARTH_RADIUS * 3.28084);
        FACTORS.put(Units.INCHES, EARTH_RADIUS * 39.37);
        FACTORS.put(Units.KILOMETERS, EARTH_RADIUS / 1000);
        FACTORS.put(Units.KILOMETRES, EARTH_RADIUS / 1000);
        FACTORS.put(Units.METERS, EARTH_RADIUS);
        FACTORS.put(Units.METRES, EARTH_RADIUS);
        FACTORS.put(Units.MILES, EARTH_RADIUS / 1609.344);
        FACTORS.put(Units.MILLIMETERS, EARTH_RADIUS * 1000);
        FACTORS.put(Units.MILLIMETRES, EARTH_RADIUS * 1000);
        FACTORS.put(Units.NAUTICAL_MILES, EARTH_RADIUS / 1852);
        FACTORS.put(Units.RADIANS, 1D);
        FACTORS.put(Units.YARDS, EARTH_RADIUS * 1.0936);
    }

    /**
     * 将距离测量值（假设地球为球形）从弧度转换为公里单位。
     *
     * @param radians 弧度值
     * @return double
     */
    public static double radiansToLength(double radians) {
        return radiansToLength(radians, Units.KILOMETERS);
    }

    /**
     * 将距离测量值（假设地球为球形）从弧度转换为更友好的单位。
     * 有效单位：MILES、NAUTICAL_MILES、INCHES、YARDS、METERS、METRES、KILOMETERS、CENTIMETERS、FEET
     *
     * @param radians 弧度值
     * @param units   单位
     * @return double
     */
    public static double radiansToLength(double radians, Units units) {
        if (units == null) {
            units = Units.KILOMETERS;
        }

        Double factor = FACTORS.get(units);
        if (factor == null) {
            throw new IllegalArgumentException(units + " units is invalid");
        }

        return radians * factor;
    }

    /**
     * 将以度为单位的角度转换为弧度
     *
     * @param degrees 度数
     * @return double 弧度
     */
    public static double degreesToRadians(double degrees) {
        return (degrees % 360 * Math.PI) / 180;
    }

    /**
     * 角度转换为弧度
     *
     * @param angle 角度
     * @return double 弧度
     */
    public static double angleToRadians(double angle) {
        return angle * Math.PI / 180;
    }

    /**
     * 将以弧度为单位的角度转换为度
     *
     * @param radians 弧度
     * @return double 角度
     */
    public static double radiansToDegrees(double radians) {
        double degrees = radians % (2 * Math.PI);
        return degrees * 180 / Math.PI;
    }

    /**
     * 将距离测量值（假设地球为球形）从实际单位转换为弧度。
     * 有效单位： MILES, NAUTICAL_MILES, INCHES, YARDS, METERS, METRES, KILOMETERS, CENTIMETERS, FEET
     *
     * @param distance 对应单位的距离
     * @param units    距离对应的单位
     * @return double
     */
    public static double lengthToRadians(double distance, Units units) {
        if (units == null) {
            units = Units.KILOMETERS;
        }

        Double factor = FACTORS.get(units);
        if (factor == null) {
            throw new Error(units + " units is invalid");
        }
        return distance / factor;
    }

    /**
     * 将距离测量值（假设地球为球形）从实际单位转换为度。
     * 有效单位： MILES, NAUTICAL_MILES, INCHES, YARDS, METERS, METRES, KILOMETERS, CENTIMETERS, FEET
     *
     * @param distance 对应单位的距离
     * @param units    距离对应的单位
     * @return double 角度
     */
    public static double lengthToDegrees(double distance, Units units) {
        return radiansToDegrees(lengthToRadians(distance, units));
    }

    /**
     * 将长度转换为指定的单位。
     * 有效单位：MILES, NAUTICAL_MILES, INCHES, YARDS, METERS, METRES, KILOMETERS, CENTIMETERS, FEET
     *
     * @param distance     要转换的长度
     * @param originalUnit 原始单位
     * @param finalUnit    转换为的指定单位
     * @return 返回转换之后的长度
     */
    public static double convertLength(double distance, Units originalUnit, Units finalUnit) {
        if (distance < 0) {
            throw new JTurfException("length must be a positive number");
        }
        if (originalUnit == finalUnit) {
            return distance;
        }

        return radiansToLength(lengthToRadians(distance, originalUnit), finalUnit);
    }

    /**
     * 将轴角度转化为方位角
     *
     * @param bearing 轴角度，介于 -180 和 +180 度之间
     * @return 0 到 360 度之间的角度
     */
    public static double bearingToAzimuth(double bearing) {
        double angle = bearing % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 采用任何角度（以度为单位），并返回介于 0-360 度之间的有效角度
     *
     * @param alfa -180-180度之间的角度
     * @return 0-360度之间的角度
     */
    public static double convertAngleTo360(double alfa) {
        double beta = alfa % 360;
        if (beta < 0) {
            beta += 360;
        }
        return beta;
    }

    /**
     * 四舍五入小数
     *
     * @param number 数值
     * @return 舍入后的数值
     */
    public static double round(double number) {
        return Math.round(number);
    }

    /**
     * 四舍五入并保留指定位数的小数
     *
     * @param number    数值
     * @param precision 保留的小数位
     * @return 舍入后的数值
     */
    public static double round(double number, int precision) {
        if (precision < 0) {
            throw new JTurfException("precision must be a positive number");
        } else if (precision == 0) {
            return Math.round(number);
        }

        double multiplier = Math.pow(10, precision);
        return Math.round(number * multiplier) / multiplier;
    }

    /**
     * 验证数组是否是一个BBox数组
     *
     * @param bbox double数组
     */
    public static void validateBBox(double[] bbox) {
        if (bbox.length != 4) {
            throw new JTurfException("bbox must be an Array of 4 numbers");
        }
    }

    /**
     * 比较两个坐标点是否一致（二维）
     *
     * @param pt1 Point
     * @param pt2 Point
     * @return 一致返回true
     */
    public static boolean equals(Point pt1, Point pt2) {
        return equalsD2(pt1, pt2);
    }

    /**
     * 比较两个坐标点是否一致（二维）
     *
     * @param pt1 Point
     * @param pt2 Point
     * @return 一致返回true
     */
    public static boolean equalsD2(Point pt1, Point pt2) {
        return pt1.getX() == pt2.getX() && pt1.getY() == pt2.getY();
    }

    /**
     * 比较两个坐标点是否一致（三维）
     *
     * @param pt1 Point
     * @param pt2 Point
     * @return 一致返回true
     */
    public static boolean equalsD3(Point pt1, Point pt2) {
        return pt1.getX() == pt2.getX() && pt2.getY() == pt2.getY() && pt1.getZ() == pt2.getZ();
    }

    /**
     * 将传入的参数转换成集合
     *
     * @param lon 经度
     * @param lat 纬度
     * @return List<Double>
     */
    public static List<Double> lonLatToList(double lon, double lat) {
        return Arrays.asList(lon, lat);
    }

    /**
     * 将传入的参数转换成集合
     *
     * @param lon 经度
     * @param lat 纬度
     * @param alt 高度
     * @return List<Double>
     */
    public static List<Double> lonLatAltToList(double lon, double lat, double alt) {
        return Double.isNaN(alt)
                ? Arrays.asList(lon, lat) :
                Arrays.asList(lon, lat, alt);
    }

    /**
     * 根据传入的参数决定是否获取一个新的Geometry对象
     *
     * @param geometry 原Geometry对象
     * @param mutate   false则深度克隆传入的Geometry对象
     * @return 如果传入的mutate为true则返回自身，否则返回一个深度克隆的Geometry
     */
    public static <T extends Geometry> T getGeometryByMutate(T geometry, boolean mutate) {
        // 确保我们不会直接修改传入的geometry对象。
        T newGeometry;
        if (!mutate) {
            newGeometry = JTurfTransformation.clone(geometry);
        } else {
            newGeometry = geometry;
        }

        return newGeometry;
    }

    /**
     * 深度比较两个点集合是否一致
     *
     * @param p1 点集合1
     * @param p2 点集合2
     * @return 完全一致则返回true
     */
    public static boolean deepEquals(List<Point> p1, List<Point> p2) {
        if (p1 == null && p2 == null) {
            return true;
        } else if (p1 != null && p2 == null) {
            return false;
        } else if (p1 == null) {
            return false;
        }

        // 两个均不为空
        if (p1.size() != p2.size()) {
            return false;
        }

        for (int i = 0, size = p1.size(); i < size; i++) {
            if (!equalsD2(p1.get(i), p2.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 验证多边形是否是首尾相连
     *
     * @param points 多边形点集合
     * @return 如果收尾相连则返回true
     */
    public static boolean validatePolygonEndToEnd(List<Point> points) {
        if (points == null || points.size() < 2) {
            throw new JTurfException("points must be an Array of 2 numbers");
        }

        Point first = points.get(0), end = points.get(points.size() - 1);

        return first.longitude() == end.longitude() && first.latitude() == end.latitude();
    }

    /**
     * 判断一个点是否在BBox矩形中
     *
     * @param point 要验证的点
     * @param bbox  矩形
     * @return true则在矩形中，否则为false
     */
    public static boolean inBBox(Point point, BoundingBox bbox) {
        double[] b = bbox.bbox();
        double longitude = point.longitude(), latitude = point.latitude();

        return b[0] <= longitude && b[1] <= latitude && b[2] >= longitude && b[3] >= latitude;
    }

    /**
     * 判断点是否在线中（不忽略线段的两端）
     *
     * @param pt          要判断的点
     * @param lineString  线段
     * @return 如果pt在line中，则返回true
     */
    public static boolean isPointOnLine(Point pt, LineString lineString) {
        List<Point> points = lineString.coordinates();
        for (int i = 0, size = points.size(); i < size - 1; i++) {
            if (isPointOnLineSegment(points.get(i), points.get(i + 1), pt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断点是否在点组合中
     *
     * @param pt 要判断的点
     * @param mp 点组合
     * @return 如果pt在mp中，则返回true
     */
    public static boolean isPointInMultiPoint(Point pt, MultiPoint mp) {
        for (Point p : mp.coordinates()) {
            if (equalsD2(pt, p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断mp1中的点是否均在mp2中
     *
     * @param mp1 要判断的点组合
     * @param mp2 点组合
     * @return 如果mp1中的所有点均在mp2中，则返回true
     */
    public static boolean isMultiPointInMultiPoint(MultiPoint mp1, MultiPoint mp2) {
        // FIXME turf-boolean-within/index.ts 有个BUG，忘记break，不影响结果判定
        for (Point p1 : mp1.coordinates()) {
            boolean anyMatch = false;
            for (Point p2 : mp2.coordinates()) {
                if (equalsD2(p1, p2)) {
                    anyMatch = true;
                    break;
                }
            }

            if (!anyMatch) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断点组合中的点是否均在线上（至少有一个点不在开始和结束顶点上）
     *
     * @param mp         要判断的点组合
     * @param lineString 线段
     * @return 如果mp中所有点均在Line上，则返回true
     */
    public static boolean isMultiPointOnLine(MultiPoint mp, LineString lineString) {
        boolean foundInsidePoint = false;

        for (Point p : mp.coordinates()) {
            if (!JTurfBooleans.booleanPointOnLine(p, lineString)) {
                return false;
            }
            if (!foundInsidePoint) {
                foundInsidePoint = JTurfBooleans.booleanPointOnLine(p, lineString, true);
            }
        }

        return foundInsidePoint;
    }

    /**
     * 判断点组合中的点是否均在多边形中(必须至少一个点在多边形内)
     *
     * @param mp      要判断的点组合
     * @param polygon 多边形
     * @return 如果mp中所有点均在polygon中，则返回true
     */
    public static boolean isMultiPointInPolygon(MultiPoint mp, Polygon polygon) {
        boolean oneInside = false;
        for (Point p : mp.coordinates()) {
            if (!JTurfBooleans.booleanPointInPolygon(p, polygon)) {
                return false;
            }
            if (!oneInside) {
                oneInside = JTurfBooleans.booleanPointInPolygon(p, polygon, true);
            }
        }
        return oneInside;
    }

    /**
     * 判断点组合中的点是否均在多边形中(必须至少一个点在多边形内)
     *
     * @param mp           要判断的点组合
     * @param multiPolygon 组合多边形
     * @return 如果mp中所有点均在polygon中，则返回true
     */
    public static boolean isMultiPointInPolygon(MultiPoint mp, MultiPolygon multiPolygon) {
        boolean oneInside = false;
        for (Point p : mp.coordinates()) {
            if (!JTurfBooleans.booleanPointInPolygon(p, multiPolygon)) {
                return false;
            }
            if (!oneInside) {
                oneInside = JTurfBooleans.booleanPointInPolygon(p, multiPolygon, true);
            }
        }
        return oneInside;
    }

    /**
     * 判断线段1是否在线段2上
     *
     * @param line1 要判断的线段
     * @param line2 线段
     * @return 如果line1完全在line2中，则返回true
     */
    public static boolean isLineOnLine(LineString line1, LineString line2) {
        for (Point p : line1.coordinates()) {
            if (!JTurfBooleans.booleanPointOnLine(p, line2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断线段是否在多边形中
     *
     * @param line    要判断的线段
     * @param polygon 多边形
     * @return 如果line在多边形内，则返回true
     */
    public static boolean isLineInPolygon(LineString line, Polygon polygon) {
        return isLineInPoly(line, polygon);
    }

    /**
     * 判断线段是否在多边形中
     *
     * @param line         要判断的线段
     * @param multiPolygon 组合多边形
     * @return 如果line在多边形内，则返回true
     */
    public static boolean isLineInPolygon(LineString line, MultiPolygon multiPolygon) {
        return isLineInPoly(line, multiPolygon);
    }

    /**
     * 判断线段是否在多边形中
     *
     * @param line     要判断的线段
     * @param geometry 多边形，只支持Polygon、MultiPolygon
     * @return 如果line在多边形内，则返回true
     */
    private static boolean isLineInPoly(LineString line, Geometry geometry) {
        BoundingBox polyBbox = JTurfMeasurement.bbox(geometry);
        BoundingBox lineBbox = JTurfMeasurement.bbox(line);
        if (!isBBoxOverlap(polyBbox, lineBbox)) {
            return false;
        }

        boolean foundInsidePoint = false;
        List<Point> coordinates = line.coordinates();
        for (int i = 0, size = coordinates.size() - 1; i < size; i++) {
            Point p = coordinates.get(i);
            // 如果点不在多边形中，直接返回
            if (!JTurfBooleans.booleanPointInPolygon(p, geometry, false)) {
                return false;
            }
            // 下面军事判断点是否在多边形内（不能在边界上）
            if (!foundInsidePoint) {
                foundInsidePoint = JTurfBooleans.booleanPointInPolygon(p, geometry, true);
            }
            // 跑到这里，证明点在多边形的边界上，则计算当前点与下一个点的中间点是否在多边形上，如果在的话，则证明先在多边形内。
            if (!foundInsidePoint) {
                Point midPoint = getMidPoint(p, coordinates.get(i + 1));
                foundInsidePoint = JTurfBooleans.booleanPointInPolygon(midPoint, geometry, true);
            }
        }

        return foundInsidePoint;
    }

    /**
     * 判断多边形1是否在多边形2中，<b>注意：这里只会判断polygon1的outer形状是否在polygon2中</b>
     *
     * @param polygon1 要判断的多边形
     * @param polygon2 多边形，支持Polygon、MultiPolygon
     * @return 如果多边形1在多边形2中，则返回true
     */
    public static boolean isPolygonInPolygon(Polygon polygon1, Geometry polygon2) {
        BoundingBox poly1Bbox = JTurfMeasurement.bbox(polygon1);
        BoundingBox poly2Bbox = JTurfMeasurement.bbox(polygon2);
        if (!isBBoxOverlap(poly2Bbox, poly1Bbox)) {
            return false;
        }

        for (Point p : polygon1.coordinates().get(0)) {
            if (!JTurfBooleans.booleanPointInPolygon(p, polygon2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断bbox1是否覆盖了bbox2
     *
     * @param bbox1 矩形Box1
     * @param bbox2 矩形Box2
     * @return bbox1覆盖了bbox2则返回true
     */
    public static boolean isBBoxOverlap(BoundingBox bbox1, BoundingBox bbox2) {
        if (bbox1.get(0) > bbox2.get(0)) {
            return false;
        }
        if (bbox1.get(2) < bbox2.get(2)) {
            return false;
        }
        if (bbox1.get(1) > bbox2.get(1)) {
            return false;
        }
        if (bbox1.get(3) < bbox2.get(3)) {
            return false;
        }
        return true;
    }

    /**
     * 计算两个点之间的中间点，仅仅是两个点的位置/2。与 #{@link JTurfMeasurement#midpoint } 是有区别的
     *
     * @param p1 点1
     * @param p2 点2
     * @return 返回两点之间的中点
     */
    public static Point getMidPoint(Point p1, Point p2) {
        return Point.fromLngLat((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    }

    /**
     * 判断point是否位于start和end之间的线段上（不忽略线的两端）
     *
     * @param start 开始的点
     * @param end   结束的点
     * @param point 需要判断的点
     * @return 在线段上则返回true
     */
    public static boolean isPointOnLineSegment(Point start, Point end, Point point) {
        return isPointOnLineSegment(start, end, point, true);
    }

    /**
     * 判断点是否在线的两端之间
     *
     * @param lineSegmentStart 线段开始坐标对的行首
     * @param lineSegmentEnd   线段结束坐标对的行尾
     * @param point            判断的点
     * @param incEndVertices   是否允许点落在线两端
     * @return boolean
     */
    public static boolean isPointOnLineSegment(Point lineSegmentStart, Point lineSegmentEnd, Point point, boolean incEndVertices) {
        double x = point.getX(), y = point.getY();
        double x1 = lineSegmentStart.getX(), y1 = lineSegmentStart.getY();
        double x2 = lineSegmentEnd.getX(), y2 = lineSegmentEnd.getY();
        double dxc = x - x1, dyc = y - y1;
        double dxl = x2 - x1, dyl = y2 - y1;
        double cross = dxc * dyl - dyc * dxl;

        if (cross != 0) {
            return false;
        }

        boolean dxlGtdyl = Math.abs(dxl) >= Math.abs(dyl);
        // 是否允许判断两端
        if (incEndVertices) {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 <= x && x <= x2 : x2 <= x && x <= x1;
            }
            return dyl > 0 ? y1 <= y && y <= y2 : y2 <= y && y <= y1;
        } else {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 < x && x < x2 : x2 < x && x < x1;
            }
            return dyl > 0 ? y1 < y && y < y2 : y2 < y && y < y1;
        }
    }

    /**
     * 判断点是否在线的两端之间
     *
     * @param lineSegmentStart 线段开始坐标对的行首
     * @param lineSegmentEnd   线段结束坐标对的行尾
     * @param point            判断的点
     * @param excludeBoundary  排除边界是否允许点落在线端，0不忽略，1开头，2结尾，3中间
     * @param epsilon          要与交叉乘积结果进行比较的小数。用于处理浮点，例如经纬度点
     * @return boolean
     */
    public static boolean isPointOnLineSegment(Point lineSegmentStart, Point lineSegmentEnd, Point point, int excludeBoundary, Number epsilon) {
        double x = point.getX(), y = point.getY();
        double x1 = lineSegmentStart.getX(), y1 = lineSegmentStart.getY();
        double x2 = lineSegmentEnd.getX(), y2 = lineSegmentEnd.getY();
        double dxc = x - x1, dyc = y - y1;
        double dxl = x2 - x1, dyl = y2 - y1;
        double cross = dxc * dyl - dyc * dxl;

        if (epsilon != null) {
            if (Math.abs(cross) > epsilon.doubleValue()) {
                return false;
            }
        } else if (cross != 0) {
            return false;
        }

        boolean dxlGtdyl = Math.abs(dxl) >= Math.abs(dyl);
        if (excludeBoundary == 0) {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 <= x && x <= x2 : x2 <= x && x <= x1;
            }
            return dyl > 0 ? y1 <= y && y <= y2 : y2 <= y && y <= y1;
        } else if (excludeBoundary == 1) {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 < x && x <= x2 : x2 <= x && x < x1;
            }
            return dyl > 0 ? y1 < y && y <= y2 : y2 <= y && y < y1;
        } else if (excludeBoundary == 2) {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 <= x && x < x2 : x2 < x && x <= x1;
            }
            return dyl > 0 ? y1 <= y && y < y2 : y2 < y && y <= y1;
        } else if (excludeBoundary == 3) {
            if (dxlGtdyl) {
                return dxl > 0 ? x1 < x && x < x2 : x2 < x && x < x1;
            }
            return dyl > 0 ? y1 < y && y < y2 : y2 < y && y < y1;
        }

        return false;
    }

    /**
     * 判断点集合是否是闭合的，常用于判断Polygon的点集合
     *
     * @param pointList 点集合
     * @return 如果首尾两个点一致，则返回true
     */
    public static boolean checkRingsClose(List<Point> pointList) {
        if (pointList == null || pointList.size() < 2) {
            return false;
        }

        return equalsD2(pointList.get(0), pointList.get(pointList.size() - 1));
    }

    /**
     * 检查点集合是否有来回穿刺，常用于判断Polygon的点集合
     *
     * @param pointList 点集合
     * @return 如果有穿刺，则返回true
     */
    public static boolean checkRingsForSpikesPunctures(List<Point> pointList) {
        if (pointList == null || pointList.size() < 2) {
            return false;
        }

        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            Point point = pointList.get(i);
            for (int ii = i + 1; ii < size - 2; ii++) {
                if (isPointOnLine(point, LineString.fromLngLats(pointList.get(ii), pointList.get(ii + 1)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如果传入的点集合与另一个点组集合是否正常（不相交，但是又交叉）
     *
     * @param poly  要判断的点集合
     * @param geom  点组集合
     * @param index 从geom的哪个位置开始循环
     * @return 如果正常则返回true
     */
    public static boolean checkPolygonAgainstOthers(List<List<Point>> poly, List<List<List<Point>>> geom, int index) {
        Polygon polyToCheck = Polygon.fromLngLats(poly);
        for (int i = index + 1, size = geom.size(); i < size; i++) {
            // 判断是否不相交
            if (!JTurfBooleans.booleanDisjoint(polyToCheck, Polygon.fromLngLats(geom.get(i)))) {
                // 判断是否交叉
                if (JTurfBooleans.booleanCrosses(polyToCheck, LineString.fromLngLats(geom.get(i).get(0)))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 深度克隆图形集合
     *
     * @param geometryList 被克隆的图形集合
     * @return 新的图形集合
     */
    public static <T extends Geometry> List<T> deepCloneList(List<T> geometryList) {
        List<T> list = new ArrayList<>(geometryList.size());
        for (T o : geometryList) {
            list.add(JTurfTransformation.clone(o));
        }
        return list;
    }

    /**
     * 深度克隆点集合
     *
     * @param pointList 要被克隆的点集合
     * @return 新的点集合
     */
    public static Point[] deepCloneArray(List<Point> pointList) {
        int size = pointList.size();
        Point[] array = new Point[size];
        for (int i = 0; i < size; i++) {
            array[i] = Point.fromLngLat(pointList.get(i));
        }
        return array;
    }

    /**
     * 比较两个线条的斜率并返回结果
     *
     * @param line1 线段1
     * @param line2 线段2
     * @return 如果斜率相等返回true
     */
    public static boolean isParallel(LineString line1, LineString line2) {
        double slope1 = JTurfHelper.bearingToAzimuth(JTurfMeasurement.rhumbBearing(line1.coordinates().get(0), line1.coordinates().get(1)));
        double slope2 = JTurfHelper.bearingToAzimuth(JTurfMeasurement.rhumbBearing(line2.coordinates().get(0), line2.coordinates().get(1)));

        return slope1 == slope2;
    }

    /**
     * 比较两个线条的斜率并返回结果
     *
     * @param feature1 线段1
     * @param feature2 线段2
     * @return 如果斜率相等返回true
     */
    public static boolean isParallel(Feature<LineString> feature1, Feature<LineString> feature2) {
        return isParallel(feature1.geometry(), feature2.geometry());
    }

}
